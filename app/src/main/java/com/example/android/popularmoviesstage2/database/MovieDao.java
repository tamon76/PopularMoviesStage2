package com.example.android.popularmoviesstage2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popularmoviesstage2.model.Movie;

@Dao
public interface MovieDao {

    @Insert
    void insertFavorite(Movie movie);

    @Delete
    void deleteFavorite(Movie movie);

    @Query("SELECT * FROM favorites")
    Movie[] loadFavorites();

    @Query("SELECT * FROM favorites WHERE id = :movieId")
    int loadMovieById(String movieId);
}
