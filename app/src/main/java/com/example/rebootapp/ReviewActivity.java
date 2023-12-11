package com.example.rebootapp;

import android.os.Bundle;
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

        EditText reviewText = findViewById(R.id.etReviewTextR);
        String text = reviewText.getText().toString();

        //Use the context from previous page to access game data
        review = (GameReview) Parcels.unwrap(getIntent().getParcelableExtra(GameReview.class.getSimpleName()));

        String id = review.getId();
        //Add ID to Parse backend under Review Object


        gameTitle.setText(review.getTitle());

        ImageView gamePoster = (ImageView) findViewById(R.id.ivPosterR);
        Glide.with(this)
                .load(review.getPosterPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(gamePoster);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Submit data to backend.
                ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery("Review");
                ParseUser currentUser = ParseUser.getCurrentUser();

                //ParseObject Review = new ParseObject("Review");
                reviewQuery.whereEqualTo("ReviewUser", currentUser.getObjectId());
                reviewQuery.whereEqualTo("ReviewUsername",currentUser.getUsername());
                reviewQuery.whereEqualTo("ReviewText",text);
                reviewQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {

                    }
                });

                ParseObject Game = new ParseObject("Game");
                Game.put("GameID", id);
                Toast.makeText(getApplicationContext(), "Review Submitted", Toast.LENGTH_SHORT).show();
            }
        });

    }
}