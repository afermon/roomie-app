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
    @SerializedName("lat")
    @Expose
    private BigDecimal lat;
    @SerializedName("lon")
    @Expose
    private BigDecimal lon;
    @SerializedName("city")
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

    public Address(Long id, BigDecimal lat, BigDecimal lon, String city, String state, String description) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
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

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
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

    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setAltitude(0);
        location.setLatitude(lat.doubleValue());
        location.setLongitude(lon.doubleValue());
        return location;
    }

}
