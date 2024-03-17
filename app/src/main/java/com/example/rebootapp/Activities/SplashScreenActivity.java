package com.example.rebootapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rebootapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String POPULAR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&page_size=40";
    public static final String NEW_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-01-01,2023-11-01&ordering=-added&page_size=40";

    public static final String YOUR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-12-01,2024-11-01&page_size=40";

    JSONArray result1;

    JSONObject result2;

    JSONObject result3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getSupportActionBar().hide();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(POPULAR_GAMES_URL , new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//
//                JSONObject jsonObject = json.jsonObject;
//                Log.i("xqc", "Results from splash" + jsonObject.toString());
//                try{
//                    JSONArray results = jsonObject.getJSONArray("results");
//                    Log.i("results", "Results from splash" + results.toString());
//                    Log.i("filer", jsonObject.toString());
//                    Bundle jsonData = new Bundle();
//                    if (results != null) {
//                        Log.i("not null", "" +  results.toString());
//                        intent.putExtra("jsonArray", results.toString());
//                        startActivity(intent);
//                    }
//
//
//                } catch(JSONException e){
//                    Log.e("errror ", "hit json expception", e);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.d("error", "onFailure");
//            }
//        });


        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


//                client.get(NEW_GAMES_URL, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Headers headers, JSON json) {
//                        result2 = json.jsonObject;
//                        try{
//                            JSONArray results = result2.getJSONArray("results");
//                            Log.i("result", "Results from json" + results.toString());
//
//
//
//                        } catch(JSONException e){
//                            Log.e("error", "hit json expception", e);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                        Log.d("error", "onFailure");
//                    }
//                });
//
//                client.get(YOUR_GAMES_URL, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Headers headers, JSON json) {
//                        result3 = json.jsonObject;
//                        try{
//                            JSONArray results = result3.getJSONArray("results");
//                            Log.i("json", "Results from splash" + results.toString());
//                        } catch(JSONException e){
//                            Log.e("error", "hit json expception", e);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//                    }
//                });



                startActivity(intent);
                finish();

            }
        }, 2000);


    }
}
