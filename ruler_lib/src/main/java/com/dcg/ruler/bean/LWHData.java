package com.dcg.ruler.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 张明_ on 2018/2/11.
 */

public class LWHData implements Parcelable {
    public String L;
    public String W;
    public String H;

    public LWHData(String l, String w, String h) {
        L = l;
        W = w;
        H = h;
    }

    protected LWHData(Parcel in) {
        L = in.readString();
        W = in.readString();
        H = in.readString();
    }

    public static final Creator<LWHData> CREATOR = new Creator<LWHData>() {
        @Override
        public LWHData createFromParcel(Parcel in) {
            return new LWHData(in);
        }

        @Override
        public LWHData[] newArray(int size) {
            return new LWHData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(L);
        dest.writeString(W);
        dest.writeString(H);
    }
}
