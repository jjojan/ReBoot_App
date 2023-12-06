package com.example.rebootapp;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel

public class GameSearch {


    String title;
    String id;
    String date;
    String posterPath;
    String backdropPath;
    Double voteAverage;



    public Double getVoteAverage() {
        return voteAverage;
    }




    // no-arg, empty constructor required for Parceler
    public GameSearch() {}

    public GameSearch(JSONObject game) throws JSONException {
        Log.i("searchh", "searching");
        title = game.getString("name");
        id = game.getString("id");
        String decName = game.getString("metacritic");
        Log.i("name", decName);
        date = game.getString("released");
        posterPath = game.getString("background_image");
//        backdropPath = movie.getString("backdrop_path");
//        voteAverage = movie.getDouble("vote_average");
    }
//
//    public Movie(JSONObject jsonObject){
//        try {
////            backdropPath = jsonObject.getString("backdrop_path");
//            posterPath = jsonObject.getString("background_image");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            title = jsonObject.getString("name");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            overview = jsonObject.getString("description");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public static List<GameSearch> fromJSONArray(JSONArray gameJSONArray) throws JSONException{
        List<GameSearch> games = new ArrayList<>();
        for(int i =0; i < gameJSONArray.length(); i++){
            games.add(new GameSearch(gameJSONArray.getJSONObject(i)));
        }
        return games;
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

}
