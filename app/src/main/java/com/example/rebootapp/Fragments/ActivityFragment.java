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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    List<ReviewModel> reviewModel;

    ReviewAdapter reviewAdapter;

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
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvFeed = view.findViewById(R.id.rvFeed);
        reviewModel = new ArrayList<>();
        reviewAdapter =  new ReviewAdapter(getContext(), (ArrayList<ReviewModel>) reviewModel);

        rvFeed.setAdapter(reviewAdapter);

        fetchReviews();
    }

    public void fetchReviews() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> reviews, ParseException e) {
                if (e == null) {
                    reviewModel = new ArrayList<>();
                    for (ParseObject reviewObject : reviews) {
                        String reviewUser = reviewObject.getString("ReviewUser") != null ? reviewObject.getString("ReviewUser") : "";
                        String reviewUserName = reviewObject.getString("ReviewUsername") != null
                                ? reviewObject.getString("ReviewUsername") : "";
                        String reviewText = reviewObject.getString("ReviewText") != null ? reviewObject.getString("ReviewText") : "";
                        String gameID = reviewObject.getString("GameID") != null ? reviewObject.getString("GameID") : "";
                        String objectId = reviewObject.getObjectId();
                        boolean isShowOnlyFriends = reviewObject.getBoolean("isShowOnlyFriends");
                        // Number to float conversion with null check
                        float ratingStar = reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0;
                        int upCount = reviewObject.has("upCount") ? reviewObject.getInt("upCount") : 0;
                        int downCount = reviewObject.has("downCount") ? reviewObject.getInt("downCount") : 0;
                        ReviewModel review = new ReviewModel(
                                reviewUser,
                                reviewUserName,
                                reviewText,
                                gameID,
                                objectId,
                                reviewObject.getCreatedAt(),
                                reviewObject.getUpdatedAt(),
                                isShowOnlyFriends,
                                ratingStar,
                                upCount,
                                downCount
                        );

                        reviewModel.add(review);
                    }
                    ArrayList<ReviewModel> limitedList = reviewModel.size() > 5 ?
                            new ArrayList<>(reviewModel.subList(0, 5)) : new ArrayList<>(reviewModel);
                    reviewAdapter = new ReviewAdapter(getContext(), limitedList);



                } else {
                    Log.e("fetchReviews", "Error: " + e.getMessage());
                }
            }
        });

    }

}