package com.juliazluo.www.mdbsocials;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {

    private ArrayList<Social> socials;
    private FeedAdapter adapter;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socialsList");
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("Got", "here");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Got", "here");
                Intent intent = new Intent(getApplicationContext(), NewSocialActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i("User", "onAuthStateChanged:signed_in:" + user.getUid());
                    /*ref.addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot snapshot, String s) {
                            String id = snapshot.getKey();
                            Log.i("Got", id);
                            String name = snapshot.child("name").getValue(String.class);
                            Log.i("Got", name);
                            String email = snapshot.child("email").getValue().toString();
                            Log.i("Got", email);
                            long numRSVP = (Long) snapshot.child("numRSVP").getValue();
                            Log.i("Got", numRSVP + "");
                            String imageName = snapshot.child("imageName").getValue().toString();
                            Social social = new Social(id, name, email, numRSVP, imageName);
                            Log.i("Got", id + " " + name + " " + email + " " + numRSVP + " " + imageName);
                            socials.add(social);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }); */
                } else {
                    // User is signed out
                    Log.i("User", "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

        socials = new ArrayList<>();
        adapter = new FeedAdapter(getApplicationContext(), socials);
        RecyclerView recyclerAdapter = (RecyclerView)findViewById(R.id.feed_recycler);
        recyclerAdapter.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                socials.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    Log.i("Got", id);
                    String name = snapshot.child("name").getValue(String.class);
                    Log.i("Got", name);
                    String email = snapshot.child("email").getValue(String.class);
                    Log.i("Got", email);
                    long numRSVP = snapshot.child("numRSVP").getValue(Long.class);
                    Log.i("Got", numRSVP + "");
                    String imageName = snapshot.child("imageName").getValue(String.class);
                    Social social = new Social(id, name, email, numRSVP, imageName);
                    socials.add(social);
                    Log.i("Got", id + " " + name + " " + email + " " + numRSVP + " " + imageName);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
