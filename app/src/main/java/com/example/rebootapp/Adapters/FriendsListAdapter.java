package com.example.rebootapp.Adapters;


import androidx.annotation.NonNull;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.rebootapp.Activities.FriendProfileActivity;
import com.example.rebootapp.Activities.SuggestedFriendProfileActivity;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder>{
    private List<String> photoUris;
    private List<String> photoUsernames;
    private List<FriendModel> friendModels;
    Context context;
    private BroadcastReceiver updateReceiver;
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

    public FriendsListAdapter(List<FriendModel> friendModels, Context context){
        this.friendModels = friendModels;
        this.context = context;
        setupBroadcastReceiver();
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
//                                Log.d("ClickedPosition", "Position: " + tposition + ", FriendModel ID: " + friendUserId);
//                                String id = friend.getObjectId();
//                                Intent intent = new Intent(holder.imageViewProfilePhoto.getContext(), FriendProfileActivity.class);
//                                intent.putExtra("FRIEND_ID", id);
//                                holder.imageViewProfilePhoto.getContext().startActivity(intent);
//
//                            } else {
//                                if (e != null) {
//                                    Log.e("QueryFriendError", "Error: " + e.getMessage());
//                                } else {
//                                    Log.e("QueryFriendError", "FriendModel not found");
//                                }
//                            }
//                        }
//                    });
//                } else {
//                    // Handle cases where friendList is null, or the position is out of bounds
//                    Log.e("FriendListError", "FriendModel list is null or position is out of bounds");
//                }
//
//
//            }
//        });
//    }

    @Override
    public void onBindViewHolder(FriendsListAdapter.ViewHolder holder, int position){
        FriendModel friendModel = friendModels.get(position);
        String uri = friendModel.getProfilePicUrl();
        if (uri != null) {
            Glide.with(holder.imageViewProfilePhoto.getContext()).load(uri).into(holder.imageViewProfilePhoto);
        } else {
            holder.imageViewProfilePhoto.setBackgroundColor(Color.BLACK);
        }

        holder.tv_username.setText(friendModel.getUsername());


        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(v.getContext(), FriendProfileActivity.class);
//            intent.putExtra("FRIEND_ID", friendModel.getObjectId());
//            v.getContext().startActivity(intent);
            ParseUser currentUser = ParseUser.getCurrentUser();
            String oid = currentUser.getObjectId();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userId1", oid);
            String fid = friendModel.getObjectId();
            params.put("userId2", fid);
            ParseCloud.callFunctionInBackground("checkFriend", params, new FunctionCallback<HashMap>() {
                @Override
                public void done(HashMap result, ParseException e){
                    if (e == null){
                        try{
                            boolean isFriend = (boolean) result.get("isFriend");

                            if(isFriend){
                                Intent intent = new Intent(v.getContext(), FriendProfileActivity.class);
                                intent.putExtra("FRIEND_ID", friendModel.getObjectId());
                                v.getContext().startActivity(intent);
                            } else {
                                Intent intent = new Intent(v.getContext(), SuggestedFriendProfileActivity.class);
                                intent.putExtra("FRIEND_ID", friendModel.getObjectId());
                                v.getContext().startActivity(intent);
                            }




                        } catch (Exception e2) {
                            System.out.println("null ptr");
                        }

                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return friendModels.size();
    }



    public void updateData(List<FriendModel> newFriendModels) {
        this.friendModels.clear();
        this.friendModels.addAll(newFriendModels);
        notifyDataSetChanged();
    }

    private void removeFriend(String friendId) {
        for (int i = 0; i < friendModels.size(); i++) {
            if (friendModels.get(i).getObjectId().equals(friendId)) {
                friendModels.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        LocalBroadcastManager.getInstance(recyclerView.getContext()).registerReceiver(updateReceiver, new IntentFilter("com.example.UPDATE_FRIEND"));
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        LocalBroadcastManager.getInstance(recyclerView.getContext()).unregisterReceiver(updateReceiver);
    }

    private void setupBroadcastReceiver() {
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getStringExtra("action");
                if ("remove".equals(action)) {
                    String friendId = intent.getStringExtra("friendId");
                    removeFriend(friendId);
                }
            }
        };
        IntentFilter filter = new IntentFilter("com.example.UPDATE_FRIEND");
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, filter);
    }



}
