package com.example.shiv.mdbsocials;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class NewSocialActivity extends AppCompatActivity {

    public static final int GET_FROM_GALLERY = 3;
    public static final int GET_FROM_CAMERA = 4;
    public Uri currentImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_social);

        EditText eventName = (EditText) findViewById(R.id.editText3);
        ImageButton imgButton = (ImageButton) findViewById(R.id.imageButton);
        Button add = (Button) findViewById(R.id.button7);
        Button butt = (Button) findViewById(R.id.button8);
        EditText eventDate = (EditText) findViewById(R.id.editText4);
        EditText eventDescription = (EditText) findViewById(R.id.editText7);






        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Adding Event",Toast.LENGTH_SHORT).show();
                sendToServer();
                Toast.makeText(getApplicationContext(), "Event Saved!",Toast.LENGTH_SHORT).show();

            }
        });

        butt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(NewSocialActivity.this).create();
                                        alertDialog.setTitle("Set a Photo");
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Take a Photo",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Open Camera
                                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                                            startActivityForResult(takePictureIntent, GET_FROM_CAMERA);
                                                        }


//                                Bundle extras = takePictureIntent.getExtras();
//                                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                                mImageView.setImageBitmap(imageBitmap);
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Upload from Gallery",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //launch gallery
                                                        dialog.dismiss();
                                                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                                                    }
                                                });
                                        alertDialog.show();

                                    }
                                });






//            imgButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
//            }
//        });



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            currentImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ((ImageButton) findViewById(R.id.imageButton)).setBackgroundDrawable(bitmapDrawable);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if (requestCode == GET_FROM_CAMERA) {

            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ((ImageView) findViewById(R.id.imageButton)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /*

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Uri selectedImage = getImageUri(getApplicationContext(), imageBitmap); //data.getData();
            currentImage = selectedImage;//data.getData();
            Bitmap bitmap = null;


            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                bitmap = imageBitmap;

                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ((ImageButton) findViewById(R.id.imageButton)).setBackgroundDrawable(bitmapDrawable);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */

        }
    }

    private void sendToServer(){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final String key = ref.child("events").push().getKey();
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-56ed5.appspot.com");
        StorageReference riversRef = storageRef.child(key + ".png");

        //issue with camera
        riversRef.putFile(currentImage).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(NewSocialActivity.this, "need an image!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String description = ((EditText) findViewById(R.id.editText7)).getText().toString();
                String date = ((EditText) findViewById(R.id.editText4)).getText().toString();
                String name = ((EditText) findViewById(R.id.editText3)).getText().toString();
                String email = MainActivity.email;
                ref.child("events").child(key).child("name").setValue(name);
                ref.child("events").child(key).child("url").setValue(key);
                ref.child("events").child(key).child("date").setValue(date);
                ref.child("events").child(key).child("description").setValue(description);
                ref.child("events").child(key).child("email").setValue(email);
                ref.child("events").child(key).child("interested").setValue("1");
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(email);

                ref.child("events").child(key).child("peopleinterested").setValue(temp);
                ref.child("events").child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);


                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(intent);
            }
        });


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
