package com.example.android.popularmoviesstage2.utils;

import android.os.AsyncTask;

import com.example.android.popularmoviesstage2.interfaces.OnTrailerTaskCompleted;
import com.example.android.popularmoviesstage2.model.Trailer;

public class TrailerAsyncTask extends AsyncTask<String,Void, Trailer[]> {

    private static final String LOG_TAG = TrailerAsyncTask.class.getSimpleName();
    private final OnTrailerTaskCompleted trailerTaskCompleted;
    private final String movieId;

    public TrailerAsyncTask(OnTrailerTaskCompleted activityContext, String movieId) {
        this.trailerTaskCompleted = activityContext;
        this.movieId = movieId;

    }
    @Override
    protected Trailer[] doInBackground(String... strings) {
        return JsonUtils.fetchTrailers(movieId);
    }

    @Override
    protected void onPostExecute(Trailer[] trailers) {
        trailerTaskCompleted.onTrailerTaskCompleted(trailers);
    }
}
