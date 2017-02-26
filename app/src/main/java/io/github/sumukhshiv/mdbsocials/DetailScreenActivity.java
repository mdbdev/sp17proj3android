package io.github.sumukhshiv.mdbsocials;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class DetailScreenActivity extends AppCompatActivity {

    private Social social;
    TextView textViewEventNameDetail;
    ImageView imageViewDetail;
    TextView emailDetail;
    Button buttonInterestedDetail;
    TextView textViewDescription;
    Button buttonAreYouInterested;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    int numInterestedForButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);
        textViewEventNameDetail = (TextView) findViewById(R.id.textViewEventNameDetail);
        imageViewDetail = (ImageView) findViewById(R.id.imageViewDetail);
        emailDetail = (TextView) findViewById(R.id.textViewEmailDetail);
        buttonInterestedDetail = (Button) findViewById(R.id.buttonInterstedDetail);
        buttonAreYouInterested = (Button) findViewById(R.id.buttonAreYouInterested);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        buttonAreYouInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("/users").child(firebaseUser.getUid());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object user = dataSnapshot.child("email").getValue();

                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "Please Update Profile!",
                                    Toast.LENGTH_LONG).show();
                        } else if (social.peopleInterested.contains(firebaseUser.getUid())) {
                            Toast.makeText(getApplicationContext(), "You're already Interested!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            addInterested();

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        });

        buttonInterestedDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("/users").child(firebaseUser.getUid());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object user = dataSnapshot.child("email").getValue();

                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "Please Update Profile!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), InterestedMembersActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("SOCIAL", social);
                            intent.putExtras(bundle);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        social = (Social) bundle.getSerializable("SOCIAL");
        textViewEventNameDetail.setText(social.nameOfEvent);
        emailDetail.setText(social.emailOfHost);
        buttonInterestedDetail.setText(Integer.toString(social.numberIntersted));
        textViewDescription.setText(social.description);
        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... strings) {
                try {return Glide.
                        with(getApplicationContext()).
                        load(strings[0]).
                        asBitmap().
                        into(100, 100). // Width and height
                        get();}
                catch (Exception e) {return null;}
            }

            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                imageViewDetail.setImageBitmap(result);
            }
        }

        //FirebaseOptions opts = FirebaseApp.getInstance().getOptions();
        //Log.i(TAG, "Bucket = " + opts.getStorageBucket());

        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-e2598.appspot.com").child(social.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                (new DownloadFilesTask()).execute(uri.toString());
//                Log.d("ye", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
//                Log.d("sad", exception.toString());
            }
        });
//        Glide.with(this)
//                .load("http://assets.pokemon.com/assets/cms2/img/pokedex/full/" + pokemon.number + ".png")
//                .into(image);
    }

    private void addInterested() {

        //NOTE, also add self email to people interested arraylist
        final String socialKey = social.image.substring(0, social.image.length() - 4);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials").child(socialKey);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numberIntersted = dataSnapshot.child("numberIntersted").getValue(Integer.class);
                numberIntersted++;
                buttonInterestedDetail.setText(Integer.toString(numberIntersted));
                addInterestedToDatabase(numberIntersted, socialKey);
                ArrayList<String> a = social.peopleInterested;
                if (a != null && !a.contains(firebaseUser.getUid() + " hello")) {
                    a.add(firebaseUser.getUid());
                    social.peopleInterested = a;
                    ref.child("peopleInterested").setValue(a);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        ref.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Log.d("debug", "we testing transaction");
//                Social mutableSocial = mutableData.getValue(Social.class);
//                int numInterested = 1;
//
//                social.numberIntersted += 1;
//                numInterestedForButton = social.numberIntersted;
//                buttonInterestedDetail.setText("8");
//
//                mutableData.child("numberIntersted").setValue(numInterestedForButton);
//                addInterestedToDatabase(numInterestedForButton, socialKey);
//
//                ArrayList<String> a = social.peopleInterested;
//
//                if (a != null && !a.contains(firebaseUser.getUid())) {
//                    a.add(firebaseUser.getUid());
//                    social.peopleInterested = a;
//                    ref.child(socialKey).child("peopleInterested").setValue(a);
//                }
//                mutableData.setValue(social);
//
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//            }
//        });

    }
    public static void addInterestedToDatabase(int interested, String firekey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("socials");
        ref.child(firekey).child("numberIntersted").setValue(interested);

    }

}
