package com.example.rebootapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class GameDetailsActivity extends AppCompatActivity {

    Game movie;

    List<Game> game;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivPoster;
    ToggleButton heartButton;
    int saveFavoriteQueue = 0;

    String GAME_URL = "https://api.rawg.io/api/games/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        heartButton = findViewById(R.id.toggleButton);


        movie = (Game) Parcels.unwrap(getIntent().getParcelableExtra(Game.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());


//        float voteAverage = movie.getVoteAverage().floatValue();
//        rbVoteAverage.setRating(voteAverage / 2.0f);

        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        Glide.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(ivPoster);


        String tempID = movie.getID();
        Log.i("id", tempID);
        GAME_URL = GAME_URL + tempID + "?key=63502b95db9f41c99bb3d0ecf77aa811";
        Log.i("id", GAME_URL);
        String favPath = movie.getPosterPath();

        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserID = currentUser.getObjectId();


        AsyncHttpClient client = new AsyncHttpClient();


        client.get(GAME_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("sucess", "onSucess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    Log.i("id", jsonObject.getString("description_raw"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i("RETURN results", "Results" + results.toString());
                    game.addAll(Game.fromJSONArray(results));
                    Log.i("return list", "Movies" + game.toString());
                } catch(JSONException e){
                    Log.e("error", "hit json expception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("fail", "onFailure");
            }
        });

        checkMovieID(currentUserID, tempID, new QueryCheckCallback() {
            @Override
            public void onResult(boolean isEmpty) {
                heartButton.setChecked(!isEmpty);
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(heartButton.isChecked()){
                    addFavoriteGame(currentUserID, tempID, favPath);
                }
                else{
                    removeFavoriteGame(currentUserID, tempID);
                }
            }
        });







    }

    public void addFavoriteGame(String UserID, String gameID, String path){
        ParseObject object = new ParseObject("FavoriteGames");
        object.put("user_id", UserID);
        object.put("game_id", gameID);
        object.put("picture_uri", path);
        object.saveInBackground();

    }

    public void removeFavoriteGame(String UserID, String gameID){
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FavoriteGames");
            query.whereEqualTo("user_id", UserID);
            query.whereEqualTo("game_id", gameID);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null){
                        object.deleteInBackground();
                    }
                    else System.out.println("cannot find to delete");
                }
            });

        }catch (Exception e){
            System.out.println("Parse Problem deleting");
        }
    }
    public void checkMovieID(String userID, String gameID, QueryCheckCallback callback){
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FavoriteGames");
            query.whereEqualTo("user_id", userID);
            query.whereEqualTo("game_id", gameID);

            query.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (e == null) {
                        callback.onResult(count == 0);
                    } else callback.onResult(false);

                }
            });
        } catch (Exception e){
            System.out.println("Parse problem checking");
        }

    }

    public interface QueryCheckCallback {
        void onResult(boolean isEmpty);
    }

    @Override
    protected void onPause() {
        super.onPause();



    }




}