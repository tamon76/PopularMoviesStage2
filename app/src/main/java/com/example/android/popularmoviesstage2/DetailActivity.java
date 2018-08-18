package com.example.android.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.adapters.ReviewAdapter;
import com.example.android.popularmoviesstage2.adapters.TrailerAdapter;
import com.example.android.popularmoviesstage2.interfaces.OnReviewTaskCompleted;
import com.example.android.popularmoviesstage2.interfaces.OnTrailerTaskCompleted;
import com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.model.Review;
import com.example.android.popularmoviesstage2.model.Trailer;
import com.example.android.popularmoviesstage2.utils.JsonUtils;
import com.example.android.popularmoviesstage2.utils.ReviewAsyncTask;
import com.example.android.popularmoviesstage2.utils.TrailerAsyncTask;
import com.squareup.picasso.Picasso;

//public class DetailActivity extends AppCompatActivity implements ReviewAdapter.ItemClickListener {
public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerListener {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String BASE_TRAILER_URL = "https://www.youtube.com/watch?v=";
    private String movieId;
    private RecyclerView rvReview;
    private RecyclerView rvTrailer;
    private Review[] mReviews = null;
    private Trailer[] mTrailers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String mBaseImagePath = getString(R.string.base_image_url);
        String mImageSize = getString(R.string.image_size);

        TextView mTitle = findViewById(R.id.movieTitle_tv);
        ImageView mMovieImage = findViewById(R.id.movieImage_iv);
        TextView mOverview = findViewById(R.id.overview_tv);
        TextView mRating = findViewById(R.id.rating_tv);
        TextView mDate = findViewById(R.id.releaseDate_tv);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");

        rvReview = findViewById(R.id.reviews_rv);
        rvReview.setLayoutManager(new LinearLayoutManager(this));

        rvTrailer = findViewById(R.id.trailers_rv);
        rvTrailer.setLayoutManager(new LinearLayoutManager(this));
        movieId = movie.getId();

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

        if (checkConnection()) {
            ReviewAsyncTask reviewTask = new ReviewAsyncTask(new ReviewListener(), movieId);
            reviewTask.execute();
        } else {
            noConnection();
        }

        if (checkConnection()) {
            TrailerAsyncTask trailerTask = new TrailerAsyncTask(new TrailerListener(), movieId);
            trailerTask.execute();
        }
    }

    public void onItemClick(int position) {
        Log.d(LOG_TAG,"===> mTrailer[position] = " + mTrailers[position].getName());
        startTrailer(mTrailers[position]);
//        startDetailActivity(mMovies[position]);
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
        return isConnected;
    }

    private void noConnection() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.network_unavailable, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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

