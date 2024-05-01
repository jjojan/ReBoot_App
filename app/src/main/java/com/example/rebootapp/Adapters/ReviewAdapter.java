package com.example.rebootapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rebootapp.Activities.FriendProfileActivity;
import com.example.rebootapp.Activities.MainActivity;
import com.example.rebootapp.Activities.SuggestedFriendProfileActivity;
import com.example.rebootapp.Models.ReviewModel;
import com.example.rebootapp.Models.YourReviewModel;
import com.example.rebootapp.R;
import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ReviewModel> reviewList;

    // Constructor
    public ReviewAdapter(Context context, ArrayList<ReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvUserName, tvReviewText, tvUp, tvDown,tvDate, reportMessage, reportNumber;
        RatingBar ratingBar;
        View view;
        ImageButton btnDelete;



        public ViewHolder(View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            tvUp = itemView.findViewById(R.id.tvUp);
            tvDown = itemView.findViewById(R.id.tvDown);
            tvDate = itemView.findViewById(R.id.tvDate);
            ratingBar = itemView.findViewById(R.id.ratingbarRecyclerView);
            view=itemView.getRootView();
            imageView = itemView.findViewById(R.id.imageView);
            btnDelete = itemView.findViewById(R.id.btn_deleteReview);
            reportMessage = itemView.findViewById(R.id.tvReport);
            reportNumber = itemView.findViewById(R.id.tvReportNum);
        }
    }
    public void updateData(ArrayList<ReviewModel> newData) {
        this.reviewList = newData;
        notifyDataSetChanged();
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);

        if (review.getRatingStar()>0) {
            holder.ratingBar.setRating(review.getRatingStar());
        }
        holder.tvUserName.setText(review.getReviewUserName());
        holder.tvReviewText.setText(review.getReviewText());
        holder.tvUp.setText(review.getUpCount()+"");
        holder.tvDown.setText(review.getDownCount()+"");
        holder.tvDate.setText(formatDate(review.getUpdatedAt()));
