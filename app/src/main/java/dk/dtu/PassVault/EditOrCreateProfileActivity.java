package dk.dtu.PassVault;

import androidx.annotation.RequiresApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


public class EditOrCreateProfileActivity extends BaseActivity {

    private EditText mEditText, mEditText1, mEditText2, mEditText3;
    public static int RESULT_LOAD_IMAGE = 1;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_new);

        mEditText  = (EditText) findViewById(R.id.editText1);
        mEditText1 = (EditText) findViewById(R.id.editText2);
        mEditText2 = (EditText) findViewById(R.id.editText3);
        mEditText3 = (EditText) findViewById(R.id.editText4);




        Button createButton = findViewById(R.id.button5);
        createButton.setOnClickListener(new OnClickListener() {

        @Override
            public void onClick(View v) {
                createClicked();
            }
        });

                Button genBtn = (Button) findViewById(R.id.generate_password);
        
        genBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PasswordGeneratorActivity.class);
            startActivityForResult(intent, 0);
        });

        }

    /*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
    */

    private void createClicked(){

        String title = mEditText1.getText().toString();
        String URI = mEditText2.getText().toString();
        String username = mEditText.getText().toString();
        String password = mEditText3.getText().toString();

        Intent i = new Intent(getApplicationContext(), VaultActivity.class);
        i.putExtra("URI", URI);
        i.putExtra("displayName", title);
        i.putExtra("username", username);
        i.putExtra("password", password);

        setResult(RESULT_OK,i);
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            System.out.println(intent.getData().toString());
        }
    }
}