package com.example.rebootapp.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.rebootapp.Adapters.ReviewAdapter;
import com.example.rebootapp.Adapters.UserListNamesAdapter;
import com.example.rebootapp.GameModel;
import com.example.rebootapp.Models.ReviewModel;
import com.example.rebootapp.Models.UserListModel;
import com.example.rebootapp.R;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;

public class GameDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    com.example.rebootapp.databinding.ActivityGameDetailsNewBinding binding;
    GameModel movie;

    List<GameModel> gameModel;


    String currentUserID;

    String userName;

    String gameID;


    String GAME_URL = "https://api.rawg.io/api/games/";
    ArrayList<ReviewModel> reviewList;
    ReviewAdapter reviewAdapter;

    ToggleButton imgFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        binding = com.example.rebootapp.databinding.ActivityGameDetailsNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        movie = (GameModel) Parcels.unwrap(getIntent().getParcelableExtra(GameModel.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));
        Log.i("MARDUK",
                "Tıtle: " + movie.getTitle() + "\nOverView: " + movie.getOverview() + "\n ID: " + movie.getID() +
                        "\nBackgropPath: " + movie.getBackdropPath() +
                        "\nPosterPath: " + movie.getPosterPath() + "\nVote: " + movie.getVoteAverage());
        gameID = movie.getID();
        binding.tvTitle.setText(movie.getTitle());
        binding.tvGameDescription.setText(movie.getOverview());
        binding.tvReleaseDate.setText( "Release Date: " + movie.getReleaseDate());
        imgFav = findViewById(R.id.imgFav);


        Glide.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(binding.imgPreview);

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUserID = currentUser.getObjectId();

        // Create a query for the _User table
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // Filter the query to find the user with a specific objectId
        query.whereEqualTo("objectId", currentUserID);

        // Execute the query and get the result
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    // User found, get the username
                    userName = user.getUsername();
                    // Use the username (e.g., display it in a TextView)
                    Log.d("userName", "userName: " + userName);
                } else {
                    // An error occurred, log the error message
                    Log.e("ParseError", "User not found: " + e.getMessage());
                }
            }
        });


        Log.i("id", gameID);
        GAME_URL = GAME_URL + gameID + "?key=63502b95db9f41c99bb3d0ecf77aa811";
        Log.i("id", GAME_URL);
        String favPath = movie.getPosterPath();

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(GAME_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("sucess", "");
                JSONObject jsonObject = json.jsonObject;
                try {
                    Log.i("id", jsonObject.getString("description_raw"));
                    binding.tvGameDescription.setText(jsonObject.getString("description_raw"));


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i("RETURN results", "Results" + results.toString());
                    gameModel.addAll(GameModel.fromJSONArray(results));
                    Log.i("return list", "Movies" + gameModel.toString());

                } catch (JSONException e) {
                    Log.e("error", "hit json expception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("fail", "onFailure");
            }
        });

        checkMovieID(currentUserID, gameID, isEmpty -> imgFav.setChecked(!isEmpty));

        binding.imgFav.setOnClickListener(v -> {
            if (imgFav.isChecked()) {
                addFavoriteGame(currentUserID, gameID, favPath);
            } else {
                removeFavoriteGame(currentUserID, gameID);
            }
        });


        binding.imgAddToList.setOnClickListener(v -> {
            addToUserListDialog();
        });
