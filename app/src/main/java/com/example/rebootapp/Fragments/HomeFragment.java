package com.example.rebootapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.rebootapp.GameModel;
import com.example.rebootapp.Adapters.GameAdapter;
import com.example.rebootapp.Models.GameViewModel;
import com.example.rebootapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    public static final String POPULAR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&page_size=40";
    public static final String NEW_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-01-01,2023-11-01&ordering=-added&page_size=40";

    public static final String YOUR_GAMES_URL = "https://api.rawg.io/api/games?key=63502b95db9f41c99bb3d0ecf77aa811&dates=2023-12-01,2024-11-01&page_size=40";
    public static final String TAG = "HomeAct";

    List<GameModel> gameModel;
    List<GameModel> newGameModels;

    List<GameModel> yourGameModels;

    JSONObject result;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public HomeFragment() {
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

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.activity_home, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        setContentView(R.layout.activity_home);
        RecyclerView rvGames = view.findViewById(R.id.hzRecyclerView);
        RecyclerView rvNewGames = view.findViewById(R.id.newGamesRV);
        RecyclerView rvYourGames = view.findViewById(R.id.rvYourGames);


        gameModel = new ArrayList<>();
        newGameModels = new ArrayList<>();
        yourGameModels = new ArrayList<>();

        GameAdapter gameAdapter =  new GameAdapter(getContext(), gameModel);
        GameAdapter newGameAdapter = new GameAdapter(getContext(), newGameModels);
        GameAdapter yourGamesAapter = new GameAdapter(getContext(), yourGameModels);

        rvGames.setAdapter(gameAdapter);
        rvNewGames.setAdapter(newGameAdapter);
        rvYourGames.setAdapter(yourGamesAapter);

        rvGames.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNewGames.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvYourGames.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

//        GameViewModel gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
//        GameViewModel gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
//        gameViewModel.getGames(POPULAR_GAMES_URL).observe(getViewLifecycleOwner(), games ->{
//            gameAdapter.updateData(games);
//        });
//
//        GameViewModel newGameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
//        newGameViewModel.getGames(NEW_GAMES_URL).observe(getViewLifecycleOwner(), games ->{
//            newGameAdapter.updateData(games);
//        });
//
//        GameViewModel yourGameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
//        yourGameViewModel.getGames(YOUR_GAMES_URL).observe(getViewLifecycleOwner(), games ->{
//            yourGamesAapter.updateData(games);
//        });
        GameViewModel gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        gameViewModel.getGames(POPULAR_GAMES_URL).observe(getViewLifecycleOwner(), gameAdapter::updateData);
        gameViewModel.getGames(NEW_GAMES_URL).observe(getViewLifecycleOwner(), newGameAdapter::updateData);
        gameViewModel.getGames(YOUR_GAMES_URL).observe(getViewLifecycleOwner(), yourGamesAapter::updateData);


//        AsyncHttpClient client = new AsyncHttpClient();


//        client.get(POPULAR_GAMES_URL , new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                new Thread(() ->{
//                    Log.d(TAG, "onSucess");
//                    JSONObject jsonObject = json.jsonObject;
//                    try {
//                        JSONArray results = jsonObject.getJSONArray("results");
//                        List<GameModel> games = GameModel.fromJSONArray(results);
//                        getActivity().runOnUiThread(() ->{
//                            gameModel.addAll(games);
//                            gameAdapter.notifyItemRangeInserted(0, 40);
//                        });
//
//                    } catch (JSONException e) {
//                        Log.e(TAG, "hit json expception", e);
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.d(TAG, "onFailure");
//            }
//        });

//        client.get(NEW_GAMES_URL, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                new Thread(() -> {
//                    JSONObject jsonObject = json.jsonObject;
//                    try {
//                        JSONArray results = jsonObject.getJSONArray("results");
//                        List<GameModel> games = GameModel.fromJSONArray(results);
//
//                        getActivity().runOnUiThread(() -> {
//                            newGameModels.addAll(games);
//                            newGameAdapter.notifyItemRangeInserted(0, 40);
//                        });
//
//
//                    } catch (JSONException e) {
//                        Log.e(TAG, "hit json expception", e);
//                    }
//                }).start();
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.d(TAG, "onFailure");
//            }
//        });

//        client.get(YOUR_GAMES_URL, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                new Thread(() -> {
//                    JSONObject jsonObject = json.jsonObject;
//                    try{
//                        JSONArray results = jsonObject.getJSONArray("results");
//                        List<GameModel> games = GameModel.fromJSONArray(results);
//
//                        getActivity().runOnUiThread(() -> {
//                            yourGameModels.addAll(games);
//
//                            yourGamesAapter.notifyItemRangeInserted(0, 40);
//                        });
//
//
//
//                    } catch(JSONException e){
//                        Log.e(TAG, "hit json expception", e);
//                    }
//
//                }).start();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//            }
//        });
    }
}

