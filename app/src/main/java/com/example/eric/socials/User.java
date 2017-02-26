package com.example.eric.socials;

import java.util.ArrayList;

/**
 * Created by eric on 2/23/17.
 */

public class User {
    String name;
    String email;
    String profilePicture;
    ArrayList<String> socials;

    public User(String name, String profilePicture, String email){
        this.name = name;
        this.socials = new ArrayList<>();
        this.profilePicture = profilePicture;
        this.email = email;
    }
}
