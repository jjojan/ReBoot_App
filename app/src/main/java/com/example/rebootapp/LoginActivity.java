package com.example.rebootapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rebootapp.Activities.SignUpActivity;
import com.example.rebootapp.Activities.SplashScreenActivity;
import com.example.rebootapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleImage;
    GoogleSignInAccount account;

    Button btnLogin;
    TextView btnSignUp;
    EditText edtEmail;
    EditText edtPassword;
    @Override
    protected void onStart() {
        super.onStart();

        // Check if the current user is already logged in
        if (ParseUser.getCurrentUser() != null) {
            // User is logged in, navigate to the MainActivity
            navigateToHomePage();
        }
        // No else part needed, if no user is logged in, stay on the LoginActivity screen
    }
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin); //LoginActivity Button
        btnSignUp = findViewById(R.id.btnSignUpLink); //Sign Up Link
        googleImage = findViewById(R.id.google_img_login); //Google LoginActivity

        //Create Google Client
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        //User info for Manual Setup
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        //Manual LoginActivity Button onClickListener
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if(TextUtils.isEmpty(username)){
                    Toast.makeText(getApplicationContext(), "userName is Required.", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Password is Required.", Toast.LENGTH_LONG).show();

                }
                else if(TextUtils.isEmpty(username)&TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "userName and Password is Required.", Toast.LENGTH_LONG).show();
                }
                else{
                    logInManual(username, password);
                }

            }
        });

        //SignUpActivity Link
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }

        });


        googleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        account = GoogleSignIn.getLastSignedInAccount(this);
//        if(account == null){
//            //If GoogleSignIn.getLastSignedInAccount returns null, the user has not yet signed in to your app with Google. Update your UI to display the Google Sign-in button.
//            //updateUI(account);
//            //Go to SignUpActivity
//        } else{
//            //Update your UI accordingly—that is, hide the sign-in button, launch your main activity, or whatever is appropriate for your app.
//            //updateUI(account);
//        }
//
//    }

    void logIn(){
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 1000);
    }

    boolean logInManual(String username, String password){

        Log.i("Before LoginActivity", "Attempting to log in user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Log.e("After LoginActivity", "Error with LoginActivity");
                    System.out.println(e.toString());
                    return;
                }
                else{
                    //System.out.println(e.toString());
                }
                navigateToHomePage();
                Toast.makeText(LoginActivity.this, "Sucess", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
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
            GoogleSignInAccount account = task.getResult(ApiException.class);


            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

            String email = acct.getGivenName();
            String password = acct.getId();
            Log.i("Google Email", email);
            Log.i("Google Password", password);
            logInManual(email, password);

        } catch (ApiException e) {

            Log.w(ContentValues.TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

        }
    }

    void navigateToHomePage(){
        finish();
        Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
        startActivity(intent);
    }


}