package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomExpenseSplitRecord implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.date);
        dest.writeString(this.state);
        dest.writeValue(this.splitId);
    }

    protected RoomExpenseSplitRecord(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.date = in.readString();
        this.state = in.readString();
        this.splitId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<RoomExpenseSplitRecord> CREATOR = new Parcelable.Creator<RoomExpenseSplitRecord>() {
        @Override
        public RoomExpenseSplitRecord createFromParcel(Parcel source) {
            return new RoomExpenseSplitRecord(source);
        }

        @Override
        public RoomExpenseSplitRecord[] newArray(int size) {
            return new RoomExpenseSplitRecord[size];
        }
    };
}
