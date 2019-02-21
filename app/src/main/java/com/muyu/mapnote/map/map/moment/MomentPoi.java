package com.muyu.mapnote.map.map.moment;

import android.os.Parcel;
import android.os.Parcelable;

import com.muyu.mapnote.map.map.poi.Poi;

import java.util.ArrayList;

public class MomentPoi extends Poi implements Parcelable {

    public ArrayList<String> pictureUrlLiat = new ArrayList<>();
    public String createtime = "";
    public int like = 0;
    public String nickname = "";
    public String content = "";
    public String place = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        String[] pictures = new String[pictureUrlLiat.size()];
        pictureUrlLiat.toArray(pictures);
        out.writeStringArray(pictures);
        out.writeString(createtime);
        out.writeInt(like);
        out.writeString(nickname);
        out.writeString(content);
        out.writeString(place);
    }
}
