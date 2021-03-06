package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosmicode.roomie.domain.enumeration.RoomTaskState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomTask implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("deadline")
    @Expose
    private String deadline;

    @SerializedName("state")
    @Expose
    private RoomTaskState state;

    @SerializedName("roomId")
    @Expose
    private Long roomId;

    public RoomTask() {
    }

    public RoomTask(Long id, String created, String title, String description, String deadline, RoomTaskState state, Long roomId) {
        this.id = id;
        this.created = created;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.state = state;
        this.roomId = roomId;
    }

    public RoomTask(String created, String title, String description, String deadline, RoomTaskState state, Long roomId) {
        this.created = created;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.state = state;
        this.roomId = roomId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public RoomTaskState getState() {
        return state;
    }

    public void setState(RoomTaskState state) {
        this.state = state;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "RoomTask{" +
                "id=" + id +
                ", created='" + created + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", deadline='" + deadline + '\'' +
                ", state=" + state +
                ", roomId=" + roomId +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.created);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.deadline);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeValue(this.roomId);
    }

    protected RoomTask(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.created = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.deadline = in.readString();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : RoomTaskState.values()[tmpState];
        this.roomId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<RoomTask> CREATOR = new Creator<RoomTask>() {
        @Override
        public RoomTask createFromParcel(Parcel source) {
            return new RoomTask(source);
        }

        @Override
        public RoomTask[] newArray(int size) {
            return new RoomTask[size];
        }
    };
}
