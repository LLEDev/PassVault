package dk.dtu.PassVault.Business.Database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import dk.dtu.PassVault.Business.Database.Entities.Credential;
import dk.dtu.PassVault.Business.Database.Entities.VaultItem;

public class Database {

    public abstract static class Transaction<ResultType> extends AsyncTask<Database, Void, ResultType> {

        abstract public ResultType doRequest(Database db);
        abstract public void onResult(ResultType result);

        @Override
        protected ResultType doInBackground(Database... dbs) {
            return this.doRequest(dbs[0]);
        }

        @Override
        protected void onPostExecute(ResultType result) {
            this.onResult(result);
        }
    }

    /*
     * Static methods
     */

    protected static Database instance = null;

    protected static Database getInstance(Context context) {
        if(instance == null) {
            instance = new Database(context);
        }

        return instance;
    }

    public static <ResultType> void dispatch(Context context, Transaction<ResultType> transaction) {
        transaction.execute(getInstance(context));
    }


    /*
     * Instance methods
     */

    protected RoomDatabase roomInstance;

    protected Database(Context context) {
        this.roomInstance = Room.databaseBuilder(context, RoomDatabase.class, "pass-vault")
                .fallbackToDestructiveMigration()
                .build();
    }

    public void addVaultItem(VaultItem item) {
        this.roomInstance.vaultItemDao().insertAll(item);
    }

    public boolean hasCredential() {
        return this.roomInstance.credentialDao().get() != null;
    }

    public Credential getCredential() {
        return this.roomInstance.credentialDao().get();
    }

    public String getSetting(String key) {
        return this.roomInstance.settingDao().get(key);
    }

    public VaultItem getVaultItemByURI(String URI) {
        return this.roomInstance.vaultItemDao().getByURI(URI);
    }

    public VaultItem[] getVaultItems() {
        return this.roomInstance.vaultItemDao().getAll().toArray(new VaultItem[0]);
    }

    public void deleteVaultItem(VaultItem vaultItem) {
        this.roomInstance.vaultItemDao().delete(vaultItem);
    }

    public void setCredential(Credential cred) {
        this.roomInstance.credentialDao().set(cred);
    }

    public void setSetting(String key, String value) {
        this.roomInstance.settingDao().set(key, value);
    }

}
