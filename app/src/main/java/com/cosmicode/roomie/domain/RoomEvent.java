package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

public class RoomEvent implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("isPrivate")
    @Expose
    private Boolean isPrivate;

    @SerializedName("startTime")
    @Expose
    private String startTime;

    @SerializedName("endTime")
    @Expose
    private String endTime;

    @SerializedName("roomId")
    @Expose
    private Long roomId;

    @SerializedName("organizerId")
    @Expose
    private Long organizerId;

    private Calendar startTimeCalendar;

    public RoomEvent() {
    }

    public RoomEvent(Long id, String title, String description, Boolean isPrivate, String startTime, String endTime, Long roomId, Long organizerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isPrivate = isPrivate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomId = roomId;
        this.organizerId = organizerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public Calendar getStartTimeCalendar() {
        return startTimeCalendar;
    }

    public void setStartTimeCalendar(Calendar startTimeCalendar) {
        this.startTimeCalendar = startTimeCalendar;
    }

    @Override
    public String toString() {
        return "RoomEvent{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isPrivate=" + isPrivate +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", roomId=" + roomId +
                ", organizerId=" + organizerId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeValue(this.isPrivate);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeValue(this.roomId);
        dest.writeValue(this.organizerId);
    }

    protected RoomEvent(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.description = in.readString();
        this.isPrivate = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.roomId = (Long) in.readValue(Long.class.getClassLoader());
        this.organizerId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<RoomEvent> CREATOR = new Parcelable.Creator<RoomEvent>() {
        @Override
        public RoomEvent createFromParcel(Parcel source) {
            return new RoomEvent(source);
        }

        @Override
        public RoomEvent[] newArray(int size) {
            return new RoomEvent[size];
        }
    };
}
