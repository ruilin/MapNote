package com.muyu.mapnote.footmark;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.muyu.mapnote.app.okayapi.been.OkMomentItem;

import java.util.ArrayList;

public class FootmarkViewModel extends ViewModel {
    private MutableLiveData<ArrayList<OkMomentItem>> momentItems;

    public void init() {

    }

    public MutableLiveData<ArrayList<OkMomentItem>> getMyMoment() {
        if (momentItems == null) {
            momentItems = new MutableLiveData<>();
        }
        return momentItems;
    }
}
