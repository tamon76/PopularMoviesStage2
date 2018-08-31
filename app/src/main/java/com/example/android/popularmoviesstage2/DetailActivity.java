package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.adapters.ReviewAdapter;
import com.example.android.popularmoviesstage2.adapters.TrailerAdapter;
import com.example.android.popularmoviesstage2.database.AppExecutors;
import com.example.android.popularmoviesstage2.database.FavoriteDatabase;
import com.example.android.popularmoviesstage2.interfaces.GetMovieService;
import com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.model.Review;
import com.example.android.popularmoviesstage2.model.ReviewResponse;
import com.example.android.popularmoviesstage2.model.Trailer;
import com.example.android.popularmoviesstage2.model.TrailerResponse;
import com.example.android.popularmoviesstage2.utils.NetworkUtils;
import com.example.android.popularmoviesstage2.utils.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("WeakerAccess")
public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.movieTitle_tv) TextView mTitle;
    @BindView(R.id.movieImage_iv) ImageView mMovieImage;
    @BindView(R.id.overview_tv) TextView mOverview;
    @BindView(R.id.rating_tv) TextView mRating;
    @BindView(R.id.releaseDate_tv) TextView mDate;
    @BindView(R.id.favorites_fab) FloatingActionButton fabFavorite;

    @BindView(R.id.trailers_rv) RecyclerView rvTrailer;
    @BindView(R.id.reviews_rv) RecyclerView rvReview;

    private FavoriteDatabase mDb;
    private Movie movie;
    private boolean isFavorite;
    public static final String KEY_MOVIE = "key_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mDb = FavoriteDatabase.getInstance(getApplicationContext());

        String mBaseImagePath = getString(R.string.base_image_url);
        String mImageSize = getString(R.string.image_size);

        Intent intent = getIntent();
        movie = intent.getParcelableExtra(KEY_MOVIE);

        rvReview.setLayoutManager(new LinearLayoutManager(this));
        rvTrailer.setLayoutManager(new LinearLayoutManager(this));

        mTitle.setText(movie.getOriginalTitle());
        mOverview.setText(movie.getOverview());
        mRating.setText(movie.getVoteAverage());
        mDate.setText(movie.getReleaseDate());
        String movieId = movie.getId();

        String mPosterPath = movie.getThumbnailPath();

        if (mPosterPath.equals("null")) {
            mMovieImage.setImageResource(R.drawable.film_reel);
        } else {
            mPosterPath = mBaseImagePath + mImageSize + mPosterPath;
            Picasso.get().load(mPosterPath).into(mMovieImage);
        }

        if (NetworkUtils.checkConnection(this)) {
            loadReviews(movieId);
        } else {
            NetworkUtils.noConnection(this);
        }

        if (NetworkUtils.checkConnection(this)) {
            loadTrailers(movieId);

        } else {
            NetworkUtils.noConnection(this);
        }

        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFavorites(movie);
            }
        });

        checkFavorite(movie);
    }

    private void checkFavorite(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String movieId = mDb.movieDao().loadMovieById(movie.getId());
                if (movieId != null) {
                    isFavorite = true;
                    fabFavorite.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.ic_favorite_added_24dp));
                } else {
                    isFavorite = false;
                    fabFavorite.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.ic_favorite_default_24dp));
                }
            }
        });
    }

    private void updateFavorites(final Movie movie) {
        if (isFavorite) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().deleteFavorite(movie);
                }
            });
            isFavorite = false;
            fabFavorite.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.ic_favorite_default_24dp));

        } else {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().addFavorite(movie);
                }
            });
            isFavorite = true;
            fabFavorite.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.ic_favorite_added_24dp));
        }
    }

    private void loadReviews(String movieId) {
        GetMovieService service = RetrofitClientInstance.getRetrofitInstance().create(GetMovieService.class);
        Call<ReviewResponse> call = service.getReviews(movieId, BuildConfig.API_KEY);
        if (call != null) {
            call.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                    if (response.isSuccessful()) {
                        ReviewResponse reviewResponse = response.body();
                        List<Review> reviews = reviewResponse.getResults();

                        if ((reviews != null) && (reviews.size() > 0)) {
                            ReviewAdapter mReviewAdapter;
                            mReviewAdapter = new ReviewAdapter(reviews);
                            rvReview.setAdapter(mReviewAdapter);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                    NetworkUtils.noConnection(DetailActivity.this);
                }
            });
        }
    }

    private void loadTrailers(String movieId) {
        GetMovieService service = RetrofitClientInstance.getRetrofitInstance().create(GetMovieService.class);
        Call <TrailerResponse> call = service.getTrailers(movieId, BuildConfig.API_KEY);
        if (call != null) {
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                    if (response.isSuccessful()) {
                        TrailerResponse trailerResponse = response.body();
                        List<Trailer> trailers = trailerResponse.getResults();

                        if ((trailers != null) && (trailers.size() > 0)) {
                            TrailerAdapter mTrailerAdapter;
                            mTrailerAdapter = new TrailerAdapter(trailers);
                            rvTrailer.setAdapter(mTrailerAdapter);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                    NetworkUtils.noConnection(DetailActivity.this);
                }
            });
        }
    }
}

