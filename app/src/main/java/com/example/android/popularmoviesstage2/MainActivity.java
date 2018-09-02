package com.example.android.popularmoviesstage2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.popularmoviesstage2.adapters.MovieAdapter;
import com.example.android.popularmoviesstage2.database.MainViewModel;
import com.example.android.popularmoviesstage2.interfaces.GetMovieService;
import com.example.android.popularmoviesstage2.interfaces.MovieListener;
import com.example.android.popularmoviesstage2.model.MovieResponse;
import com.example.android.popularmoviesstage2.utils.NetworkUtils;
import com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.utils.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("WeakerAccess")
public class MainActivity extends AppCompatActivity implements MovieListener, AdapterView.OnItemSelectedListener {

    @BindView(R.id.movies_rv) RecyclerView mRecyclerView;

    private List<Movie> mMovies = null;
    public static List<Movie> favoriteMovies;
    private MainViewModel mViewModel;
    private MovieAdapter mAdapter;
    private SharedPreferences mPreferences;
    private Parcelable mState;

    private int mSortMethod;
    private int mAdapterPosition = 0;

    private static Bundle mBundleViewState;
    private GridLayoutManager mLayoutManager;

    public static final String SORT_PREFERENCE = "SortPreference";
    public static final String SORT_POPULAR = "popular";
    public static final String SORT_RATING = "top_rated";
    private static final String SORT_FAVORITES = "favorites";

    public static final String KEY_LAYOUT_STATE = "key_layout_state";
    public static final String KEY_ADAPTER_POSITION = "key_adapter_position";
    public static final String KEY_MOVIE = "key_movie";
    public static final String KEY_SPINNER_VALUE = "key_spinnerValue";

    private String sortBy = SORT_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        resetData();
        setupRecyclerView();
        mSortMethod = getSort();
    }

    private void resetData() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        if (mMovies != null) {
            mMovies.clear();
        }
        sortBy = SORT_POPULAR;
    }

    private void setupRecyclerView() {
        int columns = getColumns();
        mMovies = new ArrayList<>();
        mAdapter = new MovieAdapter(mMovies, this, this);
        mLayoutManager = new GridLayoutManager(this, columns);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(KEY_MOVIE, movie);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner mSpinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> menuAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, R.layout.support_simple_spinner_dropdown_item);
        menuAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinner.setAdapter(menuAdapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setSelection(mSortMethod);

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                saveSort(position);
                sortBy = SORT_POPULAR;
                if (NetworkUtils.checkConnection(this)) {
                    resetData();
                    pullPopular();
                } else {
                    NetworkUtils.noConnection(this);
                }
                break;

            case 1:
                saveSort(position);
                sortBy = SORT_RATING;
                if (NetworkUtils.checkConnection(this)) {
                    resetData();
                    pullTopRated();
                } else {
                    NetworkUtils.noConnection(this);
                }
                break;

            case 2:
                saveSort(position);
                sortBy = SORT_FAVORITES;
                populateFavorites();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void populateFavorites() {
        if (favoriteMovies == null)
            favoriteMovies = new ArrayList<>();
        mViewModel.getFavoriteMovies().observe(this,
                new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(@Nullable List<Movie> movies) {
                        if (movies != null) {
                            favoriteMovies.clear();
                            favoriteMovies.addAll(movies);

                            if (sortBy.equals(SORT_FAVORITES))
                                setFavoriteList(favoriteMovies);

                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void pullPopular() {
        GetMovieService service = RetrofitClientInstance.getRetrofitInstance().create(GetMovieService.class);
        Call<MovieResponse> call = service.getPopularMovies(BuildConfig.API_KEY, 1);
        if (call != null) {
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                    if (response.isSuccessful()) {
                        MovieResponse jsonResponse = response.body();
                        List<Movie> movie = jsonResponse.getResults();

                        mMovies.clear();
                        mMovies.addAll(movie);
                        mAdapter.notifyItemInserted(mMovies.size() - 1);
                        mAdapter.notifyDataSetChanged();
                        if (mAdapterPosition != 0) {
                            mRecyclerView.scrollToPosition(mAdapterPosition);
                            mAdapterPosition = 0;
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieResponse> call,@NonNull Throwable t) {
                    NetworkUtils.noConnection(MainActivity.this);
                }
            });
        }
    }

    private void pullTopRated() {
        GetMovieService service = RetrofitClientInstance.getRetrofitInstance().create(GetMovieService.class);
        Call<MovieResponse> call = service.getTopRatedMovies(BuildConfig.API_KEY, 1);
        if (call != null) {
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                    if (response.isSuccessful()) {
                        MovieResponse jsonResponse = response.body();
                        List<Movie> movie = jsonResponse.getResults();

                        mMovies.clear();
                        mMovies.addAll(movie);
                        mAdapter.notifyItemInserted(mMovies.size() - 1);
                        mAdapter.notifyDataSetChanged();
                        if (mAdapterPosition != 0) {
                            mRecyclerView.scrollToPosition(mAdapterPosition);
                            mAdapterPosition = 0;
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieResponse> call,@NonNull Throwable t) {
                    NetworkUtils.noConnection(MainActivity.this);
                }
            });
        }

    }

    private void setFavoriteList(List<Movie> movies) {
        this.mMovies.clear();
        this.mMovies.addAll(movies);
    }

    @Override
    protected void onResume() {
        super.onResume();
            mSortMethod = getSort();
            switch (mSortMethod) {
                case 0:
                    sortBy = SORT_POPULAR;
                    if (NetworkUtils.checkConnection(this)) {
                        resetData();
                        pullPopular();
                    } else {
                        NetworkUtils.noConnection(this);
                    }
                    break;

                case 1:
                    sortBy = SORT_RATING;
                    if (NetworkUtils.checkConnection(this)) {
                        resetData();
                        pullTopRated();
                    } else {
                        NetworkUtils.noConnection(this);
                    }
                    break;

                case 2:
                    sortBy = SORT_FAVORITES;
                    populateFavorites();
                    break;
            }

        if (mBundleViewState != null) {
            Parcelable listState = mBundleViewState.getParcelable(KEY_LAYOUT_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBundleViewState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleViewState.putParcelable(KEY_LAYOUT_STATE, listState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_LAYOUT_STATE, mState);
        outState.putInt(KEY_ADAPTER_POSITION, mLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state != null) {
            mState = state.getParcelable(KEY_LAYOUT_STATE);
            mAdapterPosition = state.getInt(KEY_ADAPTER_POSITION);
        }
    }

    public int getColumns() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return 4;
        } else {
            return 2;
        }
    }

    public void saveSort(int selected) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_SPINNER_VALUE, selected);
        editor.apply();
    }

    private int getSort() {
        mPreferences = getSharedPreferences(SORT_PREFERENCE, MODE_PRIVATE);
        if (mPreferences.contains(KEY_SPINNER_VALUE)) {
            return mPreferences.getInt(KEY_SPINNER_VALUE, 0);
        }
        return 0;
    }
}
