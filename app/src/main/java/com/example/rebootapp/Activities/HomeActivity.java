package com.example.rebootapp.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rebootapp.GameModel;

import java.util.List;


public class HomeActivity extends AppCompatActivity {

    public static final String POPULAR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811";
    public static final String NEW_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-01-01,2023-11-01&ordering=-added";
    public static final String YOUR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-12-01,2024-11-01";
    public static final String TAG = "HomeAct";

    List<GameModel> gameModel;
    List<GameModel> newGameModels;

    List<GameModel> yourGameModels;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//        RecyclerView rvGames = findViewById(R.id.hzRecyclerView);
//        RecyclerView rvNewGames = findViewById(R.id.newGamesRV);
//        RecyclerView rvYourGames = findViewById(R.id.rvYourGames);
//
//
//        gameModel = new ArrayList<>();
//        newGameModels = new ArrayList<>();
//        yourGameModels = new ArrayList<>();
//
//        GameAdapter gameAdapter =  new GameAdapter(this , gameModel);
//        GameAdapter newGameAdapter = new GameAdapter(this, newGameModels);
//        GameAdapter yourGamesAapter = new GameAdapter(this, yourGameModels);
//
//        rvGames.setAdapter(gameAdapter);
//        rvNewGames.setAdapter(newGameAdapter);
//        rvYourGames.setAdapter(yourGamesAapter);
//
//        rvGames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        rvNewGames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        rvYourGames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//
//        AsyncHttpClient client = new AsyncHttpClient();
//
//        client.get(POPULAR_GAMES_URL , new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                Log.d(TAG, "onSucess");
//                JSONObject jsonObject = json.jsonObject;
//                try{
//                    JSONArray results = jsonObject.getJSONArray("results");
//                    Log.i(TAG, "Results" + results.toString());
//                    gameModel.addAll(CustomListGameModel.fromJSONArray(results));
//                    gameAdapter.notifyDataSetChanged();
//                    Log.i(TAG, "Movies" + gameModel.size());
//                } catch(JSONException e){
//                    Log.e(TAG, "hit json expception", e);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.d(TAG, "onFailure");
//            }
//        });
//
//        client.get(NEW_GAMES_URL, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                JSONObject jsonObject = json.jsonObject;
//                try{
//                    JSONArray results = jsonObject.getJSONArray("results");
//                    Log.i(TAG, "Results" + results.toString());
//                    newGameModels.addAll(CustomListGameModel.fromJSONArray(results));
//                    newGameAdapter.notifyDataSetChanged();
//                    Log.i(TAG, "Movies" + newGameModels.size());
//                } catch(JSONException e){
//                    Log.e(TAG, "hit json expception", e);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.d(TAG, "onFailure");
//            }
//        });
//
//        client.get(YOUR_GAMES_URL, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                JSONObject jsonObject = json.jsonObject;
//                try{
//                    JSONArray results = jsonObject.getJSONArray("results");
//                    Log.i(TAG, "Results" + results.toString());
//                    yourGameModels.addAll(CustomListGameModel.fromJSONArray(results));
//                    yourGamesAapter.notifyDataSetChanged();
//                    Log.i(TAG, "Movies" + yourGameModels.size());
//                } catch(JSONException e){
//                    Log.e(TAG, "hit json expception", e);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//            }
//        });
//
//
//
//    }
    }
}
