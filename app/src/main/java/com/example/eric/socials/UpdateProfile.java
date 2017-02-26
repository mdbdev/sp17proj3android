package com.example.eric.socials;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class UpdateProfile extends AppCompatActivity {

    EditText nameView;
    ImageView profilePicture;
    Button saveButton;
    private Intent data;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");
    private StorageReference mStorageRef;

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        nameView = (EditText) findViewById(R.id.nameView);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        saveButton = (Button) findViewById(R.id.saveButton);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(UpdateProfile.this).create();
                alertDialog.setTitle("Set a Photo");
//                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Take a Photo",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                //Open Camera
//                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                                    startActivityForResult(takePictureIntent, 1);
//                                }
//                                dialog.dismiss();
//                            }
//                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Upload from Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //launch gallery
                                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdb-socials-47b33.appspot.com");
                final String key = myRef.child("user").push().getKey();
                StorageReference imageRef = mStorageRef.child(key + ".png");

                imageRef.putFile(data.getData()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "need an image!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        User u = new User(nameView.getText().toString(), key + ".png", mUser.getEmail());
                        myRef.child(mUser.getUid()).setValue(u);
                        Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if((requestCode == 3 || requestCode == 1) && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ((ImageView) findViewById(R.id.profilePicture)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.data = data;


    }
}


