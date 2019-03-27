package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosmicode.roomie.domain.enumeration.ReportType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserReport implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("type")
    @Expose
    private ReportType type;

    @SerializedName("roomieId")
    @Expose
    private Long roomieId;

    @SerializedName("roomId")
    @Expose
    private Long roomId;

    public UserReport() {
    }

    public UserReport(Long id, String date, String description, ReportType type, Long roomieId, Long roomId) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.type = type;
        this.roomieId = roomieId;
        this.roomId = roomId;
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

    public String getDesciption() {
        return description;
    }

    public void setDesciption(String description) {
        this.description = description;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Long getRoomieId() {
        return roomieId;
    }

    public void setRoomieId(Long roomieId) {
        this.roomieId = roomieId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.date);
        dest.writeString(this.description);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeValue(this.roomieId);
        dest.writeValue(this.roomId);
    }

    protected UserReport(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.date = in.readString();
        this.description = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : ReportType.values()[tmpType];
        this.roomieId = (Long) in.readValue(Long.class.getClassLoader());
        this.roomId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserReport> CREATOR = new Parcelable.Creator<UserReport>() {
        @Override
        public UserReport createFromParcel(Parcel source) {
            return new UserReport(source);
        }

        @Override
        public UserReport[] newArray(int size) {
            return new UserReport[size];
        }
    };
}
