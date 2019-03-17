package com.cosmicode.roomie.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomExpenseSplitRecord {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("splitId")
    @Expose
    private Long splitId;

    public RoomExpenseSplitRecord() {
    }

    public RoomExpenseSplitRecord(Long id, String date, String state, Long splitId) {
        this.id = id;
        this.date = date;
        this.state = state;
        this.splitId = splitId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getSplitId() {
        return splitId;
    }

    public void setSplitId(Long roomExpenseSplitId) {
        this.splitId = roomExpenseSplitId;
    }

}
