package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class RoomieUser implements Parcelable {

    private String login;
    private String email;
    private final String password;
    private String firstName;
    private String lastName;

    public RoomieUser(String login, String email, String firstName, String lastName, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public RoomieUser(String login, String email, String firstName, String lastName) {
        this(login, email, firstName, lastName, null);
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "RoomieUser{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.login);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
    }

    protected RoomieUser(Parcel in) {
        this.login = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
    }

    public static final Parcelable.Creator<RoomieUser> CREATOR = new Parcelable.Creator<RoomieUser>() {
        @Override
        public RoomieUser createFromParcel(Parcel source) {
            return new RoomieUser(source);
        }

        @Override
        public RoomieUser[] newArray(int size) {
            return new RoomieUser[size];
        }
    };
}
