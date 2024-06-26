package com.example.rebootapp.Adapters;


import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.rebootapp.Activities.FriendProfileActivity;
import com.example.rebootapp.Activities.SuggestedFriendProfileActivity;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.Models.SuggestedFriendModel;
import com.example.rebootapp.R;

import java.util.List;

public class SuggestedFriendsAdapter extends RecyclerView.Adapter<SuggestedFriendsAdapter.ViewHolder>{
    private List<String> photoUris;
    private List<String> photoUsernames;
    private List<SuggestedFriendModel> suggestedFriendModels;
    Context context;
    private BroadcastReceiver updateReceiver;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfilePhoto;
        TextView tv_username, tv_mutualFriends;


        public ViewHolder(View view) {
            super(view);
            imageViewProfilePhoto = view.findViewById(R.id.imageViewSuggestedProfile);
            tv_username = view.findViewById(R.id.tv_SuggestedFriendUsername);
            tv_mutualFriends = view.findViewById(R.id.tv_SuggestedFriendMutual);
        }


    }
    public SuggestedFriendsAdapter(List<String> photoUris, List<String> Usernames){
        this.photoUris = photoUris;
        this.photoUsernames = Usernames;
    }

    public SuggestedFriendsAdapter(List<SuggestedFriendModel> SuggestedFriendModels, Context context){
        this.suggestedFriendModels = SuggestedFriendModels;
        this.context = context;
        setupBroadcastReceiver();
    }

    @NonNull
    @Override
    public SuggestedFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion_photo, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(SuggestedFriendsAdapter.ViewHolder holder, int position){
        SuggestedFriendModel suggestedFriendModel = suggestedFriendModels.get(position);
        String uri = suggestedFriendModel.getProfilePicUrl();
        if (uri != null) {
            Glide.with(holder.imageViewProfilePhoto.getContext()).load(uri).into(holder.imageViewProfilePhoto);
        } else {
            holder.imageViewProfilePhoto.setBackgroundColor(Color.BLACK);
        }

        holder.tv_username.setText(suggestedFriendModel.getUsername());
//        holder.tv_mutualFriends.setText(suggestedFriendModel.getMutualFriends());
        int num = suggestedFriendModel.getMutualFriends();
        String numString = String.valueOf(num);
        String numString2 = numString + " mutual friends";
        holder.tv_mutualFriends.setText(numString2);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SuggestedFriendProfileActivity.class);
            intent.putExtra("FRIEND_ID", suggestedFriendModel.getObjectId());
            intent.putExtra("FRIEND_SIZE", getItemCount());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return suggestedFriendModels.size();
    }



    public void updateData(List<SuggestedFriendModel> newSuggestedFriendModels) {
        this.suggestedFriendModels.clear();
        this.suggestedFriendModels.addAll(newSuggestedFriendModels);
        notifyDataSetChanged();
    }



    private void removeFriend(String friendId) {
        for (int i = 0; i < suggestedFriendModels.size(); i++) {
            if (suggestedFriendModels.get(i).getObjectId().equals(friendId)) {
                suggestedFriendModels.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        LocalBroadcastManager.getInstance(recyclerView.getContext()).registerReceiver(updateReceiver, new IntentFilter("com.example.UPDATE_ACTION"));
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
        IntentFilter filter = new IntentFilter("com.example.UPDATE_ACTION");
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, filter);
    }





}