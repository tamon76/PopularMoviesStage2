package com.example.android.popularmoviesstage2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Trailer implements Parcelable {

    @SerializedName("name")
    private String mName;

    @SerializedName("key")
    private String mKey;

    public Trailer() {

    }

    //Setter methods
    public void setName(String name) {
        mName = name;
    }

    public void setKey(String key) {
        mKey = key;
    }

    //Getter methods
    public String getName() {
        return mName;
    }

    public String getKey() {
        return mKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mKey);
    }

    private Trailer(Parcel source) {
        mName = source.readString();
        mKey = source.readString();
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
