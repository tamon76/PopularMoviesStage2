package com.example.android.popularmoviesstage2.utils;

import android.os.AsyncTask;

import com.example.android.popularmoviesstage2.interfaces.OnMovieTaskCompleted;
import com.example.android.popularmoviesstage2.model.Movie;

public class MovieAsyncTask extends AsyncTask<String, Void, Movie[]> {

    private final OnMovieTaskCompleted taskCompleted;
    private final String sortBy;

    public MovieAsyncTask(OnMovieTaskCompleted activityContext, String sortBy) {
        this.taskCompleted = activityContext;
        this.sortBy = sortBy;
    }

    @Override
    protected Movie[] doInBackground(String... urls) {
        if ((urls.length < 1) || urls[0] == null) {
            return null;
        }
        return JsonUtils.fetchMovies(sortBy);
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        taskCompleted.onMovieTaskCompleted(movies);
    }
}
