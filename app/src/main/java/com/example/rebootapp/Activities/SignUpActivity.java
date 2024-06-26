package com.example.rebootapp.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rebootapp.LoginActivity;
import com.example.rebootapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleImage;
    GoogleSignInAccount account;

    //Manual Sign Up Variables
    EditText edtUserName;
    EditText edtEmail;
    EditText edtPassword, edtRptPassword;
    Button btnSignUp;
    TextView tvLoginLink;
    Context context;

    CheckBox modCheckbox;


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
        modCheckbox = findViewById(R.id.checkBox);

        googleImage = findViewById(R.id.google_img);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

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
                finish();
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
                String password2 = edtRptPassword.getText().toString();
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
                else {
                    signInManual(username, email, password, password2);
//                    navigateToHomePage();

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

    void signInManual(String username, String email, String password, String passwordConfirm ){

        if(!isValidEmail2(email)){
            Toast.makeText(getApplicationContext(), "Invalid Email.", Toast.LENGTH_LONG).show();
            return;
        }
        if(!password.equals(passwordConfirm)){
            Toast.makeText(getApplicationContext(), "Passwords Must Match", Toast.LENGTH_LONG).show();
            return;
        }

        ParseQuery<ParseUser> users = ParseUser.getQuery();
        users.whereEqualTo("username", username);
        users.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(e == null) {
                    if (!list.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Username taken.", Toast.LENGTH_LONG).show();
                    } else {
                        ParseUser user = new ParseUser();
                        user.setUsername(username);
                        user.setEmail(email);
                        user.setPassword(password);

                        Log.i("modCheck",String.valueOf(modCheckbox.isChecked()) );

                        if(modCheckbox.isChecked()){
                            user.put("isMod", true);
                            Log.i("modCheck",  "true--");
                        }
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_LONG).show();
                                    navigateToHomePage();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Sign Up Unsuccessful", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
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

                Log.i("modCheck",String.valueOf(modCheckbox.isChecked()) );

                if(modCheckbox.isChecked()){
                    user.put("isMod", true);
                    Log.i("modCheck",  "true--");
                }

//                modCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                       if (isChecked){
//                           user.put("isMod", true);
//                           Log.i("mod", )
//                       }
//                    }
//                });

                ParseQuery<ParseObject> query = ParseQuery.getQuery("User");

                Log.i("personGivenName", personGivenName);
                Log.i("id", personId);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(getApplicationContext(), "User Signed In", Toast.LENGTH_SHORT).show();
                            navigateToHomePage();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Failed to Sign In User.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    static boolean isValidEmail2(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}