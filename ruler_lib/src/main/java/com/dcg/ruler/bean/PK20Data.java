package com.dcg.ruler.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 张明_ on 2017/9/5.
 */

public class PK20Data implements Parcelable {
    public String barCode;

    public String wangDian;
    public String center;
    public String muDi;
    public String liuCheng;
    public String L;
    public String W;
    public String H;
    public String V;
    public String G;
    public String time;
    public String zhu;
    public String zi;
    public String mac;
    public String biaoshi;
    public String biaoJi;
    public String name;

    public PK20Data(String barCode, String wangDian, String center, String muDi, String liuCheng, String l, String w, String h, String v, String g, String time, String zhu, String zi, String mac, String biaoshi, String biaoJi, String name) {
        this.barCode = barCode;
        this.wangDian = wangDian;
        this.center = center;
        this.muDi = muDi;
        this.liuCheng = liuCheng;
        L = l;
        W = w;
        H = h;
        V = v;
        G = g;
        this.time = time;
        this.zhu = zhu;
        this.zi = zi;
        this.mac = mac;
        this.biaoshi = biaoshi;
        this.biaoJi = biaoJi;
        this.name = name;
    }

    protected PK20Data(Parcel in) {
        barCode = in.readString();
        wangDian = in.readString();
        center = in.readString();
        muDi = in.readString();
        liuCheng = in.readString();
        L = in.readString();
        W = in.readString();
        H = in.readString();
        V = in.readString();
        G = in.readString();
        time = in.readString();
        zhu = in.readString();
        zi = in.readString();
        mac = in.readString();
        biaoshi = in.readString();
        biaoJi = in.readString();
        name = in.readString();
    }

    public static final Creator<PK20Data> CREATOR = new Creator<PK20Data>() {
        @Override
        public PK20Data createFromParcel(Parcel in) {
            return new PK20Data(in);
        }

        @Override
        public PK20Data[] newArray(int size) {
            return new PK20Data[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barCode);
        dest.writeString(wangDian);
        dest.writeString(center);
        dest.writeString(muDi);
        dest.writeString(liuCheng);
        dest.writeString(L);
        dest.writeString(W);
        dest.writeString(H);
        dest.writeString(V);
        dest.writeString(G);
        dest.writeString(time);
        dest.writeString(zhu);
        dest.writeString(zi);
        dest.writeString(mac);
        dest.writeString(biaoshi);
        dest.writeString(biaoJi);
        dest.writeString(name);
    }
}
