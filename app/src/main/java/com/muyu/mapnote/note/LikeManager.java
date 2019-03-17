package com.muyu.mapnote.note;

import android.content.Context;

import com.muyu.mapnote.app.MapApplication;
import com.muyu.minimalism.utils.SPUtils;
import com.muyu.minimalism.utils.StringUtils;

import java.util.HashSet;
import java.util.Hashtable;

public class LikeManager {
    private HashSet<String> mSet = new HashSet<>();
    private static LikeManager ins = new LikeManager();

    private LikeManager() {
        String like = SPUtils.get("moment_like", "");
        if (!StringUtils.isEmpty(like)) {
            String[] items = like.split(":");
            for (String item : items) {
                put(item);
            }
        }
    }

    public static LikeManager get() {
        return ins;
    }

    public void put(String id) {
        if (!mSet.contains(id)) {
            mSet.add(id);
        }
    }

    public boolean hadPut(String id) {
        return mSet.contains(id);
    }

    public void finish() {
        StringBuffer sb = new StringBuffer();
        for (String item : mSet) {
            sb.append(item);
            sb.append(':');
        }
        SPUtils.put(MapApplication.getInstance(), "moment_like", sb.toString());
    }
}
