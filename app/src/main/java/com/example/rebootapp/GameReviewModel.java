package com.example.rebootapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel

public class GameReviewModel {
    String title;
    String id;
    String date;
    String posterPath;
    String backdropPath;
    Double voteAverage;



    public GameReviewModel() {}


    public GameReviewModel(JSONObject game) throws JSONException {
        Log.i("search", "searching");
        title = game.getString("name");
        Log.i("CustomListGameModel Name", title);
        id = game.getString("id");
        Log.i("CustomListGameModel ID", id);
        String decName = game.getString("metacritic");
        Log.i("name", decName);
        date = game.getString("released");
        posterPath = game.getString("background_image");

    }

    public static List<GameReviewModel> fromJSONArray(JSONArray gameJSONArray) throws JSONException{
        List<GameReviewModel> reviews = new ArrayList<>();
        for(int i =0; i < gameJSONArray.length(); i++){
            reviews.add(new GameReviewModel(gameJSONArray.getJSONObject(i)));
        }
        return reviews;
    }

    public String getPosterPath() {
        return posterPath;
    }
    public String getBackdropPath(){
        return "https://image.tmdb.org/t/p/w342" + backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return date;
    }
    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getId() {return id;}



}
