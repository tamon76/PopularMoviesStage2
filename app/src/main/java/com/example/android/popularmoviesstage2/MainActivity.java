package com.example.android.popularmoviesstage2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmoviesstage2.adapters.MovieAdapter;
import com.example.android.popularmoviesstage2.interfaces.OnMovieTaskCompleted;
import com.example.android.popularmoviesstage2.model.FavoriteViewModel;
import com.example.android.popularmoviesstage2.utils.MovieAsyncTask;
import com.example.android.popularmoviesstage2.utils.NetworkUtils;
import com.example.android.popularmoviesstage2.model.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {

    @BindView(R.id.movies_rv) RecyclerView mRecyclerView;

    private Movie[] mMovies = null;
    private FavoriteViewModel mViewModel;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String SORT_POPULAR = "popular";
    private static final String SORT_RATING = "top_rated";
    private static final String SORT_FAVORITES = "favorites";
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String KEY_MOVIE = "movie";
    public static final String KEY_SORT_BY = "key_sortBy";
    public static final String KEY_SORTED_MOVIES = "movieList";

    private String sortBy = SORT_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Set up the RecyclerView
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_SORT_BY)) {
                sortBy = savedInstanceState.getString(KEY_SORT_BY);
            }
        }

        if (NetworkUtils.checkConnection(this)) {

            connect();
        } else {
            NetworkUtils.noConnection(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String savedSort = sortBy;

        outState.putString(KEY_SORT_BY, savedSort);
        outState.putParcelable(KEY_SORTED_MOVIES, mRecyclerView.getLayoutManager().onSaveInstanceState());
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
                populateFavorites();
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

    private void populateFavorites() {
        mViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        final MovieAdapter favoriteAdapter;
        favoriteAdapter = new MovieAdapter(MainActivity.this, MainActivity.this, mMovies);
        mRecyclerView.setAdapter(favoriteAdapter);
        mViewModel.getFavoriteList().observe(MainActivity.this, new Observer<Movie[]>() {
            @Override
            public void onChanged(@Nullable Movie[] favorites) {
                favoriteAdapter.addMovie(favorites);
            }
        });
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
