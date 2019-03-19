package com.cosmicode.roomie.domain;


import android.location.Location;
import android.location.LocationManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Address {

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

}
