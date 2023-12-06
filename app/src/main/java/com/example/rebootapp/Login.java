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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.parse.ParseUser;


public class Login extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleImage;
    GoogleSignInAccount account;

    Button btnLogin;
    EditText edtEmail;
    EditText edtPassword;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleImage = findViewById(R.id.google_img_login);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        Button btnSignUp = (Button)findViewById(R.id.btnSignUpLink);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            String username = edtEmail.toString();
            String password = edtPassword.toString();

            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(username)){
                    Toast.makeText(getApplicationContext(), "Username is Required.", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Password is Required.", Toast.LENGTH_LONG).show();
                }
                else {
                    logInManual(username, password);
                    navigateToHomePage();

                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
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

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            //If GoogleSignIn.getLastSignedInAccount returns null, the user has not yet signed in to your app with Google. Update your UI to display the Google Sign-in button.
            //updateUI(account);
            //Go to SignUp
        } else{
            //Update your UI accordingly—that is, hide the sign-in button, launch your main activity, or whatever is appropriate for your app.
            //updateUI(account);
        }

    }

    void logIn(){
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 1000);
    }

    void logInManual(String username, String password){

        ParseUser.logInInBackground(username, password, (parseUser, e) -> {
            //progressDialog.dismiss();
            if (parseUser != null) {
                //showAlert("Successful Login", "Welcome back " + username + " !");
                Toast.makeText(Login.this, "Login Successful.", Toast.LENGTH_LONG).show();

            } else {
                ParseUser.logOut();
                Toast.makeText(Login.this, "Login Unsuccesful.", Toast.LENGTH_LONG).show();
            }
        });
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

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            navigateToHomePage();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(ContentValues.TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            //updateUI(null);
        }
    }

    void navigateToHomePage(){
        finish();
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }


}