package com.example.android.popularmoviesstage2.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popularmoviesstage2.BuildConfig;
import  com.example.android.popularmoviesstage2.model.Movie;
import com.example.android.popularmoviesstage2.model.Review;
import com.example.android.popularmoviesstage2.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonUtils {

//    private static final String SAMPLE_MOVIE_QUERY = "https://api.themoviedb.org/3/movie/550?api_key=ccb621025458c323746b303c8db124ce&language=en-US";
//    private static final String SAMPLE_REVIEW_QUERY = "https://api.themoviedb.org/3/movie/550/reviews?api_key=ccb621025458c323746b303c8db124ce&language=en-US";
//    private static final String SAMPLE_TRAILER_QUERY = "https://api.themoviedb.org/3/movie/550/videos?api_key=ccb621025458c323746b303c8db124ce&language=en-US";
//    private static final String SAMPLE_QUERY = "https://api.themoviedb.org/3/movie/popular?api_key=ccb621025458c323746b303c8db124ce&language=en-US";

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String BASE_TRAILER_URL = "https://youtu.be/";
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String LOG_TAG = JsonUtils.class.getSimpleName();


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "===> Error connecting to server");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    ///////////////////
    // Movie methods //
    ///////////////////


    public static Movie[] fetchMovies(String sort) {
        URL url = getSortedUrl(sort);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "===> Error closing input stream", e);
        }
        return parseMovieFromJson(jsonResponse);
    }

    private static URL getSortedUrl(String sortOrder) {
        try {
            Uri builtUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendPath(sortOrder)
                    .appendQueryParameter("api_key", API_KEY)
                    .build();

            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "===> Error creating Sort URL", e);
            return null;
        }
    }

    private static Movie[] parseMovieFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        final String RESULTS = "results";
        final String MOVIE_ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String THUMBNAIL = "poster_path";
        final String OVERVIEW = "overview";
        final String RATING = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject movieJson;
        String id;
        String title;
        String thumbnail;
        String overview;
        String rating;
        String releaseDate;

        try {
            movieJson = new JSONObject(json);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            Movie[] movieList = new Movie[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                movieList[i] = new Movie();
                JSONObject currentMovie = movieArray.getJSONObject(i);

                id = currentMovie.getString(MOVIE_ID);
                title = currentMovie.getString(ORIGINAL_TITLE);
                thumbnail = currentMovie.getString(THUMBNAIL);
                overview = currentMovie.getString(OVERVIEW);
                rating = currentMovie.getString(RATING);
                releaseDate = currentMovie.getString(RELEASE_DATE);

                movieList[i].setId(id);
                movieList[i].setOriginalTitle(title);
                movieList[i].setThumbnailPath(thumbnail);
                movieList[i].setOverview(overview);
                movieList[i].setVoteAverage(rating);
                movieList[i].setReleaseDate(releaseDate);
            }
            return movieList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "===> Problem parsing Movie JSON", e);
            return null;
        }
    }


    ////////////////////
    // Review methods //
    ////////////////////


    public static Review[] fetchReviews(String movieId) {
        URL url = getReviewUrl(movieId);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "===> Error closing Review input stream", e);
        }
        return parseReviewFromJson(jsonResponse);
    }

    private static URL getReviewUrl(String movieId) {
        try {
            Uri builtUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendPath(movieId)
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", API_KEY)
                    .build();

            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "===> Error creating  Review URL", e);
            return null;
        }
    }

    private static Review[] parseReviewFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        final String RESULTS = "results";
        final String MOVIE_ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        JSONObject reviewJson;
        String id;
        String author;
        String content;

        try {
            reviewJson = new JSONObject(json);
            JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);

            Review[] reviewList = new Review[reviewArray.length()];

            for (int i = 0; i < reviewArray.length(); i++) {
                reviewList[i] = new Review();
                JSONObject currentReview = reviewArray.getJSONObject(i);

                id = currentReview.getString(MOVIE_ID);
                author = currentReview.getString(AUTHOR);
                content = currentReview.getString(CONTENT);

                reviewList[i].setId(id);
                reviewList[i].setAuthor(author);
                reviewList[i].setContent(content);
            }
            return reviewList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "===> Problem parsing Review JSON", e);
            return null;
        }
    }


    /////////////////////
    // Trailer methods //
    /////////////////////

    public static Trailer[] fetchTrailers(String movieId) {
        URL url = getTrailerUrl(movieId);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "===> Error closing Trailer input stream", e);
        }
        return parseTrailerFromJson(jsonResponse);
    }

    private static URL getTrailerUrl(String movieId) {
        try {
            Uri builtUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendPath(movieId)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", API_KEY)
                    .build();

            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "===> Error creating  Trailer URL", e);
            return null;
        }
    }

    private static Trailer[] parseTrailerFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        final String RESULTS = "results";
        final String NAME = "name";
        final String KEY = "key";

        JSONObject trailerJson;
        String name;
        String key;

        try {
            trailerJson = new JSONObject(json);
            JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);

            Trailer[] trailerList = new Trailer[trailerArray.length()];

            for (int i = 0; i < trailerArray.length(); i++) {
                trailerList[i] = new Trailer();
                JSONObject currentReview = trailerArray.getJSONObject(i);

                name = currentReview.getString(NAME);
                key = currentReview.getString(KEY);

                trailerList[i].setName(name);
                trailerList[i].setKey(key);
            }
            return trailerList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "===> Problem parsing Trailer JSON", e);
            return null;
        }
    }
}
