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
import com.example.rebootapp.Activities.GameDetailsActivity;
import com.example.rebootapp.MovieModel;
import com.example.rebootapp.R;

import org.parceler.Parcels;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<MovieModel> movieModels;

    public MovieAdapter(Context context, List<MovieModel> movieModels){
        this.context = context;
        this.movieModels = movieModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieModel movieModel = movieModels.get(position);
        holder.bind(movieModel);
    }

    @Override
    public int getItemCount() {
        return movieModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle;
        TextView tvOverview;
        ImageView tvPoster;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            tvPoster = itemView.findViewById(R.id.gamePoster);
        }

        public void bind(MovieModel movieModel) {
            tvTitle.setText(movieModel.getTitle());
            tvOverview.setText(movieModel.getOverview());
            String imageUrl;


            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                Log.i("movieAdapter", "We are in landscape");
                imageUrl = movieModel.getBackdropPath();
            }
            else {
                imageUrl = movieModel.getPosterPath();
            }

            Glide.with(context).load(imageUrl).into(tvPoster);

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.flicks_movie_placeholder)
                    .error(R.drawable.flicks_movie_placeholder)
                    .into(tvPoster);
            tvOverview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GameDetailsActivity.class);

                    intent.putExtra(MovieModel.class.getSimpleName(), Parcels.wrap(movieModel));

                    context.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                MovieModel movieModel = movieModels.get(position);

                Intent intent = new Intent(context, GameDetailsActivity.class);

                intent.putExtra(MovieModel.class.getSimpleName(), Parcels.wrap(movieModel));

                context.startActivity(intent);
            }
        }





    }


}
