package com.example.rebootapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleImage;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        googleImage = findViewById(R.id.google_img);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        googleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            //If GoogleSignIn.getLastSignedInAccount returns null, the user has not yet signed in to your app with Google. Update your UI to display the Google Sign-in button.
            updateUI(account);
            Toast.makeText(getApplicationContext(), "User has not yet signed into ReBoot with Google", Toast.LENGTH_SHORT).show();
        } else{
            //Update your UI accordingly—that is, hide the sign-in button, launch your main activity, or whatever is appropriate for your app.
            Toast.makeText(getApplicationContext(), "Users Google info has been saved.", Toast.LENGTH_SHORT).show();
            updateUI(account);
        }

    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    void updateUI(GoogleSignInAccount account){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            Log.i("sign", "signup");
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.i("sign", String.valueOf(account));

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

            if(acct != null){
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personEmail = account.getEmail();
                String personId = account.getId();

                ParseUser user = new ParseUser();
                user.setUsername(personGivenName);
                user.setPassword(personId);
                user.setEmail(personEmail);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("User");

                Log.i("personGivenName", personGivenName);
                Log.i("id", personId);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(getApplicationContext(), "User Signed In", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Failed to Sign In User.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                navigateToHomePage();
            }
        } catch (ApiException e) {
            Log.i("sign", "error");
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(ContentValues.TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }

    void navigateToHomePage(){
        finish();
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        startActivity(intent);
    }
}