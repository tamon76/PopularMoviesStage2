package com.example.android.popularmoviesstage2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private final Review[] mReview;
    private TextView mAuthor;
    private TextView mContent;

    public ReviewAdapter(Review[] review) {
        this.mReview = review;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        String author = mReview[position].getAuthor();
        String content = mReview[position].getContent();
        mAuthor.setText(author);
        mContent.setText(content);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mAuthor = itemView.findViewById(R.id.author_tv);
            mContent = itemView.findViewById(R.id.content_tv);
        }
    }

    @Override
    public int getItemCount() {
        return mReview.length;
    }
}
