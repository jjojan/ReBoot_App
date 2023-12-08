package com.example.rebootapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
public class EditProfileActivity extends AppCompatActivity {

    Button btn_ProfileDone;
    Button btn_SaveProfile;
    Button Save;
    EditText et_editUsername;


    // currentUser global Variable



    public interface UsernameCheckCallback {
        void onResult(boolean isUsernameAvailable);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserObjectID = currentUser.getObjectId();


        btn_ProfileDone = findViewById(R.id.btn_ProfileDone);
        btn_SaveProfile = findViewById(R.id.btn_SaveProfile);
        et_editUsername = findViewById(R.id.et_editUsername);


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
                checkUsername(strUsername, new UsernameCheckCallback() {
                    @Override
                    public void onResult(boolean isUsernameAvailable) {
                        if (isUsernameAvailable) {
                            updateUsername(currentUserObjectID, strUsername);
                        }
                    }
                });


            }
        });


    }

    public void checkUsername(String str, UsernameCheckCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("username", str);

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null)
                    callback.onResult(count == 0);
                else
                    callback.onResult(false);
            }
        });

    }

    public void updateUsername(String objectID, String newUsername) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(objectID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    object.setUsername(newUsername);
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
}