package com.example.rebootapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.rebootapp.Models.FriendModel;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class FriendTest {

    @Test
    public void TestFriend()  {
        String test_usr = "jjoeyjjoey";
        String test_bio = "here's a new bio ";
        String test_url = "https://parsefiles.back4app.com/NNyEnZ8QLzklluhbnUE52mgWU9zfnrUdDEK2ihQl/67fcdfed014cf8a967f7929ea4e32b6b_profile_picture.jpg";

        String friendID = "FFrJgqlQ5V";
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", friendID);

        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                String bio = parseUser.getString("bio");
                String username = parseUser.getString("username");
                ParseFile img = parseUser.getParseFile("profile_pic");
                String img_url = img.getUrl().toString();
                FriendModel f = new FriendModel(username, bio, img_url);

                if (testString(f, test_usr) && testString(f, test_bio)   && testString(f, test_url)){
                    assertTrue(true);
                }
                else assertTrue(false);
            }
        });


    }

    public boolean testString(FriendModel f, String a){
        return f.getUsername().equals(a);
    }
}