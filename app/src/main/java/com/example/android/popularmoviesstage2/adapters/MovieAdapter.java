package com.example.android.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage2.interfaces.MovieListener;
import com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String SIZE = "w185";
    private final MovieListener mClickListener;
    private final LayoutInflater mInflater;
    final private List<Movie> movies;

    public MovieAdapter(List<Movie> movies, MovieListener listener, Context context) {
        this.movies = movies;
        this.mClickListener = listener;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_item, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final Movie movie = movies.get(position);
        String poster = movie.getThumbnailPath();
        String posterPath = BASE_IMAGE_URL + SIZE + poster;

        if (poster.equals("null")) {
            ((MovieHolder) viewHolder).posterView.setImageResource(R.drawable.film_reel);
        } else {
            Picasso.get().load(posterPath).into(((MovieHolder) viewHolder).posterView);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onMovieClick(movie
                );
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class MovieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail_iv) ImageView posterView;

        private MovieHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}