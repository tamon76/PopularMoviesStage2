package com.example.android.popularmoviesstage2.utils;

import android.os.AsyncTask;

import com.example.android.popularmoviesstage2.interfaces.OnReviewTaskCompleted;
import com.example.android.popularmoviesstage2.model.Review;

public class ReviewAsyncTask extends AsyncTask<String, Void, Review[]> {

    private static final String LOG_TAG = ReviewAsyncTask.class.getSimpleName();
    private final OnReviewTaskCompleted reviewTaskCompleted;
    private final String movieId;

    public ReviewAsyncTask(OnReviewTaskCompleted activityContext, String movieId) {
        this.reviewTaskCompleted = activityContext;
        this.movieId = movieId;
    }

    @Override
    protected Review[] doInBackground(String... strings) {
        return JsonUtils.fetchReviews(movieId);
    }

    @Override
    protected void onPostExecute(Review[] reviews) {
        reviewTaskCompleted.onReviewTaskCompleted(reviews);
    }
}
