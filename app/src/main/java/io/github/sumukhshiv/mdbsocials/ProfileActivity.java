package io.github.sumukhshiv.mdbsocials;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    public final static int REQUEST_CAMERA = 1;
    public static final int GET_FROM_GALLERY = 3;
    Intent data;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button buttonSaveProfileUpdate = (Button) findViewById(R.id.buttonSaveProfileUpdate);
        ImageView updateProfilePicture = (ImageView) findViewById(R.id.imageViewUpdateProfilePicture);
        final EditText editTextUpdateName = (EditText) findViewById(R.id.editTextUpdateName);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        updateProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("Set a Photo");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Take a Photo",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Open Camera
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                                }
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Upload from Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //launch gallery
                                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        buttonSaveProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-e2598.appspot.com");
                final String imageKey = myRef.child("users").push().getKey();
                StorageReference imageRef = mStorageRef.child(imageKey + ".png");

                imageRef.putFile(data.getData()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Profile updatedProfile = new Profile(editTextUpdateName.getText().toString(), firebaseUser.getEmail(), imageKey + ".png" , new ArrayList<String>() );
                        myRef.child(firebaseUser.getUid()).setValue(updatedProfile);
                        Toast.makeText(getApplicationContext(), "Updated your Profile!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), UserArea.class);
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
        if((requestCode==GET_FROM_GALLERY || requestCode == REQUEST_CAMERA) && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ((ImageView) findViewById(R.id.imageViewUpdateProfilePicture)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        this.data = data;
    }
}
