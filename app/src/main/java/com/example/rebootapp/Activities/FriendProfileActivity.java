package com.example.rebootapp.Activities;

import static com.parse.Parse.getApplicationContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.rebootapp.Adapters.FavoriteGamesAdapter;
import com.example.rebootapp.Adapters.FriendsListAdapter;
import com.example.rebootapp.Adapters.ManageListAdapter;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.Models.ReviewModel;
import com.example.rebootapp.Models.UserListModel;
import com.example.rebootapp.R;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendProfileActivity extends AppCompatActivity {
    Button done;
    ImageView profile_pic;
    TextView username, bio;
    ImageButton starred, friends, lists, messageButton, deleteButton;
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
        deleteButton = findViewById(R.id.btn_delete);

        //Friends Favorites
        friendFavoritesUris = new ArrayList<>();
        friendFavoritesAdapter = new FavoriteGamesAdapter(friendFavoritesUris, friendUserID);
        friendsListAdapter = new FriendsListAdapter(friendsList, getApplicationContext());


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
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String o_Id = currentUser.getObjectId();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("currentUserId", o_Id);
                params.put("friendUserId", friendUserID);

                ParseCloud.callFunctionInBackground("checkAndRemoveFriendById", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String result, ParseException e) {
                        if (e == null) {
                            System.out.println(result);
                            runOnUiThread(() -> {
                                Intent intent = new Intent("com.example.UPDATE_FRIEND");
                                intent.putExtra("action", "remove");
                                intent.putExtra("friendId", friendUserID);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                finish();
                            });
                        }
                    }
                });

//                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
//                userQuery.whereEqualTo("objectId", friendUserID);
//                userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
//                    @Override
//                    public void done(ParseUser parseUser, ParseException e) {
//                        if (e == null){
//
//                        }
//                    }
//                });


            }
        });



        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendProfileActivity.this, FriendsMessageActivity.class);
                String userName = ParseUser.getCurrentUser().getObjectId();
                intent.putExtra("userName", userName);
                intent.putExtra("friendName", friendUserID);
                FriendProfileActivity.this.startActivity(intent);
            }
        });

        starred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendProfileActivity.this, FriendFavoriteGamesActivity.class);
                intent.putExtra("FRIEND_ID", friendUserID);
                FriendProfileActivity.this.startActivity(intent);
            }
        });

        lists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageCustomListDialog();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.item_report){
            reportUser();
        }
        return super.onOptionsItemSelected(item);
    }

    public void reportUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", friendUserID);

        ParseCloud.callFunctionInBackground("reportUser", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException e) {
                if (e == null) {

                    Log.d("Cloud", "reported: " + response.toString());
                    Toast.makeText(getApplicationContext(), "User has been reported!", Toast.LENGTH_LONG).show();
                } else {

                    Log.e("Cloud", "Error " + e.getMessage());
                }
            }
        });

//
//        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
//        query.whereEqualTo("objectId", friendUserID);
//
//        query.getInBackground(friendUserID, new GetCallback<ParseUser>() {
//            public void done(ParseUser user, ParseException e) {
//                if (e == null) {
//                    Log.i("report", user.getObjectId());
//                    int currentReportNumber = user.getInt("reportNum");
//
//                    currentReportNumber++;
//                    user.put("reportNum", currentReportNumber);
//
//
//                    user.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                // ReportNumber field updated successfully
//                                Log.d("Parse", "ReportNumber increased by one");
//                            } else {
//                                // Error occurred while saving the object
//                                Log.e("Parse", "Error updating reportNumber: " + e.getMessage());
//                            }
//                        }
//                    });
//                } else {
//                    Log.i("report", "error");
//                }
//            }
//        });
    }

    public void manageCustomListDialog() {
        // Inflate the custom layout using layout inflater
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View customView = inflater.inflate(R.layout.layout_user_list, null);

        // Apply the custom style to the AlertDialog
        AlertDialog.Builder listDialog = new AlertDialog.Builder(
                new androidx.appcompat.view.ContextThemeWrapper(FriendProfileActivity.this, R.style.AlertDialogCustom));

        listDialog.setView(customView); // Set the custom view for the dialog
        AlertDialog userListDialogBuilder = listDialog.create();

        Button btnAddNewList=customView.findViewById(R.id.btnNewList);
        TextView tvTitleList=customView.findViewById(R.id.tvTitleList);
        tvTitleList.setText("Manage Lists");
        btnAddNewList.setVisibility(View.GONE);
        Button btnClose=customView.findViewById(R.id.btnClose);
        RecyclerView recyclerView=customView.findViewById(R.id.recyclerView);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userListDialogBuilder.dismiss();
            }
        });
        ParseUser currentUser = ParseUser.getCurrentUser();
        String userId = friendUserID;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomUserList");
        query.whereEqualTo("userID", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> customUserLists, ParseException e) {
                if (e == null) {

                    ArrayList<UserListModel> userListModelArrayList = new ArrayList<>();
                    for (ParseObject object : customUserLists) {
                        String listName = object.getString("listName");
                        List<String> gameName = object.getList("gameName");
                        List<String> gamePreviewLink = object.getList("gamePreviewLink");
                        String userID = object.getString("userID");
                        List<String> gameID = object.getList("gameID");
                        String objectID = object.getObjectId();

                        UserListModel model = new UserListModel(listName, gameName,
                                gamePreviewLink,gameID, userID, objectID);
                        userListModelArrayList.add(model);
                    }

                    ManageListAdapter manageListAdapter=
                            new ManageListAdapter(FriendProfileActivity.this,
                                    userListModelArrayList, userId);
                    recyclerView.setAdapter(manageListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(FriendProfileActivity.this));
                } else {

                    Log.e("ParseError", "Error retrieving CustomUserList: " + e.getMessage());
                }
            }
        });



        userListDialogBuilder.show();
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