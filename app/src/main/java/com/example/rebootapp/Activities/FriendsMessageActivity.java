package com.example.rebootapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.AppSettings;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.Models.MessageWrapper;
import com.example.rebootapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsMessageActivity extends AppCompatActivity {

    String appID = "254471f7d342c124";

    String API_KEY = "da71d0ceb524d1db314641c3436881b71f6b3f65";
    String region = "US";

    String username = "";

    MessageInput submit;

    String friendName;

    MessagesList messagesList;

    MessagesListAdapter<MessageWrapper> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsmessaging);
        initChat();

        Intent intent = getIntent();
        submit = findViewById(R.id.submit);
        messagesList = findViewById(R.id.messages);


        friendName = intent.getStringExtra("friendName");

        String senderID = CometChat.getLoggedInUser().getUid();
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        };

        adapter = new MessagesListAdapter<>(senderID, imageLoader);
        messagesList.setAdapter(adapter);


        refreshProfile();
        checkUser();
//        fetchFriends();
        registerfriends(friendName);

        submit.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
                sendMessage(input.toString());
                return true;
            }
        });

        addListeiner();

    }

    private void addListeiner() {

        String listenerID = "UNIQUE_LISTENER_ID";
        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                addMessage(textMessage);
                Log.d("listeining text", "Text message received successfully: " + textMessage.toString());
            }


        });
    }

    public void sendMessage(String message){

        String receiverID = friendName + "ReBoot";
        String receiverType = CometChatConstants.RECEIVER_TYPE_USER;

        TextMessage textMessage = new TextMessage(receiverID, message, receiverType);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                addMessage(textMessage);
                Log.d("Send Message", "Message sent successfully: " + textMessage.toString());
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Send Error", "Message sending failed with exception: " + e.getMessage());
            }
        });

    }

    public void addMessage(TextMessage textMessage) {

        adapter.addToStart(new MessageWrapper(textMessage), true);

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

    public void fetchFriends() {
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
                    registerfriends(username);
                    ParseFile profilePic = friend.getParseFile("profile_pic");
                } else {
                    Log.e("fetchFriends", "Error fetching friend data: " + e.getMessage(), e);
                }


            });
        }
    }

    public void registerfriends(String name){

        User user = new User();
        user.setUid(name + "ReBoot");
        user.setName(name);
        Log.e("username","name: " + name);

        CometChat.createUser(user, API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("createFriend", user.toString());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("createFriend", e.getMessage());
            }
        });
    }
}
