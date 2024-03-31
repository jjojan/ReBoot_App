package com.example.rebootapp.Activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import com.example.rebootapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import androidx.activity.result.contract.ActivityResultContract;
import 	androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
public class EditProfileActivity extends AppCompatActivity {

    Button exitSession;
    TextView characterCount, userName, editProfilePic, editUserName, saveProfile;
    EditText editBio;

    ActivityResultLauncher<Intent> resultLauncher;

    ShapeableImageView currentProfilePic;
    Uri profile_Uri;
    private static final int PICK_IMAGE_REQUEST = 1;


    // currentUser global Variable

    private ActivityResultLauncher<Intent> getPicture;

    public interface UsernameCheckCallback {
        void onResult(boolean isUsernameAvailable);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Get Current User Information
        ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            currentUser.fetch();
        }
        catch (Exception e){
            System.out.println("won't fetch");
        }
        String currentUserObjectID = currentUser.getObjectId();
        String currentUserName = currentUser.getUsername();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo(currentUserObjectID, "username");


        //Assign XML Elements
        editProfilePic = findViewById(R.id.btn_edit_profile_pic);
        editUserName = findViewById(R.id.btn_edit_user_name);
        saveProfile = findViewById(R.id.btn_SaveProfile);
        exitSession = findViewById(R.id.btn_ProfileDone);
        characterCount = findViewById(R.id.tv_CharCount);
        userName = findViewById(R.id.et_currentUserName);
        editBio = findViewById(R.id.edt_Bio);
        currentProfilePic = findViewById(R.id.iv_currentUserProfilePic);


        userName.setHint(currentUserName);
        editBio.setHint(currentUser.getString("bio"));

        editUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = popUpEditText();
            }
        });

        query.getInBackground(currentUserObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseFile image = object.getParseFile("profile_pic");

                    String imageUrl;
                    if (image != null) {
                        imageUrl = image.getUrl();
                        Picasso.get().load(imageUrl).into(currentProfilePic);
                    } else {
                    }


                } else {

                    System.out.println(e.toString());
                }
            }
        });

        editBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int length = text.length();
                text = (length + "/225");
                characterCount.setText(text);
            }
        });
        exitSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUsername = userName.getText().toString();
                String strBio = editBio.getText().toString();
                checkUsername(strUsername, new UsernameCheckCallback() {
                    @Override
                    public void onResult(boolean isUsernameAvailable) {
                        if (isUsernameAvailable) {
                            updateProfile(currentUserObjectID, strUsername, strBio );
                        }
                        else{
                            updateProfile(currentUserObjectID, currentUserName, strBio);
                        }
                    }
                });

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        });

//        getPicture = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if(result.getResultCode() == -1){
//                            Intent data = result.getData();
//                            if (data != null && data.getData() != null){
//                                Uri selectedImageUri = data.getData();
//                                uploadImage(selectedImageUri);
//                            }
//                        }
//
//                    }
//                });



//        btn_Profile_Pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
//                getPicture.launch(intent);
//            }
//        });

        editProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });





    }
    public void checkUsername(String str, UsernameCheckCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("username", str);
//        if (str.isEmpty()){
//            System.out.println("invalid username");
//        }
//        else {
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null)
                    callback.onResult(count == 0);
                else
                    callback.onResult(false);
            }
        });
//        }

    }

    public void updateProfile(String objectID, String newUsername, String newBio) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        String currentUsername = ParseUser.getCurrentUser().getUsername();
        query.getInBackground(objectID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    if(!newUsername.isEmpty() && !(newUsername.equals(currentUsername) ))
                        object.setUsername(newUsername);
                    if(!newBio.isEmpty())
                        object.put("bio", newBio);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                System.out.println("Save successful");
                            } else {
                                System.out.println("Save unSuccessful");
                            }
                        }
                    });
                }
                else System.out.println("Error somewhere...");
            }

        });

    }


    public void uploadImage(Uri imageUri){
        try{
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageData = readInputStreamToByteArray(inputStream);

            ParseFile parseFile = new ParseFile("profile_picture.jpg", imageData);
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        currentUser.put("profile_pic", parseFile);
                        currentUser.saveInBackground();

                        System.out.println("Saved Picture!!!");
                        Toast.makeText(getApplicationContext(), "Profile Picture changed", Toast.LENGTH_LONG).show();
                    }else {
                        System.out.println("Almost Saved picture :(");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("stream error");
        }
    }


    public byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1){
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }






    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new PickVisualMedia(), uri -> {

                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    uploadImage(uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });
//    @Override
//    public void onStart(){
//        super.onStart();
//        String currentUserName = ParseUser.getCurrentUser().getUsername();
//        et_editUsername.setHint(currentUserName);
//    }

//    public void endEditProfile(){
//        ParseUser currentUser = ParseUser.getCurrentUser();
//
//    }

    EditText popUpEditText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Username");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String strUsername = userName.getText().toString();
                String strBio = editBio.getText().toString();
                ParseUser currentUser = ParseUser.getCurrentUser();
                try {
                    currentUser.fetch();
                }
                catch (Exception e){
                    System.out.println("won't fetch");
                }
                String currentUserObjectID = currentUser.getObjectId();
                updateProfile(currentUserObjectID, strUsername, strBio );
                finish();
                startActivity(getIntent());

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        return input;
    }
}