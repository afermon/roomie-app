package com.cosmicode.roomie.domain;

import com.cosmicode.roomie.domain.enumeration.AppointmentState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

;

public class Appointment {

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

    @SerializedName("roomId")
    @Expose
    private Long roomId;


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

    public String getDesciption() {
        return description;
    }

    public void setDesciption(String description) {
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


}
