package com.example.rebootapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rebootapp.Activities.GameDetailsActivity;
import com.example.rebootapp.Adapters.GameAdapter;
import com.example.rebootapp.Adapters.ReportedUsersAdapater;
import com.example.rebootapp.Adapters.ReviewAdapter;
import com.example.rebootapp.Adapters.SearchUserAdapter;
import com.example.rebootapp.Adapters.YourReviewAdapter;
import com.example.rebootapp.GameModel;
import com.example.rebootapp.Models.ReportedUsersModel;
import com.example.rebootapp.Models.ReviewModel;
import com.example.rebootapp.Models.SuggestedFriendModel;
import com.example.rebootapp.Models.YourReviewModel;
import com.example.rebootapp.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    com.example.rebootapp.databinding.FragmentActivityBinding binding;
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    Boolean isMod;

    ArrayList<ReviewModel> reviewModel;

    ArrayList<YourReviewModel> yourreviewModel;

    List<ReportedUsersModel> sfm;

    ReviewAdapter reviewAdapter;

    YourReviewAdapter yourreviewAdapter;

    ReportedUsersAdapater searchUserAdapter;

    SwitchMaterial switchMaterial;
    List<String> friendList = new ArrayList<>();

    TextView allbutton, friendsButton, yoursButton;

    ImageButton deleteReview;



    public ActivityFragment() {

    }

    public static ActivityFragment newInstance(String param1, String param2) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = com.example.rebootapp.databinding.FragmentActivityBinding.inflate(getLayoutInflater());
        ParseUser currentUser = ParseUser.getCurrentUser();
        isMod = currentUser.getBoolean("isMod");
        allbutton = binding.tvAll;
        friendsButton = binding.tvFriends;
        yoursButton = binding.tvYours;
        if (isMod){
            friendsButton.setText("Reviews");
            yoursButton.setText("Users");


        }
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        sfm = new ArrayList<>();

        searchUserAdapter = new ReportedUsersAdapater(sfm);
        reviewModel = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(getContext(), (ArrayList<ReviewModel>) reviewModel);


        yourreviewModel = new ArrayList<>();
        yourreviewAdapter = new YourReviewAdapter(getContext(), (ArrayList<YourReviewModel>) yourreviewModel);


        binding.rvFeed.setAdapter(reviewAdapter);
        binding.rvFeed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fetchBlocked();

        allbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rvFeed.setAdapter(reviewAdapter);
                allbutton.setTextColor(getResources().getColor(R.color.color1));
                friendsButton.setTextColor(getResources().getColor(R.color.black));
                yoursButton.setTextColor(getResources().getColor(R.color.black));

                fetchBlocked();
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friendsButton.setTextColor(getResources().getColor(R.color.color1));
                allbutton.setTextColor(getResources().getColor(R.color.black));
                yoursButton.setTextColor(getResources().getColor(R.color.black));

                if(isMod){
                    fetchReportedReviews();
                }
                else{
                    binding.rvFeed.setAdapter(reviewAdapter);
                    fetchYourReviews();

                }


            }
        });

        yoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.rvFeed.setAdapter(yourreviewAdapter);
//                binding.rvFeed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                yoursButton.setTextColor(getResources().getColor(R.color.color1));
                allbutton.setTextColor(getResources().getColor(R.color.black));
                friendsButton.setTextColor(getResources().getColor(R.color.black));

                if(isMod){
                    binding.rvFeed.setAdapter(searchUserAdapter);
                    fetchReportedUsers();
                }
                else{
                    fetchYourReviews();

                }

            }
        });

//        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                Log.i("hiSwitch", "j");
//                binding.rvFeed.setAdapter(yourreviewAdapter);
//                binding.rvFeed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
////                fetchFriends2();
//                fetchYourReviews();
//
//            } else {
//                Log.i("hiSwitch", "f");
//                fetchBlocked();
//
//            }
//        });

