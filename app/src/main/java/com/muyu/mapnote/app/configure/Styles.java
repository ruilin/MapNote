package com.muyu.mapnote.app.configure;

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

    public static String formatContentToHtml(String content) {
        return content.replace("\n", "<br/>");
    }

    public static String formatContentToText(String content) {
        return content.replace("<br/>", "\n");
    }
}
