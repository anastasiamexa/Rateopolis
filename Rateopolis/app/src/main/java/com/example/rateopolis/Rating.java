package com.example.rateopolis;

import android.os.Parcel;
import android.os.Parcelable;

public class Rating implements Parcelable {
    // Rating attributes
    private String  Date, Score, Title, Uid, Comment;

    // Constructor for parcelable
    protected Rating(Parcel in) {
        Date = in.readString();
        Score = in.readString();
        Title = in.readString();
        Uid = in.readString();
        Comment = in.readString();
    }

    // Constructor
    public Rating(String date, String score, String title, String uid, String comment) {
        Date = date;
        Score = score;
        Title = title;
        Uid = uid;
        Comment = comment;
    }

    // Empty constructor
    protected Rating(){}

    // Getters
    public String getDate() {
        return Date;
    }

    public String getScore() {
        return Score;
    }

    public String getTitle() {
        return Title;
    }

    public String getUid() {
        return Uid;
    }

    public String getComment() {
        return Comment;
    }

    // Setters
    public void setDate(String date) {
        Date = date;
    }

    public void setScore(String score) {
        Score = score;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setComment(String comment) {
        Comment = comment;
    }


    // Interface that must be implemented and provided as a public CREATOR field that generates
    // instances of your Parcelable class from a Parcel.
    public static final Creator<Rating> CREATOR = new Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Date);
        parcel.writeString(Score);
        parcel.writeString(Title);
        parcel.writeString(Uid);
        parcel.writeString(Comment);
    }
}
