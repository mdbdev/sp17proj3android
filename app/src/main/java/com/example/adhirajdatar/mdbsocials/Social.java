package com.example.adhirajdatar.mdbsocials;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by datarsd1 on 2/23/17.
 */

public class Social {

    String title, host, description, date, firebaseLocation, id, hostuid;
    ArrayList<String> interested;
    Integer numInterested;

    public Social(String t, String h, String desc, String d, String fbl, String id, Integer numInterested, String hostuid) {
        this.title = t;
        this.host = h;
        this.description = desc;
        this.date = d;
        this.firebaseLocation = fbl;
        this.id = id;
        this.interested = new ArrayList<>();
        this.numInterested = numInterested;
        this.hostuid = hostuid;
    }

}
