package com.example.rebootapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
public class FriendProfileActivity extends AppCompatActivity {
    Button done;
    ImageView profile_pic;
    TextView username, bio;
    ImageButton starred, friends, lists;
    String friendUserID;







    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        friendUserID = getIntent().getStringExtra("FRIEND_ID");

        done = findViewById(R.id.btn_Friend_Profile_Done);
        profile_pic = findViewById(R.id.Friend_ProfilePic);
        username = findViewById(R.id.tvFriend_Friend_Username);
        bio = findViewById(R.id.friend_bio);
        starred = findViewById(R.id.friend_Starred);
        friends = findViewById(R.id.Friend_friends);
        lists = findViewById(R.id.friend_customList);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", friendUserID);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null && parseUser != null){
                    ParseFile pic = parseUser.getParseFile("profile_pic");
                    String pars_bio = parseUser.getString("bio");
                    String name = parseUser.getString("username");


                    if (pic != null) {
                        String picUrl = pic.getUrl();
                        Glide.with(profile_pic.getContext()).load(picUrl).into(profile_pic);
                    }
                    username.setText(name);
                    bio.setText(pars_bio);
                }
                else{
                    System.out.println("Something went wrong");
                }


            }


        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
