package com.hyy.accountassis.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Detail implements Parcelable {
    private int id;
    private int account_id;
    private Account account;
    private int type;
    private int classify_id;
    private Classify classify;
    private String comment;
    private double amount;
    private String create_date;
    private long create_time;
    private long update_time;

    public Detail() {
    }


    protected Detail(Parcel in) {
        id = in.readInt();
        account_id = in.readInt();
        type = in.readInt();
        classify_id = in.readInt();
        classify = in.readParcelable(Classify.class.getClassLoader());
        comment = in.readString();
        amount = in.readDouble();
        create_date = in.readString();
        create_time = in.readLong();
        update_time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(account_id);
        dest.writeInt(type);
        dest.writeInt(classify_id);
        dest.writeParcelable(classify, flags);
        dest.writeString(comment);
        dest.writeDouble(amount);
        dest.writeString(create_date);
        dest.writeLong(create_time);
        dest.writeLong(update_time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Detail> CREATOR = new Creator<Detail>() {
        @Override
        public Detail createFromParcel(Parcel in) {
            return new Detail(in);
        }

        @Override
        public Detail[] newArray(int size) {
            return new Detail[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getClassify_id() {
        return classify_id;
    }

    public void setClassify_id(int classify_id) {
        this.classify_id = classify_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Classify getClassify() {
        return classify;
    }

    public void setClassify(Classify classify) {
        this.classify = classify;
    }

    @Override
    public String toString() {
        return "Detail{" +
                "id=" + id +
                ", account_id=" + account_id +
                ", account=" + account +
                ", type=" + type +
                ", classify_id=" + classify_id +
                ", classify=" + classify +
                ", comment='" + comment + '\'' +
                ", amount=" + amount +
                ", create_date='" + create_date + '\'' +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                '}';
    }
}
