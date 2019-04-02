package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosmicode.roomie.domain.enumeration.AppointmentState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Appointment implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("dateTime")
    @Expose
    private String dateTime;

    @SerializedName("state")
    @Expose
    private AppointmentState state;

    @SerializedName("petitionerId")
    @Expose
    private Long petitionerId;

    @SerializedName("petitioner")
    @Expose
    private Roomie petitioner;

    @SerializedName("roomId")
    @Expose
    private Long roomId;

    @SerializedName("room")
    @Expose
    private Room room;

    private boolean isOwner;

    public Appointment() {
    }

    public Appointment(Long id, String description, String dateTime, AppointmentState state, Long petitionerId, Long roomId) {
        this.id = id;
        this.description = description;
        this.dateTime = dateTime;
        this.state = state;
        this.petitionerId = petitionerId;
        this.roomId = roomId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public AppointmentState getState() {
        return state;
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }

    public Long getPetitionerId() {
        return petitionerId;
    }

    public void setPetitionerId(Long petitionerId) {
        this.petitionerId = petitionerId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Roomie getPetitioner() {
        return petitioner;
    }

    public void setPetitioner(Roomie petitioner) {
        this.petitioner = petitioner;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void isOwner(boolean owner) {
        isOwner = owner;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", state=" + state +
                ", petitionerId=" + petitionerId +
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
        dest.writeString(this.description);
        dest.writeString(this.dateTime);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeValue(this.petitionerId);
        dest.writeValue(this.roomId);
    }

    protected Appointment(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.description = in.readString();
        this.dateTime = in.readString();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : AppointmentState.values()[tmpState];
        this.petitionerId = (Long) in.readValue(Long.class.getClassLoader());
        this.roomId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<Appointment> CREATOR = new Parcelable.Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel source) {
            return new Appointment(source);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
}
