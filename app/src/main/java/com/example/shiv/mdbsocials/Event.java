package com.example.shiv.mdbsocials;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Shiv on 2/20/17.
 */

public class Event {

    String date;
    String description;
    String email;
    String eventName;
    String emailAddress;
    String imageURL;
    String key;
    int ImageID;
    String numInterested;
    ArrayList<String> peopleInterested;

    public Event() {
        //numInterested = "0";
        peopleInterested = new ArrayList<>();

    }
}
