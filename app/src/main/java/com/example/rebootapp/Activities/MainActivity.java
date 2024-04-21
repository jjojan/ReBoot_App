package com.example.rebootapp.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.rebootapp.Fragments.HomeFragment;
import com.example.rebootapp.Fragments.ProfileFragment;
import com.example.rebootapp.MovieModel;
import com.example.rebootapp.R;
import com.example.rebootapp.Fragments.ReviewFragment;
import com.example.rebootapp.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811";
    public static final String TAG = "MainActivity";

    List<MovieModel> movieModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Resources.Theme theme = this.getTheme();
        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.color2));

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int colorCodeDark = Color.parseColor("#2B3743");
        window.setStatusBarColor(colorCodeDark);

        ActionBar actionBar;
        actionBar= getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(bottomNavigationView.getBackground());


        final FragmentManager fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        Log.i("fileName1", "waiting");
        if (getIntent().hasExtra("jsonArray")) {
            Log.i("fileName1", "" + getIntent().getStringExtra("jsonArray"));
        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.mHome:
                        HomeFragment homeFragment = new HomeFragment();
//                        Bundle bundle = new Bundle();
//                       Intent intent = getIntent();
//                        Log.i("fileName1", "waiting" );
//                        if (getIntent().hasExtra("jsonArray")){
//                            Log.i("fileName1", "it exists" );
//                        }
//                        String result = intent.getStringExtra("jsonArray");
//                        JSONArray jsonArray = null;
//                        try {
//                            jsonArray = new JSONArray(result);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                        Log.i("fileName1", "" + jsonArray);
//                        bundle.putString("result", result );
//                        homeFragment.setArguments(bundle);
                        fragment = homeFragment;
                        break;
                    case R.id.mSearch:
                        fragment = new SearchFragment();
                        break;
                    case R.id.mEvents:
                        fragment = new ReviewFragment();
                        break;
                    case R.id.mCart:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.fLayoutContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.mHome);
    }


//        RecyclerView rvMovies = findViewById(R.id.rvYourGames);
//        movieModel = new ArrayList<>();
//
//        MovieAdapter movieAdapter =  new MovieAdapter(this , movieModel);
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
//                    movieModel.addAll(MovieModel.fromJSONArray(results));
//                    movieAdapter.notifyDataSetChanged();
//                    Log.i(TAG, "Movies" + movieModel.size());
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



