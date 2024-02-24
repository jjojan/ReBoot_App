package com.example.rebootapp.AwoApp;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel

public class GameModel {


    String title;
    String id;

    String overview;
    String posterPath;
    String backdropPath;
    Double voteAverage;



    public Double getVoteAverage() {
        return voteAverage;
    }





    public GameModel() {}

    public GameModel(JSONObject game) throws JSONException {
        title = game.getString("name");
        id = game.getString("id");
        String decName = game.getString("metacritic");
        Log.i("name", decName);
        overview = game.getString("released");
        posterPath = game.getString("background_image");

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

    public static List<GameModel> fromJSONArray(JSONArray gameJSONArray) throws JSONException{
        List<GameModel> gameModels = new ArrayList<>();
        for(int i =0; i < gameJSONArray.length(); i++){
            gameModels.add(new GameModel(gameJSONArray.getJSONObject(i)));
        }
        return gameModels;
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

    public String getID() { return id; }

}
