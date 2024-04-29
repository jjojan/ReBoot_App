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
import com.example.rebootapp.GameModel;
import com.example.rebootapp.R;

import org.parceler.Parcels;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    Context context;
    List<GameModel> gameModels;

    public GameAdapter(Context context, List<GameModel> gameModels){
        this.context = context;
        this.gameModels = gameModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View gameView = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new ViewHolder(gameView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameModel gameModel = gameModels.get(position);
        holder.bind(gameModel);
    }

    @Override
    public int getItemCount() {
        return gameModels.size();
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

        public void bind(GameModel gameModel) {
            String imageUrl;


            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                Log.i("movieAdapter", "We are in landscape");
                imageUrl = gameModel.getPosterPath();
            }
            else {
                imageUrl = gameModel.getPosterPath();
            }

//            Glide.with(context).load(imageUrl).into(tvPoster);

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.flicks_movie_placeholder)
                    .error(R.drawable.flicks_movie_placeholder)
                    .into(tvPoster);
            tvPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GameDetailsActivity.class);

                    intent.putExtra(GameModel.class.getSimpleName(), Parcels.wrap(gameModel));

                    context.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                GameModel gameModel = gameModels.get(position);


                Intent intent = new Intent(context, GameDetailsActivity.class);

                intent.putExtra(GameModel.class.getSimpleName(), Parcels.wrap(gameModel));

                context.startActivity(intent);
            }
        }





    }


}
