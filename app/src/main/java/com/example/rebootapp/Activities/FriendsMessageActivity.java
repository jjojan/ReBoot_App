package com.example.rebootapp.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.chat.core.AppSettings;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.example.rebootapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FriendsMessageActivity extends AppCompatActivity {

    String appID = "254471f7d342c124";

    String API_KEY = "da71d0ceb524d1db314641c3436881b71f6b3f65";
    String region = "US";

    String username = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsmessaging);

        refreshProfile();
        initChat();
        checkUser();

    }

    private void initChat() {

        AppSettings appSettings=new AppSettings.AppSettingsBuilder()
                .subscribePresenceForAllUsers()
                .setRegion(region)
                .autoEstablishSocketConnection(true)
                .build();

        CometChat.init(this, appID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d("cometcheck", "Initialization completed successfully");
            }
            @Override
            public void onError(CometChatException e) {
                Log.d("cometerror", "Initialization failed with exception: " + e.getMessage());
            }
        });
    }

    public void checkUser(){

        String UID = username + "ReBoot";

        CometChat.getUser(UID, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("GetUser", "User details fetched successfully for user: " + user.getUid());
               loginUser();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("GetUser", "User fetching failed with exception: " + e.getMessage());
                registerUser();
            }
        });
    }

    public void registerUser(){

        User user = new User();
        user.setUid(username + "ReBoot");
        user.setName(username);
        Log.e("username","name: " + username);

        CometChat.createUser(user, API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("createUser", user.toString());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("createUser", e.getMessage());
            }
        });
    }

    public void loginUser(){
        String UID = username + "ReBoot";

        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(UID, API_KEY, new CometChat.CallbackListener<User>() {

                @Override
                public void onSuccess(User user) {
                    Log.d("loginWorks", "Login Successful : " + user.toString());
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d("loginError", "Login failed with exception: " + e.getMessage());
                }
            });
        } else {
            // User already logged in
        }
    }

    public void refreshProfile() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserObjectID = currentUser.getObjectId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");


        query.getInBackground(currentUserObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    username = object.getString("username");
                    Log.d("MessageUsername", username);



                } else {

                    System.out.println(e.toString());
                }
            }
        });
    }
}
