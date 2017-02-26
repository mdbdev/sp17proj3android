package com.example.shiv.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

    RecyclerView rview;
    EventAdapter eventAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Button logout = (Button) findViewById(R.id.button5);
        Button newsocial = (Button) findViewById(R.id.button6);


        rview = (RecyclerView) findViewById(R.id.recyclableView);
        rview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        /*
        ArrayList<Event> events = new ArrayList<>();
        Event party = new Event();
        party.eventName = "partay";
        party.email = "ksidhu@gmail.com";
        party.numInterested = 7;
        Event party2 = new Event();
        party2.eventName = "partayyyyy";
        party2.email = "ksidhu@gmailllll.com";
        party2.numInterested = 17;
        events.add(party);
        events.add(party2);
        */

        eventAdapter = new EventAdapter(getApplicationContext(), getList());//events);
        //eventAdapter.clearList();
        //eventAdapter.events = getList();
        rview.setAdapter(eventAdapter);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        newsocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewSocialActivity.class);
                startActivity(intent);
            }
        });


    }

    private ArrayList<Event> getList() {
        final ArrayList<Event> currEvents = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/events");

        

        ref.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //eventAdapter.clearList();
                currEvents.clear();

                for (DataSnapshot x : dataSnapshot.getChildren()) {

                    Log.d(x.getKey(), x.getValue().toString());
                    //would normally getKey here
                    Event e = new Event();
                    e.date = x.child("date").getValue(String.class);
                    e.description = x.child("description").getValue(String.class);
                    e.eventName = x.child("name").getValue(String.class);
                    e.imageURL = x.child("url").getValue(String.class);
                    e.email = x.child("email").getValue(String.class);

                    e.numInterested = x.child("interested").getValue(String.class);
                    e.key = x.getKey();
                    //e.peopleInterested = dataSnapshot.child("peopleinterested").getValue(ArrayList.class); //breaking here

                    currEvents.add(e);
                    //EventAdapter.events.add(e);
                    eventAdapter.notifyDataSetChanged();
                    //rview.setAdapter(eventAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return currEvents;
        /*
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                //would normally getKey here
                Event e = new Event();
                e.date = dataSnapshot.child("date").getValue(String.class);
                e.description = dataSnapshot.child("description").getValue(String.class);
                e.eventName = dataSnapshot.child("name").getValue(String.class);
                e.imageURL = dataSnapshot.child("url").getValue(String.class);
                e.email = dataSnapshot.child("email").getValue(String.class);;
                e.numInterested = dataSnapshot.child("interested").getValue(String.class);
                e.key = dataSnapshot.getKey();
                //e.peopleInterested = dataSnapshot.child("peopleinterested").getValue(ArrayList.class); //breaking here

                currEvents.add(e);
                //EventAdapter.events.add(e);
                rview.setAdapter(eventAdapter);

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
        });
        return currEvents;
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}






