package com.example.rebootapp;

import static com.parse.Parse.getApplicationContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    Button btnSignOut;
    GoogleSignInAccount account;
    Session session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignOut = view.findViewById(R.id.btnSignOut);

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


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }



}