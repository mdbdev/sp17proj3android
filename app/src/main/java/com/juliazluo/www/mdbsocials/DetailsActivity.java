package com.juliazluo.www.mdbsocials;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsActivity extends AppCompatActivity {

    DatabaseReference ref;
    StorageReference storageRef;
    TextView name, email, date, description;
    Button numInterested;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ref = FirebaseDatabase.getInstance().getReference("/socialDetails");
        storageRef = FirebaseStorage.getInstance().getReference();
        name = (TextView) findViewById(R.id.name_detail);
        email = (TextView) findViewById(R.id.email_detail);
        date = (TextView) findViewById(R.id.date_detail);
        description = (TextView) findViewById(R.id.description_detail);
        numInterested = (Button) findViewById(R.id.num_interested_btn);
        imageView = (ImageView) findViewById(R.id.image_detail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        final String id = intent.getStringExtra("SOCIAL_ID");
        String imageName = intent.getStringExtra("IMAGE_NAME");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child(id).child("name").getValue(String.class));
                email.setText("Host: " + dataSnapshot.child(id).child("email").getValue(String.class));
                description.setText(dataSnapshot.child(id).child("description").getValue(String.class));
                date.setText("Date: " + dataSnapshot.child(id).child("date").getValue(String.class));
                numInterested.setText(dataSnapshot.child(id).child("numRSVP").getValue(Long.class) + " Interested");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        storageRef.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .override(300, 300)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Storage", "Couldn't find file");
            }
        });
    }
}
