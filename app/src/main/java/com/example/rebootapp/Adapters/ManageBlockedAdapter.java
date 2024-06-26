package com.example.rebootapp.Adapters;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rebootapp.Activities.BlockedUserProfileActivity;
import com.example.rebootapp.Activities.SuggestedFriendProfileActivity;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.Models.SuggestedFriendModel;
import com.example.rebootapp.R;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rebootapp.Models.UserListModel;
import com.example.rebootapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ManageBlockedAdapter extends RecyclerView.Adapter<ManageBlockedAdapter.ViewHolder>{

    private LayoutInflater mInflater;
    private List<SuggestedFriendModel> suggestedFriendModels;
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

    public ManageBlockedAdapter(List<SuggestedFriendModel> list, Context context){
        this.suggestedFriendModels = list;
        this.context = context;
        setupBroadcastReceiver();
    }

    @NonNull
    @Override
    public ManageBlockedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profilephoto, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ManageBlockedAdapter.ViewHolder holder, int position){
        SuggestedFriendModel sfm = suggestedFriendModels.get(position);
        String uri = sfm.getProfilePicUrl();
        if (uri != null) {
            Glide.with(holder.imageViewProfilePhoto.getContext()).load(uri).into(holder.imageViewProfilePhoto);
        } else {
            holder.imageViewProfilePhoto.setBackgroundColor(Color.BLACK);
        }
        holder.tv_username.setText(sfm.getUsername());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BlockedUserProfileActivity.class);
            intent.putExtra("FRIEND_ID", sfm.getObjectId());
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount(){
        return suggestedFriendModels.size();
    }

    public void updateData(List<SuggestedFriendModel> newFriendModels) {
        this.suggestedFriendModels.clear();
        this.suggestedFriendModels.addAll(newFriendModels);
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
        LocalBroadcastManager.getInstance(recyclerView.getContext()).registerReceiver(updateReceiver, new IntentFilter("com.example.UPDATE_UNBLOCK"));
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
        IntentFilter filter = new IntentFilter("com.example.UPDATE_UNBLOCK");
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, filter);
    }
}
