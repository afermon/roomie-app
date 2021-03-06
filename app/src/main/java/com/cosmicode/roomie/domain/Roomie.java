package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosmicode.roomie.domain.enumeration.Gender;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Roomie implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("birthDate")
    @Expose
    private String birthDate;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("gender")
    @Expose
    private Gender gender;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("biography")
    @Expose
    private String biography;
    @SerializedName("mobileDeviceID")
    @Expose
    private String mobileDeviceID;
    @SerializedName("userId")
    @Expose
    private Long userId;
    @SerializedName("user")
    @Expose
    private JhiAccount user;
    @SerializedName("stateId")
    @Expose
    private Long stateId;
    @SerializedName("addressId")
    @Expose
    private Long addressId;
    @SerializedName("configurationId")
    @Expose
    private Long configurationId;
    @SerializedName("lifestyles")
    @Expose
    private List<RoomFeature> lifestyles = null;

    public Roomie() {
    }

    public Roomie(String birthDate, String picture, Gender gender, String phone, String biography, String mobileDeviceID, Long userId, Long stateId, Long addressId, Long configurationId, List<RoomFeature> lifestyles) {
        this.birthDate = birthDate;
        this.picture = picture;
        this.gender = gender;
        this.phone = phone;
        this.biography = biography;
        this.mobileDeviceID = mobileDeviceID;
        this.userId = userId;
        this.stateId = stateId;
        this.addressId = addressId;
        this.configurationId = configurationId;
        this.lifestyles = lifestyles;
    }

    public Roomie(Long id, String birthDate, String picture, Gender gender, String phone, String biography, String mobileDeviceID, Long userId, Long stateId, Long addressId, Long configurationId, List<RoomFeature> lifestyles) {
        this.id = id;
        this.birthDate = birthDate;
        this.picture = picture;
        this.gender = gender;
        this.phone = phone;
        this.biography = biography;
        this.mobileDeviceID = mobileDeviceID;
        this.userId = userId;
        this.stateId = stateId;
        this.addressId = addressId;
        this.configurationId = configurationId;
        this.lifestyles = lifestyles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobileDeviceID() {
        return mobileDeviceID;
    }

    public void setMobileDeviceID(String mobileDeviceID) {
        this.mobileDeviceID = mobileDeviceID;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    public List<RoomFeature> getLifestyles() {
        return lifestyles;
    }

    public void setLifestyles(List<RoomFeature> lifestyles) {
        this.lifestyles = lifestyles;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public JhiAccount getUser() {
        return user;
    }

    public void setUser(JhiAccount user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Roomie{" +
                "id=" + id +
                ", birthDate='" + birthDate + '\'' +
                ", picture='" + picture + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", biography='" + biography + '\'' +
                ", mobileDeviceID='" + mobileDeviceID + '\'' +
                ", userId=" + userId +
                ", stateId=" + stateId +
                ", addressId=" + addressId +
                ", configurationId=" + configurationId +
                ", lifestyles=" + lifestyles +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.birthDate);
        dest.writeString(this.picture);
        dest.writeInt(this.gender == null ? -1 : this.gender.ordinal());
        dest.writeString(this.phone);
        dest.writeString(this.biography);
        dest.writeString(this.mobileDeviceID);
        dest.writeValue(this.userId);
        dest.writeValue(this.stateId);
        dest.writeValue(this.addressId);
        dest.writeValue(this.configurationId);
        dest.writeTypedList(this.lifestyles);
    }

    protected Roomie(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.birthDate = in.readString();
        this.picture = in.readString();
        int tmpGender = in.readInt();
        this.gender = tmpGender == -1 ? null : Gender.values()[tmpGender];
        this.phone = in.readString();
        this.biography = in.readString();
        this.mobileDeviceID = in.readString();
        this.userId = (Long) in.readValue(Long.class.getClassLoader());
        this.stateId = (Long) in.readValue(Long.class.getClassLoader());
        this.addressId = (Long) in.readValue(Long.class.getClassLoader());
        this.configurationId = (Long) in.readValue(Long.class.getClassLoader());
        this.lifestyles = in.createTypedArrayList(RoomFeature.CREATOR);
    }

    public static final Creator<Roomie> CREATOR = new Creator<Roomie>() {
        @Override
        public Roomie createFromParcel(Parcel source) {
            return new Roomie(source);
        }

        @Override
        public Roomie[] newArray(int size) {
            return new Roomie[size];
        }
    };
}