//        binding.reviewButton.setOnClickListener(view -> checkIfUserReviewed(currentUserID,gameID));
//        fetchReviews2();
        fetchBlocked();
        fetchRatings();
        binding.tvAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewAdapter.updateData(reviewList);
                binding.tvAllReviews.setVisibility(View.GONE);
            }
        });
        binding.btnSaveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeReview2();
            }
        });
    }

    public void fetchRatings() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("GameID", gameID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> reviews, ParseException e) {
                if (e == null) {
                    List<Float> oneStarReviews = new ArrayList<>();
                    List<Float> twoStarReviews = new ArrayList<>();
                    List<Float> threeStarReviews = new ArrayList<>();
                    List<Float> fourStarReviews = new ArrayList<>();
                    List<Float> fiveStarReviews = new ArrayList<>();

                    for (ParseObject reviewObject : reviews) {
                        float ratingStar = reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0;

                        // Yuvarlama işlemi ve doğru listeye ekleme
                        int roundedRating = Math.round(ratingStar);
                        switch (roundedRating) {
                            case 1:
                                oneStarReviews.add(ratingStar);
                                break;
                            case 2:
                                twoStarReviews.add(ratingStar);
                                break;
                            case 3:
                                threeStarReviews.add(ratingStar);
                                break;
                            case 4:
                                fourStarReviews.add(ratingStar);
                                break;
                            case 5:
                                fiveStarReviews.add(ratingStar);
                                break;
                            default:
                                // Eğer 1-5 dışında bir değer varsa, bu bir hata olabilir
                                Log.e("fetchReviews", "Yıldız değerlendirme dışında bir değer bulundu: " + ratingStar);
                        }
                    }

                    // Öncelikle tüm listelerdeki float değerlerini bir listeye ekleyelim
                    ArrayList<Float> allRatings = new ArrayList<>();
                    allRatings.addAll(oneStarReviews);
                    allRatings.addAll(twoStarReviews);
                    allRatings.addAll(threeStarReviews);
                    allRatings.addAll(fourStarReviews);
                    allRatings.addAll(fiveStarReviews);

                    // Toplam puanı ve ortalama puanı hesaplayalım
                    float totalRating = 0;
                    for (Float rating : allRatings) {
                        totalRating += rating;
                    }
                    // Aritmetik ortalamayı hesaplayalım
                    float averageRating = totalRating / allRatings.size();
                    // Virgülden sonra tek hane olacak şekilde formatlayalım
                    averageRating = Math.round(averageRating * 10) / 10.0f;
                    // Sonucu TextView'a set edelim
                    binding.tvRatingCount.setText(String.valueOf(averageRating));
                    binding.ratingBarMain.setRating(averageRating);
                    binding.progressBar5.setMax(allRatings.size());
                    binding.progressBar5.setProgress(fiveStarReviews.size());
                    binding.progressBar4.setMax(allRatings.size());
                    binding.progressBar4.setProgress(fourStarReviews.size());
                    binding.progressBar3.setMax(allRatings.size());
                    binding.progressBar3.setProgress(threeStarReviews.size());
                    binding.progressBar2.setMax(allRatings.size());
                    binding.progressBar2.setProgress(twoStarReviews.size());
                    binding.progressBar1.setMax(allRatings.size());
                    binding.progressBar1.setProgress(oneStarReviews.size());

                } else {
                    Log.e("fetchReviews", "Error: " + e.getMessage());
                }
            }
        });
    }