//        RecyclerView rvFeed = getView().findViewById(R.id.rvFeed);
//        reviewModel = new ArrayList<>();
//        reviewAdapter =  new ReviewAdapter(getContext(), (ArrayList<ReviewModel>) reviewModel);
//
//        rvFeed.setAdapter(reviewAdapter);
//
//        reviewModel = new ArrayList<>();

        fetchBlocked();
    }

    public void fetchReportedUsers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.orderByDescending("reportNum");
        System.out.println("Got to first part in search, keyword = " + query);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if (e == null) {



                    for (ParseObject searchUserObject : users) {

                            String username = searchUserObject.getString("username");
                            ParseFile picFile = searchUserObject.getParseFile("profile_pic");
                            String picUrl = picFile != null ? picFile.getUrl() : null;
                            int reportNumber =  searchUserObject.getInt("reportNum");
                            String id = searchUserObject.getObjectId();
                            int b = 0;


                        sfm.add(new ReportedUsersModel(username, picUrl, id, b, reportNumber));
                            Log.i("yourreview", sfm.toString());
                            if(reportNumber != 0){

                            }
//                            reviewModel.add(review);

                    }

                    // Notify the adapter of the change on the UI thread
                    getActivity().runOnUiThread(() -> {
                        searchUserAdapter.notifyDataSetChanged();
                    });
                }
            }
        });


    }

    public void fetchReportedReviews() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String userId = currentUser.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.include("source_user");
        query.whereNotEqualTo("reportNumber",0 );
        query.orderByDescending("reportNumber");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> reviews, ParseException e) {
                if (e == null) {

                    reviewModel.clear();

                    for (ParseObject reviewObject : reviews) {
                        ParseUser user = reviewObject.getParseUser("source_user");
                        if (user != null) {


                            String username = user.getUsername();
                            ParseFile picFile = user.getParseFile("profile_pic");
                            String picUrl = picFile != null ? picFile.getUrl() : null;
                            int reportNumber =  reviewObject.getInt("reportNumber");


                            ReviewModel review = new ReviewModel(
                                    reviewObject.getString("ReviewUser"),
                                    username,
                                    reviewObject.getString("ReviewText"),
                                    reviewObject.getString("GameID"),
                                    reviewObject.getObjectId(),
                                    reviewObject.getCreatedAt(),
                                    reviewObject.getUpdatedAt(),
                                    reviewObject.getBoolean("isShowOnlyFriends"),
                                    reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0,
                                    reviewObject.getInt("upvotes"),
                                    reviewObject.getInt("downvotes"),
                                    picUrl,
                                    reportNumber
                            );
                            Log.i("yourreview", review.toString());
                            if(reportNumber != 0){
                                reviewModel.add(review);
                            }
//                            reviewModel.add(review);
                        }
                    }

                    // Notify the adapter of the change on the UI thread
                    getActivity().runOnUiThread(() -> {
                        reviewAdapter.notifyDataSetChanged();
                    });
                } else {
                    Log.e("fetchReviews", "Error: " + e.getMessage());
                }
            }
        });

    }

