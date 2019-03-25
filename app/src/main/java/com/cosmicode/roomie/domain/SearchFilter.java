package com.cosmicode.roomie.domain;

import android.location.Location;
import android.location.LocationManager;

import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchFilter {

    @SerializedName("query")
    @Expose
    private String query;

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longitude")
    @Expose
    private Double longitude;

    @SerializedName("distance")
    @Expose
    private int distance;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("currency")
    @Expose
    private CurrencyType currency;

    @SerializedName("priceMin")
    @Expose
    private int priceMin;

    @SerializedName("priceMax")
    @Expose
    private int priceMax;

    @SerializedName("features")
    @Expose
    private List<RoomFeature> features = null;

    public SearchFilter() {
    }

    public SearchFilter(String query, int distance, CurrencyType currency, int priceMin, int priceMax, List<RoomFeature> features) {
        this.query = query;
        this.distance = distance;
        this.currency = currency;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.features = features;
    }

    public SearchFilter(String query, Double latitude, Double longitude, int distance, CurrencyType currency, int priceMin, int priceMax, List<RoomFeature> features) {
        this.query = query;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.currency = currency;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.features = features;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public int getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(int priceMin) {
        this.priceMin = priceMin;
    }

    public int getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(int priceMax) {
        this.priceMax = priceMax;
    }

    public List<RoomFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<RoomFeature> features) {
        this.features = features;
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

    @Override
    public String toString() {
        return "SearchFilter{" +
                "query='" + query + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                ", currency=" + currency +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", features=" + features +
                '}';
    }

    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setAltitude(0);
        location.setLatitude(getLatitude());
        location.setLongitude(getLongitude());
        return location;
    }
}
