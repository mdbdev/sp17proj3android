package com.example.adhirajdatar.mdbsocials;

import android.content.Context;
import android.media.Image;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by datarsd1 on 2/25/17.
 */

public class InterestedAdapter extends RecyclerView.Adapter<InterestedAdapter.CustomViewHolder>
{
    Context context;
    public static ArrayList<MDBro> mdbros;

    public InterestedAdapter(Context context, ArrayList<MDBro> interestedpeople)
    {
        this.context = context;
        this.mdbros = interestedpeople;
    }

    public InterestedAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_member_view, parent, false);
        return new CustomViewHolder(view);
    }

    public void onBindViewHolder(final InterestedAdapter.CustomViewHolder holder, int position)
    {
        MDBro curr = mdbros.get(position);
        holder.interestedPersonEmail.setText(curr.email);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-8cbc0.appspot.com");
        StorageReference interestedImageRef = mStorageRef.child(curr.img);
        interestedImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                Glide.with(context)
                        .load(uri.toString())
                        .into(holder.interestedPersonImage);
            }
        });
    }

    public int getItemCount()
    {
        return mdbros.size();
    }

    public void updateList(ArrayList<MDBro> i)
    {
        mdbros = i;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
    {
        TextView interestedPersonEmail;
        ImageView interestedPersonImage;

        public CustomViewHolder(View view) {
            super(view);

            interestedPersonEmail = (TextView) view.findViewById(R.id.userProfileEmail);
            interestedPersonImage = (ImageView) view.findViewById(R.id.userProfileImage);
        }
    }

}
