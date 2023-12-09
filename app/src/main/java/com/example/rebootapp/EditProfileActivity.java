package com.example.rebootapp;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import androidx.activity.result.contract.ActivityResultContract;
import 	androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
public class EditProfileActivity extends AppCompatActivity {

    Button btn_ProfileDone;
    Button btn_SaveProfile;
    Button btn_Profile_Pic;
    EditText et_editUsername;
    EditText edt_bio;
    TextView tv_CharCount;

    ActivityResultLauncher<Intent> resultLauncher;


    // currentUser global Variable

    private ActivityResultLauncher<Intent> getPicture;

    public interface UsernameCheckCallback {
        void onResult(boolean isUsernameAvailable);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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


        btn_ProfileDone = findViewById(R.id.btn_ProfileDone);
        btn_SaveProfile = findViewById(R.id.btn_SaveProfile);
        btn_Profile_Pic = findViewById(R.id.btn_profile_pic);
        et_editUsername = findViewById(R.id.et_editUsername);
        edt_bio = findViewById(R.id.edt_Bio);
        tv_CharCount = findViewById(R.id.tv_CharCount);

        et_editUsername.setHint(currentUserName);

        edt_bio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int length = text.length();
                text = (length + "/225");
                tv_CharCount.setText(text);
            }
        });
        btn_ProfileDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_SaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUsername = et_editUsername.getText().toString();
                String strBio = edt_bio.getText().toString();
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

        btn_Profile_Pic.setOnClickListener(new View.OnClickListener() {
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
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
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
}