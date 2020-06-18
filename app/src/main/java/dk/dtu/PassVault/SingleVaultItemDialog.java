package dk.dtu.PassVault;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.lang.ref.WeakReference;

import dk.dtu.PassVault.Business.Crypto.Crypto;
import dk.dtu.PassVault.Business.Database.Database;
import dk.dtu.PassVault.Business.Database.Entities.VaultItem;

public class SingleVaultItemDialog extends DialogFragment {

    protected VaultActivity vaultActivity;
    protected Context context;
    protected VaultItem item;

    public SingleVaultItemDialog(VaultActivity vaultActivity, VaultItem item) {
        this.vaultActivity = vaultActivity;
        this.item = item;
    }

    protected static class DeleteVaultItemTransaction extends Database.Transaction<Void> {

        protected VaultItem vaultItem;
        protected WeakReference<SingleVaultItemDialog> ref;

        public DeleteVaultItemTransaction(VaultItem vaultItem, WeakReference<SingleVaultItemDialog> ref) {
            this.vaultItem = vaultItem;
            this.ref = ref;
        }

        @Override
        public Void doRequest(Database db) {
            db.deleteVaultItem(this.vaultItem);
            return null;
        }

        @Override
        public void onResult(Void v) {
            SingleVaultItemDialog activity = this.ref.get();

            if(activity == null) {
                return;
            }

            Toast.makeText(activity.getContext(), "Item deleted", Toast.LENGTH_LONG).show();
            activity.vaultActivity.refreshList();
            activity.dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        setRetainInstance(true);

        builder.setView(inflater.inflate(R.layout.single_vault_item_dialog, null));

        AlertDialog ad = builder.create();

        ad.setOnShowListener(dialogInterface -> {
            AlertDialog dialog = (AlertDialog) dialogInterface;

            TextView title = dialog.findViewById(R.id.viewTitle);
            TextView platform = dialog.findViewById(R.id.viewPlatform);
            TextView username = dialog.findViewById(R.id.viewUsername);
            TextView password = dialog.findViewById(R.id.viewPassword);
            Button deleteBtn = dialog.findViewById(R.id.delete_item_btn);

            if(title == null || platform == null || username == null || password == null || deleteBtn == null) {
                return;
            }

            title.setText(this.item.displayName);
            platform.setText(this.item.URI);
            username.setText(this.item.username);

            WeakReference<TextView> passwordRef = new WeakReference<>(password);
            Crypto.getInstance().decrypt(this.item.password, new Crypto.CryptoResponse() {
                @Override
                public void run() {
                    TextView password = passwordRef.get();
                    if(this.isSuccessful && password != null) {
                        password.setText(this.decryptedData);
                    }
                }
            });

            deleteBtn.setOnClickListener(v -> {
                DeleteVaultItemTransaction transaction = new DeleteVaultItemTransaction(
                        this.item,
                        new WeakReference<>(this)
                );
                Database.dispatch(context, transaction);
            });
        });

        return ad;
    }

}