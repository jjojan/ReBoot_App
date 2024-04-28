package com.example.rebootapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class YourReviewAdapter extends RecyclerView.Adapter<YourReviewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<YourReviewModel> reviewList;

    // Constructor
    public YourReviewAdapter(Context context, ArrayList<YourReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvUserName, tvReviewText, tvUp, tvDown,tvDate;
        RatingBar ratingBar;
        View view;

        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            tvUp = itemView.findViewById(R.id.tvUp); // ID should be unique for "up" TextView, assuming tvUp as ID here
            tvDown = itemView.findViewById(R.id.tvDown);
            tvDate = itemView.findViewById(R.id.tvDate);
            ratingBar = itemView.findViewById(R.id.ratingbarRecyclerView);
            view=itemView.getRootView();
            imageView = itemView.findViewById(R.id.imageView);
            btnDelete = itemView.findViewById(R.id.btn_remove);

        }
    }
    public void updateData(ArrayList<YourReviewModel> newData) {
        this.reviewList = newData;
        notifyDataSetChanged();
    }

    @Override
    public YourReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youreview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(YourReviewAdapter.ViewHolder holder, int position) {
        YourReviewModel review = reviewList.get(position);
        // Assuming you have a way to get user's profile image, or use a default one
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
        holder.btnDelete.setOnClickListener(view -> deleteReview(position));

        String uri = review.getPhoto_url();
        if (uri != null) {
            Glide.with(holder.imageView.getContext()).load(uri).into(holder.imageView);
        } else {
            holder.imageView.setBackgroundColor(Color.BLACK);
        }

        holder.imageView.setOnClickListener(v-> {
            ParseUser currentUser = ParseUser.getCurrentUser();
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
    private void sendUpVote(int position) {
        // Query the Review table for the specific review using the objectId
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.getInBackground(reviewList.get(position).getObjectId(), (review, e) -> {
            if (e == null) {
                // Successfully retrieved the review
                // Try to get the voterArrayList with the correct type
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

                // Check if the current user has already voted
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
                                // User has already voted, show a toast message
                                Toast.makeText(context, "You have already voted.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                }

                if (!hasVoted) {
                    // User hasn't voted yet, proceed with upvote
                    int upCount = review.getInt("upCount");
                    int downCount = review.getInt("downCount");
                    review.put("upCount", upCount + 1);
                    reviewList.get(position).setUpCount(upCount + 1);
                    notifyDataSetChanged();
                    // Add the user to the voterArrayList with an upvote
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
        YourReviewModel review = reviewList.get(position);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(review.getReviewUserName());




        String message = "Date: " + formatDate(review.getUpdatedAt()) + "\n" +
                "Total Upvotes: " + review.getUpCount() + "\n" +
                "Total Downvotes: " + review.getDownCount() + "\n" +
                "Rating: " + review.getRatingStar() + "\n" +
                "Comment: " + review.getReviewText();

        dialogBuilder.setMessage(message);

        dialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    public  String formatDate(Date date) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMMM/yyyy HH:mm",Locale.getDefault());
        return formatter.format(date);
    }
    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    public void deleteReview(int position) {

        YourReviewModel review = reviewList.get(position);

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
}
