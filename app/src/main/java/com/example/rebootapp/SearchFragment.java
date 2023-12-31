package com.example.rebootapp;

import static com.parse.Parse.getApplicationContext;
import static java.sql.DriverManager.println;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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

    List<GameSearch> searchGames; //Game Model List for RecyclerView&Adapter

    String search_term = "";//"spiderman" + "&ordering=-added"; //Test String
    private MenuItem menuItem;
    private SearchView searchView;

    private SearchView svSearch;
    String SEARCH_QUERY = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811" + search_term;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        AsyncHttpClient client = new AsyncHttpClient();
        super.onViewCreated(view, savedInstanceState);
        svSearch = view.findViewById(R.id.svSearch);
        RecyclerView searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        searchGames = new ArrayList<>();
        GameSearchAdapter gameSearchAdapter = new GameSearchAdapter(getApplicationContext(), searchGames);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchRecyclerView.setAdapter(gameSearchAdapter);
        String search = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&&search_precise&search=";

        //Create request client


        //Http Query
        client.get(SEARCH_QUERY, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //Log.d(TAG, "onSucess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    //Log.i(TAG, "Results" + results.toString());
                    searchGames.addAll(GameSearch.fromJSONArray(results));
                    gameSearchAdapter.notifyDataSetChanged();
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
                searchGames = new ArrayList<>();
                GameSearchAdapter gameSearchAdapter = new GameSearchAdapter(getContext(), searchGames);
                searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                searchRecyclerView.setAdapter(gameSearchAdapter);
                String search = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&&search_precise&search=" + query + "&ordering=-added";
                client.get(search, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try{
                            JSONArray results = jsonObject.getJSONArray("results");
                            //Log.i(TAG, "Results" + results.toString());
                            searchGames.addAll(GameSearch.fromJSONArray(results));
                            gameSearchAdapter.notifyDataSetChanged();
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
                svSearch = view.findViewById(R.id.svSearch);
                RecyclerView searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
                searchGames = new ArrayList<>();
                GameSearchAdapter gameSearchAdapter = new GameSearchAdapter(getContext(), searchGames);
                searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                searchRecyclerView.setAdapter(gameSearchAdapter);
                String search = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&&search_precise&search=" + newText + "&ordering=-added";
                client.get(search, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try{
                            JSONArray results = jsonObject.getJSONArray("results");
                            Log.i("Results", "Results" + results.toString());
                            searchGames.addAll(GameSearch.fromJSONArray(results));
                            gameSearchAdapter.notifyDataSetChanged();
                            println("hello");
                            Log.i("Movies", "Movies" + searchGames.size());
                        } catch(JSONException e){
                            Log.e("json", "hit json expception", e);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });

                return false;
            }
        });


    }


    }

