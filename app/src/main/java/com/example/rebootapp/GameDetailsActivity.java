package com.example.rebootapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.colormoon.readmoretextview.ReadMoreTextView;
import com.example.rebootapp.ListFiles.UserListModel;
import com.example.rebootapp.ListFiles.UserListNamesAdapter;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class GameDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Game movie;

    List<Game> game;

    List<ParseObject> adapterObjects;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivPoster;
    ToggleButton heartButton;

    ImageButton reviewButton;

    String UserID;

    String Username;

    String tempID;

    RecyclerView recyclerView;



    ReadMoreTextView tvDesc;
    int saveFavoriteQueue = 0;

    public Spinner spinnerTextSize;

    ImageView enter;

    String GAME_URL = "https://api.rawg.io/api/games/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        heartButton = findViewById(R.id.toggleButton);
        tvDesc = findViewById(R.id.tvDesc);
        reviewButton = findViewById(R.id.reviewButton);
        enter = findViewById(R.id.add);


        showWriteReview();




                movie = (Game) Parcels.unwrap(getIntent().getParcelableExtra(Game.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));
        Log.i("MARDUK",
                "Tıtle: "+movie.getTitle()+"\nOverView: "+movie.getOverview()+"\n ID: "+movie.getID()+
                        "\nBackgropPath: "+movie.getBackdropPath()+
                        "\nPosterPath: "+movie.getPosterPath()+"\nVote: "+movie.getVoteAverage());
        tempID = movie.getID();
        displayReviews();
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());


