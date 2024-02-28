package com.example.rebootapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class GameReviewDetailsActivity extends AppCompatActivity {

    GameReview review;


    TextView tvTitle = findViewById(R.id.tvTitle);
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivPoster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);


        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);


        review = (GameReview) Parcels.unwrap(getIntent().getParcelableExtra(GameReview.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", review.getTitle()));


        tvTitle.setText(review.getTitle().toString());
        Log.i("Title", tvTitle.getText().toString());

        tvOverview.setText(review.getOverview());




        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        Glide.with(this)
                .load(review.getPosterPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(ivPoster);
    }

}