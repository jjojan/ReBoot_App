package com.example.rebootapp.Activities;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.rebootapp.R;
import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import androidx.appcompat.app.AppCompatActivity;

public class SuggestedFriendProfileActivity extends AppCompatActivity {

    Button done;
    ImageButton request, block;
    ImageView profile_pic;
    TextView username, bio;

    String friendUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested_friend_profile);


        done = findViewById(R.id.btn_Friend_Profile_Done);
        request = findViewById(R.id.btn_request);
        block = findViewById(R.id.btn_block);

        profile_pic = findViewById(R.id.Friend_ProfilePic);
        username = findViewById(R.id.tvFriend_Friend_Username);
        bio = findViewById(R.id.friend_bio);

        friendUserID = getIntent().getStringExtra("FRIEND_ID");

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

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFriendByUserId(friendUserID);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockUser(friendUserID);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

    }



    public void requestFriendByUserId(String userId){
        HashMap<String, String> params = new HashMap<String, String>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        String oid = currentUser.getObjectId();
        params.put("currentUserId", oid);
        params.put("friendUserId", userId);

        ParseCloud.callFunctionInBackground("friendRequestByUserId", params, new FunctionCallback<String>(){
            @Override
            public void done(String result, ParseException e){
                if (e == null){
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    runOnUiThread(()-> {
                        finish();
                    });
                }
            }
        });
    }

    public void blockUser(String userId){
        HashMap<String, String> params = new HashMap<String, String>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        String oid = currentUser.getObjectId();
        params.put("currentUserId", oid);
        params.put("friendUserId", userId);

        ParseCloud.callFunctionInBackground("blockUserById", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException e){
                if (e == null){
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    runOnUiThread(()-> {
                        finish();
                    });
                }
            }
        });

    }

}
