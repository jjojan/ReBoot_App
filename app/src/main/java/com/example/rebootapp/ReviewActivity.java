package com.example.rebootapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class ReviewActivity extends AppCompatActivity {



    GameReview review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView gameTitle = findViewById(R.id.tvTitleR);

        EditText reviewText = findViewById(R.id.etReviewTextR);

        //Use the context from previous page to access game data
        review = (GameReview) Parcels.unwrap(getIntent().getParcelableExtra(GameReview.class.getSimpleName()));

        gameTitle.setText(review.getTitle());

        ImageView gamePoster = (ImageView) findViewById(R.id.ivPosterR);
        Glide.with(this)
                .load(review.getPosterPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(gamePoster);
    }
}

/*tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);



        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", review.getTitle()));

        tvTitle.setText(review.getTitle());
        tvOverview.setText(review.getOverview());


//        float voteAverage = movie.getVoteAverage().floatValue();
//        rbVoteAverage.setRating(voteAverage / 2.0f);

        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        Glide.with(this)
        .load(review.getPosterPath())
        .placeholder(R.drawable.flicks_movie_placeholder)
        .error(R.drawable.flicks_movie_placeholder)
        .into(ivPoster);*/
