package com.example.rebootapp;

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

import org.parceler.Parcels;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    Context context;
    List<Game> games;

    public GameAdapter(Context context, List<Game> games){
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View gameView = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new ViewHolder(gameView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle;
        TextView tvOverview;

        TextView tvID;
        ImageView tvPoster;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvPoster = itemView.findViewById(R.id.gamePoster);
        }

        public void bind(Game game) {
            String imageUrl;


            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                Log.i("movieAdapter", "We are in landscape");
                imageUrl = game.getPosterPath();
            }
            else {
                imageUrl = game.getPosterPath();
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
                    Intent intent = new Intent(context, GameDetailsActivity.class);

                    intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(game));

                    context.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                Game game = games.get(position);

                Intent intent = new Intent(context, GameDetailsActivity.class);

                intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(game));

                context.startActivity(intent);
            }
        }





    }


}
