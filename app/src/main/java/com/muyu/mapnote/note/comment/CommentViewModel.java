package com.muyu.mapnote.note.comment;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.muyu.mapnote.app.network.okayapi.been.OkCommentItem;

import java.util.ArrayList;

public class CommentViewModel extends ViewModel {

    private MutableLiveData<ArrayList<OkCommentItem>> commentItems;

    public MutableLiveData<ArrayList<OkCommentItem>> getComment() {
        if (commentItems == null) {
            commentItems = new MutableLiveData<>();
        }
        return commentItems;
    }
}
