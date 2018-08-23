package com.example.android.popularmoviesstage2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popularmoviesstage2.model.Movie;

@Dao
public interface MovieDao {

    @Insert
    void addFavorite(Movie movie);

    @Delete
    void deleteFavorite(Movie movie);

    @Query("SELECT * FROM favorites")
    LiveData<Movie[]> loadFavorites();

    @Query("SELECT ID FROM favorites WHERE id = :movieId")
    String loadMovieById(String movieId);
}
