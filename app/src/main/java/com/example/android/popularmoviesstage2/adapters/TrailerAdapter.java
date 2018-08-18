package com.example.android.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.model.Trailer;
import com.squareup.picasso.Picasso;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private static final String BASE_THUMBNAIL_URL = "http://img.youtube.com/vi/";
    private Trailer[] mTrailers;
    private TextView mName;
    private Context context;
    private TrailerListener trailerListener;

    public TrailerAdapter(Context context, TrailerListener trailerListener, Trailer[] trailers) {
        this.mTrailers = trailers;
        this.context = context;
        this.trailerListener = trailerListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String name = mTrailers[position].getName();
        String key = mTrailers[position].getKey();
        String thumbnail = BASE_THUMBNAIL_URL + key + "/hqdefault.jpg";
        mName.setText(name);
        Picasso.with(context).load(thumbnail).into(viewHolder.trailerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView trailerView;

        private ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mName = itemView.findViewById(R.id.trailerName_tv);
            trailerView = itemLayoutView.findViewById(R.id.trailerKey_iv);
            trailerView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (trailerListener != null) {
                trailerListener.onItemClick(getAdapterPosition());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTrailers.length;
    }

    public interface TrailerListener {
        void onItemClick(int position);
    }
}