//    public void fetchReviews() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
//        query.whereEqualTo("GameID", gameID);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> reviews, ParseException e) {
//                if (e == null) {
//                    reviewList = new ArrayList<>();
//                    for (ParseObject reviewObject : reviews) {
//                        String reviewUser = reviewObject.getString("ReviewUser") != null ? reviewObject.getString("ReviewUser") : "";
//                        String reviewUserName = reviewObject.getString("ReviewUsername") != null
//                                ? reviewObject.getString("ReviewUsername") : "";
//                        String reviewText = reviewObject.getString("ReviewText") != null ? reviewObject.getString("ReviewText") : "";
//                        String gameID = reviewObject.getString("GameID") != null ? reviewObject.getString("GameID") : "";
//                        String objectId = reviewObject.getObjectId();
//                        boolean isShowOnlyFriends = reviewObject.getBoolean("isShowOnlyFriends");
//                        // Number to float conversion with null check
//                        float ratingStar = reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0;
//                        int upCount = reviewObject.has("upCount") ? reviewObject.getInt("upCount") : 0;
//                        int downCount = reviewObject.has("downCount") ? reviewObject.getInt("downCount") : 0;
//                        ReviewModel review = new ReviewModel(
//                                reviewUser,
//                                reviewUserName,
//                                reviewText,
//                                gameID,
//                                objectId,
//                                reviewObject.getCreatedAt(),
//                                reviewObject.getUpdatedAt(),
//                                isShowOnlyFriends,
//                                ratingStar,
//                                upCount,
//                                downCount
//                        );
//
//                        reviewList.add(review);
//                    }
//                    ArrayList<ReviewModel> limitedList = reviewList.size() > 5 ?
//                            new ArrayList<>(reviewList.subList(0, 5)) : new ArrayList<>(reviewList);
//                    reviewAdapter = new ReviewAdapter(GameDetailsActivity.this, limitedList);
//                    binding.recyclerView.setAdapter(reviewAdapter);
//                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(GameDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
//
//
//                } else {
//                    Log.e("fetchReviews", "Error: " + e.getMessage());
//                }
//            }
//        });
//
//    }

    public void fetchBlocked() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", currentUserID);
        ParseCloud.callFunctionInBackground("getBlockedAndBlockedBy", params, new FunctionCallback<ArrayList<String>>() {
            @Override
            public void done(ArrayList<String> list, ParseException e){
                if (e == null){

                    fetchReviews2(list);
                }
            }
        });

    }
    public void fetchReviews2(ArrayList<String> blocked){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("GameID", gameID);
        query.whereNotContainedIn("ReviewUser", blocked);
        query.include("source_user");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> reviews, ParseException e) {
                if (e == null) {
                    reviewList = new ArrayList<>();
                    for (ParseObject reviewObject : reviews) {
                        ParseUser user = reviewObject.getParseUser("source_user");
                        if(user != null) {
                            String reviewUserName = user.getString("username");
                            ParseFile picFile = user.getParseFile("profile_pic");
                            String picUrl = picFile != null ? picFile.getUrl() : null;
                            String reviewUser = reviewObject.getString("ReviewUser") != null ? reviewObject.getString("ReviewUser") : "";
                            //                        String reviewUserName = reviewObject.getString("ReviewUsername") != null
                            //                                ? reviewObject.getString("ReviewUsername") : "";
                            String reviewText = reviewObject.getString("ReviewText") != null ? reviewObject.getString("ReviewText") : "";
                            String gameID = reviewObject.getString("GameID") != null ? reviewObject.getString("GameID") : "";
                            String objectId = reviewObject.getObjectId();
                            boolean isShowOnlyFriends = reviewObject.getBoolean("isShowOnlyFriends");
                            // Number to float conversion with null check
                            float ratingStar = reviewObject.getNumber("ratingStar") != null ? reviewObject.getNumber("ratingStar").floatValue() : 0;
                            int upCount = reviewObject.has("upvotes") ? reviewObject.getInt("upvotes") : 0;
                            int downCount = reviewObject.has("downvotes") ? reviewObject.getInt("downvotes") : 0;

                            int absCount = Math.abs(upCount - downCount);
                            if (upCount >= downCount){
                                upCount = absCount;
                                downCount = 0;
                            }
                            else{
                                downCount = absCount;
                                upCount = 0;
                            }

                            ReviewModel review = new ReviewModel(
                                    reviewUser,
                                    reviewUserName,
                                    reviewText,
                                    gameID,
                                    objectId,
                                    reviewObject.getCreatedAt(),
                                    reviewObject.getUpdatedAt(),
                                    isShowOnlyFriends,
                                    ratingStar,
                                    upCount,
                                    downCount,
                                    picUrl
                            );

                            reviewList.add(review);
                        }
                    }
                    ArrayList<ReviewModel> limitedList = reviewList.size() > 5 ?
                            new ArrayList<>(reviewList.subList(0, 5)) : new ArrayList<>(reviewList);
                    reviewAdapter = new ReviewAdapter(GameDetailsActivity.this, limitedList);
                    binding.recyclerView.setAdapter(reviewAdapter);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(GameDetailsActivity.this, LinearLayoutManager.VERTICAL, false));


                } else {
                    Log.e("fetchReviews", "Error: " + e.getMessage());
                }
            }
        });
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

        Button btnAddNewList = customView.findViewById(R.id.btnNewList);
        Button btnClose = customView.findViewById(R.id.btnClose);
        RecyclerView recyclerView = customView.findViewById(R.id.recyclerView);
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
        query.whereEqualTo("userID", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> customUserLists, ParseException e) {
                if (e == null) {

                    ArrayList<UserListModel> userListModelArrayList = new ArrayList<>();
                    for (ParseObject object : customUserLists) {
                        String listName = object.getString("listName");
                        List<String> gameName = object.getList("gameName");
                        List<String> gamePreviewLink = object.getList("gamePreviewLink");
                        String userID = object.getString("userID");
                        List<String> gameID = object.getList("gameID");
                        String objectID = object.getObjectId();


                        UserListModel model = new UserListModel(listName, gameName,
                                gamePreviewLink, gameID, userID, objectID);
                        userListModelArrayList.add(model);
                    }

                    UserListNamesAdapter userListNamesAdapter =
                            new UserListNamesAdapter(GameDetailsActivity.this,
                                    userListModelArrayList, movie.getID(), movie.getTitle()
                                    , movie.getPosterPath());
                    recyclerView.setAdapter(userListNamesAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(GameDetailsActivity.this));
                } else {

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
        EditText etListName = customView.findViewById(R.id.etListName);
        final AlertDialog createNewListBuilder = createNewListDialog.create();

        Button btnAdd = customView.findViewById(R.id.btnAdd);
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
                                            Log.i("YARDUK", e.getMessage().toString());
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

    public void addFavoriteGame(String UserID, String gameID, String path) {
        ParseObject object = new ParseObject("FavoriteGames");
        object.put("user_id", UserID);
        object.put("game_id", gameID);
        object.put("picture_uri", path);
        object.saveInBackground();

    }


//    public void addGame(String GameID, ParseObject review) {
//
//        try {
//            ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomListGameModel");
//            query.whereEqualTo("GameID", GameID);
//            query.getFirstInBackground(new GetCallback<ParseObject>() {
//                @Override
//                public void done(ParseObject object, ParseException e) {
//                    if (e == null) {
//                        Log.i("gameModel exists", "arready exists");
//                        object.add("ReviewArray", review);
//                        object.saveInBackground();
//                    } else {
//                        ParseObject game = new ParseObject("CustomListGameModel");
//                        game.put("GameID", gameID);
//                        game.add("ReviewArray", review);
//                        game.saveInBackground();
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            System.out.println("Parse Error in Saving");
//        }
//
//
//    }

    public void removeFavoriteGame(String UserID, String gameID) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FavoriteGames");
            query.whereEqualTo("user_id", UserID);
            query.whereEqualTo("game_id", gameID);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        object.deleteInBackground();
                    } else System.out.println("cannot find to delete");
                }
            });

        } catch (Exception e) {
            System.out.println("Parse Problem deleting");
        }
    }

    public void checkMovieID(String userID, String gameID, QueryCheckCallback callback) {
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
        } catch (Exception e) {
            System.out.println("Parse problem checking");
        }

    }


