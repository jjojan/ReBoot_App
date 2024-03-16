package com.example.rebootapp.Models;

import android.util.Log;

import com.parse.ParseObject;

import java.util.ArrayList;

public class ReviewModel {

    String Username;

    String reviewText;

    public ReviewModel() {}

    public ReviewModel(ArrayList<ParseObject> reviews)  {
        int i = 0;
        for (ParseObject item : reviews) {
            Log.i("review from review", reviews.get(i).getString("ReviewText") + "size");
            Username = reviews.get(i).getString("ReviewUser");
            reviewText = reviews.get(i).getString("ReviewText");
            i += 1;
        }
    }
}
