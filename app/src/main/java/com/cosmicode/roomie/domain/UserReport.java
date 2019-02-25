package com.cosmicode.roomie.domain;

import com.cosmicode.roomie.domain.enumeration.ReportType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class UserReport {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("date")
    @Expose
    private LocalDate date;

    @SerializedName("desciption")
    @Expose
    private String desciption;

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

    public UserReport(Long id, LocalDate date, String desciption, ReportType type, Long roomieId, Long roomId) {
        this.id = id;
        this.date = date;
        this.desciption = desciption;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
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

}
