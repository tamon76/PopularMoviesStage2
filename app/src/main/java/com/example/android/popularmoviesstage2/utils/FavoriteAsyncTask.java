package com.example.android.popularmoviesstage2.utils;

import android.os.AsyncTask;

import com.example.android.popularmoviesstage2.database.FavoriteDatabase;
import com.example.android.popularmoviesstage2.interfaces.OnFavoriteTaskCompleted;
import com.example.android.popularmoviesstage2.model.Movie;

public class FavoriteAsyncTask extends AsyncTask<String, Void, Movie[]> {

    private final FavoriteDatabase mDb;
    private final OnFavoriteTaskCompleted taskCompleted;

    public FavoriteAsyncTask(OnFavoriteTaskCompleted activityContext, FavoriteDatabase database) {
        this.taskCompleted = activityContext;
        this.mDb = database;
    }

    @Override
    protected Movie[] doInBackground(String... strings) {
//        return mDb.movieDao().loadFavorites();
        Movie[] movies = null;
        return movies;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        taskCompleted.onFavoriteTaskCompleted(movies);
    }
}
