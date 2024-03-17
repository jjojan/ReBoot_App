package com.example.rebootapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.rebootapp.Adapters.FavoriteGamesAdapter;
import com.example.rebootapp.R;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FavoriteGamesActivity extends AppCompatActivity {

    RecyclerView games;
    FavoriteGamesAdapter favoriteGamesAdapter;
    List<String> gamePhotoUris;
    Button btn_Favorite_Done;



    //assign XML components to their variables
    //create new arrray list to store photo uris
    //define favorite games adapter to format images
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_games);

        games = findViewById(R.id.rv_games_list);
        games.setLayoutManager(new GridLayoutManager(this, 3));
        btn_Favorite_Done = findViewById(R.id.btn_Favorite_Done);

        gamePhotoUris = new ArrayList<>();
        favoriteGamesAdapter = new FavoriteGamesAdapter(gamePhotoUris);

        games.setAdapter(favoriteGamesAdapter);

//        fillPhotos();


        btn_Favorite_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //Query to Parse database to identify the favorites of the current user
    //"gamesQuery" is a ParseQuery object that is used to request info
    public void fillPhotos(){
        ParseQuery<ParseObject> gamesQuery = ParseQuery.getQuery("FavoriteGames");
        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserID = currentUser.getObjectId();
        gamesQuery.whereEqualTo("user_id", currentUserID);

        //Adds images of favorites to ArrayList of photo uris defined earlier
        gamesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject object : objects) {
                        String uri = object.getString("picture_uri");
                        if(uri != null && !uri.isEmpty()) {
                            gamePhotoUris.add(uri);
                        }
                    }
                    favoriteGamesAdapter.notifyDataSetChanged();

                }
                else{
                    System.out.println("Parse query error");
                }
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        gamePhotoUris.clear();
        fillPhotos();
    }
}