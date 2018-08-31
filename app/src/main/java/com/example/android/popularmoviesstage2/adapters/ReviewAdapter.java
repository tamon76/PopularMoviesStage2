package com.example.android.popularmoviesstage2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.model.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private final List<Review> mReviews;

    public ReviewAdapter(List<Review> reviews) {
        this.mReviews = reviews;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_review, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder viewHolder, int position) {
        Review review = mReviews.get(position);
        String author = review.getAuthor();
        String content = review.getContent();
        viewHolder.mAuthor.setText(author);
        viewHolder.mContent.setText(content);
    }

    class ReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.author_tv) TextView mAuthor;
        @BindView(R.id.content_tv) TextView mContent;

        private ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        }
        return mReviews.size();
    }
}
