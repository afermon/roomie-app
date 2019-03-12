package com.cosmicode.roomie.domain;

import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

;

public class RoomExpense {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("currency")
    @Expose
    private CurrencyType currency;

    @SerializedName("amount")
    @Expose
    private Double amount;

    @SerializedName("periodicity")
    @Expose
    private Integer periodicity;

    @SerializedName("monthDay")
    @Expose
    private Integer monthDay;

    @SerializedName("startDate")
    @Expose
    private String startDate;

    @SerializedName("finishDate")
    @Expose
    private String finishDate;

    @SerializedName("roomId")
    @Expose
    private Long roomId;

    public RoomExpense() {
    }

    public RoomExpense(Long id, String name, String description, CurrencyType currency, Double amount, Integer periodicity, Integer monthDay, String startDate, String finishDate, Long roomId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.periodicity = periodicity;
        this.monthDay = monthDay;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.roomId = roomId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesciption() {
        return description;
    }

    public void setDesciption(String description) {
        this.description = description;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Integer periodicity) {
        this.periodicity = periodicity;
    }

    public Integer getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(Integer monthDay) {
        this.monthDay = monthDay;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

}
