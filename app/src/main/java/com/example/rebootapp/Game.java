package com.example.rebootapp;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel

public class Game {


    String title;
    String id;

    String overview;
    String posterPath;
    String backdropPath;
    Double voteAverage;



    public Double getVoteAverage() {
        return voteAverage;
    }




    // no-arg, empty constructor required for Parceler
    public Game() {}

    public Game(JSONObject game) throws JSONException {
        title = game.getString("name");
        id = game.getString("id");
        String decName = game.getString("metacritic");
        Log.i("name", decName);
        overview = game.getString("released");
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

    public static List<Game> fromJSONArray(JSONArray gameJSONArray) throws JSONException{
        List<Game> games = new ArrayList<>();
        for(int i =0; i < gameJSONArray.length(); i++){
            games.add(new Game(gameJSONArray.getJSONObject(i)));
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
        return overview;
    }

}
