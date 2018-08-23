package com.example.android.popularmoviesstage2.model;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.android.popularmoviesstage2.database.FavoriteDatabase;

public class FavoriteViewModel extends AndroidViewModel {

    private LiveData<Movie[]> favoriteList;
    private FavoriteDatabase favoriteDatabase;

    public FavoriteViewModel(Application application) {
        super(application);

        favoriteDatabase = FavoriteDatabase.getInstance(this.getApplication());

    }


    public LiveData<Movie[]> getFavoriteList() {
        getFavoritesFromDatabase();
        return favoriteList;
    }

    private void getFavoritesFromDatabase() {
        favoriteList = favoriteDatabase.movieDao().loadFavorites();
    }


    public void addFavorite(final Movie movie) {
        new addFavoriteAsyncTask(favoriteDatabase).execute(movie);
    }


    public void deleteFavorite(Movie Movie) {
        new deleteFavoriteAsyncTask(favoriteDatabase).execute(Movie);
    }


    private static class addFavoriteAsyncTask extends AsyncTask<Movie, Void, Void> {

        private FavoriteDatabase db;

        addFavoriteAsyncTask(FavoriteDatabase favoriteDatabase) {
            db = favoriteDatabase;
        }

        @Override
        protected Void doInBackground(final Movie... params) {
            db.movieDao().addFavorite(params[0]);
            return null;
        }
    }


    private static class deleteFavoriteAsyncTask extends AsyncTask<Movie, Void, Void> {

        private FavoriteDatabase db;

        deleteFavoriteAsyncTask(FavoriteDatabase favoriteDatabase) {
            db = favoriteDatabase;
        }

        @Override
        protected Void doInBackground(final Movie... params) {
            db.movieDao().deleteFavorite(params[0]);
            return null;
        }
    }
}
