package com.example.rebootapp.Activities;



import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.rebootapp.R;
import com.example.rebootapp.Adapters.WishListAdapter;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class WishListActivity extends AppCompatActivity {

    RecyclerView games;
    WishListAdapter wishListAdapter;
    List<String> gamePhotoUris;
    Button btn_WishList_Done; //Favorite



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_games);

        games = findViewById(R.id.rv_wishlist_list);
        games.setLayoutManager(new GridLayoutManager(this, 3));
        btn_WishList_Done = findViewById(R.id.btn_WishList_Done);

        gamePhotoUris = new ArrayList<>();
        wishListAdapter = new WishListAdapter(gamePhotoUris);

        games.setAdapter(wishListAdapter);


        btn_WishList_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void fillPhotos(){
        ParseQuery<ParseObject> gamesQuery = ParseQuery.getQuery("WishListGames");
        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserID = currentUser.getObjectId();
        gamesQuery.whereEqualTo("user_id", currentUserID);

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
                    wishListAdapter.notifyDataSetChanged();

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
