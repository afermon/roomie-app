package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosmicode.roomie.domain.enumeration.AccountState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomieState implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("state")
    @Expose
    private AccountState state;

    @SerializedName("suspendedDate")
    @Expose
    private String suspendedDate;

    public RoomieState() {
    }

    public RoomieState(Long id, AccountState state, String suspendedDate) {
        this.id = id;
        this.state = state;
        this.suspendedDate = suspendedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountState getState() {
        return state;
    }

    public void setState(AccountState state) {
        this.state = state;
    }

    public String getSuspendedDate() {
        return suspendedDate;
    }

    public void setSuspendedDate(String suspendedDate) {
        this.suspendedDate = suspendedDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeString(this.suspendedDate);
    }

    protected RoomieState(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : AccountState.values()[tmpState];
        this.suspendedDate = in.readString();
    }

    public static final Parcelable.Creator<RoomieState> CREATOR = new Parcelable.Creator<RoomieState>() {
        @Override
        public RoomieState createFromParcel(Parcel source) {
            return new RoomieState(source);
        }

        @Override
        public RoomieState[] newArray(int size) {
            return new RoomieState[size];
        }
    };
}