//        float voteAverage = movie.getVoteAverage().floatValue();
//        rbVoteAverage.setRating(voteAverage / 2.0f);

        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        Glide.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(ivPoster);




        Log.i("id", tempID);
        GAME_URL = GAME_URL + tempID + "?key=63502b95db9f41c99bb3d0ecf77aa811";
        Log.i("id", GAME_URL);
        String favPath = movie.getPosterPath();

        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserID = currentUser.getObjectId();


        AsyncHttpClient client = new AsyncHttpClient();


        client.get(GAME_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("sucess", "");
                JSONObject jsonObject = json.jsonObject;
                try {
                    Log.i("id", jsonObject.getString("description_raw"));
                    tvDesc.setText(jsonObject.getString("description_raw"));



                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i("RETURN results", "Results" + results.toString());
                    game.addAll(Game.fromJSONArray(results));
                    Log.i("return list", "Movies" + game.toString());

                } catch(JSONException e){
                    Log.e("error", "hit json expception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("fail", "onFailure");
            }
        });




        checkMovieID(currentUserID, tempID, new QueryCheckCallback() {
            @Override
            public void onResult(boolean isEmpty) {
                heartButton.setChecked(!isEmpty);
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(heartButton.isChecked()){
                    addFavoriteGame(currentUserID, tempID, favPath);
                }
                else{
                    removeFavoriteGame(currentUserID, tempID);
                }
            }
        });


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToUserListDialog();
//                String text = favPath;
//                addListGame(currentUserID, tempID, favPath);

            }
        });





        spinnerTextSize = findViewById(R.id.spinnerTextSize);

        spinnerTextSize.setOnItemSelectedListener(this);

        String[] textSizes = getResources().getStringArray(R.array.font_sizes);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, textSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerTextSize.setAdapter(adapter);


    }
    public void addToUserListDialog() {
        // Inflate the custom layout using layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.layout_user_list, null);

        // Apply the custom style to the AlertDialog
        AlertDialog.Builder listDialog = new AlertDialog.Builder(
                new androidx.appcompat.view.ContextThemeWrapper(this, R.style.AlertDialogCustom));

        listDialog.setView(customView); // Set the custom view for the dialog
        AlertDialog userListDialogBuilder = listDialog.create();

        Button btnAddNewList=customView.findViewById(R.id.btnNewList);
        Button btnClose=customView.findViewById(R.id.btnClose);
        RecyclerView recyclerView=customView.findViewById(R.id.recyclerView);
        btnAddNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userListDialogBuilder.dismiss();
                createNewList();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            userListDialogBuilder.dismiss();
            }
        });
        ParseUser currentUser = ParseUser.getCurrentUser();
        String userId = currentUser.getObjectId();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomUserList");
                query.whereEqualTo("userID", userId); // objectId'ler içinde sorgula
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> customUserLists, ParseException e) {
                        if (e == null) {
                            // Sorgu başarılı, listName'leri çekiyoruz
                            ArrayList<UserListModel> userListModelArrayList = new ArrayList<>();
                            for (ParseObject object : customUserLists) {
                                String listName = object.getString("listName");
                                List<String> gameName = object.getList("gameName");
                                List<String> gamePreviewLink = object.getList("gamePreviewLink");
                                String userID = object.getString("userID");
                                List<String> gameID = object.getList("gameID");
                                String objectID = object.getObjectId(); // ParseObject'in kendine özgü ID'si

                                // Model nesnesini oluştur ve listeye ekle
                                UserListModel model = new UserListModel(listName, gameName,
                                        gamePreviewLink,gameID, userID, objectID);
                                userListModelArrayList.add(model);
                            }
                            // TODO: Burada RecyclerView Adapter'ını güncelle veya başka bir işlem yap
                            UserListNamesAdapter userListNamesAdapter=
                                    new UserListNamesAdapter(GameDetailsActivity.this,
                                            userListModelArrayList,movie.getID(),movie.getTitle()
                                            ,movie.getPosterPath());
                            recyclerView.setAdapter(userListNamesAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(GameDetailsActivity.this));
                        } else {
                            // Sorgu sırasında hata oluştu, hata mesajını logla veya göster
                            Log.e("ParseError", "Error retrieving CustomUserList: " + e.getMessage());
                        }
                    }
                });



        userListDialogBuilder.show();
    }
    public void createNewList() {
        // Inflate the custom layout using layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.layout_new_list, null);

        // Apply the custom style to the AlertDialog
        AlertDialog.Builder createNewListDialog = new AlertDialog.Builder(
                new androidx.appcompat.view.ContextThemeWrapper(this, R.style.AlertDialogCustom));

        createNewListDialog.setView(customView); // Set the custom view for the dialog
        EditText etListName= customView.findViewById(R.id.etListName);
        final AlertDialog createNewListBuilder = createNewListDialog.create();

        Button btnAdd= customView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String listName = etListName.getText().toString();

                if (!listName.isEmpty()) {
                    // Check if the list name already exists
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomUserList");
                    query.whereEqualTo("listName", listName);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> lists, ParseException e) {
                            if (e == null && lists.isEmpty()) {
                                // List name doesn't exist, create new list
                                ParseObject customUserList = new ParseObject("CustomUserList");
                                customUserList.put("listName", listName);
                                customUserList.put("userID", currentUser.getObjectId());
                                // Save the new list asynchronously
                                customUserList.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            createNewListBuilder.dismiss();
                                            Toast.makeText(GameDetailsActivity.this, "The list has been created successfully: " + listName, Toast.LENGTH_SHORT).show();
                                            currentUser.add("userList", customUserList); // Add the new object to the user list
                                            addToUserListDialog();
                                            // Update the user
                                            currentUser.saveInBackground(e1 -> {
                                                if (e1 == null) {
                                                    // User updated successfully
                                                } else {
                                                    // Error updating the user
                                                }
                                            });
                                        } else {
                                            createNewListBuilder.dismiss();
                                            Toast.makeText(GameDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.i("YARDUK",e.getMessage().toString());
                                        }
                                    }
                                });
                            } else if (e == null) {
                                // List name exists, show a toast message
                                Toast.makeText(GameDetailsActivity.this, "A list with this name already exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                // An error occurred during the query
                                Toast.makeText(GameDetailsActivity.this,
                                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                } else {
                    Toast.makeText(GameDetailsActivity.this, "Please enter a list name!", Toast.LENGTH_SHORT).show();
                }
            }
        });




        createNewListBuilder.show();
    }
    public void addFavoriteGame(String UserID, String gameID, String path){
        ParseObject object = new ParseObject("FavoriteGames");
        object.put("user_id", UserID);
        object.put("game_id", gameID);
        object.put("picture_uri", path);
        object.saveInBackground();

    }

    public void addListGame(String UserID, String gameID, String path){
        ParseObject object = new ParseObject("ListGames");
        object.put("user_id", UserID);
        object.put("game_id", gameID);
        object.put("picture_uri", path);
        object.saveInBackground();

    }

    public void addReview(String UserID, String username, String reviewText){

        ParseObject object = new ParseObject("Review");
        object.put("ReviewUser", UserID);
        object.put("ReviewUsername", username);
        object.put("ReviewText", reviewText);
        object.put("GameID", tempID);
        object.saveInBackground();

        addGame(tempID, object);



    }



    public void displayReviews(){

        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
            Log.i("temp ID", tempID);
            query.whereEqualTo("GameID", tempID);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){


//                        Log.i("review exists", objects.get(0).getString("ReviewText") + "size");
                        int i = 0;

                        recyclerView = (RecyclerView) findViewById(R.id.rvReviews);
                        recyclerView.setLayoutManager(new LinearLayoutManager(GameDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        ReviewAdapter adapter = new ReviewAdapter(objects, GameDetailsActivity.this);
                        Log.i("adapter rsult", adapter.toString());
                        recyclerView.setAdapter(adapter);

                        for (ParseObject item : objects) {
                            Log.i("review exists", objects.get(i).getString("ReviewText") + "size");
                            i += 1;
                        }


                    }
                    else {
                    }

                }
            });

        }catch (Exception e){
            System.out.println("Parse Error Getting Reviews");
            Log.e("MYAPP", "exception", e);
        }


    }

    public void addGame(String GameID, ParseObject review){

        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("GameModel");
            query.whereEqualTo("GameID", GameID);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null){
                        Log.i("game exists", "arready exists");
                        object.add("ReviewArray", review);
                        object.saveInBackground();
                    }
                    else {
                        ParseObject game = new ParseObject("GameModel");
                        game.put("GameID", tempID);
                        game.add("ReviewArray", review);
                        game.saveInBackground();
                    }
                }
            });

        }catch (Exception e){
            System.out.println("Parse Error in Saving");
        }


    }

    public void removeFavoriteGame(String UserID, String gameID){
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FavoriteGames");
            query.whereEqualTo("user_id", UserID);
            query.whereEqualTo("game_id", gameID);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null){
                        object.deleteInBackground();
                    }
                    else System.out.println("cannot find to delete");
                }
            });

        }catch (Exception e){
            System.out.println("Parse Problem deleting");
        }
    }
    public void checkMovieID(String userID, String gameID, QueryCheckCallback callback){
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FavoriteGames");
            query.whereEqualTo("user_id", userID);
            query.whereEqualTo("game_id", gameID);

            query.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (e == null) {
                        callback.onResult(count == 0);
                    } else callback.onResult(false);

                }
            });
        } catch (Exception e){
            System.out.println("Parse problem checking");
        }

    }

    private void  showWriteReview(){
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder( GameDetailsActivity.this );
                builder.setTitle("Write Review");
                EditText etReview = new EditText(GameDetailsActivity.this);
                builder.setView(etReview);

                ParseUser currentUser = ParseUser.getCurrentUser();
                String currentUserObjectID = currentUser.getObjectId();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");

                query.getInBackground(currentUserObjectID, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e==null){
                             UserID = object.getObjectId().toString();
                             Username = object.getString("username");
                             Log.i("userID", UserID);
                             Log.i("userID", Username);
                        }
                    }
                });

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String reviewText = etReview.getText().toString();
                        addReview(UserID, Username, reviewText);
                        Toast.makeText(GameDetailsActivity.this, "Review: " + reviewText, Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface QueryCheckCallback {
        void onResult(boolean isEmpty);
    }

    @Override
    protected void onPause() {
        super.onPause();



    }


}