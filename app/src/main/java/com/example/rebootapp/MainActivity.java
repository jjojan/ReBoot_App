package com.example.rebootapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811";
    public static final String TAG = "MainActivity";

    List<Movie> movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        final FragmentManager fragmentManager = getSupportFragmentManager();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.mHome:
                        fragment = new HomeFragment();
                        break;
                    case R.id.mSearch:
                        fragment = new HomeFragment();
                        break;
                    case R.id.mEvents:
                        fragment = new HomeFragment();
                        break;
                    case R.id.mCart:
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.fLayoutContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.mHome);
    }




//        RecyclerView rvMovies = findViewById(R.id.rvYourGames);
//        movie = new ArrayList<>();
//
//        MovieAdapter movieAdapter =  new MovieAdapter(this , movie);
//        rvMovies.setAdapter(movieAdapter);
//        rvMovies.setLayoutManager(new LinearLayoutManager(this ));
//
//        AsyncHttpClient client = new AsyncHttpClient();
//
//        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                Log.d(TAG, "onSucess");
//                JSONObject jsonObject = json.jsonObject;
//                try{
//                    JSONArray results = jsonObject.getJSONArray("results");
//                    Log.i(TAG, "Results" + results.toString());
//                    movie.addAll(Movie.fromJSONArray(results));
//                    movieAdapter.notifyDataSetChanged();
//                    Log.i(TAG, "Movies" + movie.size());
//                } catch(JSONException e){
//                    Log.e(TAG, "hit json expception", e);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.d(TAG, "onFailure");
//            }
//        });
//
}


