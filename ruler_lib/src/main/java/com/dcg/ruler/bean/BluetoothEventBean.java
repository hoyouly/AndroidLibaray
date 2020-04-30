package com.dcg.ruler.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: Severn
 * date: 2020/4/26
 * email: shiszb@digitalchina.com
 * description:
 */
public class BluetoothEventBean implements Parcelable {

    private String tips;
    private String width;
    private String length;
    private String height;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tips);
        dest.writeString(this.width);
        dest.writeString(this.length);
        dest.writeString(this.height);
    }

    public BluetoothEventBean() {
    }

    protected BluetoothEventBean(Parcel in) {
        this.tips = in.readString();
        this.width = in.readString();
        this.length = in.readString();
        this.height = in.readString();
    }

    public static final Parcelable.Creator<BluetoothEventBean> CREATOR = new Parcelable.Creator<BluetoothEventBean>() {
        @Override
        public BluetoothEventBean createFromParcel(Parcel source) {
            return new BluetoothEventBean(source);
        }

        @Override
        public BluetoothEventBean[] newArray(int size) {
            return new BluetoothEventBean[size];
        }
    };
}
