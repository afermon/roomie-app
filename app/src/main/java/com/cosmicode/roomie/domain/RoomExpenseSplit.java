package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomExpenseSplit implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("amount")
    @Expose
    private Double amount;

    @SerializedName("expenseId")
    @Expose
    private Long expenseId;

    @SerializedName("roomieId")
    @Expose
    private Long roomieId;

    public RoomExpenseSplit() {
    }

    public RoomExpenseSplit(Long id, Double amount, Long expenseId, Long roomieId) {
        this.id = id;
        this.amount = amount;
        this.expenseId = expenseId;
        this.roomieId = roomieId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long roomExpenseId) {
        this.expenseId = roomExpenseId;
    }

    public Long getRoomieId() {
        return roomieId;
    }

    public void setRoomieId(Long roomieId) {
        this.roomieId = roomieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.amount);
        dest.writeValue(this.expenseId);
        dest.writeValue(this.roomieId);
    }

    protected RoomExpenseSplit(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.expenseId = (Long) in.readValue(Long.class.getClassLoader());
        this.roomieId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<RoomExpenseSplit> CREATOR = new Parcelable.Creator<RoomExpenseSplit>() {
        @Override
        public RoomExpenseSplit createFromParcel(Parcel source) {
            return new RoomExpenseSplit(source);
        }

        @Override
        public RoomExpenseSplit[] newArray(int size) {
            return new RoomExpenseSplit[size];
        }
    };
}
