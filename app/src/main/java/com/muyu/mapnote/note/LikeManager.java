package com.muyu.mapnote.note;

import java.util.HashSet;
import java.util.Hashtable;

public class LikeManager {
    private HashSet<String> mSet = new HashSet<>();
    private static LikeManager ins = new LikeManager();

    private LikeManager() {
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
}
