package com.example.shiv.mdbsocials;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean IsLogin;
    public static String email;
    //private EditText username = (EditText) findViewById(R.id.editText);
    //private EditText Password = (EditText) findViewById(R.id.editText2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button loginButton = (Button) findViewById(R.id.button2);
        Button signUpButton = (Button) findViewById(R.id.button3);
        signUpButton.setPaintFlags(signUpButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        ImageView logo = (ImageView) findViewById(R.id.imageView);
        logo.setImageResource(R.drawable.logo);


        mAuth = FirebaseAuth.getInstance();

//        if (mAuth.getCurrentUser() == null) {
//            Log.d("LOOK","Kirat");
//        }



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    Log.d("Login Status", "onAuthStateChanged:signed_in:" + user.getUid());
                    email = user.getEmail();
                    Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                    startActivity(intent);
                    //Below is sign out code
                    /*
                    FirebaseAuth.getInstance().signOut();

                    */

                } else {
                    // User is signed out
                    Log.d("Login Status", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
            }
        });

    }


    private void attemptLogin() {
        String email = ((EditText) findViewById(R.id.editText)).getText().toString();
        String password = ((EditText) findViewById(R.id.editText2)).getText().toString();
        if (!email.equals("") && !password.equals("")) {
            //Question 4: add sign in capability. If it is successful, go to the listactivity, else display a Toast
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("SignIn Status", "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w("SignIn Status", "signInWithEmail", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                                startActivity(intent);
                            }

                            // ...
                        }
                    });
        }
    }

    private void attemptSignup() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
