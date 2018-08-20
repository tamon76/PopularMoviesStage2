package com.example.android.popularmoviesstage2.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "favorites")
public class Movie implements Parcelable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="id")
    private String mId;
    @ColumnInfo(name="title")
    private String mOriginalTitle;
    @ColumnInfo(name = "image")
    private String mThumbnailPath;
    @ColumnInfo(name = "overview")
    private String mOverview;
    @ColumnInfo(name = "rating")
    private String mVoteAverage;
    @ColumnInfo(name = "release_date")
    private String mReleaseDate;
    @Ignore
    private int mFavorite = 0;

    public Movie() {

    }

    //Setter methods
    public void setId(String id) {
        mId = id;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public void setThumbnailPath(String thumbnailPath) {
        mThumbnailPath = thumbnailPath;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public void setFavorite(int favorite) {
        mFavorite = favorite;
    }


    //Getter methods
    public String getId() {
        return mId;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getThumbnailPath() {
       return mThumbnailPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getFavorite() {
        return mFavorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mOriginalTitle);
        dest.writeString(mThumbnailPath);
        dest.writeString(mOverview);
        dest.writeString(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeInt(mFavorite);

    }

    private Movie(Parcel source) {
        mId = source.readString();
        mOriginalTitle = source.readString();
        mThumbnailPath = source.readString();
        mOverview = source.readString();
        mVoteAverage = source.readString();
        mReleaseDate = source.readString();
        mFavorite = source.readInt();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
