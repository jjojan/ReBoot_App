package com.example.rebootapp.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rebootapp.LoginActivity;
import com.example.rebootapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleImage;
    GoogleSignInAccount account;

    //Manual Sign Up Variables
    EditText edtUserName, edtEmail, edtPassword, edtRptPassword;
    Button btnSignUp;
    TextView tvLoginLink;
    Context context;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtUserName = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtRptPassword = findViewById(R.id.edtRptPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        googleImage = findViewById(R.id.google_img);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                    }
                }
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Age");
        builder.setMessage("Are you 13+ years of age?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String username = edtUserName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String reptpassword = edtPassword.getText().toString();
                if(username.isEmpty()){
                    Log.i("username", username);
                    Toast.makeText(getApplicationContext(), "userName is Required.", Toast.LENGTH_LONG).show();
                }
                else if(email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email is Required.", Toast.LENGTH_LONG).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Password is Required.", Toast.LENGTH_LONG).show();
                }
                else if(reptpassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Must enter password twice.", Toast.LENGTH_LONG).show();
                }
                else {
                    signInManual(username, email, password, reptpassword);
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
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            Toast.makeText(getApplicationContext(), "User has not signed up with Google", Toast.LENGTH_SHORT).show();
            updateUI(account);
        } else{

            Toast.makeText(getApplicationContext(), "Users Google info has been saved.", Toast.LENGTH_SHORT).show();
            updateUI(account);
        }

    }


    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        //launcher.launch(signInIntent);
        startActivityForResult(signInIntent, 1000);
    }



    void signInManual(String username, String email, String password, String reptpassword ){
        ParseUser user = new ParseUser();

        isValidEmail(email);

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        if(password.equals(reptpassword)){
            user.setPassword(password);
        }
        else{
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();

        }

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    static boolean isValidEmail(String email){
        if(email.indexOf("@") != -1 && email.indexOf(".com") != -1 && email.indexOf(".net") != -1){
            return true;
        }
        else return false;
    }


    void updateUI(GoogleSignInAccount acct){
        if(acct != null){
            /*String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();*/

            ParseUser user = ParseUser.getCurrentUser();
            /*user.setUsername(personGivenName);
            user.setEmail(personEmail);
            user.setPassword(personId);*/

            ParseQuery<ParseObject> query = ParseQuery.getQuery("User");

            /*Log.i("personGivenName", personGivenName);
            Log.i("id", personId);*/

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
        else{

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
        else{
            Toast.makeText(getApplicationContext(), "Different Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acct = task.getResult(ApiException.class);


        } catch (ApiException e) {
            Log.i("sign", "error");
            Log.w(ContentValues.TAG, "signInResult:failed code=" + e.getStatusCode());
            //Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }

    void navigateToHomePage(){
        finish();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

}