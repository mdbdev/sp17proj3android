package com.example.adhirajdatar.mdbsocials;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSocial extends AppCompatActivity {

    private EditText newTitleText, newDateText, newDescText;
    private FirebaseAuth mAuth;
    private String firebasePathLocation;
    private DatabaseReference mDatabase;
    private ImageView preview;

    private Uri eventimageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_social);

        newTitleText = (EditText)findViewById(R.id.newEventTitle);
        newDateText = (EditText)findViewById(R.id.newEventDate);
        newDescText = (EditText)findViewById(R.id.newEventDesc);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String id = mDatabase.child("Socials").push().getKey();
        mAuth = FirebaseAuth.getInstance();

        preview = (ImageView)findViewById(R.id.eventImagePreview);

        ((Button)findViewById(R.id.newEventCreate)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                if (eventimageuri==null|| newTitleText.getText()==null || newDateText.getText()==null || newDescText.getText()==null ||
                        newTitleText.getText().toString().length()==0 || newDateText.getText().toString().length()==0 || newDescText.getText().toString().length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter All Fields Properly",Toast.LENGTH_LONG).show();
                }else
                {
                    createEventOnServer();
                }
            }
        });
        ((Button)findViewById(R.id.newEventImage)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                AlertDialog alertDialog = new AlertDialog.Builder(AddSocial.this).create();
                alertDialog.setTitle("Image Upload Options");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Take a Photo", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, "Image");
                                Uri tempuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
                                startActivityForResult(takePictureIntent, 0);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Upload from Gallery", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, 1);                            }
                        });
                alertDialog.show();
                Button glitchy = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                glitchy.setEnabled(false);
            }
        });
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED)
        {


                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 12);
                    }
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }*/


    /*private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(AddSocial.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddSocial.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(AddSocial.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(AddSocial.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }*/




    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK && requestCode==1)
        {
            int amtSocials = getIntent().getExtras().getInt("amtSocials") + 1;
            firebasePathLocation = "eventPics/social" + amtSocials + ".jpg";
            eventimageuri = imageReturnedIntent.getData();
            preview.setImageURI(eventimageuri);

        }
        /*if (resultCode == RESULT_OK && requestCode == 0)
        {
            Bundle extras = imageReturnedIntent.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            preview.setImageBitmap(imageBitmap);
        }*/
    }

    public void createEventOnServer()
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final String key = ref.child("Socials").push().getKey();
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-8cbc0.appspot.com");
        StorageReference riversRef = storageRef.child(firebasePathLocation);

        riversRef.putFile(eventimageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String name = newTitleText.getText().toString();
                String date = newDateText.getText().toString();
                String desc = newDescText.getText().toString();
                String host = mAuth.getCurrentUser().getEmail();


                ArrayList<String> interested = new ArrayList<String>();
                Integer numInterested = 0;
                Map<String, Object> post = new HashMap<String, Object>();
                post.put("name", name);
                post.put("host", host);
                post.put("date", date);
                post.put("desc", desc);
                post.put("hostuid",mAuth.getCurrentUser().getUid());
                post.put("interestedList", interested);
                post.put("firebasepath", firebasePathLocation);
                post.put("id",key);
                post.put("numInterested", numInterested);
                if (name.length()==0 || host==null || host.length()==0 || date.length()==0 || desc.length()==0 || firebasePathLocation==null || firebasePathLocation.length()==0)
                {

                }else
                {
                    mDatabase.child("Socials").child(key).setValue(post);
                    startActivity(new Intent(getApplicationContext(), UserFeedActivity.class));
                }
            }
        });
    }


}
