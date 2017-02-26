package io.github.sumukhshiv.mdbsocials;

import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserArea extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("/socials");
    ArrayList<Social> arrayListSocials;
    ValueEventListener mSocialsEventListener;
    SocialsAdapter toSetSocialsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);



//        Button logoutButton = (Button) findViewById(R.id.buttonLogout);
//        Button buttonNewSocial = (Button) findViewById(R.id.buttonNewSocial);
//        Button buttonUpdateProfile = (Button) findViewById(R.id.buttonUpdateProfile);
//
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent logoutIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(logoutIntent);
//            }
//        });
//
//        buttonNewSocial.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent newSocialIntent = new Intent(getApplicationContext(), NewSocialActivity.class);
//                startActivity(newSocialIntent);
//            }
//        });
//
//        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent updateProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
//                startActivity(updateProfileIntent);
//            }
//        });

        final ListView mDrawerList;
        final ArrayAdapter<String> mAdapter;
        String[] optionsArray = {"Update Profile", "Create New Social", "Logout"};
        final Intent grabIntent = getIntent();

        mDrawerList = (ListView) findViewById(R.id.navList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsArray);
        mDrawerList.setAdapter(mAdapter);

        Toast.makeText(UserArea.this, "Swipe from the left for more options", Toast.LENGTH_LONG).show();

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

                        if(item.equalsIgnoreCase("Update Profile")){
                            Intent updateProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                            updateProfileIntent.putExtra("email", grabIntent.getStringExtra("email"));
                            startActivity(updateProfileIntent);
                        }

                        if(item.equalsIgnoreCase("Create New Social")){
                            Intent newSocialIntent = new Intent(getApplicationContext(), NewSocialActivity.class);
                            newSocialIntent.putExtra("email", grabIntent.getStringExtra("email"));
                            startActivity(newSocialIntent);
                        }

                        if(item.equalsIgnoreCase("Logout")){
                            FirebaseAuth.getInstance().signOut();
                            Intent logoutIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(logoutIntent);
                        }
                    }
                }
            }
        });


        //RECYCLER VIEW STUFF
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        arrayListSocials = new ArrayList<>();

        toSetSocialsAdapter = new SocialsAdapter(getApplicationContext(), arrayListSocials);

        recyclerView.setAdapter(toSetSocialsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(UserArea.this, "Swipe from the left for more options", Toast.LENGTH_LONG).show();
        if (mSocialsEventListener == null) {
            mSocialsEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        //Create a new Social Object
                        String name = postSnapshot.child("nameOfEvent").getValue(String.class);
                        String date = postSnapshot.child("date").getValue(String.class);
                        String description = postSnapshot.child("description").getValue(String.class);
                        String image = postSnapshot.child("image").getValue(String.class);
                        String emailOfHost = postSnapshot.child("emailOfHost").getValue(String.class);
                        int numberInterested = postSnapshot.child("numberIntersted").getValue(Integer.class);
                        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                        ArrayList<String> listOfUsersInSocial = postSnapshot.child("peopleInterested").getValue(t);
                        Social newSocial = new Social(name, date, description, image, emailOfHost, numberInterested, listOfUsersInSocial);
                        arrayListSocials.add(newSocial);
                        Log.d("DEBUG", arrayListSocials.size() + "");
                    }
                    toSetSocialsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            myRef.addValueEventListener(mSocialsEventListener);
        }
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.android_action_bar_spinner_menu, menu);
//
//        MenuItem item = menu.findItem(R.id.spinner);
//        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(adapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
//        {
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//            {
//                String selectedItem = parent.getItemAtPosition(position).toString();
//                if(selectedItem.equals("Update Profile"))
//                {
//                    Intent updateProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
//                    startActivity(updateProfileIntent);
//                }
//            } // to close the onItemSelected
//            public void onNothingSelected(AdapterView<?> parent)
//            {
//
//            }
//        });
//
//        return true;
//    }
}
