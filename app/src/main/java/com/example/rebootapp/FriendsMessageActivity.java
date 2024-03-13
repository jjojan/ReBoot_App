package com.example.rebootapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.cometchat.chat.core.AppSettings;
//import com.cometchat.chat.core.CometChat;
//import com.cometchat.chat.exceptions.CometChatException;
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

//        initChat();
    }

//    private void initChat() {
//
//
//        AppSettings appSettings=new AppSettings.AppSettingsBuilder()
//                .subscribePresenceForAllUsers()
//                .setRegion(region)
//                .autoEstablishSocketConnection(true)
//                .build();
//
//        CometChat.init(this, appID,appSettings, new CometChat.CallbackListener<String>() {
//            @Override
//            public void onSuccess(String successMessage) {
//                Log.d("MessageTag", "Initialization completed successfully");
//            }
//            @Override
//            public void onError(CometChatException e) {
//                Log.d("MessageTag", "Initialization failed with exception: " + e.getMessage());
//            }
//        });
//    }

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
