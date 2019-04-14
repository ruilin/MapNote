package com.muyu.mapnote.app;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;

import com.muyu.mapnote.R;

public class Styles {

    public static void refreshView(Activity activity, SwipeRefreshLayout mRefreshView) {
        mRefreshView.setColorSchemeColors(
                activity.getResources().getColor(R.color.colorPrimary),
                activity.getResources().getColor(R.color.darkorange),
                activity.getResources().getColor(R.color.greenyellow));
    }
}
