package com.example.rebootapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rebootapp.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileFragment2 extends Fragment {

    ShapeableImageView ProfilePic;
    ImageButton EditProfileBtn, CustomListBtn;
    TextView Username, Email, BioContent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile2, container, false);

        ProfilePic = view.findViewById(R.id.ProfilePic);
        EditProfileBtn = view.findViewById(R.id.EditProfileButton);
        CustomListBtn = view.findViewById(R.id.customList);
        Username = view.findViewById(R.id.tvUser_Username);
        Email = view.findViewById(R.id.tvUser_Email);
        BioContent = view.findViewById(R.id.bioContent);
        CustomListBtn = view.findViewById(R.id.customList);


        CustomListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile2, container, false);

    }
}