package com.example.rebootapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
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
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsActivity extends AppCompatActivity {

    Button btn_friends_done;
    ImageButton btn_newFriend;
    EditText et_newFriend;
    RecyclerView rv_Friends_List;
    List<String> friendUrls;
    List<String> friendUsernames;
    FriendsListAdapter friendsListAdapter;

    List<Friend> friendsList = new ArrayList<>();

    private final FriendUpdateCallback friendUpdateCallback = this::fetchFriendsAndUpdateUI;





    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        btn_friends_done = findViewById(R.id.btn_Friends_Done);
        rv_Friends_List = findViewById(R.id.rv_Friends_list);
//        rv_Friends_List.setLayoutManager(new GridLayoutManager(this, 3));
        rv_Friends_List.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        btn_newFriend = findViewById(R.id.btn_addFriend);
        et_newFriend = findViewById(R.id.et_addFriend);


        friendUrls = new ArrayList<>();
        friendUsernames = new ArrayList<>();

        friendsListAdapter = new FriendsListAdapter(friendsList);
        rv_Friends_List.setAdapter(friendsListAdapter);




        btn_friends_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_newFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendUserName = et_newFriend.getText().toString();
                addFriendByUsername(friendUserName, friendUpdateCallback);
            }
        });




    };

//    public void fillPhotos(){
////        ParseQuery<ParseObject> friendQuery = ParseQuery.getQuery("friend_list");
////        ParseUser currentUser = ParseUser.getCurrentUser();
////        String currentUserID = currentUser.getObjectId();
////        friendQuery.whereEqualTo("user_id", currentUserID);
//
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        List<String> friends_list = currentUser.getList("friend_list");
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//
//
//        if (query != null && friends_list != null) {
//            query.whereContainedIn("objectId", friends_list);
//            query.findInBackground(new FindCallback<ParseUser>() {
//                @Override
//                public void done(List<ParseUser> list, ParseException e) {
//                    if (e == null) {
//                        System.out.println(list.size());
//                        for (ParseUser friend : list) {
//                            ParseFile pic = friend.getParseFile("profile_pic");
//                            String usr = friend.getString("username");
//                            System.out.println("this happened");
//                            if (pic != null) {
//                                String picUrl = pic.getUrl();
//                                friendUrls.add(picUrl);
//                                System.out.println(picUrl);
//                                friendUsernames.add(usr);
//                                Log.i("msg", picUrl);
//                                System.out.println("this happened 2");
//                            }
//                            else{
//                                System.out.println("this didnt happened");
//                                friendUrls.add(null);
//                                friendUsernames.add(usr);
//                            }
//                        }
//                        System.out.println("this happened sssss");
//
//                        friendsListAdapter.notifyDataSetChanged();
//                    } else {
//                        Log.e("ParseQueryError", "Error" + e.getMessage());
//                    }
//                }
//
//            });
//        }
//        else{
//            if(query == null) Log.i("msg", "query is null");
//            if(friends_list == null) Log.i("msg", "friends list null");
//        }
//
//
//
//    }

    @Override
    protected void onStart(){
        super.onStart();
        friendUrls.clear();
        friendUsernames.clear();
        fetchFriendsAndUpdateUI();
    }

//    private void refresh(){
//        friendUrls.clear();
//        friendUsernames.clear();
//        fetchFriendsAndUpdateUI();
//    }

    public void addFriendByUsername(String username, FriendUpdateCallback callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(e == null && !list.isEmpty()){
                    ParseUser friend = list.get(0);
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    List<String> friendList = currentUser.getList("friend_list");
                    if(friendList == null){
                        friendList = new ArrayList<>();
                    }

                    if(!friendList.contains(friend.getObjectId())) {
                        friendList.add(friend.getObjectId());
                        currentUser.put("friend_list", friendList);
                        currentUser.saveInBackground( e1 -> {
                            if (e1 == null){
                                Toast.makeText(getApplicationContext(), "Friend " + username + " added successfully!", Toast.LENGTH_SHORT).show();
                                if(callback != null) {
                                    callback.onFriendListUpdated();
                                }
                            }
                            else Toast.makeText(getApplicationContext(), "Failed to add " + username ,Toast.LENGTH_SHORT).show();
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "User " + username + " Already in Friends List!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "User " + username + " Not Found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fetchFriendsAndUpdateUI() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> friendIds = currentUser.getList("friend_list");
        if (friendIds == null) return;

        List<Friend> fetchedFriends = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(friendIds.size());

        for (String friendId : friendIds) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getInBackground(friendId, (friend, e) -> {
                if (e == null) {
                    // Assuming 'username' and 'profilePic' are the keys
                    String id = friend.getObjectId();
                    String username = friend.getString("username");
                    ParseFile profilePic = friend.getParseFile("profile_pic");
                    String profilePicUrl = profilePic != null ? profilePic.getUrl() : null;
                    fetchedFriends.add(new Friend(username, profilePicUrl, id));
                } else {
                    Log.e("fetchFriends", "Error fetching friend data: " + e.getMessage(), e);
                }

                if (counter.decrementAndGet() == 0) {
                    // All fetches are complete, update UI
                    runOnUiThread(() -> {
                        friendsListAdapter.updateData(fetchedFriends);
                    });
                }
            });
        }
    }

    public interface FriendUpdateCallback {
        void onFriendListUpdated();
    }



}
