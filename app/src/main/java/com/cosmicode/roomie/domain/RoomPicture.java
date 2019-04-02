package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomPicture implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("isMain")
    @Expose
    private Boolean isMain;

    @SerializedName("roomId")
    @Expose
    private Long roomId;

    public RoomPicture() {
    }

    public RoomPicture(Long id, String url, Boolean isMain, Long roomId) {
        this.id = id;
        this.url = url;
        this.isMain = isMain;
        this.roomId = roomId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
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
        dest.writeString(this.url);
        dest.writeValue(this.isMain);
        dest.writeValue(this.roomId);
    }

    protected RoomPicture(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.url = in.readString();
        this.isMain = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.roomId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<RoomPicture> CREATOR = new Parcelable.Creator<RoomPicture>() {
        @Override
        public RoomPicture createFromParcel(Parcel source) {
            return new RoomPicture(source);
        }

        @Override
        public RoomPicture[] newArray(int size) {
            return new RoomPicture[size];
        }
    };
}
