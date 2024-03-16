package com.example.rebootapp.Adapters;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.rebootapp.Activities.GameDetailsActivity;
import com.example.rebootapp.GameModel;
import com.example.rebootapp.R;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter .ViewHolder>{
    private List<String> photoUris;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewGamePhoto;
        Context context;

        public ViewHolder(View view) {
            super(view);
            imageViewGamePhoto = view.findViewById(R.id.imageViewGame);

        }


    }

    public WishListAdapter (List<String> photoUris){

        this.photoUris = photoUris;
    }

    @NonNull
    @Override
    public WishListAdapter .ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gamephoto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String uri = photoUris.get(position);
        int tempPos = position;
        // Using Glide to load the image from the URI
        Glide.with(holder.imageViewGamePhoto.getContext()).load(uri).into(holder.imageViewGamePhoto);
        holder.imageViewGamePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> gamesQuery = ParseQuery.getQuery("WishListGames");
                ParseUser currentUser = ParseUser.getCurrentUser();
                String currentUserID = currentUser.getObjectId();
                gamesQuery.whereEqualTo("user_id", currentUserID);
                gamesQuery.whereEqualTo("picture_uri", photoUris.get(tempPos));
                gamesQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null){
                            String id = object.getString("game_id");
                            System.out.println(id);

                            String search = "https://api.rawg.io/api/games/" + id + "?key=63502b95db9f41c99bb3d0ecf77aa811";
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(search, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Headers headers, JSON json) {
                                    JSONObject jsonobject = json.jsonObject;
                                    try {
                                        System.out.println("line 1");
//                                        JSONArray results = jsonobject.getJSONArray("results");
//                                        System.out.println("count: " + results);
                                        com.example.rebootapp.GameModel game = new com.example.rebootapp.GameModel((jsonobject));
                                        System.out.println("line 2");
                                        Intent intent = new Intent(holder.imageViewGamePhoto.getContext(), GameDetailsActivity.class);
                                        System.out.println("line 3");
                                        intent.putExtra(GameModel.class.getSimpleName(), Parcels.wrap(game));
                                        System.out.println("line 4");
                                        holder.imageViewGamePhoto.getContext().startActivity(intent);
                                        System.out.println("line 5");

                                    } catch (JSONException ex) {
                                        System.out.println("almost");
                                        throw new RuntimeException(ex);

                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                    System.out.println("oops");
                                }
                            });
                        }
                    }
                });


            }
        });



    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

}