//        holder.tvUp.setOnClickListener(view -> sendUpVote(position));
//        holder.tvDown.setOnClickListener(view -> sendDownVote(position));
        holder.tvUp.setOnClickListener(view -> sendVote(position, "up"));
        holder.tvDown.setOnClickListener(view -> sendVote(position, "down"));
        holder.view.setOnClickListener(view -> showDialog(position));
        int reportNum = review.getreportNum();
        String reportString = String.valueOf(reportNum);
        holder.reportNumber.setText(reportString);

        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i("deleting", currentUser.getObjectId());
        Boolean isMod = currentUser.getBoolean("isMod");
        Log.i("deleting", isMod.toString());
        if (isMod){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.reportMessage.setVisibility(View.VISIBLE);
            holder.reportNumber.setVisibility(View.VISIBLE);
        }

        holder.btnDelete.setOnClickListener(view -> deleteReview(position));

        String uri = review.getPhoto_url();
        if (uri != null) {
            Glide.with(holder.imageView.getContext()).load(uri).into(holder.imageView);
        } else {
            holder.imageView.setBackgroundColor(Color.BLACK);
        }

        holder.imageView.setOnClickListener(v-> {
            String oid = currentUser.getObjectId();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userId1", oid);
            String fid = review.getReviewUser();
            params.put("userId2", fid);
            if(oid.equals(fid)){
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("openFragment", "profile");
                v.getContext().startActivity(intent);
            }
            else {
                ParseCloud.callFunctionInBackground("checkFriend", params, new FunctionCallback<HashMap>() {
                    @Override
                    public void done(HashMap result, ParseException e) {
                        if (e == null) {


                            try {
                                boolean isFriend = (boolean) result.get("isFriend");

                                if (isFriend) {
                                    Intent intent = new Intent(v.getContext(), FriendProfileActivity.class);
                                    intent.putExtra("FRIEND_ID", review.getReviewUser());
                                    v.getContext().startActivity(intent);
                                } else {
                                    Intent intent = new Intent(v.getContext(), SuggestedFriendProfileActivity.class);
                                    intent.putExtra("FRIEND_ID", review.getReviewUser());
                                    v.getContext().startActivity(intent);
                                }


                            } catch (Exception e2) {
                                System.out.println("null ptr");
                            }

                        }
                    }
                });
            }
        });
    }

    public void deleteReview(int position) {

        ReviewModel review = reviewList.get(position);

        String objectID = review.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("objectId", objectID);

        query.getInBackground(objectID, new GetCallback<ParseObject>() {
            public void done(ParseObject review, ParseException e) {
                if (e == null) {
                    Log.i("delte", review.getObjectId());
                    review.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                reviewList.remove(position);
                                notifyDataSetChanged();
                                // Review deleted successfully
                                // Update UI or data structures as needed
                                // Optionally, provide user feedback (e.g., Toast message)
                            } else {
                                // Handle deletion error (e.g., log the error, display user-friendly message)
                                Log.e("ReviewDeletion", "Error deleting review: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.i("delte", "error");
                }
            }
        });



    }

    private void sendUpVote(int position) {
        // Query the Review table for the specific review using the objectId
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.getInBackground(reviewList.get(position).getObjectId(), (review, e) -> {
            if (e == null) {

                List<Object> rawList = review.getList("voterArrayList");
                ArrayList<HashMap<String, Object>> voterArrayList = new ArrayList<>();
                if (rawList != null) {
                    for (Object item : rawList) {
                        if (item instanceof HashMap) {
                            HashMap<String, Object> voterMap = (HashMap<String, Object>) item;
                            voterArrayList.add(voterMap);
                        }
                    }
                }
                String currentUserId = ParseUser.getCurrentUser().getObjectId();
                boolean hasVoted = false;

                if (voterArrayList != null) {
                    for (HashMap<String, Object> voter : voterArrayList) {
                        String userID = (String) voter.get("userID");
                        String gameVote = (String) voter.get("gameVote");
                        if (userID != null && userID.equals(currentUserId)) {
                            hasVoted = true;
                            if ("-1".equals(gameVote)) {
                                // User has previously downvoted, allow upvote
                                int upCount = review.getInt("upCount");
                                int downCount = review.getInt("downCount");
                                review.put("upCount", upCount + 1);
                                review.put("downCount", downCount - 1);
                                review.saveInBackground();
                                reviewList.get(position).setUpCount(upCount + 1);
                                reviewList.get(position).setDownCount(downCount - 1);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Upvote successful!", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(context, "You have already voted.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                }

                if (!hasVoted) {

                    int upCount = review.getInt("upCount");
                    int downCount = review.getInt("downCount");
                    review.put("upCount", upCount + 1);
                    reviewList.get(position).setUpCount(upCount + 1);
                    notifyDataSetChanged();

                    if (voterArrayList == null) voterArrayList = new ArrayList<>();
                    HashMap<String, Object> voteInfo = new HashMap<>();
                    voteInfo.put("userID", currentUserId);
                    voteInfo.put("gameVote", "+1");
                    voterArrayList.add(voteInfo);
                    review.put("voterArrayList", voterArrayList);
                    review.saveInBackground(e1 -> {
                        if (e1 == null) {
                            Toast.makeText(context, "Upvote successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle error
                            Toast.makeText(context, "Error saving upvote: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                // Error retrieving the review
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendVote(int position, String upDown){
        String oid = ParseUser.getCurrentUser().getObjectId();
        String rid = reviewList.get(position).getObjectId();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("currentUserId", oid);
        params.put("reviewId", rid);
        params.put("vote", upDown);
        ParseCloud.callFunctionInBackground("addVote", params, new FunctionCallback<HashMap<String,Object>>() {
            @Override
            public void done(HashMap<String, Object> result, ParseException e){
                if (e == null){
                    try {

                        String msg = (String) result.get("message");
                        int upvote = (int) result.get("upvotes");
                        int downvote = (int) result.get("downvotes");

                        int absCount = Math.abs(upvote - downvote);
                        if (upvote >= downvote){
                            upvote = absCount;
                            downvote = 0;
                        }
                        else{
                            downvote = absCount;
                            upvote = 0;
                        }
                        reviewList.get(position).setDownCount(downvote);
                        reviewList.get(position).setUpCount(upvote);
                        notifyItemChanged(position);
                        System.out.println(msg + " " + upvote + " " + downvote);
//                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();


                    } catch (Error e2){
                        System.out.println("Null return on sendvote");
                    }
                }
            }
        });

    }
    private void sendDownVote(int position) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.getInBackground(reviewList.get(position).getObjectId(), (review, e) -> {
            if (e == null) {
                List<Object> rawList = review.getList("voterArrayList");
                ArrayList<HashMap<String, Object>> voterArrayList = new ArrayList<>();
                if (rawList != null) {
                    for (Object item : rawList) {
                        if (item instanceof HashMap) {
                            voterArrayList.add((HashMap<String, Object>) item);
                        }
                    }
                }
                String currentUserId = ParseUser.getCurrentUser().getObjectId();
                boolean hasVoted = false;

                for (HashMap<String, Object> voter : voterArrayList) {
                    String userID = (String) voter.get("userID");
                    String gameVote = (String) voter.get("gameVote");
                    if (userID != null && userID.equals(currentUserId)) {
                        hasVoted = true;
                        if ("+1".equals(gameVote)) {
                            int upCount = review.getInt("upCount");
                            int downCount = review.getInt("downCount");
                            review.put("upCount", upCount - 1);
                            review.put("downCount", downCount + 1);
                            HashMap<String, Object> updatedVoteInfo = new HashMap<>();
                            updatedVoteInfo.put("userID", currentUserId);
                            updatedVoteInfo.put("gameVote", "-1");
                            voterArrayList.remove(voter);
                            voterArrayList.add(updatedVoteInfo);
                            review.put("voterArrayList", voterArrayList);
                            review.saveInBackground();
                            reviewList.get(position).setUpCount(upCount - 1);
                            reviewList.get(position).setDownCount(downCount + 1);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Downvote successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "You have already voted.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }

                if (!hasVoted) {
                    int downCount = review.getInt("downCount");
                    review.put("downCount", downCount + 1);
                    HashMap<String, Object> newVoteInfo = new HashMap<>();
                    newVoteInfo.put("userID", currentUserId);
                    newVoteInfo.put("gameVote", "-1");
                    voterArrayList.add(newVoteInfo);
                    review.put("voterArrayList", voterArrayList);
                    review.saveInBackground(e1 -> {
                        if (e1 == null) {
                            reviewList.get(position).setDownCount(downCount + 1);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Downvote successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error saving downvote: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDialog(int position) {
        ReviewModel review = reviewList.get(position);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(review.getReviewUserName());




        String message = "Date: " + formatDate(review.getUpdatedAt()) + "\n" +
                "Total Upvotes: " + review.getUpCount() + "\n" +
                "Total Downvotes: " + review.getDownCount() + "\n" +
                "Rating: " + review.getRatingStar() + "\n" +
                "Comment: " + review.getReviewText();

        dialogBuilder.setMessage(message);

//        dialogBuilder.setPositiveButton("Report", (dialog, which) -> dialog.dismiss());



        dialogBuilder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("report", "repoeted");
                        reportReview(position);
                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    public void reportReview(int position) {

        ReviewModel review = reviewList.get(position);

        String objectID = review.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("objectId", objectID);

        query.getInBackground(objectID, new GetCallback<ParseObject>() {
            public void done(ParseObject review, ParseException e) {
                if (e == null) {
                    Log.i("report", review.getObjectId());
                    int currentReportNumber = review.getInt("reportNumber");

                    currentReportNumber++;
                    review.put("reportNumber", currentReportNumber);

                    review.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // ReportNumber field updated successfully
                                Log.d("Parse", "ReportNumber increased by one");
                            } else {
                                // Error occurred while saving the object
                                Log.e("Parse", "Error updating reportNumber: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.i("report", "error");
                }
            }
        });
    }

    public  String formatDate(Date date) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMMM/yyyy HH:mm",Locale.getDefault());
        return formatter.format(date);
    }
    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
