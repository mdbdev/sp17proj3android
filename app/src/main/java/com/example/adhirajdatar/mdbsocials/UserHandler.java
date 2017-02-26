package com.example.adhirajdatar.mdbsocials;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Adhiraj Datar on 2/23/2017.
 */

public class UserHandler
{

    private FirebaseAuth mAuth;

    public UserHandler(FirebaseAuth mAuth)
    {
        this.mAuth = mAuth;
    }

    public void signIn(final Context context, String userEmail, String userPassword)
    {
        if (userPassword == null || userEmail == null || userEmail.length() == 0 || userPassword.length() == 0)
        {
            Toast.makeText(context, "You didn't Enter Anything", Toast.LENGTH_SHORT).show();
        }else
        {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w("wellthisfailed", "signInWithEmail", task.getException());
                        Toast.makeText(context, "Sign in Failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void signOut()
    {
        mAuth.signOut();
    }

}
