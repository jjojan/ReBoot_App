package com.example.rebootapp;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel

public class GameSearchModel {


    String title;
    String id;
    String date;
    String posterPath;
    String backdropPath;
    Double voteAverage;



    public Double getVoteAverage() {
        return voteAverage;
    }





    public GameSearchModel() {}

    public GameSearchModel(JSONObject game) throws JSONException {
        Log.i("search", "searching");
        title = game.getString("name");
        Log.i("CustomListGameModel Name", title);
        id = game.getString("id");
        String decName = game.getString("metacritic");
        Log.i("name", decName);
        date = game.getString("released");
        posterPath = game.getString("background_image");

    }
//
//    public MovieModel(JSONObject jsonObject){
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

    public static List<GameSearchModel> fromJSONArray(JSONArray gameJSONArray) throws JSONException{
        List<GameSearchModel> games = new ArrayList<>();
        for(int i =0; i < gameJSONArray.length(); i++){
            games.add(new GameSearchModel(gameJSONArray.getJSONObject(i)));
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

    public String getID() {return id;}

}
