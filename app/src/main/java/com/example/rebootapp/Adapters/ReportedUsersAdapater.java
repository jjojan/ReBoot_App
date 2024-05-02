package com.example.rebootapp.Adapters;

import static com.parse.Parse.getApplicationContext;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rebootapp.Activities.BlockedUserProfileActivity;
import com.example.rebootapp.Activities.FriendProfileActivity;
import com.example.rebootapp.Activities.SuggestedFriendProfileActivity;
import com.example.rebootapp.Models.FriendModel;
import com.example.rebootapp.Models.ReportedUsersModel;
import com.example.rebootapp.Models.ReviewModel;
import com.example.rebootapp.Models.SuggestedFriendModel;
import com.example.rebootapp.R;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rebootapp.Models.UserListModel;
import com.example.rebootapp.R;
import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportedUsersAdapater extends RecyclerView.Adapter<ReportedUsersAdapater.ViewHolder>{
    private LayoutInflater mInflater;
    private List<ReportedUsersModel> reportedUsersModel;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfilePhoto;
        TextView tv_username;

        TextView tvReports;

        ImageButton deleteUser;



        public ViewHolder(View view) {
            super(view);
            imageViewProfilePhoto = view.findViewById(R.id.imageViewProfile);
            tv_username = view.findViewById(R.id.tv_FriendUsername);
            tvReports = view.findViewById(R.id.tvReportUser);
            deleteUser = view.findViewById(R.id.btn_deleteUser);

        }


    }
    public ReportedUsersAdapater(List<ReportedUsersModel> list){
        this.reportedUsersModel = list;
    }

    @NonNull
    @Override
    public ReportedUsersAdapater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reporteduser, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportedUsersAdapater.ViewHolder holder, int position){
        ReportedUsersModel reportedUsers = reportedUsersModel.get(position);
        String uri = reportedUsers.getProfilePicUrl();
        if (uri != null) {
            Glide.with(holder.imageViewProfilePhoto.getContext()).load(uri).into(holder.imageViewProfilePhoto);
        } else {
            holder.imageViewProfilePhoto.setBackgroundColor(Color.BLACK);
        }
        holder.tv_username.setText(reportedUsers.getUsername());
        int reportNum = reportedUsers.getreportNum();
        String reportString = "Reports: " + String.valueOf(reportNum);
        Log.i("userRreport", reportedUsers.getUsername().toString());
        holder.tvReports.setText(reportString);

        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i("deleting", currentUser.getObjectId());
        Boolean isMod = currentUser.getBoolean("isMod");
        Log.i("deleting", isMod.toString());
        if (isMod){
            holder.tvReports.setVisibility(View.VISIBLE);
            holder.deleteUser.setVisibility(View.VISIBLE);

        }

        holder.deleteUser.setOnClickListener(view -> deleteUser(position));


        holder.itemView.setOnClickListener(v -> {
            String oid = currentUser.getObjectId();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userId1", oid);
            String fid = reportedUsers.getObjectId();
            params.put("userId2", fid);
            ParseCloud.callFunctionInBackground("checkFriend", params, new FunctionCallback<HashMap>() {
                @Override
                public void done(HashMap result, ParseException e){
                    if (e == null){
                        try{
                            boolean isFriend = (boolean) result.get("isFriend");

                            if(isFriend){
                                Intent intent = new Intent(v.getContext(), FriendProfileActivity.class);
                                intent.putExtra("FRIEND_ID", reportedUsers.getObjectId());
                                v.getContext().startActivity(intent);
                            } else {
                                Intent intent = new Intent(v.getContext(), SuggestedFriendProfileActivity.class);
                                intent.putExtra("FRIEND_ID", reportedUsers.getObjectId());
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

    public void deleteUser(int position) {

        ReportedUsersModel sfm = reportedUsersModel.get(position);

        String objectID = sfm.getObjectId();

        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", objectID);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", objectID);

        ParseCloud.callFunctionInBackground("deleteUser", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException e) {
                if (e == null) {

                    Log.d("Cloud Function", "User deleted: " + response.toString());
                    reportedUsersModel.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "User has been banned!", Toast.LENGTH_LONG).show();
                } else {

                    Log.e("Cloud", "Error deleting " + e.getMessage());
                }
            }
        });


//        query.getInBackground(objectID, new GetCallback<ParseUser>() {
//            public void done(ParseUser user, ParseException e) {
//                if (e == null) {
//                    Log.i("delte", sfm.getObjectId());
//                    user.deleteInBackground(new DeleteCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                reportedUsersModel.remove(position);
//                                notifyDataSetChanged();
//                                // Review deleted successfully
//                                // Update UI or data structures as needed
//                                // Optionally, provide user feedback (e.g., Toast message)
//                            } else {
//                                // Handle deletion error (e.g., log the error, display user-friendly message)
//                                Log.e("SSERDelte", "Error deleting review: " + e.getMessage());
//                            }
//                        }
//                    });
//                } else {
//                    Log.e("userdelte", "id:  " + objectID);
//                    Log.i("userdelte", "error");
//                }
//            }
//        });
    }

    @Override
    public int getItemCount(){
        return reportedUsersModel.size();
    }

    public void updateData(List<ReportedUsersModel> newRepotedUsers) {
        int c = getItemCount();
        this.reportedUsersModel.clear();
        notifyItemRangeRemoved(0, c);
        this.reportedUsersModel.addAll(newRepotedUsers);
        notifyItemRangeInserted(0, reportedUsersModel.size());
    }

    public void updateClear(int size){
        if(size <= 0) size = 0;
        this.reportedUsersModel.clear();
        notifyItemRangeRemoved(0, size);
    }
}
