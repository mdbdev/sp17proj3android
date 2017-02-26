package io.github.sumukhshiv.mdbsocials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InterestedMembersActivity extends AppCompatActivity {

    Social social;

//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference("/socials/" + social.peopleInterested);
//    ValueEventListener mSocialsEventListener;
    InterestedProfilesAdapter toSetProfilesAdapter;
    ArrayList<String> profilesInterested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_members);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        social = (Social) bundle.getSerializable("SOCIAL");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewInterestedMembers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        profilesInterested = social.peopleInterested;
        toSetProfilesAdapter = new InterestedProfilesAdapter(getApplicationContext(), profilesInterested);

        recyclerView.setAdapter(toSetProfilesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        social = (Social) bundle.getSerializable("SOCIAL");
    }



}
