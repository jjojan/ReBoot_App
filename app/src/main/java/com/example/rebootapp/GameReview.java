package com.example.rebootapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel

public class GameReview {
    String title;
    String id;
    String date;
    String posterPath;
    String backdropPath;
    Double voteAverage;


    // no-arg, empty constructor required for Parceler
    public GameReview() {}


    public GameReview(JSONObject game) throws JSONException {
        Log.i("search", "searching");
        title = game.getString("name");
        Log.i("Game Name", title);
        id = game.getString("id");
        String decName = game.getString("metacritic");
        Log.i("name", decName);
        date = game.getString("released");
        posterPath = game.getString("background_image");
//        backdropPath = movie.getString("backdrop_path");
//        voteAverage = movie.getDouble("vote_average");
    }

    public static List<GameReview> fromJSONArray(JSONArray gameJSONArray) throws JSONException{
        List<GameReview> reviews = new ArrayList<>();
        for(int i =0; i < gameJSONArray.length(); i++){
            reviews.add(new GameReview(gameJSONArray.getJSONObject(i)));
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



}