//    public void checkIfUserReviewed(String userId, String gameId) {
//        // Create a query for the Review table
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
//        // Filter for reviews made by the current user for the specific game
//        query.whereEqualTo("ReviewUser", userId);
//        query.whereEqualTo("GameID", gameId);
//
//        // Execute the query
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> reviews, ParseException e) {
//                if (e == null && !reviews.isEmpty()) {
//                    // User has already reviewed this game, show the existing review with an option to delete
//                    ParseObject review = reviews.get(0); // Assuming there's only one review per user per game
//                    AlertDialog.Builder builder = new AlertDialog.Builder(GameDetailsActivity.this);
//                    builder.setTitle("Your Review");
//                    String reviewText = review.getString("ReviewText");
//                    builder.setMessage(reviewText);
//                    builder.setPositiveButton("Delete", (dialog, which) -> {
//                        // Delete the review
//                        review.deleteInBackground(e1 -> {
//                            if (e1 == null) {
//                                Toast.makeText(GameDetailsActivity.this, "Review deleted successfully.", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(GameDetailsActivity.this, "Failed to delete review: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    });
//                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                } else if (e != null) {
//                    // An error occurred
//                    Log.e("checkIfUserReviewed", "Error: " + e.getMessage());
//                } else {
//                    // User has not reviewed this game, show the review dialog
//                    writeReview();
//                }
//            }
//        });
//    }


//    public void writeReview() {
//
//        String reviewText = binding.etReviewBox.getText().toString();
//        float ratingFloat = binding.ratingBarReview.getRating();
//        boolean isShowOnlyFriend ;
//        if (binding.spinner.getSelectedItem().equals("Everyone")){
//            isShowOnlyFriend=false;
//        }else {
//            isShowOnlyFriend=true;
//        }
//
//        if (reviewText.isEmpty()) {
//            Toast.makeText(GameDetailsActivity.this, "Please write a review!", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (ratingFloat == 0) {
//            Toast.makeText(GameDetailsActivity.this, "Please select a review!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // create new parse objecet
//        ParseObject review = new ParseObject("Review");
//        review.put("ReviewUser", currentUserID);
//        review.put("ReviewUsername", userName);
//        review.put("GameID", gameID);
//        review.put("ReviewText", reviewText);
//        review.put("isShowOnlyFriends", isShowOnlyFriend);
//        review.put("ratingStar", ratingFloat);
//        ProgressDialog progressDialog=new ProgressDialog(GameDetailsActivity.this);
//        progressDialog.setMessage("Posting...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        // save to DB
//        review.saveInBackground(e -> {
//            if (e == null) {
//                // succesful
//                Toast.makeText(GameDetailsActivity.this, "Review saved successfully!", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//
//            } else {
//                // Error
//                Toast.makeText(GameDetailsActivity.this, "Error saving review: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }

    public void writeReview2(){
        String reviewText = binding.etReviewBox.getText().toString();
        float ratingFloat = binding.ratingBarReview.getRating();
        boolean isShowOnlyFriend ;

        if (binding.spinner.getSelectedItem().equals("Everyone")){
            isShowOnlyFriend=false;
        }else {
            isShowOnlyFriend=true;
        }
        if (reviewText.isEmpty()) {
            Toast.makeText(GameDetailsActivity.this, "Please write a review!", Toast.LENGTH_SHORT).show();
            return;
        } else if (ratingFloat == 0) {
            Toast.makeText(GameDetailsActivity.this, "Please select a review!", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject review = new ParseObject("Review");
        review.put("ReviewUser", currentUserID);
        review.put("ReviewUsername", userName);
        review.put("GameID", gameID);
        review.put("ReviewText", reviewText);
        review.put("isShowOnlyFriends", isShowOnlyFriend);
        review.put("ratingStar", ratingFloat);
        review.put("source_user", currentUser);
        ProgressDialog progressDialog=new ProgressDialog(GameDetailsActivity.this);
        progressDialog.setMessage("Posting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        review.saveInBackground(e -> {
            if (e == null) {
                // succesful
                Toast.makeText(GameDetailsActivity.this, "Review saved successfully!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                binding.etReviewBox.getText().clear();
                finish();
                startActivity(getIntent());

            } else {
                // Error
                Toast.makeText(GameDetailsActivity.this, "Error saving review: " + e.getMessage(), Toast.LENGTH_LONG).show();
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