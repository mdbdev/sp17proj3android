package com.example.adhirajdatar.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Adhiraj Datar on 2/23/2017.
 */

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedAdapter.CustomViewHolder>
{

    private Context context;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();;

    private ArrayList<Social> socialList = new ArrayList<>();

    public UserFeedAdapter(Context context, ArrayList<Social> socials) {
        this.context = context;
        this.socialList = socials;
    }

    public void setSocialsList(ArrayList<Social> schedule)
    {
        socialList = schedule;
    }

    @Override
    public int getItemCount() {
        return socialList.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position)
    {
        Social s = socialList.get(position);
        holder.hostNameView.setText(s.host);
        holder.eventNameView.setText(s.title);

        if(s.interested == null)
        {
            s.interested = new ArrayList<>();
        }

        holder.numInterestedView.setText("Interested: "+s.numInterested);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(socialList.get(position).firebaseLocation);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Glide.with(context).load(downloadUrl).into(holder.eventImageView);
            }

        });
    }

    class CustomViewHolder extends RecyclerView.ViewHolder
    {
        TextView eventNameView, hostNameView, numInterestedView;
        ImageView eventImageView;
        String fireBasePath;
        //Hashtable<String, String> nameEmailTable = new Hashtable<String, String>();
        ArrayList<String> interestedNames, interestedEmails;

        public CustomViewHolder(View v)
        {
            super(v);
            this.eventNameView = (TextView) v.findViewById(R.id.eventTitle);
            this.eventImageView = (ImageView) v.findViewById(R.id.eventImage);
            this.hostNameView = (TextView) v.findViewById(R.id.eventHost);
            this.numInterestedView = (TextView) v.findViewById(R.id.eventInterested);

            interestedNames = new ArrayList<String>();
            interestedEmails = new ArrayList<String>();

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Social s = socialList.get(getAdapterPosition());
                    Intent intent = new Intent(context, EventDetailsActivity.class);

                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> interested = s.interested;
                            for (DataSnapshot d: dataSnapshot.getChildren()) {
                                HashMap<String, Object> evt = (HashMap<String, Object>) d.getValue();
                                if (interested.contains(evt.get("uid"))) {
                                    interestedNames.add((String) evt.get("name"));
                                    interestedNames.add((String) evt.get("email"));
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Error", "Something went wrong");
                        }

                    };
                    mDatabase.child("Users").addValueEventListener(postListener);
                    intent.putExtra("title", s.title);
                    intent.putExtra("host", s.host);
                    intent.putExtra("desc", s.description);
                    intent.putExtra("date", s.date);
                    intent.putExtra("interestedList", s.interested.size());
                    intent.putExtra("firebasePath", s.firebaseLocation);
                    intent.putExtra("id",s.id);
                    intent.putExtra("hostuid",s.hostuid);
                    intent.putExtra("numInterested", s.numInterested);
                    Log.e("fbpl",s.firebaseLocation);
                    intent.putStringArrayListExtra("interestedName", interestedNames);
                    intent.putStringArrayListExtra("interestedEmail", interestedEmails);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }


}
