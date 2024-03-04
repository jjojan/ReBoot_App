package com.example.rebootapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ToggleButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

import okhttp3.Headers;
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder>{
    private List<String> photoUris;
    private List<String> photoUsernames;
    private List<Friend> friends;
    Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfilePhoto;
        TextView tv_username;


        public ViewHolder(View view) {
            super(view);
            imageViewProfilePhoto = view.findViewById(R.id.imageViewProfile);
            tv_username = view.findViewById(R.id.tv_FriendUsername);

        }


    }
    public FriendsListAdapter(List<String> photoUris, List<String> Usernames){
        this.photoUris = photoUris;
        this.photoUsernames = Usernames;
    }

    public FriendsListAdapter(List<Friend> friends){
        this.friends = friends;
    }

    @NonNull
    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profilephoto, parent, false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(FriendsListAdapter.ViewHolder holder, int position) {
//        int tposition = position;
//        String uri = photoUris.get(tposition);
//        if(uri != null) {
//            Glide.with(holder.imageViewProfilePhoto.getContext()).load(uri).into(holder.imageViewProfilePhoto);
//        }
//        else{
//            holder.imageViewProfilePhoto.setBackgroundColor(Color.BLACK);
//        }
//
//        String tmpUsr = photoUsernames.get(tposition);
//        holder.tv_username.setText(tmpUsr);
//
//        holder.imageViewProfilePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                ParseUser currentUser = ParseUser.getCurrentUser();
//                List<String> friendList = currentUser.getList("friend_list");
//
//                if (friendList != null && tposition >= 0 && tposition < friendList.size()) {
//                    String friendUserId = friendList.get(tposition);
//
//
//                    ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
//                    friendQuery.whereEqualTo("objectId", friendUserId);
//                    friendQuery.getFirstInBackground(new GetCallback<ParseUser>() {
//                        @Override
//                        public void done(ParseUser friend, ParseException e) {
//                            if (e == null && friend != null) {
//                                Log.d("ClickedPosition", "Position: " + tposition + ", Friend ID: " + friendUserId);
//                                String id = friend.getObjectId();
//                                Intent intent = new Intent(holder.imageViewProfilePhoto.getContext(), FriendProfileActivity.class);
//                                intent.putExtra("FRIEND_ID", id);
//                                holder.imageViewProfilePhoto.getContext().startActivity(intent);
//
//                            } else {
//                                if (e != null) {
//                                    Log.e("QueryFriendError", "Error: " + e.getMessage());
//                                } else {
//                                    Log.e("QueryFriendError", "Friend not found");
//                                }
//                            }
//                        }
//                    });
//                } else {
//                    // Handle cases where friendList is null, or the position is out of bounds
//                    Log.e("FriendListError", "Friend list is null or position is out of bounds");
//                }
//
//
//            }
//        });
//    }

    @Override
    public void onBindViewHolder(FriendsListAdapter.ViewHolder holder, int position){
        Friend friend = friends.get(position);
        String uri = friend.getProfilePicUrl();
        if (uri != null) {
            Glide.with(holder.imageViewProfilePhoto.getContext()).load(uri).into(holder.imageViewProfilePhoto);
        } else {
            holder.imageViewProfilePhoto.setBackgroundColor(Color.BLACK);
        }

        holder.tv_username.setText(friend.getUsername());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FriendProfileActivity.class);
            intent.putExtra("FRIEND_ID", friend.getObjectId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }



    public void updateData(List<Friend> newFriends) {
        this.friends.clear();
        this.friends.addAll(newFriends);
        notifyDataSetChanged();
    }



}
