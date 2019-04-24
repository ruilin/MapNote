package com.muyu.mapnote.app.network.okayapi.been;

import android.os.Parcel;
import android.os.Parcelable;

public class OkMomentItem implements Parcelable {
    public boolean isValid = true;
    public String id;
    public String uuid = "";
    public String moment_content = "";
    public String moment_url = "";
    public String moment_createtime = "";
    public int moment_like = 0;
    public String moment_picture1;
    public String moment_picture2;
    public String moment_picture3;
    public String moment_picture4;
    public String moment_picture5;
    public String moment_picture6;
    public String moment_picture7;
    public String moment_picture8;
    public String moment_picture9;

    public double moment_lat = 0;
    public double moment_lng = 0;
    public String moment_place = "";
    public String moment_nickname = "";
    public String moment_headimg = "";

    protected OkMomentItem(Parcel in) {
        isValid = in.readByte() != 0;
        id = in.readString();
        uuid = in.readString();
        moment_content = in.readString();
        moment_url = in.readString();
        moment_createtime = in.readString();
        moment_like = in.readInt();
        moment_picture1 = in.readString();
        moment_picture2 = in.readString();
        moment_picture3 = in.readString();
        moment_picture4 = in.readString();
        moment_picture5 = in.readString();
        moment_picture6 = in.readString();
        moment_picture7 = in.readString();
        moment_picture8 = in.readString();
        moment_picture9 = in.readString();
        moment_lat = in.readDouble();
        moment_lng = in.readDouble();
        moment_place = in.readString();
        moment_nickname = in.readString();
        moment_headimg = in.readString();
    }

    public static final Creator<OkMomentItem> CREATOR = new Creator<OkMomentItem>() {
        @Override
        public OkMomentItem createFromParcel(Parcel in) {
            return new OkMomentItem(in);
        }

        @Override
        public OkMomentItem[] newArray(int size) {
            return new OkMomentItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isValid ? 1 : 0));
        dest.writeString(id);
        dest.writeString(uuid);
        dest.writeString(moment_content);
        dest.writeString(moment_url);
        dest.writeString(moment_createtime);
        dest.writeInt(moment_like);
        dest.writeString(moment_picture1);
        dest.writeString(moment_picture2);
        dest.writeString(moment_picture3);
        dest.writeString(moment_picture4);
        dest.writeString(moment_picture5);
        dest.writeString(moment_picture6);
        dest.writeString(moment_picture7);
        dest.writeString(moment_picture8);
        dest.writeString(moment_picture9);
        dest.writeDouble(moment_lat);
        dest.writeDouble(moment_lng);
        dest.writeString(moment_place);
        dest.writeString(moment_nickname);
        dest.writeString(moment_headimg);
    }
}
