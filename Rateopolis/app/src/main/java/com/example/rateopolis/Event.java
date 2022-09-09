package com.example.rateopolis;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    // Event attributes
    private String  Address, Hour, Latitude, Longitude, Area, Title, Cover;

    // Constructor for parcelable
    protected Event(Parcel in) {
        Address = in.readString();
        Hour = in.readString();
        Latitude = in.readString();
        Longitude = in.readString();
        Area = in.readString();
        Title = in.readString();
        Cover = in.readString();
    }

    // Empty constructor
    protected Event(){}


    // Interface that must be implemented and provided as a public CREATOR field that generates
    // instances of your Parcelable class from a Parcel.
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    // Getters
    public String getAddress() {
        return Address;
    }

    public String getHour() {
        return Hour;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String getArea() {
        return Area;
    }

    public String getTitle() {
        return Title;
    }

    public String getCover() { return Cover; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Address);
        dest.writeString(Hour);
        dest.writeString(Latitude);
        dest.writeString(Longitude);
        dest.writeString(Area);
        dest.writeString(Title);
        dest.writeString(Cover);
    }
}
