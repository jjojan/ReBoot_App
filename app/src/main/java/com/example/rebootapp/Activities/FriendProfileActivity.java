package com.example.rebootapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.rebootapp.Adapters.FavoriteGamesAdapter;
import com.example.rebootapp.Adapters.FriendsListAdapter;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendProfileActivity extends AppCompatActivity {
    Button done;
    ImageView profile_pic;
    TextView username, bio;
    ImageButton starred, friends, lists, messageButton;
    String friendUserID;

    //RecyclerView for Favorite Games
    RecyclerView friendFavoritesRv, friendsRV;
    List<String> friendFavoritesUris;
    List<FriendModel> friendsList = new ArrayList<>();
    FavoriteGamesAdapter friendFavoritesAdapter;
    FriendsListAdapter friendsListAdapter;


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
        messageButton = findViewById(R.id.messageButton);

        //Friends Favorites
        friendFavoritesUris = new ArrayList<>();
        friendFavoritesAdapter = new FavoriteGamesAdapter(friendFavoritesUris);
        friendsListAdapter = new FriendsListAdapter(friendsList);


        friendFavoritesRv = findViewById(R.id.favoritesRecyclerView);
        friendFavoritesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        friendFavoritesRv.setAdapter(friendFavoritesAdapter);

        friendsRV = findViewById(R.id.friendsRecyclerView);
        friendsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        friendsRV.setAdapter(friendsListAdapter);


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

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendProfileActivity.this, FriendsMessageActivity.class);
                String userName = ParseUser.getCurrentUser().getUsername();
                intent.putExtra("userName", userName);
                intent.putExtra("friendName", username.getText());
                FriendProfileActivity.this.startActivity(intent);
            }
        });


    }

    public void fillPhotos(String id){
//        ParseQuery<ParseObject> gamesQuery = ParseQuery.getQuery("FavoriteGames");
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        String currentUserID = currentUser.getObjectId();
//        gamesQuery.whereEqualTo("user_id", currentUserID);

        ParseQuery<ParseObject> gamesQuery = ParseQuery.getQuery("FavoriteGames");
        gamesQuery.whereEqualTo("user_id", friendUserID);

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

    public void nestedFetchFriendsAndUpdateUI() {
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        List<String> friendIds = currentUser.getList("friend_list");
        System.out.println("Got to nestedFetch");
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(friendUserID, (parseUser, e1) -> { // first Parse Call
            System.out.println("Got to first parse call");
            if (e1 == null) {
                List<String> friendIds = parseUser.getList("friend_list");

                if (friendIds == null || friendIds.isEmpty()) {
                    runOnUiThread(() -> {
                        System.out.println("empty or null");
                        friendsListAdapter.updateData(new ArrayList<FriendModel>());
                    });
                    return;
                }

                List<FriendModel> fetchedFriendModels = new ArrayList<>();
                AtomicInteger counter = new AtomicInteger(friendIds.size());

                for (String friendId : friendIds) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.getInBackground(friendId, (friend, e2) -> { // second Parse Call
                        if (e2 == null) {
                            String id = friend.getObjectId();
                            String username = friend.getString("username");
                            ParseFile profilePic = friend.getParseFile("profile_pic");
                            String profilePicUrl = profilePic != null ? profilePic.getUrl() : null;
                            fetchedFriendModels.add(new FriendModel(username, profilePicUrl, id));
                        } else {
                            Log.e("fetchFriends", "Error fetching friend data: " + e2.getMessage(), e2);
                        }

                        if (counter.decrementAndGet() == 0) {
                            runOnUiThread(() -> {
                                System.out.println("Somehow got here?");
                                friendsListAdapter.updateData(fetchedFriendModels);
                            });
                        }
                    });
                }
            }
            else {
                Log.e("fetchFriends", "Error fetching friend data: " + e1.getMessage(), e1);
            }
        });
    }
    public void nestedFetchFriendsAndUpdateUI2(){
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();

        userQuery.getInBackground(friendUserID, (parseUser, e1) -> {
            if (e1 == null){

                ParseRelation<ParseUser> friendsRelation = parseUser.getRelation("friends");
                ParseQuery<ParseUser> query = friendsRelation.getQuery();

                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e2) {
                        if(e2 == null){
                            List<FriendModel> fetchedFriendModels = new ArrayList<>();
                            AtomicInteger counter = new AtomicInteger(list.size());

                            for (ParseUser iUser : list){
                                String id = iUser.getObjectId();
                                String username = iUser.getString("username");
                                ParseFile file = iUser.getParseFile("profile_pic");
                                String fileUrl = file != null ? file.getUrl() : null;
                                fetchedFriendModels.add(new FriendModel(username, fileUrl, id));

                                if(counter.decrementAndGet() == 0){
                                    runOnUiThread(() -> {
                                        friendsListAdapter.updateData(fetchedFriendModels);
                                    });
                                }
                            }
                        }
                        else{
                            System.out.println("error in 2nd call");
                        }
                    }
                });
            }
            else{
                System.out.println("error in first call");
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        friendFavoritesUris.clear();
        fillPhotos(friendUserID);
//        nestedFetchFriendsAndUpdateUI();
        nestedFetchFriendsAndUpdateUI2();
    }


}