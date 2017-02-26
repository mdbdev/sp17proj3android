package com.example.adhirajdatar.mdbsocials;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    Button signUpButton;
    EditText usernameInput, passwordInput, nameInput;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Uri profileImage;
    String profilepiclocation;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpButton = (Button)findViewById(R.id.signUpButton);
        usernameInput = (EditText)findViewById(R.id.signUpEmailInput);
        passwordInput = (EditText)findViewById(R.id.signUpPasswordInput);
        nameInput = (EditText)findViewById(R.id.signUpNameInput);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        ((Button)findViewById(R.id.addProfilePhotoButton)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    public void signUp()
    {
        final String userName, userPassword, userEmail;
        userName = nameInput.getText().toString();
        userEmail = usernameInput.getText().toString();
        userPassword = passwordInput.getText().toString();
        if (profileImage == null || userPassword == null || userEmail == null || userEmail.length() == 0 || userPassword.length() == 0 || userName.length()==0)
        {
            Toast.makeText(getApplicationContext(), "You didn't Enter Anything", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String key = mAuth.getCurrentUser().getUid();
                                Map<String, Object> post = new HashMap<>();

                                profilepiclocation = "profiles/" + key + ".jpg";
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-8cbc0.appspot.com");
                                StorageReference riversRef = storageRef.child(profilepiclocation);
                                riversRef.putFile(profileImage);

                                post.put("uid", key);
                                post.put("name", userName);
                                post.put("email", userEmail);
                                mDatabase.child("Users").push().setValue(post);
                                startActivity(new Intent(SignUpActivity.this, UserFeedActivity.class));
                            } else if (!(task.isSuccessful())) {
                                Toast.makeText(SignUpActivity.this, "Sign Up Failed. Make sure you entered a valid email and a STRONG password (uppercase, symbols, 10+ chars)",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK && requestCode==1)
        {
            //int amtSocials = getIntent().getExtras().getInt("amtSocials") + 1;
            //firebasePathLocation = "eventPics/social" + amtSocials + ".jpg";
            profileImage = imageReturnedIntent.getData();
            ((ImageView)findViewById(R.id.profilePicPreview)).setImageURI(profileImage);

        }
    }

}
