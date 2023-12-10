package com.example.rebootapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.colormoon.readmoretextview.ReadMoreTextView;
import com.parse.CountCallback;
import com.parse.FindCallback;
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

    List<ParseObject> adapterObjects;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivPoster;
    ToggleButton heartButton;

    ImageButton reviewButton;

    String UserID;

    String Username;

    String tempID;

    RecyclerView recyclerView;



    ReadMoreTextView tvDesc;
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
        tvDesc = findViewById(R.id.tvDesc);
        reviewButton = findViewById(R.id.reviewButton);


        showWriteReview();




                movie = (Game) Parcels.unwrap(getIntent().getParcelableExtra(Game.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));
        tempID = movie.getID();
        displayReviews();
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
                    tvDesc.setText(jsonObject.getString("description_raw"));



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

    public void addReview(String UserID, String username, String reviewText){

        ParseObject object = new ParseObject("Review");
        object.put("ReviewUser", UserID);
        object.put("ReviewUsername", username);
        object.put("ReviewText", reviewText);
        object.put("GameID", tempID);
        object.saveInBackground();

        addGame(tempID, object);



    }

    public void displayReviews(){

        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
            Log.i("temp ID", tempID);
            query.whereEqualTo("GameID", tempID);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){


//                        Log.i("review exists", objects.get(0).getString("ReviewText") + "size");
                        int i = 0;

                        recyclerView = (RecyclerView) findViewById(R.id.rvReviews);
                        recyclerView.setLayoutManager(new LinearLayoutManager(GameDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        ReviewAdapter adapter = new ReviewAdapter(objects, GameDetailsActivity.this);
                        Log.i("adapter rsult", adapter.toString());
                        recyclerView.setAdapter(adapter);

                        for (ParseObject item : objects) {
                            Log.i("review exists", objects.get(i).getString("ReviewText") + "size");
                            i += 1;
                        }


                    }
                    else {
                    }

                }
            });

        }catch (Exception e){
            System.out.println("Parse Error Getting Reviews");
            Log.e("MYAPP", "exception", e);
        }


    }

    public void addGame(String GameID, ParseObject review){

        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
            query.whereEqualTo("GameID", GameID);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null){
                        Log.i("game exists", "arready exists");
                        object.add("ReviewArray", review);
                        object.saveInBackground();
                    }
                    else {
                        ParseObject game = new ParseObject("Game");
                        game.put("GameID", tempID);
                        game.add("ReviewArray", review);
                        game.saveInBackground();
                    }
                }
            });

        }catch (Exception e){
            System.out.println("Parse Error in Saving");
        }


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

    private void  showWriteReview(){
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder( GameDetailsActivity.this );
                builder.setTitle("Write Review");
                EditText etReview = new EditText(GameDetailsActivity.this);
                builder.setView(etReview);

                ParseUser currentUser = ParseUser.getCurrentUser();
                String currentUserObjectID = currentUser.getObjectId();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");

                query.getInBackground(currentUserObjectID, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e==null){
                             UserID = object.getObjectId().toString();
                             Username = object.getString("username");
                             Log.i("userID", UserID);
                             Log.i("userID", Username);
                        }
                    }
                });

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String reviewText = etReview.getText().toString();
                        addReview(UserID, Username, reviewText);
                        Toast.makeText(GameDetailsActivity.this, "Review: " + reviewText, Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    public interface QueryCheckCallback {
        void onResult(boolean isEmpty);
    }

    @Override
    protected void onPause() {
        super.onPause();



    }




}