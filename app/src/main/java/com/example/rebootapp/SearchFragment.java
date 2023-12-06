package com.example.rebootapp;

import static java.sql.DriverManager.println;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
//import androidx.appcompat.widget.SearchView;
import android.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class SearchFragment extends Fragment {

    List<GameSearch> Games; //Game Model List for RecyclerView&Adapter

    String search_term = "";//"spiderman" + "&ordering=-added"; //Test String
    private MenuItem menuItem;
    private SearchView searchView;

    private SearchView svSearch;
    String SEARCH_QUERY = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811" + search_term;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //Set up RecyclerView, Apadpter, and Layout Manager to format scroll view
//        RecyclerView searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
//        Games = new ArrayList<>();
//        GameAdapter searchGameAdapter = new GameAdapter(getContext(), Games);
//        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        searchRecyclerView.setAdapter(searchGameAdapter);
//
//        //Create request client
//        AsyncHttpClient client = new AsyncHttpClient();
//        //Http Query
//        client.get(SEARCH_QUERY, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                //Log.d(TAG, "onSucess");
//                JSONObject jsonObject = json.jsonObject;
//                try {
//                    JSONArray results = jsonObject.getJSONArray("results");
//                    //Log.i(TAG, "Results" + results.toString());
//                    Games.addAll(Game.fromJSONArray(results));
//                    searchGameAdapter.notifyDataSetChanged();
//                    println("hello");
//                    //Log.i(TAG, "Movies" + searchGame.size());
//                } catch (JSONException e) {
//                    //Log.e(TAG, "hit json expception", e);
//                }
//            }
//
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                //Log.d(TAG, "onFailure");
//                println("hello");
//            }
//        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        svSearch = view.findViewById(R.id.svSearch);
        RecyclerView searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        Games = new ArrayList<>();
        GameSearchAdapter searchGameAdapter = new GameSearchAdapter(getContext(), Games);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchRecyclerView.setAdapter(searchGameAdapter);

        //Create request client
        AsyncHttpClient client = new AsyncHttpClient();

        //Http Query
        client.get(SEARCH_QUERY, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //Log.d(TAG, "onSucess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    //Log.i(TAG, "Results" + results.toString());
                    Games.addAll(GameSearch.fromJSONArray(results));
                    searchGameAdapter.notifyDataSetChanged();
                    println("hello");
                    //Log.i(TAG, "Movies" + searchGame.size());
                } catch (JSONException e) {
                    //Log.e(TAG, "hit json expception", e);
                }
            }


            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                //Log.d(TAG, "onFailure");
                println("hello");
            }
        });



        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("new", query);

                svSearch = view.findViewById(R.id.svSearch);
                RecyclerView searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
                Games = new ArrayList<>();
                GameSearchAdapter searchGameAdapter = new GameSearchAdapter(getContext(), Games);
                searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                searchRecyclerView.setAdapter(searchGameAdapter);
                String search = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&&search_precise&search=" + query + "&ordering=-added";
                client.get(search, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try{
                            JSONArray results = jsonObject.getJSONArray("results");
                            //Log.i(TAG, "Results" + results.toString());
                            Games.addAll(GameSearch.fromJSONArray(results));
                            searchGameAdapter.notifyDataSetChanged();
                            println("hello");
                            //Log.i(TAG, "Movies" + searchGame.size());
                        } catch(JSONException e){
                            //Log.e(TAG, "hit json expception", e);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_bar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item, menu);
        menuItem = menu.findItem(R.id.search_bar);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String search = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&search=" + newText + "&ordering=-added";

                //Set up RecyclerView, Apadpter, and Layout Manager to format scroll view
                //RecyclerView searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
                List<Game> searchGames = new ArrayList<>();
                GameAdapter searchGameAdapter = new GameAdapter(getContext(), searchGames);
                //searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                //searchRecyclerView.setAdapter(searchGameAdapter);

                //Create request client
                AsyncHttpClient client = new AsyncHttpClient();
                //Http Query
                client.get(search , new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        //Log.d(TAG, "onSucess");
                        JSONObject jsonObject = json.jsonObject;
                        try{
                            JSONArray results = jsonObject.getJSONArray("results");
                            //Log.i(TAG, "Results" + results.toString());
                            Games.addAll(GameSearch.fromJSONArray(results));
                            searchGameAdapter.notifyDataSetChanged();
                            println("hello");
                            //Log.i(TAG, "Movies" + searchGame.size());
                        } catch(JSONException e){
                            //Log.e(TAG, "hit json expception", e);
                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        //Log.d(TAG, "onFailure");
                        println("hello");
                    }
                });

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    }

