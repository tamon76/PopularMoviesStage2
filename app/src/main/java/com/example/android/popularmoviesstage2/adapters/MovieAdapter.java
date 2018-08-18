package com.example.android.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.R;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String SIZE = "w185";
    private ItemClickListener mClickListener;

    private final Movie[] movies;
    private final Context context;

    public MovieAdapter(Context context, ItemClickListener itemClickListener, Movie[] movies) {
        this.movies = movies;
        this.context = context;
        this.mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.grid_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String poster = movies[position].getThumbnailPath();
        String posterPath = BASE_IMAGE_URL + SIZE + poster;

        if (poster.equals("null")) {
            viewHolder.posterView.setImageResource(R.drawable.film_reel);
        } else {
            Picasso.with(context).load(posterPath).into(viewHolder.posterView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        private final ImageView posterView;

        private ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            posterView = itemLayoutView.findViewById(R.id.thumbnail_iv);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}
