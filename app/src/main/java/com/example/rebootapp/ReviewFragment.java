package com.example.rebootapp;

import static java.sql.DriverManager.println;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class ReviewFragment extends Fragment {

    List<GameReview> Reviews;
    String search_term = "";
    private MenuItem menuItem;
    private SearchView svSearchGame;
    String SEARCH_QUERY = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811" + search_term + "&ordering=-added";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public ReviewFragment() {

    }


    public static ReviewFragment newInstance(String param1, String param2) {
        ReviewFragment fragment = new ReviewFragment();
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

        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        svSearchGame = view.findViewById(R.id.svSearchGame);
        RecyclerView reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        Reviews = new ArrayList<>();
        GameReviewAdapter GameReviewAdapter = new GameReviewAdapter(getContext(), Reviews);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        reviewRecyclerView.setAdapter(GameReviewAdapter);


        AsyncHttpClient client = new AsyncHttpClient();


        client.get(SEARCH_QUERY, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    Reviews.addAll(GameReview.fromJSONArray(results));
                    GameReviewAdapter.notifyDataSetChanged();
                    println("hello");

                } catch (JSONException e) {

                }
            }


            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                println("hello");
            }
        });



        svSearchGame.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("new", query);

                svSearchGame = view.findViewById(R.id.svSearchGame);
                RecyclerView reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
                Reviews = new ArrayList<>();
                GameReviewAdapter GameReviewAdapter = new GameReviewAdapter(getContext(), Reviews);
                reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                reviewRecyclerView.setAdapter(GameReviewAdapter);
                String search = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&&search_precise&search=" + query + "&ordering=-added";
                client.get(search, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try{
                            JSONArray results = jsonObject.getJSONArray("results");

                            Reviews.addAll(GameReview.fromJSONArray(results));
                            GameReviewAdapter.notifyDataSetChanged();
                            println("hello");

                        } catch(JSONException e){

                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                svSearchGame = view.findViewById(R.id.svSearch);
                RecyclerView reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
                Reviews = new ArrayList<>();
                GameReviewAdapter GameReviewAdapter = new GameReviewAdapter(getContext(), Reviews);
                reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                reviewRecyclerView.setAdapter(GameReviewAdapter);
                String search = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&&search_precise&search=" + newText + "&ordering=-added";
                client.get(search, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try{
                            JSONArray results = jsonObject.getJSONArray("results");

                            Reviews.addAll(GameReview.fromJSONArray(results));
                            GameReviewAdapter.notifyDataSetChanged();
                            println("hello");

                        } catch(JSONException e){

                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });

                return false;
            }
        });


    }

}