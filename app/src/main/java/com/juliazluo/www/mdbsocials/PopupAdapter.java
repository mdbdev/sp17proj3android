package com.juliazluo.www.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by julia on 2017-02-23.
 */

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<User> data;

    public PopupAdapter(Context context, ArrayList<User> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_item, parent, false);
        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        User user = data.get(position);
        holder.nameText.setText(user.getName());
        Glide.with(context)
            .load(user.getImageURI())
            .override(150, 150)
            .into(holder.image);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * A card displayed in the RecyclerView
     */
    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        ImageView image;

        public CustomViewHolder (View view) {
            super(view);
            this.nameText = (TextView) view.findViewById(R.id.popup_name);
            this.image = (ImageView) view.findViewById(R.id.popup_image);
        }
    }
}
