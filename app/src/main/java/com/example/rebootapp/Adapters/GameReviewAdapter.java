package com.example.rebootapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rebootapp.Activities.GameReviewDetailsActivity;
import com.example.rebootapp.Activities.ReviewActivity;
import com.example.rebootapp.GameReviewModel;
import com.example.rebootapp.R;

import org.parceler.Parcels;

import java.util.List;

public class GameReviewAdapter extends RecyclerView.Adapter<GameReviewAdapter.ViewHolder> {

    Context context;
    List<GameReviewModel> reviews;

    public GameReviewAdapter(Context context, List<GameReviewModel> reviews){
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View gameView = LayoutInflater.from(context).inflate(R.layout.item_gamereview, parent, false); //?
        return new ViewHolder(gameView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameReviewModel review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle;
        TextView tvDate;

        String tvID;
        ImageView tvPoster;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvGameTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPoster = itemView.findViewById(R.id.gamePoster);

        }

        public void bind(GameReviewModel review) {
            String id = review.getId();
            String date = review.getOverview();
            String[] arrOfStr = date.split("-", 2);
            tvTitle.setText(review.getTitle());
            tvDate.setText(arrOfStr[0]);
            String imageUrl;


            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                Log.i("movieAdapter", "We are in landscape");
                imageUrl = review.getPosterPath();
            }
            else {
                imageUrl = review.getPosterPath();
            }

            Glide.with(context).load(imageUrl).into(tvPoster);

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.flicks_movie_placeholder)
                    .error(R.drawable.flicks_movie_placeholder)
                    .into(tvPoster);
            tvPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //int position = getAdapterPosition();
                    //GameReviewModel game = reviews.get(position);
                    Intent intent = new Intent(context, ReviewActivity.class);

                    intent.putExtra(GameReviewModel.class.getSimpleName(), Parcels.wrap(review));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                GameReviewModel game = reviews.get(position); //?

                Intent intent = new Intent(context, GameReviewDetailsActivity.class);

                intent.putExtra(GameReviewModel.class.getSimpleName(), Parcels.wrap(game));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        }





    }


}