//    public void fetchFriends() {
//
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if (currentUser != null) {
//           friendList = currentUser.getList("friend_list");
//            if (friendList != null) {
//                // Process your friend list here
//                for (String friend : friendList) {
//                    Log.d("Friend", friend);
//                }
//            } else {
//                Log.d("Error", "No friends list found");
//            }
//        } else {
//            Log.d("Error", "No user logged in");
//        }
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> reviews, ParseException e) {
//                if (e == null) {
//                    // Clear existing data
//                    reviewModel.clear();
//
//                    // Convert ParseObjects into your ReviewModel instances
//                    for (ParseObject reviewObject : reviews) {
//                        ReviewModel review = new ReviewModel(
//                                reviewObject.getString("ReviewUser"),
//                                reviewObject.getString("ReviewUsername"),
//                                reviewObject.getString("ReviewText"),
//                                reviewObject.getString("GameID"),
//                                reviewObject.getObjectId(),
//                                reviewObject.getCreatedAt(),
//                                reviewObject.getUpdatedAt(),
//                                reviewObject.getBoolean("isShowOnlyFriends"),
//                                reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0,
//                                reviewObject.getInt("upCount"),
//                                reviewObject.getInt("downCount")
//                        );
//
//                        if (friendList.contains(review.getReviewUser())){
//                            Log.i("friendList", review.getReviewUser().toString());
//                            reviewModel.add(review);
//
//                        }
//
//                    }
//
//                    // Notify the adapter of the change on the UI thread
//                    getActivity().runOnUiThread(() -> {
//                        reviewAdapter.notifyDataSetChanged();
//                    });
//                } else {
//                    Log.e("fetchReviews", "Error: " + e.getMessage());
//                }
//            }
//        });
//
//
//    }

    public void fetchBlocked() {
        HashMap<String, String> params = new HashMap<String, String>();
        String currentUserID = ParseUser.getCurrentUser().getObjectId();
        params.put("userId", currentUserID);
        ParseCloud.callFunctionInBackground("getBlockedAndBlockedBy", params, new FunctionCallback<ArrayList<String>>() {
            @Override
            public void done(ArrayList<String> list, ParseException e){
                if (e == null){
                    fetchReviews(list);
                }
            }
        });

    }

    public void fetchYourReviews(){

        ParseUser currentUser = ParseUser.getCurrentUser();
        String userId = currentUser.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.include("source_user");
        query.whereEqualTo("ReviewUser",userId );
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> reviews, ParseException e) {
                if (e == null) {
                    // Clear existing data
                    yourreviewModel.clear();

                    // Convert ParseObjects into your ReviewModel instances
                    for (ParseObject reviewObject : reviews) {
                        ParseUser user = reviewObject.getParseUser("source_user");
                        if (user != null) {


                            String username = user.getUsername();
                            ParseFile picFile = user.getParseFile("profile_pic");
                            String picUrl = picFile != null ? picFile.getUrl() : null;

                            YourReviewModel review = new YourReviewModel(
                                    reviewObject.getString("ReviewUser"),
                                    username,
                                    reviewObject.getString("ReviewText"),
                                    reviewObject.getString("GameID"),
                                    reviewObject.getObjectId(),
                                    reviewObject.getCreatedAt(),
                                    reviewObject.getUpdatedAt(),
                                    reviewObject.getBoolean("isShowOnlyFriends"),
                                    reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0,
                                    reviewObject.getInt("upvotes"),
                                    reviewObject.getInt("downvotes"),
                                    picUrl
                            );
                            Log.i("yourreview", review.toString());
                            yourreviewModel.add(review);
                        }
                    }

                    // Notify the adapter of the change on the UI thread
                    getActivity().runOnUiThread(() -> {
                        yourreviewAdapter.notifyDataSetChanged();
                    });
                } else {
                    Log.e("fetchReviews", "Error: " + e.getMessage());
                }
            }
        });

    }
    public void fetchFriends2() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseRelation<ParseUser> friendsRelation = currentUser.getRelation("friends");
        ParseQuery<ParseUser> friendsRelationQuery = friendsRelation.getQuery();

        friendsRelationQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(e == null){
                    ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery("Review");
                    reviewQuery.whereContainedIn("source_user", list);
                    reviewQuery.include("source_user");
                    reviewQuery.orderByDescending("createdAt");
                    reviewQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> reviews, ParseException e) {
                            if(e == null) {
                                reviewModel.clear();

                                for (ParseObject reviewObject : reviews) {
                                    ParseUser user = reviewObject.getParseUser("source_user");
                                    if (user != null) {
                                        String username = user.getUsername();
                                        ParseFile picFile = user.getParseFile("profile_pic");
                                        String picUrl = picFile != null ? picFile.getUrl() : null;
                                        int reportNum = reviewObject.getInt("reportNumber");

                                        ReviewModel review = new ReviewModel(
                                                reviewObject.getString("ReviewUser"),
                                                username,
                                                reviewObject.getString("ReviewText"),
                                                reviewObject.getString("GameID"),
                                                reviewObject.getObjectId(),
                                                reviewObject.getCreatedAt(),
                                                reviewObject.getUpdatedAt(),
                                                reviewObject.getBoolean("isShowOnlyFriends"),
                                                reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0,
                                                reviewObject.getInt("upvotes"),
                                                reviewObject.getInt("downvotes"),
                                                picUrl,
                                                reportNum
                                        );
                                        reviewModel.add(review);
                                    }
                                }
                                getActivity().runOnUiThread(() -> {
                                    reviewAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                    });
                } else {
                    Log.e("fetchFriends2", "Error: " + e.getMessage());
                }

            }
        });





    }


    public void fetchReviews(ArrayList<String> blocked) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.include("source_user");
        query.whereNotContainedIn("ReviewUser", blocked);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> reviews, ParseException e) {
                if (e == null) {
                    // Clear existing data
                    reviewModel.clear();

                    // Convert ParseObjects into your ReviewModel instances
                    for (ParseObject reviewObject : reviews) {
                        ParseUser user = reviewObject.getParseUser("source_user");
                        if (user != null) {
                            String currentUserId = ParseUser.getCurrentUser().getObjectId();
                            String reviewUserId = reviewObject.getString("ReviewUser");
                            boolean isShowOnlyFriends = reviewObject.getBoolean("isShowOnlyFriends");

                            if (isShowOnlyFriends) {
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("userId1", currentUserId);
                                params.put("userId2", reviewUserId);
                                ParseCloud.callFunctionInBackground("checkFriend", params, new FunctionCallback<HashMap>() {
                                    @Override
                                    public void done(HashMap result, ParseException e2) {
                                        if (e2 == null) {
                                            boolean isFriend = (boolean) result.get("isFriend");

                                            if (isFriend || (currentUserId.equals(reviewUserId))) {
                                                String username = user.getUsername();
                                                ParseFile picFile = user.getParseFile("profile_pic");
                                                String picUrl = picFile != null ? picFile.getUrl() : null;
                                                int reportNum = reviewObject.getInt("reportNumber");

                                                ReviewModel review = new ReviewModel(
                                                        reviewUserId,
                                                        username,
                                                        reviewObject.getString("ReviewText"),
                                                        reviewObject.getString("GameID"),
                                                        reviewObject.getObjectId(),
                                                        reviewObject.getCreatedAt(),
                                                        reviewObject.getUpdatedAt(),
                                                        isShowOnlyFriends,
                                                        reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0,
                                                        reviewObject.getInt("upvotes"),
                                                        reviewObject.getInt("downvotes"),
                                                        picUrl,
                                                        reportNum
                                                );
                                                reviewModel.add(review);
                                            }
                                        }
                                    }
                                });
                            } else{
                                String username = user.getUsername();
                                ParseFile picFile = user.getParseFile("profile_pic");
                                String picUrl = picFile != null ? picFile.getUrl() : null;
                                int reportNum = reviewObject.getInt("reportNumber");

                                ReviewModel review = new ReviewModel(
                                        reviewUserId,
                                        username,
                                        reviewObject.getString("ReviewText"),
                                        reviewObject.getString("GameID"),
                                        reviewObject.getObjectId(),
                                        reviewObject.getCreatedAt(),
                                        reviewObject.getUpdatedAt(),
                                        isShowOnlyFriends,
                                        reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0,
                                        reviewObject.getInt("upvotes"),
                                        reviewObject.getInt("downvotes"),
                                        picUrl,
                                        reportNum
                                );
                                reviewModel.add(review);
                            }



                        }
                    }

                    // Notify the adapter of the change on the UI thread
                    getActivity().runOnUiThread(() -> {
                        reviewAdapter.notifyDataSetChanged();
                    });
                } else {
                    Log.e("fetchReviews", "Error: " + e.getMessage());
                }
            }
        });
    }


