package com.example.rebootapp.Utilities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rebootapp.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView imageViewProfilePhoto;
    TextView tv_username;

    public ViewHolder(View view) {
        super(view);
        imageViewProfilePhoto = view.findViewById(R.id.imageViewProfile);
        tv_username = view.findViewById(R.id.tv_FriendUsername);
    }
}