package com.example.rebootapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
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
public class BlockedUserProfileActivity extends AppCompatActivity {
    Button done;
    ImageButton unblock;
    ImageView profile_pic;
    TextView username, bio;

    String friendUserID;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blocked_user_profile);


        done = findViewById(R.id.btn_Friend_Profile_Done);
        unblock = findViewById(R.id.btn_unblock);

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


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unblockUser(friendUserID);
            }
        });
    }

    public void unblockUser(String id){
        ParseUser currentUser = ParseUser.getCurrentUser();
        String oid = currentUser.getObjectId();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("currentUserId", oid);
        params.put("friendUserId", id);
        ParseCloud.callFunctionInBackground("unblock", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException e){
                if (e == null){
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
        });
    }
}
