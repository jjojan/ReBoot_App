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
import com.example.rebootapp.Activities.GameSearchDetailsActivity;
import com.example.rebootapp.GameSearchModel;
import com.example.rebootapp.R;

import org.parceler.Parcels;

import java.util.List;

public class GameSearchAdapter extends RecyclerView.Adapter<GameSearchAdapter.ViewHolder> {

    Context context;
    List<GameSearchModel> games;

    public GameSearchAdapter(Context context, List<GameSearchModel> games){
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View gameView = LayoutInflater.from(context).inflate(R.layout.item_gamesearch, parent, false);
        return new ViewHolder(gameView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameSearchModel game = games.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle;
        TextView tvDate;

        TextView tvID;
        ImageView tvPoster;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvGameTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPoster = itemView.findViewById(R.id.gamePoster);
        }

        public void bind(GameSearchModel game) {
            String date = game.getOverview();
            String[] arrOfStr = date.split("-", 2);
            tvTitle.setText(game.getTitle());
            tvDate.setText(arrOfStr[0]);
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
                    Intent intent = new Intent(context, GameSearchDetailsActivity.class);

                    intent.putExtra(GameSearchModel.class.getSimpleName(), Parcels.wrap(game));
                    intent.putExtra("ModelType", "Search");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                GameSearchModel game = games.get(position);

                Intent intent = new Intent(context, GameSearchDetailsActivity.class);

                intent.putExtra(GameSearchModel.class.getSimpleName(), Parcels.wrap(game));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        }





    }


}
