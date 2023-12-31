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
import com.parse.ParseObject;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    List<ParseObject> objects;
    GameDetailsActivity activity;
    View selectReview;

    public ReviewAdapter(List<ParseObject> objects, GameDetailsActivity activity) {
        this.objects=objects;
        this.activity=activity;
    }


    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View bankListLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, null);
        ReviewViewHolder bankListViewHolder = new ReviewViewHolder(bankListLayout);
        return bankListViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Log.i("pos", "" + objects.size());
        Log.i("pos", "" + position);
        Log.i("pos", "" +  objects.get(position).getString("ReviewUser"));


        holder.tvUsername.setText(objects.get(position).getString("ReviewUsername"));
        holder.tvReview.setText(objects.get(position).getString("ReviewText"));

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            tvUsername = (TextView) itemView.findViewById(R.id.tvReviewUser);
            tvReview = (TextView) itemView.findViewById(R.id.tvReviewText);



        }
    }

}




