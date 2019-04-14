package com.muyu.mapnote.message

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import com.muyu.mapnote.app.okayapi.been.OkMessageItem

class MessageViewModel : ViewModel() {
    private var msgItems: MutableLiveData<ArrayList<OkMessageItem>>? = null

    fun getMessage() : MutableLiveData<ArrayList<OkMessageItem>> {
        if (msgItems == null) {
            msgItems = MutableLiveData()
        }
        return msgItems as MutableLiveData<ArrayList<OkMessageItem>>
    }
}
