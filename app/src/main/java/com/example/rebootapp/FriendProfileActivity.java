package com.example.rebootapp;

import static java.security.AccessController.getContext;

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

    //RecyclerView for Favorite Games
    RecyclerView friendFavoritesRv;
    List<String> friendFavoritesUris;
    FavoriteGamesAdapter friendFavoritesAdapter;


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

        //Friends Favorites
        friendFavoritesUris = new ArrayList<>();
        friendFavoritesAdapter = new FavoriteGamesAdapter(friendFavoritesUris);
        friendFavoritesRv = findViewById(R.id.favoritesRecyclerView);
        friendFavoritesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        friendFavoritesRv.setAdapter(friendFavoritesAdapter);


        //Friends Friends
        friends = findViewById(R.id.Friends_friends);

        //Friends Collections
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

    public void fillPhotos(){
        ParseQuery<ParseObject> gamesQuery = ParseQuery.getQuery("FavoriteGames");
        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserID = currentUser.getObjectId();
        gamesQuery.whereEqualTo("user_id", currentUserID);

        //Adds images of favorites to ArrayList of photo uris defined earlier
        gamesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject object : objects) {
                        String uri = object.getString("picture_uri");
                        if(uri != null && !uri.isEmpty()) {
                            friendFavoritesUris.add(uri);
                        }
                    }
                    friendFavoritesAdapter.notifyDataSetChanged();

                }
                else{
                    System.out.println("Parse query error");
                }
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        friendFavoritesUris.clear();
        fillPhotos();
    }


}
