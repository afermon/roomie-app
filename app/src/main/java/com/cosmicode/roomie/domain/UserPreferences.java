package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPreferences implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("currency")
    @Expose
    private CurrencyType currency;

    @SerializedName("todoListNotifications")
    @Expose
    private Boolean todoListNotifications;

    @SerializedName("calendarNotifications")
    @Expose
    private Boolean calendarNotifications;

    @SerializedName("paymentsNotifications")
    @Expose
    private Boolean paymentsNotifications;

    @SerializedName("appointmentsNotifications")
    @Expose
    private Boolean appointmentsNotifications;

    public UserPreferences() {
    }

    public UserPreferences(Long id, CurrencyType currency, Boolean todoListNotifications, Boolean calendarNotifications, Boolean paymentsNotifications, Boolean appointmentsNotifications) {
        this.id = id;
        this.currency = currency;
        this.todoListNotifications = todoListNotifications;
        this.calendarNotifications = calendarNotifications;
        this.paymentsNotifications = paymentsNotifications;
        this.appointmentsNotifications = appointmentsNotifications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public Boolean isTodoListNotifications() {
        return todoListNotifications;
    }

    public void setTodoListNotifications(Boolean todoListNotifications) {
        this.todoListNotifications = todoListNotifications;
    }

    public Boolean isCalendarNotifications() {
        return calendarNotifications;
    }

    public void setCalendarNotifications(Boolean calendarNotifications) {
        this.calendarNotifications = calendarNotifications;
    }

    public Boolean isPaymentsNotifications() {
        return paymentsNotifications;
    }

    public void setPaymentsNotifications(Boolean paymentsNotifications) {
        this.paymentsNotifications = paymentsNotifications;
    }

    public Boolean isAppointmentsNotifications() {
        return appointmentsNotifications;
    }

    public void setAppointmentsNotifications(Boolean appointmentsNotifications) {
        this.appointmentsNotifications = appointmentsNotifications;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.currency == null ? -1 : this.currency.ordinal());
        dest.writeValue(this.todoListNotifications);
        dest.writeValue(this.calendarNotifications);
        dest.writeValue(this.paymentsNotifications);
        dest.writeValue(this.appointmentsNotifications);
    }

    protected UserPreferences(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        int tmpCurrency = in.readInt();
        this.currency = tmpCurrency == -1 ? null : CurrencyType.values()[tmpCurrency];
        this.todoListNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.calendarNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.paymentsNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.appointmentsNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserPreferences> CREATOR = new Parcelable.Creator<UserPreferences>() {
        @Override
        public UserPreferences createFromParcel(Parcel source) {
            return new UserPreferences(source);
        }

        @Override
        public UserPreferences[] newArray(int size) {
            return new UserPreferences[size];
        }
    };
}
