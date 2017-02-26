package com.example.adhirajdatar.mdbsocials;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EventDetailsActivity extends AppCompatActivity{

    TextView name, host, date, desc;
    ImageView wallpaper, hostimg;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;
    CheckBox interestCheckBox;
    Button interestButton;
    FloatingActionButton eventadd;

    ArrayList<String> interestedNames, interestedEmails, interestedIds;

    String user;

    int initialNumInterested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.w("ID ID ID ID ID", getIntent().getExtras().getString("id"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        interestedNames = new ArrayList<>();
        interestedEmails = new ArrayList<>();
        interestedIds = new ArrayList<>();

        wallpaper = (ImageView)findViewById(R.id.profilewallpaper);
        hostimg = (ImageView)findViewById(R.id.profileimage);

        name = (TextView)findViewById(R.id.eventNameDisplay);
        name.setText(getIntent().getExtras().getString("title"));

        host = (TextView)findViewById(R.id.eventHostDisplay);
        host.setText("By: " + getIntent().getExtras().getString("host"));

        date = (TextView)findViewById(R.id.eventDateDisplay);
        date.setText("Date: " + getIntent().getExtras().getString("date"));

        desc = (TextView)findViewById(R.id.eventDescDisplay);
        desc.setText(getIntent().getExtras().getString("desc"));

        initialNumInterested = getIntent().getExtras().getInt("numInterested");

        StorageReference evtImgRef = mStorageRef.child(getIntent().getExtras().getString("firebasePath"));
        evtImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .into(wallpaper);
            }
        });

        StorageReference hostImgRef = mStorageRef.child("profiles/"+getIntent().getExtras().getString("hostuid")+".jpg");
        hostImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .into(hostimg);
            }
        });

        interestButton = (Button)findViewById(R.id.eventInterestedButton);
        interestButton.setText("Interested: "+getIntent().getExtras().getInt("numInterested"));

        eventadd = (FloatingActionButton) findViewById(R.id.addInterestButton);

        eventadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInterest();
                eventadd.setEnabled(false);
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Socials/"+getIntent().getExtras().getString("id"));

            }
        });


        interestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InterestedActivity.class);
                if (initialNumInterested == 0)
                {
                    Toast.makeText(getApplicationContext(),"Nobody is interested in this",Toast.LENGTH_LONG).show();

                }else {
                    intent.putExtra("key", getIntent().getExtras().getString("id"));
                    startActivity(intent);
                }
            }
        });

    }

    public void addInterest()
    {
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Socials/"+getIntent().getExtras().getString("id"));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref.child("interested").child("" + dataSnapshot.child("numInterested").getValue(Integer.class)).setValue(email);
                ref.child("interestedUIDs").child("" + dataSnapshot.child("numInterested").getValue(Integer.class)).setValue(mAuth.getCurrentUser().getUid());
                interestButton.setText("Interested: "+((int)(dataSnapshot.child("numInterested").getValue(Integer.class) + 1)));
                ref.child("numInterested").setValue((int)(dataSnapshot.child("numInterested").getValue(Integer.class) + 1));
                initialNumInterested+=1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenuButton:
                UserHandler fb = new UserHandler(mAuth);
                fb.signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
