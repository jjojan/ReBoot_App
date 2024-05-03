package com.example.rebootapp.Activities;

import static android.app.PendingIntent.getActivity;

import static com.parse.Parse.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.rebootapp.Fragments.ActivityFragment;
import com.example.rebootapp.Fragments.HomeFragment;
import com.example.rebootapp.Fragments.ProfileFragment;
import com.example.rebootapp.LoginActivity;
import com.example.rebootapp.MovieModel;
import com.example.rebootapp.R;
import com.example.rebootapp.Fragments.ReviewFragment;
import com.example.rebootapp.Fragments.SearchFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811";
    public static final String TAG = "MainActivity";

    List<MovieModel> movieModel;

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;

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


        FragmentManager fragmentManager = getSupportFragmentManager();

        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.navigation);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.fLayoutContainer, homeFragment).commit();


//        Intent intent = getIntent();
//        Log.i("fileName1", "waiting");
//        if (getIntent().hasExtra("jsonArray")) {
//            Log.i("fileName1", "" + getIntent().getStringExtra("jsonArray"));
//        }


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
                    case R.id.mFeed:
                        fragment = new ActivityFragment();
                        break;
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.fLayoutContainer, fragment).commit();
                return true;
            }
        });
        boolean profile = false;
        Intent intent = getIntent();
        System.out.println("got 2 here in main");
        if (intent != null && intent.getExtras() != null) {
            if (intent.getExtras().containsKey("openFragment")) {
                String fragmentToOpen = intent.getStringExtra("openFragment");
                if (fragmentToOpen != null && fragmentToOpen.equals("profile")) {
                    bottomNavigationView.setSelectedItemId(R.id.mCart);
                    profile = true;
                }
            }
        }
        //if(!profile) bottomNavigationView.setSelectedItemId(R.id.mHome);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().build();
                GoogleSignInClient gsc = GoogleSignIn.getClient(getApplicationContext(), gso);

                switch (menuItem.getItemId()) {
                    case R.id.nav_account: {
                        Fragment frag = new ProfileFragment();
                        fragmentManager.beginTransaction().replace(R.id.fLayoutContainer, frag).commit();
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_friends:{
                        Intent intent1 = new Intent(getApplicationContext(), FriendsActivity.class);
                        startActivity(intent1);
                        break;
                    }
                    case R.id.nav_favorites:{
                        Intent intent2 = new Intent(getApplicationContext(), FavoriteGamesActivity.class);
                        startActivity(intent2);
                        break;
                    }
                    case R.id.nav_logout:{
                        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                ParseUser.logOutInBackground(e -> {
                                    if (e == null) {
                                        SharedPreferences getUser = Parse.getApplicationContext().getSharedPreferences("user info", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor ed = getUser.edit();
                                        ed.remove("username");
                                        ed.apply();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {

                                        Log.e("LogoutError", "Parse logout failed", e);
                                    }
                                });
                            }
                        });
                        break;
                    }

                }
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(getApplicationContext(), gso);

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.nav_account: {
                Fragment frag = new ProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.fLayoutContainer, frag).commit();
                break;
            }
            case R.id.nav_friends:{
                Intent intent1 = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(intent1);
                break;
            }
            case R.id.nav_favorites:{
                Intent intent2 = new Intent(getApplicationContext(), FavoriteGamesActivity.class);
                startActivity(intent2);
                break;
            }
            case R.id.nav_logout:{
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ParseUser.logOutInBackground(e -> {
                            if (e == null) {
                                SharedPreferences getUser = Parse.getApplicationContext().getSharedPreferences("user info", Context.MODE_PRIVATE);
                                SharedPreferences.Editor ed = getUser.edit();
                                ed.remove("username");
                                ed.apply();
                                Intent intent4 = new Intent(getApplicationContext(), LoginActivity.class);
                                intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent4);
                            } else {

                                Log.e("LogoutError", "Parse logout failed", e);
                            }
                        });
                    }
                });
            }
            break;
        }

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            //fragmentManager.beginTransaction().replace(R.id.fLayoutContainer, fragment).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        System.out.println("got 1 here in main");
//        setIntent(intent);
//        handleIntent(intent);
//    }
//
//    public void handleIntent(Intent intent) {
//        System.out.println("got 2 here in main");
//        if (intent != null && intent.getExtras() != null) {
//            if (intent.getExtras().containsKey("openFragment")) {
//                String fragmentToOpen = intent.getStringExtra("openFragment");
//                if (fragmentToOpen != null && fragmentToOpen.equals("profile")) {
//                    openProfileFragment();
//                }
//            }
//        }
//    }

//    public void openProfileFragment(FragmentManager f) {
//        System.out.println("got 3 here in main");
//        ProfileFragment profileFragment = new ProfileFragment();
////        getSupportFragmentManager().beginTransaction()
//        f.beginTransaction()
//                .replace(R.id.fLayoutContainer, profileFragment)
//                .commit();
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.mCart);
//    }


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



