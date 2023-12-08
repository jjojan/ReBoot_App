package com.example.rebootapp;

import static com.parse.Parse.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.se.omapi.Session;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.squareup.picasso.Picasso;





public class ProfileFragment extends Fragment {

    Context context;
    Button btnSignOut;
    //Button btnUploadImage;
    ImageButton Friends;
    ImageButton Starred;
    ImageButton EditProfile;
    GoogleSignInAccount account;
    Session session;
    TextView tvUser_Username;
    TextView tvUser_Email;

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

    public void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    public void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            ProfilePic.setImageURI(imageUri);
                            profile_Uri = imageUri;
                        }
                        catch (Exception e){
                            System.out.println("no image selected");
                        }
                    }
                }
        );
    }

    @Override
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




        GoogleSignInOptions gso = new  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(getActivity(), gso);







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
                }
                else{
                    //Get current Parse User
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        ParseUser.logOutInBackground(e -> {
                            //progressDialog.dismiss();
                            if (e == null)
                                //showAlert("So, you're going...", "Ok...Bye-bye then");
                                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_SHORT).show();

                        });
                    } else {
                        // show the signup or login screen
                    }

                }
            }

        });



//        btnUploadImage.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                ActivityResultContracts.StartActivityForResult(intent, );
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);
//
//            }
//        });


        // Inflate the layout for this fragment
        return view;
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


//        btnUploadImage = view.findViewById(R.id.button_upload_image);
//        registerResult();
//
//        btnUploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pickImage();
//
////                ParseUser currentUser = ParseUser.getCurrentUser();
////                String currentUserObjectID = currentUser.getObjectId();
////                ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
////                query.getInBackground(currentUserObjectID, new GetCallback<ParseObject>() {
////                    @Override
////                    public void done(ParseObject object, ParseException e) {
////                        if (e == null) {
////                            object.put("profile_pic", profile_Uri);
////                            object.saveInBackground();
////                        }
////                    }
////                });

//            }
//        });

    }

    @Override
    public void onStart(){
        super.onStart();

        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserObjectID = currentUser.getObjectId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");


        query.getInBackground(currentUserObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null){
//                    tempString[0] = object.getString("username");
                    tvUser_Username.setText(object.getString("username"));
                    tvUser_Email.setText(object.getString("email"));
                    ParseFile image = object.getParseFile("profile_pic");
                    String imageUrl;
                    if (image != null) {
                        imageUrl = image.getUrl();
                        Picasso.get().load(imageUrl).into(ProfilePic);
                    }
                    else {}


                }
                else {

                    System.out.println(e.toString());
                }
            }
        });
//        String tmpString = currentUser.getObjectId();


    }






}