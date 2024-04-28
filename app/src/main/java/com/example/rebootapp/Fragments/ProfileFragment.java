package com.example.rebootapp.Fragments;

import static com.parse.Parse.getApplicationContext;

import static java.sql.DriverManager.println;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rebootapp.Activities.EditProfileActivity;
import com.example.rebootapp.Activities.FavoriteGamesActivity;
import com.example.rebootapp.Adapters.ManageListAdapter;
import com.example.rebootapp.Adapters.FavoriteGamesAdapter;
import com.example.rebootapp.LoginActivity;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.Activities.FriendsActivity;
import com.example.rebootapp.Adapters.FriendsListAdapter;
import com.example.rebootapp.Models.UserListModel;
import com.example.rebootapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;
import android.view.MenuItem;


public class ProfileFragment extends Fragment {

    Context context;
    GoogleSignInAccount account;
    Session session;
    Button btnSignOut;
    ImageButton Friends, Starred, EditProfileButton, CustomLists;
    TextView tvUser_Username, tvUser_Email, bio;
    ShapeableImageView ProfilePic;
    private static final int PICK_IMAGE_REQUEST = 1;
    ActivityResultLauncher<Intent> resultLauncher;
    Uri profile_Uri;

    //RecyclerView for Favorite Games
    RecyclerView favoritesRv, friendsRV;
    List<String> favoritesUris, friendsUris, friendsUsernames;
    List<FriendModel> friendsList = new ArrayList<>();
    FavoriteGamesAdapter favoritesAdapter;
    FriendsListAdapter friendsListAdapter;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    public void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            ProfilePic.setImageURI(imageUri);
                            profile_Uri = imageUri;
                        } catch (Exception e) {
                            System.out.println("no image selected");
                        }
                    }
                }
        );
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        tvUser_Username = view.findViewById(R.id.tvUser_Username);
        tvUser_Email = view.findViewById(R.id.tvUser_Email);
        ProfilePic = view.findViewById(R.id.ProfilePic);
        Friends = view.findViewById(R.id.friendsButton);
        Starred = view.findViewById(R.id.Starred);
        //EditProfileButton = view.findViewById(R.id.EditProfileButton);
        bio = view.findViewById(R.id.bioContent);
        CustomLists = view.findViewById(R.id.customList);

        //drawerLayout = view.findViewById(R.id.my_drawer_layout);
        //actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.nav_open, R.string.nav_close);
        //drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //actionBarDrawerToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(getActivity(), gso);

        refreshProfile();

//        view.findViewById(R.id.customList).setOnClickListener(new View.OnClickListener() {
        CustomLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageCustomListDialog();
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ParseUser.logOutInBackground(e -> {
                            if (e == null) {
                                SharedPreferences getUser = getActivity().getSharedPreferences("user info", Context.MODE_PRIVATE);
                                SharedPreferences.Editor ed = getUser.edit();
                                ed.remove("username");
                                ed.apply();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {

                                Log.e("LogoutError", "Parse logout failed", e);
                            }
                        });
                    }
                });
            }});
        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void manageCustomListDialog() {
        // Inflate the custom layout using layout inflater
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View customView = inflater.inflate(R.layout.layout_user_list, null);

        // Apply the custom style to the AlertDialog
        AlertDialog.Builder listDialog = new AlertDialog.Builder(
                new androidx.appcompat.view.ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));

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
        String userId = currentUser.getObjectId();
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
                            new ManageListAdapter(getActivity(),
                                    userListModelArrayList);
                    recyclerView.setAdapter(manageListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                } else {

                    Log.e("ParseError", "Error retrieving CustomUserList: " + e.getMessage());
                }
            }
        });



        userListDialogBuilder.show();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // User Favorites
        favoritesUris = new ArrayList<>();
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        favoritesAdapter = new FavoriteGamesAdapter(favoritesUris, currentUserId);
        favoritesRv = view.findViewById(R.id.favoritesRecyclerView);
        favoritesRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        favoritesRv.setAdapter(favoritesAdapter);

        // User Friends
        friendsUris = new ArrayList<>();
        friendsUsernames = new ArrayList<>();
        friendsListAdapter = new FriendsListAdapter(friendsList, getApplicationContext());
        friendsRV = view.findViewById(R.id.friendsRecyclerView);
        friendsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        friendsRV.setAdapter(friendsListAdapter);


        EditProfileButton = view.findViewById(R.id.EditProfileButton);
        EditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Starred = view.findViewById(R.id.Starred);
        Starred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FavoriteGamesActivity.class);
                getActivity().startActivity(intent);

            }
        });

        Friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                intent.putExtra("profileName",tvUser_Username.getText() );
                getActivity().startActivity(intent);
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
                            println(uri);
//                            System.out.println(uri);
                            favoritesUris.add(uri);
                        }
                    }
                    favoritesAdapter.notifyDataSetChanged();

                }
                else{
                    System.out.println("Parse query error");
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
                    getActivity().runOnUiThread(() -> {
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
                            getActivity().runOnUiThread(() -> {
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

    public void refreshProfile() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserObjectID = currentUser.getObjectId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");


        query.getInBackground(currentUserObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    tvUser_Username.setText(object.getString("username"));
                    tvUser_Email.setText(object.getString("email"));
                    bio.setText(object.getString("bio"));
                    ParseFile image = object.getParseFile("profile_pic");

                    String imageUrl;
                    if (image != null) {
                        imageUrl = image.getUrl();
                        Picasso.get().load(imageUrl).into(ProfilePic);
                    } else {
                    }


                } else {

                    System.out.println(e.toString());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshProfile();
        favoritesUris.clear();
        friendsUris.clear();
        friendsUsernames.clear();
//        fetchFriendsAndUpdateUI();
        fetchFriendsAndUpdateUI2();
        fillPhotos();
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        String currentUserObjectID = currentUser.getObjectId();
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
//
//
//        query.getInBackground(currentUserObjectID, new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null){
//                    tvUser_Username.setText(object.getString("username"));
//                    tvUser_Email.setText(object.getString("email"));
//                    bio.setText(object.getString("bio"));
//                    ParseFile image = object.getParseFile("profile_pic");
//
//                    String imageUrl;
//                    if (image != null) {
//                        imageUrl = image.getUrl();
//                        Picasso.get().load(imageUrl).into(ProfilePic);
//                    }
//                    else {}
//
//
//                }
//                else {
//
//                    System.out.println(e.toString());
//                }
//            }
//        });


    }


}

