package com.example.rebootapp.ListFiles;

import static com.parse.Parse.getApplicationContext;

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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rebootapp.EditProfileActivity;
import com.example.rebootapp.FavoriteGamesActivity;
import com.example.rebootapp.Login;
import com.example.rebootapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    Context context;
    Button btnSignOut;
    ImageButton Friends;
    ImageButton Starred;
    ImageButton EditProfile;
    GoogleSignInAccount account;
    Session session;
    TextView tvUser_Username;
    TextView tvUser_Email;
    TextView bio;
    ImageView ProfilePic;
    private static final int PICK_IMAGE_REQUEST = 1;
    ActivityResultLauncher<Intent> resultLauncher;
    Uri profile_Uri;

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
        Friends = view.findViewById(R.id.Friends);
        Starred = view.findViewById(R.id.Starred);
        EditProfile = view.findViewById(R.id.EditProfile);
        bio = view.findViewById(R.id.textView7);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(getActivity(), gso);

        refreshProfile();

        view.findViewById(R.id.customList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageCustomListDialog();
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (gsc != null) {
                    gsc.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SharedPreferences getUser = getActivity().getSharedPreferences("user info", getActivity().MODE_PRIVATE);
                            SharedPreferences.Editor ed = getUser.edit();
                            ed.putString("username", null);
                            ed.commit();
                            Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getActivity(), Login.class);
                            startActivity(intent);

                        }
                    });
                } else {

                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        ParseUser.logOutInBackground(e -> {

                            if (e == null)

                                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_SHORT).show();

                        });
                    } else {

                    }

                }
            }

        });



        return view;
    }
    public void manageCustomListDialog() {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View customView = inflater.inflate(R.layout.layout_user_list, null);


        AlertDialog.Builder listDialog = new AlertDialog.Builder(
                new androidx.appcompat.view.ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));

        listDialog.setView(customView);
        AlertDialog userListDialogBuilder = listDialog.create();

        Button btnAddNewList=customView.findViewById(R.id.btnNewList);
        TextView tvTitleList=customView.findViewById(R.id.tvTitleList);
        tvTitleList.setText("Manage List");
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

        EditProfile = view.findViewById(R.id.EditProfile);
        EditProfile.setOnClickListener(new View.OnClickListener() {
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

