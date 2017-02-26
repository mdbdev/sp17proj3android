package io.github.sumukhshiv.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

/**
 * Created by sumukhshivakumar on 2/25/17.
 */

public class InterestedProfilesAdapter extends RecyclerView.Adapter<InterestedProfilesAdapter.CustomViewHolder> {

    Context context;
    ArrayList<String> profiles;

    public InterestedProfilesAdapter(Context context, ArrayList<String> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    @Override
    public InterestedProfilesAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view2, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InterestedProfilesAdapter.CustomViewHolder holder, int position) {
        final String profile = profiles.get(position);
        //In the onBindViewHolder, you want to set each of the parameters of ComputerCompanies very similiar
        //to what you did to the layout manager.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/users").child(profile);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("email").getValue().toString();
                holder.MemberName.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //Set up the variables of Computer Companies here
        ImageView profileImage;
        TextView MemberName;


        public CustomViewHolder(View view) {
            super(view);

//            profileImage = (ImageView) view.findViewById(R.id.imageViewProfilePicture);
            MemberName = (TextView) view.findViewById(R.id.textViewInterestedMember);

        }
    }
}
