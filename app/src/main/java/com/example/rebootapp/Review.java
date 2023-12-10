package com.example.rebootapp;

import android.util.Log;

import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Review {

    String Username;

    String reviewText;

    public Review() {}

    public Review(ArrayList<ParseObject> reviews)  {
        int i = 0;
        for (ParseObject item : reviews) {
            Log.i("review from review", reviews.get(i).getString("ReviewText") + "size");
            Username = reviews.get(i).getString("ReviewUser");
            reviewText = reviews.get(i).getString("ReviewText");
            i += 1;
        }
    }
}
