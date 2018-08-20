package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.adapters.ReviewAdapter;
import com.example.android.popularmoviesstage2.adapters.TrailerAdapter;
import com.example.android.popularmoviesstage2.database.AppExecutors;
import com.example.android.popularmoviesstage2.database.FavoriteDatabase;
import com.example.android.popularmoviesstage2.interfaces.OnReviewTaskCompleted;
import com.example.android.popularmoviesstage2.interfaces.OnTrailerTaskCompleted;
import com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.model.Review;
import com.example.android.popularmoviesstage2.model.Trailer;
import com.example.android.popularmoviesstage2.utils.NetworkUtils;
import com.example.android.popularmoviesstage2.utils.ReviewAsyncTask;
import com.example.android.popularmoviesstage2.utils.TrailerAsyncTask;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerListener {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String BASE_TRAILER_URL = "https://www.youtube.com/watch?v=";
    private String movieId;
    private RecyclerView rvReview;
    private RecyclerView rvTrailer;
    private Review[] mReviews = null;
    private Trailer[] mTrailers = null;
    private FloatingActionButton fabFavorite;
    private FavoriteDatabase mDb;
    private int favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = FavoriteDatabase.getInstance(getApplicationContext());

        String mBaseImagePath = getString(R.string.base_image_url);
        String mImageSize = getString(R.string.image_size);

        TextView mTitle = findViewById(R.id.movieTitle_tv);
        ImageView mMovieImage = findViewById(R.id.movieImage_iv);
        TextView mOverview = findViewById(R.id.overview_tv);
        TextView mRating = findViewById(R.id.rating_tv);
        TextView mDate = findViewById(R.id.releaseDate_tv);
        fabFavorite = findViewById(R.id.favorites_fab);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");

        rvReview = findViewById(R.id.reviews_rv);
        rvReview.setLayoutManager(new LinearLayoutManager(this));

        rvTrailer = findViewById(R.id.trailers_rv);
        rvTrailer.setLayoutManager(new LinearLayoutManager(this));
        movieId = movie.getId();

        if (movie.getFavorite() == 1) {
            fabFavorite.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.ic_favorite_added_24dp));
        }

        mTitle.setText(movie.getOriginalTitle());
        mOverview.setText(movie.getOverview());
        mRating.setText(movie.getVoteAverage());
        mDate.setText(movie.getReleaseDate());

        String mPosterPath = movie.getThumbnailPath();

        if (mPosterPath.equals("null")) {
            mMovieImage.setImageResource(R.drawable.film_reel);
        } else {
            mPosterPath = mBaseImagePath + mImageSize + mPosterPath;
            Picasso.with(this).load(mPosterPath).into(mMovieImage);
        }

        if (NetworkUtils.checkConnection(this)) {
            ReviewAsyncTask reviewTask = new ReviewAsyncTask(new ReviewListener(), movieId);
            reviewTask.execute();
        } else {
            NetworkUtils.noConnection(this);
        }

        if (NetworkUtils.checkConnection(this)) {
            TrailerAsyncTask trailerTask = new TrailerAsyncTask(new TrailerListener(), movieId);
            trailerTask.execute();
        } else {
            NetworkUtils.noConnection(this);
        }
        final Movie mMovie = movie;
        fabFavorite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateFavorites(mMovie);
            }
        });
    }

    private void updateFavorites(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int alreadyExists = mDb.movieDao().loadMovieById(movie.getId());
                if (alreadyExists == 0) {
                    mDb.movieDao().insertFavorite(movie);
                    movie.setFavorite(1);
                } else {
                    mDb.movieDao().deleteFavorite(movie);
                    movie.setFavorite(0);
                }
                updateIcon(movie.getFavorite());
            }
        });
    }

    private void updateIcon(final int fav) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fav == 1) {
                    fabFavorite.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.ic_favorite_added_24dp));
                } else {
                    fabFavorite.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.ic_favorite_default_24dp));
                }
            }
        });
    }

    public void onItemClick(int position) {
        Log.d(LOG_TAG,"===> mTrailer[position] = " + mTrailers[position].getName());
        startTrailer(mTrailers[position]);
    }

    public void updateFavorites() {
    }

    private class TrailerListener implements OnTrailerTaskCompleted {
        @Override
        public void onTrailerTaskCompleted(Trailer[] trailers) {
            rvTrailer.setVisibility(View.VISIBLE);
            mTrailers = trailers;
            TrailerAdapter mTrailerAdapter;
            mTrailerAdapter = new TrailerAdapter(DetailActivity.this, DetailActivity.this, mTrailers);
            rvTrailer.setAdapter(mTrailerAdapter);
        }
    }

    private class ReviewListener implements OnReviewTaskCompleted {
        @Override
        public void onReviewTaskCompleted(Review[] reviews) {
            rvReview.setVisibility(View.VISIBLE);
            mReviews = reviews;
            ReviewAdapter mReviewAdapter;
            mReviewAdapter = new ReviewAdapter(mReviews);
            rvReview.setAdapter(mReviewAdapter);
        }
    }

    private void startTrailer(Trailer trailer) {
        String url = BASE_TRAILER_URL + trailer.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}

