package com.cosmicode.roomie.domain;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.cosmicode.roomie.domain.enumeration.RoomState;
import com.cosmicode.roomie.domain.enumeration.RoomType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoomCreate implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("state")
    @Expose
    private RoomState state;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("published")
    @Expose
    private String published;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("rooms")
    @Expose
    private Integer rooms;
    @SerializedName("roomType")
    @Expose
    private RoomType roomType;
    @SerializedName("apoinmentsNotes")
    @Expose
    private String apoinmentsNotes;
    @SerializedName("lookingForRoomie")
    @Expose
    private Boolean lookingForRoomie;
    @SerializedName("availableFrom")
    @Expose
    private String availableFrom;
    @SerializedName("isPremium")
    @Expose
    private Boolean isPremium;
    @SerializedName("addressId")
    @Expose
    private Long addressId;
    @SerializedName("roomies")
    @Expose
    private List<Roomie> roomies = null;
    @SerializedName("features")
    @Expose
    private List<RoomFeature> features = null;
    @SerializedName("expenses")
    @Expose
    private List<RoomExpense> expenses = null;
    @SerializedName("ownerId")
    @Expose
    private Long ownerId;

    @SerializedName("priceId")
    @Expose
    private Long priceId;
    @SerializedName("pictures")
    @Expose
    private List<RoomPicture> pictures = null;

    private List<Uri> picturesUris = null;

    private RoomExpense monthly = null;

    public RoomCreate() {
    }

    public RoomCreate(Long id, RoomState state, String created, String published, String title, String description, Integer rooms, RoomType roomType, String apoinmentsNotes, Boolean lookingForRoomie, String availableFrom, Boolean isPremium, Long addressId, List<Roomie> roomies, List<RoomFeature> features, List<RoomExpense> expenses, Long ownerId, Long priceId, List<RoomPicture> pictures, List<Uri> picturesUris, RoomExpense monthly) {
        this.id = id;
        this.state = state;
        this.created = created;
        this.published = published;
        this.title = title;
        this.description = description;
        this.rooms = rooms;
        this.roomType = roomType;
        this.apoinmentsNotes = apoinmentsNotes;
        this.lookingForRoomie = lookingForRoomie;
        this.availableFrom = availableFrom;
        this.isPremium = isPremium;
        this.addressId = addressId;
        this.roomies = roomies;
        this.features = features;
        this.expenses = expenses;
        this.ownerId = ownerId;
        this.priceId = priceId;
        this.pictures = pictures;
        this.picturesUris = picturesUris;
        this.monthly = monthly;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomState getState() {
        return state;
    }

    public void setState(RoomState state) {
        this.state = state;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
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

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getApoinmentsNotes() {
        return apoinmentsNotes;
    }

    public void setApoinmentsNotes(String apoinmentsNotes) {
        this.apoinmentsNotes = apoinmentsNotes;
    }

    public Boolean getLookingForRoomie() {
        return lookingForRoomie;
    }

    public void setLookingForRoomie(Boolean lookingForRoomie) {
        this.lookingForRoomie = lookingForRoomie;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public List<Roomie> getRoomies() {
        return roomies;
    }

    public void setRoomies(List<Roomie> roomies) {
        this.roomies = roomies;
    }

    public List<RoomFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<RoomFeature> features) {
        this.features = features;
    }

    public List<RoomExpense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<RoomExpense> expenses) {
        this.expenses = expenses;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public List<RoomPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<RoomPicture> pictures) {
        this.pictures = pictures;
    }

    public List<Uri> getPicturesUris() {
        return picturesUris;
    }

    public void setPicturesUris(List<Uri> picturesUris) {
        this.picturesUris = picturesUris;
    }

    public RoomExpense getMonthly() {
        return monthly;
    }

    public void setMonthly(RoomExpense monthly) {
        this.monthly = monthly;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeString(this.created);
        dest.writeString(this.published);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeValue(this.rooms);
        dest.writeInt(this.roomType == null ? -1 : this.roomType.ordinal());
        dest.writeString(this.apoinmentsNotes);
        dest.writeValue(this.lookingForRoomie);
        dest.writeString(this.availableFrom);
        dest.writeValue(this.isPremium);
        dest.writeValue(this.addressId);
        dest.writeTypedList(this.roomies);
        dest.writeTypedList(this.features);
        dest.writeTypedList(this.expenses);
        dest.writeValue(this.ownerId);
        dest.writeValue(this.priceId);
        dest.writeTypedList(this.pictures);
        dest.writeTypedList(this.picturesUris);
        dest.writeParcelable(this.monthly, flags);
    }

    protected RoomCreate(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : RoomState.values()[tmpState];
        this.created = in.readString();
        this.published = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.rooms = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpRoomType = in.readInt();
        this.roomType = tmpRoomType == -1 ? null : RoomType.values()[tmpRoomType];
        this.apoinmentsNotes = in.readString();
        this.lookingForRoomie = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.availableFrom = in.readString();
        this.isPremium = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.addressId = (Long) in.readValue(Long.class.getClassLoader());
        this.roomies = in.createTypedArrayList(Roomie.CREATOR);
        this.features = in.createTypedArrayList(RoomFeature.CREATOR);
        this.expenses = in.createTypedArrayList(RoomExpense.CREATOR);
        this.ownerId = (Long) in.readValue(Long.class.getClassLoader());
        this.priceId = (Long) in.readValue(Long.class.getClassLoader());
        this.pictures = in.createTypedArrayList(RoomPicture.CREATOR);
        this.picturesUris = in.createTypedArrayList(Uri.CREATOR);
        this.monthly = in.readParcelable(RoomExpense.class.getClassLoader());
    }

    public static final Creator<RoomCreate> CREATOR = new Creator<RoomCreate>() {
        @Override
        public RoomCreate createFromParcel(Parcel source) {
            return new RoomCreate(source);
        }

        @Override
        public RoomCreate[] newArray(int size) {
            return new RoomCreate[size];
        }
    };
}
