package com.example.rebootapp.Models;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.rebootapp.GameModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

public class GameViewModel extends ViewModel {
    public static final String POPULAR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&page_size=40";
    public static final String NEW_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-01-01,2023-11-01&ordering=-added&page_size=40";
    public static final String YOUR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-12-01,2024-11-01&page_size=40";
    private MutableLiveData<List<GameModel>> popularGames = new MutableLiveData<>();
    private MutableLiveData<List<GameModel>> newGames = new MutableLiveData<>();
    private MutableLiveData<List<GameModel>> yourGames = new MutableLiveData<>();
    private boolean isPopularGamesLoaded = false;
    private boolean isNewGamesLoaded = false;
    private boolean isYourGamesLoaded = false;

//    public LiveData<List<GameModel>> getGames(String url) {
//        if (!isDataLoaded) {
//            loadGames(url);
//        }
//        return gamesLiveData;
//    }
    public LiveData<List<GameModel>> getGames(String url) {
        if (url.equals(POPULAR_GAMES_URL) && popularGames.getValue() == null) {
            loadGames(url, popularGames);
        } else if (url.equals(NEW_GAMES_URL) && newGames.getValue() == null) {
            loadGames(url, newGames);
        } else if (url.equals(YOUR_GAMES_URL) && yourGames.getValue() == null) {
            loadGames(url, yourGames);
        }
        switch (url) {
            case POPULAR_GAMES_URL:
                return popularGames;
            case NEW_GAMES_URL:
                return newGames;
            case YOUR_GAMES_URL:
                return yourGames;
            default:
                return null;
        }
    }
    public void loadGames(String url, MutableLiveData<List<GameModel>> liveData){

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Headers headers, JSON json) {

                    JSONObject jsonObject = json.jsonObject;
                    try {
                        Log.d("working!!!!!!", "its loading...");
                        JSONArray results = jsonObject.getJSONArray("results");
                        List<GameModel> games = GameModel.fromJSONArray(results);
                        liveData.postValue(games);



                    } catch (JSONException e) {
                        System.out.println("hey, it didnt work");
                    }
                }

                @Override
                public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                    System.out.println("reallly didnt work");
                }
            });

    }
}
