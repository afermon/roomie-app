package com.cosmicode.roomie.domain;


import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("location")
    @Expose
    private String location;

    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("description")
    @Expose
    private String description;

    public Address() {
    }

    public Address(Long id, String location, String city, String state, String description) {
        this.id = id;
        this.location = location;
        this.city = city;
        this.state = state;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return  Double.valueOf(location.split(",")[0]);
    }

    public Double getLongitude() {
        return Double.valueOf(location.split(",")[1]);
    }

    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setAltitude(0);
        location.setLatitude(getLatitude());
        location.setLongitude(getLongitude());
        return location;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.location);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.description);
    }

    protected Address(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.location = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.description = in.readString();
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
