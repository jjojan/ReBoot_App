package com.example.rebootapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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



public class HomeActivity extends AppCompatActivity {

    public static final String POPULAR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811";
    public static final String NEW_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-01-01,2023-11-01&ordering=-added";
    public static final String YOUR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-12-01,2024-11-01";
    public static final String TAG = "HomeAct";

    List<Game> game;
    List<Game> newGames;

    List<Game> yourGames;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        RecyclerView rvGames = findViewById(R.id.hzRecyclerView);
        RecyclerView rvNewGames = findViewById(R.id.newGamesRV);
        RecyclerView rvYourGames = findViewById(R.id.rvYourGames);


        game = new ArrayList<>();
        newGames = new ArrayList<>();
        yourGames = new ArrayList<>();

        GameAdapter gameAdapter =  new GameAdapter(this , game);
        GameAdapter newGameAdapter = new GameAdapter(this, newGames);
        GameAdapter yourGamesAapter = new GameAdapter(this, yourGames);

        rvGames.setAdapter(gameAdapter);
        rvNewGames.setAdapter(newGameAdapter);
        rvYourGames.setAdapter(yourGamesAapter);

        rvGames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvNewGames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvYourGames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(POPULAR_GAMES_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSucess");
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results" + results.toString());
                    game.addAll(Game.fromJSONArray(results));
                    gameAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies" + game.size());
                } catch(JSONException e){
                    Log.e(TAG, "hit json expception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        client.get(NEW_GAMES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results" + results.toString());
                    newGames.addAll(Game.fromJSONArray(results));
                    newGameAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies" + newGames.size());
                } catch(JSONException e){
                    Log.e(TAG, "hit json expception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        client.get(YOUR_GAMES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results" + results.toString());
                    yourGames.addAll(Game.fromJSONArray(results));
                    yourGamesAapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies" + yourGames.size());
                } catch(JSONException e){
                    Log.e(TAG, "hit json expception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });



    }
}
