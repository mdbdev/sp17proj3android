package com.example.eric.socials;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private String[]optionsArray = {"Update Profile","Create New Social" ,"Logout"};
    RecyclerView recyclerView;
    FeedAdapter feedAdapter;
    private ArrayList<Social> mSocials = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        recyclerView = (RecyclerView)findViewById(R.id.recylcerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mDrawerList = (ListView) findViewById(R.id.navList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SparseBooleanArray clickedItemPositions = mDrawerList.getCheckedItemPositions();
                mAdapter.notifyDataSetChanged();

                for(int j = 0;j < clickedItemPositions.size(); j ++){
                    boolean checked = clickedItemPositions.valueAt(j);

                    if(checked){
                        int key = clickedItemPositions.keyAt(j);
                        String item = (String) mDrawerList.getItemAtPosition(key);

                        if(item.equalsIgnoreCase("update profile")){
                            Intent intent = new Intent(getApplicationContext(), UpdateProfile.class);
                            startActivity(intent);
                        }
                        if(item.equalsIgnoreCase("logout")){
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                        if(item.equalsIgnoreCase("create new social")){
                            Intent intent = new Intent(getApplicationContext(), AddEvent.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("/socials");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("PLEASE 1", Long.toString(dataSnapshot.getChildrenCount()));
                mSocials.clear();
                for (DataSnapshot socialSnapShot: dataSnapshot.getChildren()) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    Social social = new Social(socialSnapShot.child("eventName").getValue(String.class),
                            socialSnapShot.child("eventImage").getValue(String.class),
                            socialSnapShot.child("emailOfCreator").getValue(String.class),
                            socialSnapShot.child("description").getValue(String.class),
                            socialSnapShot.child("numRSVP").getValue(Integer.class),
                            socialSnapShot.child("date").getValue(String.class),
                            socialSnapShot.child("usersInterested").getValue(t)
                    );
                    mSocials.add(social);
                }
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        feedAdapter = new FeedAdapter(this, mSocials);
        recyclerView.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart(){
        super.onStart();
        feedAdapter.notifyDataSetChanged();
    }

}