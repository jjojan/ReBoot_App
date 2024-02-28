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

    //Manual Sign Up Variables
    EditText edtUserName;
    EditText edtEmail;
    EditText edtPassword;
    Button btnSignUp;
    TextView tvLoginLink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtUserName = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        googleImage = findViewById(R.id.google_img);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = edtUserName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if(username.isEmpty()){
                    Log.i("username", username);
                    Toast.makeText(getApplicationContext(), "Username is Required.", Toast.LENGTH_LONG).show();
                }
                else if(email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email is Required.", Toast.LENGTH_LONG).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Password is Required.", Toast.LENGTH_LONG).show();
                }
                else {
                    signInManual(username, email, password);
                    navigateToHomePage();

                }


            }

        });

        googleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){

            updateUI(account);
        } else{

            Toast.makeText(getApplicationContext(), "Users Google info has been saved.", Toast.LENGTH_SHORT).show();
            updateUI(account);
        }

    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    void signInManual(String username, String email, String password ){
        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(getApplicationContext(), "Sign Up Unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        });
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

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

            if(account != null){
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personEmail = account.getEmail();
                String personId = account.getId();

                ParseUser user = new ParseUser();
                user.setUsername(personGivenName);
                user.setEmail(personEmail);
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