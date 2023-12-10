package com.example.rebootapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class GameReviewDetailsActivity extends AppCompatActivity {

    GameReview review;

    // the view objects
    TextView tvTitle;
    //TextView tvOverview;
    //RatingBar rbVoteAverage;
    ImageView ivPoster;
    EditText etReviewText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_review_details);

        tvTitle = (TextView) findViewById(R.id.tvTitleR);
        //tvOverview = (TextView) findViewById(R.id.tvOverview);
        etReviewText = (EditText) findViewById(R.id.etReviewTextR);
        ivPoster = (ImageView) findViewById(R.id.ivPosterR);

        review = (GameReview) Parcels.unwrap(getIntent().getParcelableExtra(GameReview.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", review.getTitle()));

        tvTitle.setText(review.getTitle());
        //tvOverview.setText(review.getOverview());

        ivPoster = (ImageView) findViewById(R.id.ivPosterR);
        Glide.with(this)
                .load(review.getPosterPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(ivPoster);
    }

}