//    public void fetchReviews() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> reviews, ParseException e) {
//                if (e == null) {
//                    reviewModel = new ArrayList<>();
//                    for (ParseObject reviewObject : reviews) {
//                        Log.i("reviewTag", reviewObject.getString("ReviewText"));
//                        String reviewUser = reviewObject.getString("ReviewUser") != null ? reviewObject.getString("ReviewUser") : "";
//                        String reviewUserName = reviewObject.getString("ReviewUsername") != null
//                                ? reviewObject.getString("ReviewUsername") : "";
//                        String reviewText = reviewObject.getString("ReviewText") != null ? reviewObject.getString("ReviewText") : "";
//                        String gameID = reviewObject.getString("GameID") != null ? reviewObject.getString("GameID") : "";
//                        String objectId = reviewObject.getObjectId();
//                        boolean isShowOnlyFriends = reviewObject.getBoolean("isShowOnlyFriends");
//                        // Number to float conversion with null check
//                        float ratingStar = reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0;
//                        int upCount = reviewObject.has("upCount") ? reviewObject.getInt("upCount") : 0;
//                        int downCount = reviewObject.has("downCount") ? reviewObject.getInt("downCount") : 0;
//                        ReviewModel review = new ReviewModel(
//                                reviewUser,
//                                reviewUserName,
//                                reviewText,
//                                gameID,
//                                objectId,
//                                reviewObject.getCreatedAt(),
//                                reviewObject.getUpdatedAt(),
//                                isShowOnlyFriends,
//                                ratingStar,
//                                upCount,
//                                downCount
//                        );
//
//                        reviewModel.add(review);
//                    }
//                    ArrayList<ReviewModel> limitedList = reviewModel.size() > 5 ?
//                            new ArrayList<>(reviewModel.subList(0, 5)) : new ArrayList<>(reviewModel);
//                    reviewAdapter = new ReviewAdapter(getContext(), (ArrayList<ReviewModel>) reviewModel);
//                    binding.rvFeed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//
//                } else {
//                    Log.e("fetchReviews", "Error: " + e.getMessage());
//                }
//            }
//        });
//
//    }

}