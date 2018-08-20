package com.example.android.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.model.Movie;
import com.squareup.picasso.Picasso;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String SIZE = "w185";

    private FloatingActionButton favorite;
    private final Context mContext;
    private Movie[] mFavorites;
    final private ItemClickListener mListener;

    public FavoriteAdapter(Context context, ItemClickListener listener, Movie[] movies) {
        mContext = context;
        mListener = listener;
        mFavorites = movies;
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
        String poster = mFavorites[position].getThumbnailPath();
        String posterPath = BASE_IMAGE_URL + SIZE + poster;

        if (poster.equals("null")) {
            viewHolder.posterView.setImageResource(R.drawable.film_reel);
        } else {
            Picasso.with(mContext).load(posterPath).into(viewHolder.posterView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView posterView;

        private ViewHolder(View view) {
            super(view);
            posterView = view.findViewById(R.id.thumbnail_iv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(getAdapterPosition());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mFavorites == null) {
            return 0;
        }
        return mFavorites.length;
    }

    public Movie[] getFavorites() {
        return mFavorites;
    }

    public void setFavorites(Movie[] movies) {
        mFavorites = movies;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(int itemId);
    }
}
