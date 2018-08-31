package com.example.android.popularmoviesstage2.interfaces;

import com.example.android.popularmoviesstage2.model.MovieResponse;
import com.example.android.popularmoviesstage2.model.ReviewResponse;
import com.example.android.popularmoviesstage2.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMovieService {

    @GET("popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("{movie_id}/reviews")
    Call<ReviewResponse> getReviews(@Path("movie_id") String movieId, @Query("api_key") String apiKey);

    @GET("{movie_id}/videos")
    Call <TrailerResponse> getTrailers(@Path("movie_id") String movieId, @Query("api_key") String apiKey);
}
