package io.github.sumukhshiv.mdbsocials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
    private EditText editTextEmailLogin;
    private EditText editTextPasswordLogin;
    private Button buttonSignIn;
    private TextView textViewRegister;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editTextEmailLogin = (EditText) findViewById(R.id.editTextEmail);
        editTextPasswordLogin = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = firebaseAuth.getInstance();


        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toHome = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(toHome);
            }
        });

    }




    public void userLogin() {
        final String email = editTextEmailLogin.getText().toString().trim();
        String password = editTextPasswordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter a valid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a Password", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Logging in User...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            //finish();
                            Intent loginToFeedIntent = new Intent(LogInActivity.this, UserArea.class);
                            loginToFeedIntent.putExtra("email", email);
                            startActivity(loginToFeedIntent);
                        } else if (!task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Email/Password combination is not correct. Please Try Again.", Toast.LENGTH_LONG).show();
                        }



                    }
                });

    }


}
