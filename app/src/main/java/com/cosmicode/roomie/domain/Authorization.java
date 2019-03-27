package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Authorization implements Parcelable {
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("rememberMe")
    @Expose
    private Boolean rememberMe;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("id_token")
    @Expose
    private String idToken;

    public Authorization(String username, String password, boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return "Authorization{" +
                "password='" + password + '\'' +
                ", rememberMe=" + rememberMe +
                ", username='" + username + '\'' +
                ", idToken='" + idToken + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.password);
        dest.writeValue(this.rememberMe);
        dest.writeString(this.username);
        dest.writeString(this.idToken);
    }

    protected Authorization(Parcel in) {
        this.password = in.readString();
        this.rememberMe = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.username = in.readString();
        this.idToken = in.readString();
    }

    public static final Parcelable.Creator<Authorization> CREATOR = new Parcelable.Creator<Authorization>() {
        @Override
        public Authorization createFromParcel(Parcel source) {
            return new Authorization(source);
        }

        @Override
        public Authorization[] newArray(int size) {
            return new Authorization[size];
        }
    };
}
