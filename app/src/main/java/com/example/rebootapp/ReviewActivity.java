package com.example.rebootapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ReviewActivity extends AppCompatActivity {

    GameReview review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView gameTitle = findViewById(R.id.tvTitleR);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);



        ImageView gamePoster = (ImageView) findViewById(R.id.ivPosterR);




        review = (GameReview) Parcels.unwrap(getIntent().getParcelableExtra(GameReview.class.getSimpleName()));
        gameTitle.setText(review.getTitle());
        Glide.with(this)
                .load(review.getPosterPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(gamePoster);
        String id = review.getId();

        ParseUser currentUser = ParseUser.getCurrentUser();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText reviewText = findViewById(R.id.etReviewTextR);
                String text = reviewText.getText().toString();
                String userID = currentUser.getObjectId();
                String username = currentUser.getUsername();
                Log.i("Review", reviewText.getText().toString());
                addReview(userID, username, text, id);
                Toast.makeText(getApplicationContext(), "Review Submitted", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ReviewActivity.this, GameReviewDetailsActivity.class);
                startActivity(intent);
            }
        });

    }
    public void addReview(String UserID, String username, String reviewText, String id){

        ParseObject object = new ParseObject("Review");
        object.put("ReviewUser", UserID);
        object.put("ReviewUsername", username);
        object.put("ReviewText", reviewText);
        object.put("GameID", id);
        object.saveInBackground();

        addGame(id, object);

    }

    public void addGame(String GameID, ParseObject review ){

        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("GameModel");
            query.whereEqualTo("GameID", GameID);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null){
                        Log.i("game exists", "already exists");
                        object.add("ReviewArray", review);
                        object.saveInBackground();
                    }
                    else {
                        ParseObject game = new ParseObject("GameModel");
                        game.put("GameID", GameID);
                        game.add("ReviewArray", review);
                        game.saveInBackground();
                    }
                }
            });

        }catch (Exception e){
            System.out.println("Parse Error in Saving");
        }


    }


}