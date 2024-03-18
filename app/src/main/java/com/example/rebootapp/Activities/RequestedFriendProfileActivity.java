package com.example.rebootapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;


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

public class RequestedFriendProfileActivity extends AppCompatActivity {
    Button done;
    ImageButton accept, deny;
    ImageView profile_pic;
    TextView username, bio;

    String friendUserID;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_friend_profile);
        friendUserID = getIntent().getStringExtra("FRIEND_ID");

        done = findViewById(R.id.btn_Friend_Profile_Done);
        accept = findViewById(R.id.btn_accept);
        deny = findViewById(R.id.btn_deny);

        profile_pic = findViewById(R.id.Friend_ProfilePic);
        username = findViewById(R.id.tvFriend_Friend_Username);
        bio = findViewById(R.id.friend_bio);


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
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.whereEqualTo("objectId", friendUserID);
                userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null){
                            String friend_objectId = parseUser.getObjectId();
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
                            query.whereEqualTo("source_user", parseUser);
                            query.whereEqualTo("destination_user", currentUser);
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e2) {
                                    if (e2 == null) {
                                        parseObject.deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null)
                                                    System.out.println("Success");
                                                else System.out.println("Faulture to delte");
                                            }
                                        });
                                        acceptFriendRequest(friend_objectId);
                                        runOnUiThread(() -> {
                                            finish();
                                        });
                                    }
                                }
                            });


                        }
                    }
                });


            }
        });
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.whereEqualTo("objectId", friendUserID);
                userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null){
                            String friend_objectId = parseUser.getObjectId();
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
                            query.whereEqualTo("source_user", parseUser);
                            query.whereEqualTo("destination_user", currentUser);
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e2) {
                                    if (e2 == null) {
                                        parseObject.put("Response", "Deny");
                                        parseObject.saveInBackground();
                                        runOnUiThread(() -> {
                                            finish();
                                        });
                                    }
                                }
                            });


                        }
                    }
                });


            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    void acceptFriendRequest(String f_id){
        HashMap<String, String> params = new HashMap<String, String>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        String oid = currentUser.getObjectId();
        params.put("currentUserId", oid);
        params.put("friendUserId", f_id);
        ParseCloud.callFunctionInBackground("checkAndAddFriendById", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException e){
                if (e == null) {
                    System.out.println(result);
                }
            }
        });
    }


}