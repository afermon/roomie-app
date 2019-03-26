package com.cosmicode.roomie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosmicode.roomie.domain.enumeration.FeatureType;
import com.cosmicode.roomie.domain.enumeration.Lang;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomFeature implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("lang")
    @Expose
    private Lang lang;

    @SerializedName("type")
    @Expose
    private FeatureType type;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("description")
    @Expose
    private String description;

    public RoomFeature() {
    }

    public RoomFeature(Long id, Lang lang, FeatureType type, String name, String icon, String description) {
        this.id = id;
        this.lang = lang;
        this.type = type;
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    public FeatureType getType() {
        return type;
    }

    public void setType(FeatureType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDesciription() {
        return description;
    }

    public void setDesciription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RoomFeature{" +
                "id=" + id +
                ", lang=" + lang +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.lang == null ? -1 : this.lang.ordinal());
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.description);
    }

    protected RoomFeature(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        int tmpLang = in.readInt();
        this.lang = tmpLang == -1 ? null : Lang.values()[tmpLang];
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : FeatureType.values()[tmpType];
        this.name = in.readString();
        this.icon = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<RoomFeature> CREATOR = new Parcelable.Creator<RoomFeature>() {
        @Override
        public RoomFeature createFromParcel(Parcel source) {
            return new RoomFeature(source);
        }

        @Override
        public RoomFeature[] newArray(int size) {
            return new RoomFeature[size];
        }
    };
}
