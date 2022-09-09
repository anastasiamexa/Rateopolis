package com.example.rateopolis;

import android.os.Parcel;
import android.os.Parcelable;

public class Store implements Parcelable {
    // Event attributes
    private String  Address, Hour, Latitude, Longitude, Title, Cover, Area;

    // Constructor for parcelable
    protected Store(Parcel in) {
        Address = in.readString();
        Hour = in.readString();
        Latitude = in.readString();
        Longitude = in.readString();
        Title = in.readString();
        Cover = in.readString();
        Area = in.readString();
    }

    // Empty constructor
    protected Store(){}

    // Interface that must be implemented and provided as a public CREATOR field that generates
    // instances of your Parcelable class from a Parcel.
    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
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

    public String getTitle() {
        return Title;
    }

    public String getCover() {
        return Cover;
    }

    public String getArea() {
        return Area;
    }

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
        dest.writeString(Title);
        dest.writeString(Cover);
        dest.writeString(Area);
    }
}
