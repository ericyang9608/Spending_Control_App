package com.hyy.accountassis.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Classify implements Parcelable {
    private int id;
    /**
     * 1 income;2 expense
     */
    private int type;
    private String name;

    public Classify() {
    }

    protected Classify(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Classify> CREATOR = new Creator<Classify>() {
        @Override
        public Classify createFromParcel(Parcel in) {
            return new Classify(in);
        }

        @Override
        public Classify[] newArray(int size) {
            return new Classify[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Classify{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
