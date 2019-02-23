package com.muyu.mapnote.footmark;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkMoment;
import com.muyu.mapnote.app.okayapi.OkMomentItem;
import com.muyu.mapnote.app.okayapi.callback.MomentListCallback;

import java.util.ArrayList;
import java.util.List;

public class FootmarkViewModel extends ViewModel {
    private MutableLiveData<ArrayList<OkMomentItem>> momentItems;

    public void init() {

    }

    public MutableLiveData<ArrayList<OkMomentItem>> getMyMoment() {
        if (momentItems == null) {
            ArrayList<OkMomentItem> list = new ArrayList<OkMomentItem>();
            momentItems = new MutableLiveData<>();
        }
        return momentItems;
    }
}
