package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmoviesstage2.adapters.FavoriteAdapter;
import com.example.android.popularmoviesstage2.adapters.MovieAdapter;
import com.example.android.popularmoviesstage2.database.FavoriteDatabase;
import com.example.android.popularmoviesstage2.interfaces.OnFavoriteTaskCompleted;
import com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.interfaces.OnMovieTaskCompleted;
import com.example.android.popularmoviesstage2.utils.FavoriteAsyncTask;
import com.example.android.popularmoviesstage2.utils.MovieAsyncTask;
import com.example.android.popularmoviesstage2.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener, FavoriteAdapter.ItemClickListener {

    private RecyclerView mRecyclerView;
    private Movie[] mMovies = null;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String SORT_POPULAR = "popular";
    private static final String SORT_RATING = "top_rated";
    private static final String SORT_FAVORITES = "favorites";
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String KEY_MOVIE = "movie";

    private String sortBy = SORT_POPULAR;
    private FavoriteDatabase mDb;
    private FavoriteAdapter mFavoriteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up the RecyclerView
        mRecyclerView = findViewById(R.id.movies_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if (NetworkUtils.checkConnection(this)) {
            connect();
        } else {
            NetworkUtils.noConnection(this);
        }
        mDb = FavoriteDatabase.getInstance(getApplicationContext());
    }

    private void startDetailActivity(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(KEY_MOVIE, movie);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.popular:
                sortBy = SORT_POPULAR;
                if (NetworkUtils.checkConnection(this)) {
                    connect();
                } else {
                    NetworkUtils.noConnection(this);
                }
                break;

            case R.id.rated:
                sortBy = SORT_RATING;
                if (NetworkUtils.checkConnection(this)) {
                    connect();
                } else {
                    NetworkUtils.noConnection(this);
                }
                break;

            case R.id.favorites:
                sortBy = SORT_FAVORITES;
                FavoriteAsyncTask task = new FavoriteAsyncTask(new FavoriteListener(), mDb);
                task.execute();
                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemClick(int position) {
        Log.d(LOG_TAG, "===> onItemClick = " + mMovies[position].getOriginalTitle());
        startDetailActivity(mMovies[position]);
    }

    private void connect() {
        MovieAsyncTask task = new MovieAsyncTask(new MovieListener(), sortBy);
        task.execute(BASE_URL);
    }

    private class FavoriteListener implements OnFavoriteTaskCompleted {
        @Override
        public void onFavoriteTaskCompleted(Movie[] movies) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mMovies = movies;
            FavoriteAdapter mFavoriteAdapter;
            mFavoriteAdapter = new FavoriteAdapter(MainActivity.this, MainActivity.this, mMovies);
            mRecyclerView.setAdapter(mFavoriteAdapter);
        }
    }

    private class MovieListener implements OnMovieTaskCompleted {
        @Override
        public void onMovieTaskCompleted(Movie[] movies) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mMovies = movies;
            MovieAdapter mMovieAdapter;
            mMovieAdapter = new MovieAdapter(MainActivity.this, MainActivity.this, mMovies);
            mRecyclerView.setAdapter(mMovieAdapter);
        }
    }
}
