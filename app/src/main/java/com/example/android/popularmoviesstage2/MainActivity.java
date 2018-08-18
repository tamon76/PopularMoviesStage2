package com.example.android.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.adapters.MovieAdapter;
import com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.interfaces.OnMovieTaskCompleted;
import com.example.android.popularmoviesstage2.utils.MovieAsyncTask;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {

    private RecyclerView mRecyclerView;
    private Movie[] mMovies = null;
    private Menu mMenu;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String SORT_POPULAR = "popular";
    private static final String SORT_RATING = "top_rated";
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

    private String sortBy = SORT_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up the RecyclerView
        mRecyclerView = findViewById(R.id.movies_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        checkConnection();
    }

    private void startDetailActivity(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        mMenu = menu;
        updateMenu();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(sortBy) {
            case SORT_POPULAR:
                sortBy = SORT_RATING;
                updateMenu();
                checkConnection();
                break;

            case SORT_RATING:
                sortBy = SORT_POPULAR;
                updateMenu();
                checkConnection();
                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenu() {
        if (sortBy.equals(SORT_POPULAR)) {
            mMenu.findItem(R.id.popular).setVisible(true);
            mMenu.findItem(R.id.rated).setVisible(false);
        } else {
            mMenu.findItem(R.id.popular).setVisible(false);
            mMenu.findItem(R.id.rated).setVisible(true);
        }
    }

    public void onItemClick(int position) {
        Log.d(LOG_TAG, "===> onItemClick = " + mMovies[position].getOriginalTitle());
        startDetailActivity(mMovies[position]);
    }

    private void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));

        if (isConnected) {
            MovieAsyncTask task = new MovieAsyncTask(new MovieListener(), sortBy);
            task.execute(BASE_URL);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), com.example.android.popularmoviesstage2.R.string.network_unavailable, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
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
