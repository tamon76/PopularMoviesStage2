package com.example.android.popularmoviesstage2.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesstage2.model.Movie;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Movie>> favoriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        FavoriteDatabase mdb = FavoriteDatabase.getInstance(this.getApplication());
        favoriteMovies = mdb.movieDao().loadFavorites();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }

}
