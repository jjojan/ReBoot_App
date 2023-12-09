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
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class GameDetailsActivity extends AppCompatActivity {

    Game movie;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivPoster;
    ToggleButton heartButton;

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
        ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            currentUser.fetch();
        }
        catch(Exception e){
            System.out.println("can't fetch");
        }
        String currentUserID = currentUser.getObjectId();
        checkMovieID(currentUserID, tempID, new QueryCheckCallback() {
            @Override
            public void onResult(boolean isEmpty) {
                if (isEmpty) heartButton.setChecked(false);
                else heartButton.setChecked(true);
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(heartButton.isChecked()){
                    checkMovieID(currentUserID, tempID, new QueryCheckCallback() {
                        @Override
                        public void onResult(boolean isEmpty) {
                            if(isEmpty) addFavoriteGame(currentUserID, tempID);
                            else System.out.println("already favorite");
                        }
                    });

                }
                else{
                    System.out.println("don't do it");
                    checkMovieID(currentUserID, tempID, new QueryCheckCallback() {
                        @Override
                        public void onResult(boolean isEmpty) {
                            if(isEmpty) System.out.println("can't find game");
                            else removeFavoriteGame(currentUserID, tempID);
                        }
                    });
                }
            }
        });







    }

    public void addFavoriteGame(String UserID, String gameID){
        ParseObject object = new ParseObject("FavoriteGames");
        object.put("user_id", UserID);
        object.put("game_id", gameID);
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

}