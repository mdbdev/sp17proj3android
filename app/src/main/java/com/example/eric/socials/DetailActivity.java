package com.example.eric.socials;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView socialName;
    TextView emailAddress;
    TextView socialDescription;

    ImageView socialImage;

    Button interestedButton;
    Button numRSVPButton;

    DatabaseReference myRef;

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;

    ArrayList<String> usersInterested;

    boolean isInterested = false;
    int intNumRSVP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        Intent grabIntent = getIntent();

        socialName = (TextView) findViewById(R.id.socialName);
        socialDescription = (TextView) findViewById(R.id.socialDescription);
        emailAddress = (TextView) findViewById(R.id.emailAddress);

        socialImage = (ImageView) findViewById(R.id.socialImage);

        interestedButton = (Button) findViewById(R.id.interestedButton);
        numRSVPButton = (Button) findViewById(R.id.numRSVPButton);

        socialName.setText(grabIntent.getStringExtra("name"));
        emailAddress.setText(grabIntent.getStringExtra("email"));
        socialDescription.setText(grabIntent.getStringExtra("desc"));

        final String numrsvp = grabIntent.getStringExtra("numRSVP");
        if (Integer.parseInt(numrsvp) == 1) {
            numRSVPButton.setText("1 Person Interested!");
        } else {
            numRSVPButton.setText(numrsvp + " People Interested!");
        }

        intNumRSVP = Integer.parseInt(numrsvp);

        myRef = FirebaseDatabase.getInstance().getReference("/socials");

        final String socialId = grabIntent.getStringExtra("socialID");
        String imageUrl = grabIntent.getStringExtra("image");
        usersInterested = grabIntent.getStringArrayListExtra("usersInterested");
        interestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInterested) {


                    intNumRSVP += 1;
                    myRef.child(socialId).child("numRSVP").setValue(intNumRSVP);
                    numRSVPButton.setText(Integer.toString(intNumRSVP) + " People Interested!");

                    mAuth = FirebaseAuth.getInstance();
                    mUser = mAuth.getCurrentUser();

                    String userUID = mUser.getUid();
                    usersInterested.add(userUID);
                    myRef.child(socialId).child("usersInterested").setValue(usersInterested);

                    Toast.makeText(getApplicationContext(), "You have been marked as interested in this event!", Toast.LENGTH_SHORT).show();
                    isInterested = true;
                } else {
                    intNumRSVP -= 1;
                    myRef.child(socialId).child("numRSVP").setValue(intNumRSVP);
                    numRSVPButton.setText(Integer.toString(intNumRSVP) + " People Interested!");

                    mAuth = FirebaseAuth.getInstance();
                    mUser = mAuth.getCurrentUser();

                    String userUID = mUser.getUid();
                    usersInterested.remove(userUID);
                    myRef.child(socialId).child("usersInterested").setValue(usersInterested);

                    Toast.makeText(getApplicationContext(), "You have been taken off the interested list.", Toast.LENGTH_SHORT).show();
                    isInterested = false;
                }
            }
        });

        numRSVPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Interested.class);
                intent.putExtra("socialImageId", emailAddress.getText().toString());
                startActivity(intent);
            }
        });

        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... strings) {
                try {
                    return Glide.
                            with(getApplicationContext()).
                            load(strings[0]).
                            asBitmap().
                            into(100, 100). // Width and height
                            get();
                } catch (Exception e) {
                    return null;
                }
            }

            protected void onProgressUpdate(Void... progress) {
            }

            protected void onPostExecute(Bitmap result) {
                socialImage.setImageBitmap(result);
            }
        }
        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdb-socials-47b33.appspot.com").child(imageUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new DownloadFilesTask().execute(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
}
