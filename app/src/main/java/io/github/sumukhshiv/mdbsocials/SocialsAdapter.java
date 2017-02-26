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
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by sumukhshivakumar on 2/24/17.
 */

public class SocialsAdapter extends RecyclerView.Adapter<SocialsAdapter.CustomViewHolder> {

    Context context;
    ArrayList<Social> socials;

    public SocialsAdapter(Context context, ArrayList<Social> socials) {
        this.context = context;
        this.socials = socials;
    }

    @Override
    public SocialsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SocialsAdapter.CustomViewHolder holder, int position) {
        final Social social = socials.get(socials.size() - position - 1);
        //In the onBindViewHolder, you want to set each of the parameters of ComputerCompanies very similiar
        //to what you did to the layout manager.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... strings) {
                try {return Glide.
                        with(context).
                        load(strings[0]).
                        asBitmap().
                        into(100, 100). // Width and height
                        get();}
                catch (Exception e) {return null;}
            }

            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                holder.imageViewEventImage.setImageBitmap(result);
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
        //holder.imageViewEventIamge.setImageResource(social.drawable);
        holder.textViewEventName.setText(social.nameOfEvent);
        holder.textViewHostEmail.setText(social.emailOfHost);
        holder.textViewNumberInterested.setText(Integer.toString(social.numberIntersted));


        holder.cardViewFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailScreenActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SOCIAL", social);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return socials.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //Set up the variables of Computer Companies here
        ImageView imageViewEventImage;
        TextView textViewEventName;
        TextView textViewHostEmail;
        TextView textViewNumberInterested;
        CardView cardViewFeed;

        public CustomViewHolder(View view) {
            super(view);

            imageViewEventImage = (ImageView) view.findViewById(R.id.imageViewSocialImage);
            textViewEventName = (TextView) view.findViewById(R.id.textViewEventName);
            textViewHostEmail = (TextView) view.findViewById(R.id.textViewHostEmail);
            textViewNumberInterested = (TextView) view.findViewById(R.id.textViewNumberInterested);
            cardViewFeed = (CardView) view.findViewById(R.id.cardViewFeed);

        }
    }




}
