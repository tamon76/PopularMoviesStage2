package com.example.android.popularmoviesstage2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {

    private static final String BASE_TRAILER_URL = "https://www.youtube.com/watch?v=";
    private static final String BASE_THUMBNAIL_URL = "http://img.youtube.com/vi/";
    private final List<Trailer> mTrailers;
    private Context context;
    private static final String KEY_THUMBNAIL = "/hqdefault.jpg";

    public TrailerAdapter(List<Trailer> trailers) {
        this.mTrailers = trailers;
    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_trailer, parent, false);
        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder viewHolder, int position) {
        final Trailer trailer = mTrailers.get(position);
        String name = trailer.getName();
        String key = trailer.getKey();
        String thumbnail = BASE_THUMBNAIL_URL + key + KEY_THUMBNAIL;
        viewHolder.trailerName.setText(name);
        Picasso.get().load(thumbnail).into(viewHolder.trailerView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrailer(trailer);
            }
        });
    }

    class TrailerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailerKey_iv) ImageView trailerView;
        @BindView(R.id.trailerName_tv) TextView trailerName;

        private TrailerHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemCount() {
        if (mTrailers == null) {
            return 0;
        }
        return mTrailers.size();
    }

    private void startTrailer(Trailer trailer) {
        String url = BASE_TRAILER_URL + trailer.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}
