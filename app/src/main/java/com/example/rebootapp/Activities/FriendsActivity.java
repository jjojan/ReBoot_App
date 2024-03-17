package com.example.rebootapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.rebootapp.Adapters.SuggestedFriendsAdapter;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.Adapters.FriendsListAdapter;
import com.example.rebootapp.Models.SuggestedFriendModel;
import com.example.rebootapp.R;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsActivity extends AppCompatActivity {

    Button btn_friends_done;
    ImageButton btn_newFriend;
    EditText et_newFriend;
    RecyclerView rv_Friends_List, rv_Suggested_Friends_List;
    List<String> friendUrls;
    List<String> friendUsernames;
    FriendsListAdapter friendsListAdapter;
    SuggestedFriendsAdapter suggestedFriendsAdapter;

    List<FriendModel> friendsList = new ArrayList<>();
    List<SuggestedFriendModel> suggestedFriendsList = new ArrayList<>();

    private final FriendUpdateCallback friendUpdateCallback = this::fetchFriendsAndUpdateUI;
    private final FriendUpdateCallback friendUpdateCallback2 = this::fetchFriendsAndUpdateUI2;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        btn_friends_done = findViewById(R.id.btn_Friends_Done);
        rv_Friends_List = findViewById(R.id.rv_Friends_list);
//        rv_Friends_List.setLayoutManager(new GridLayoutManager(this, 3));
        rv_Friends_List.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rv_Suggested_Friends_List = findViewById(R.id.rv_Suggested_Friends_list);
        rv_Suggested_Friends_List.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        btn_newFriend = findViewById(R.id.btn_addFriend);
        et_newFriend = findViewById(R.id.et_addFriend);


        friendUrls = new ArrayList<>();
        friendUsernames = new ArrayList<>();

        friendsListAdapter = new FriendsListAdapter(friendsList);
        rv_Friends_List.setAdapter(friendsListAdapter);

        suggestedFriendsAdapter = new SuggestedFriendsAdapter(suggestedFriendsList);
        rv_Suggested_Friends_List.setAdapter(suggestedFriendsAdapter);



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
//                addFriendByUsername(friendUserName, friendUpdateCallback);
                addFriendByUsername2(friendUserName, friendUpdateCallback2);
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
//        fetchFriendsAndUpdateUI();
        fetchFriendsAndUpdateUI2();
        suggestedFriendsTest();
//        fetchSuggestedFriendsAndUpdateUI();
//        testAddFriendRelation();
    }



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
                                Toast.makeText(getApplicationContext(), "FriendModel " + username + " added successfully!", Toast.LENGTH_SHORT).show();
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

    public void addFriendByUsername2(String username, FriendUpdateCallback callback){
        HashMap<String, String> params = new HashMap<String, String>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        String oid = currentUser.getObjectId();
        params.put("currentUserId", oid);
        params.put("friendUsername", username);

        ParseCloud.callFunctionInBackground("checkAndAddFriendByUsername", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException e){
                if (e == null) {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    if(callback != null) {
                        callback.onFriendListUpdated();
                    }
                }
            }
        });

    }

    public void fetchFriendsAndUpdateUI() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> friendIds = currentUser.getList("friend_list");
        if (friendIds == null) return;

        List<FriendModel> fetchedFriendModels = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(friendIds.size());

        for (String friendId : friendIds) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getInBackground(friendId, (friend, e) -> {
                if (e == null) {
                    String id = friend.getObjectId();
                    String username = friend.getString("username");
                    ParseFile profilePic = friend.getParseFile("profile_pic");
                    String profilePicUrl = profilePic != null ? profilePic.getUrl() : null;
                    fetchedFriendModels.add(new FriendModel(username, profilePicUrl, id));
                } else {
                    Log.e("fetchFriends", "Error fetching friend data: " + e.getMessage(), e);
                }

                if (counter.decrementAndGet() == 0) {
                    runOnUiThread(() -> {
                        friendsListAdapter.updateData(fetchedFriendModels);
                    });
                }
            });
        }
    }

    public void fetchFriendsAndUpdateUI2(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseRelation<ParseUser> friendsRelation = currentUser.getRelation("friends");
        ParseQuery<ParseUser> query = friendsRelation.getQuery();


        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null){

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
                    System.out.println("Error: query -> List<ParseUser> gone wrong");
                }
            }
        });

    }
    public interface FriendUpdateCallback {
        void onFriendListUpdated();
    }

    public void fetchSuggestedFriendsAndUpdateUI(){
        HashMap<String, String> params = new HashMap<String, String>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        String oid = currentUser.getObjectId();
        params.put("userId", oid);
        ParseCloud.callFunctionInBackground("suggestFriends", params, new FunctionCallback<List<HashMap<String,Object>>>() {
            @Override
            public void done(List<HashMap<String, Object>> listMap, ParseException e){
                if (e == null){

//                    AtomicInteger counter = new AtomicInteger(listMap.size());
                    List<SuggestedFriendModel> sfml = new ArrayList<>();

                    for (HashMap<String, Object> details : listMap){
                        HashMap<String, Object> userMap = (HashMap<String, Object>) details.get("user");
                        String id = (String) userMap.get("objectId");
                        String username = (String) userMap.get("username");
                        String pic_url = (String) userMap.get("profilePicUrl");

                        int mutualFriends = (int) details.get("mutualFriendsCount");
                        sfml.add(new SuggestedFriendModel(username, pic_url, id, mutualFriends));

//                        SuggestedFriendModel sfm = new SuggestedFriendModel(username, pic_url, id, mutualFriends);

//                        if(counter.decrementAndGet() == 0){
//                            runOnUiThread(() -> {
//                                suggestedFriendsAdapter.updateData(sfml);
//                            });
//                        }
                    }


                }
            }
        });
//        ParseCloud.callFunctionInBackground("suggestFriends", params, new FunctionCallback<List<String>>() {
//            @Override
//            public void done(List<String> users, ParseException e) {
//                if (e == null){
//
//                    int count = 0;
//                    count = users.size();
//                    System.out.println(count);
//
//                    for (String user : users){
//                        System.out.println("iteration");
//                        System.out.println(user);
//                    }
//                }
//            }
//        });
//        ParseCloud.callFunctionInBackground("suggestFriends", params, new FunctionCallback<List<HashMap>>() {
//            @Override
//            public void done(List<HashMap> suggestedFriends, ParseException e) {
//                if (e == null) {
//                    System.out.println(oid);
//                    System.out.println(suggestedFriends.size());
//                    for (HashMap friend : suggestedFriends){
//                        String id = (String) friend.get("objectId");
//                        System.out.println(id);
//                    }
//                } else {
//                    System.out.println("error");
//                }
//            }
//        });
    }

    public void suggestedFriendsTest(){
        HashMap<String, String> params = new HashMap<String, String>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        String oid = currentUser.getObjectId();
        params.put("userId", oid);
        ParseCloud.callFunctionInBackground("suggestFriendsTest", params, new FunctionCallback<List<HashMap<String, Object>>>(){
            @Override
            public void done(List<HashMap<String, Object>> map, ParseException e){
                if (e == null){
                    System.out.println(map.size());
                    List<SuggestedFriendModel> sfml = new ArrayList<>();
                    AtomicInteger counter = new AtomicInteger(map.size());
                    for(HashMap<String, Object> m : map){
                        ParseUser a = (ParseUser) m.get("user");
                        String id = a.getObjectId();
                        String username = a.getString("username");
                        int b = (int) m.get("mutualFriendsCount");
                        ParseFile pic = a.getParseFile("profile_pic");
                        String picUrl = pic != null ? pic.getUrl() : null;
                        String b_String = String.valueOf(b);
                        System.out.println("username: " + username + " m_friends: " + b_String);
                        sfml.add(new SuggestedFriendModel(username, picUrl, id, b));

                        if(counter.decrementAndGet() == 0){
                            runOnUiThread(() -> {
                                suggestedFriendsAdapter.updateData(sfml);
                            });
                        }
                    }
                }
            }
        });
    }

    public void testAddFriendRelation(){
        HashMap<String, String> params = new HashMap<String, String>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        String oid = currentUser.getObjectId();
        params.put("currentUserId", oid);
        params.put("friendUsername", "joey4");
        ParseCloud.callFunctionInBackground("checkAndAddFriendByUsername", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException e){
                if (e == null) {
                    System.out.println(result);
                }
            }
        });

    }



}
