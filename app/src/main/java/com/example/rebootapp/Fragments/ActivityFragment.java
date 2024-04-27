package com.example.rebootapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rebootapp.Activities.GameDetailsActivity;
import com.example.rebootapp.Adapters.GameAdapter;
import com.example.rebootapp.Adapters.ReviewAdapter;
import com.example.rebootapp.GameModel;
import com.example.rebootapp.Models.ReviewModel;
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

public class ActivityFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    com.example.rebootapp.databinding.FragmentActivityBinding binding;
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ArrayList<ReviewModel> reviewModel;

    ReviewAdapter reviewAdapter;

    SwitchMaterial switchMaterial;
    List<String> friendList = new ArrayList<>();

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
        switchMaterial = binding.materialSwitch;
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        reviewModel = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(getContext(), (ArrayList<ReviewModel>) reviewModel);

        // Now using binding to access the RecyclerView and set its properties
        binding.rvFeed.setAdapter(reviewAdapter);
        binding.rvFeed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.i("hiSwitch", "j");
                fetchFriends2();
            } else {
                Log.i("hiSwitch", "f");
                fetchBlocked();

            }
        });

//        RecyclerView rvFeed = getView().findViewById(R.id.rvFeed);
//        reviewModel = new ArrayList<>();
//        reviewAdapter =  new ReviewAdapter(getContext(), (ArrayList<ReviewModel>) reviewModel);
//
//        rvFeed.setAdapter(reviewAdapter);
//
//        reviewModel = new ArrayList<>();

        fetchBlocked();
    }

    public void fetchFriends() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
           friendList = currentUser.getList("friend_list");
            if (friendList != null) {
                // Process your friend list here
                for (String friend : friendList) {
                    Log.d("Friend", friend);
                }
            } else {
                Log.d("Error", "No friends list found");
            }
        } else {
            Log.d("Error", "No user logged in");
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> reviews, ParseException e) {
                if (e == null) {
                    // Clear existing data
                    reviewModel.clear();

                    // Convert ParseObjects into your ReviewModel instances
                    for (ParseObject reviewObject : reviews) {
                        ReviewModel review = new ReviewModel(
                                reviewObject.getString("ReviewUser"),
                                reviewObject.getString("ReviewUsername"),
                                reviewObject.getString("ReviewText"),
                                reviewObject.getString("GameID"),
                                reviewObject.getObjectId(),
                                reviewObject.getCreatedAt(),
                                reviewObject.getUpdatedAt(),
                                reviewObject.getBoolean("isShowOnlyFriends"),
                                reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0,
                                reviewObject.getInt("upCount"),
                                reviewObject.getInt("downCount")
                        );

                        if (friendList.contains(review.getReviewUser())){
                            Log.i("friendList", review.getReviewUser().toString());
                            reviewModel.add(review);

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
                                                reviewObject.getInt("upCount"),
                                                reviewObject.getInt("downCount"),
                                                picUrl
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
                            String username = user.getUsername();
                            ParseFile picFile = user.getParseFile("profile_pic");
                            String picUrl = picFile != null ? picFile.getUrl() : null;

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
                                    reviewObject.getInt("upCount"),
                                    reviewObject.getInt("downCount"),
                                    picUrl
                            );
                            reviewModel.add(review);
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