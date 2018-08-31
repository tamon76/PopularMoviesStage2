package com.example.android.popularmoviesstage2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResponse {

    @SerializedName("results")
    private List<Trailer> results;

    public List<Trailer> getResults() {
        return results;
    }
}
