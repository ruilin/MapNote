package com.muyu.mapnote.map.user;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.muyu.mapnote.R;
import com.muyu.mapnote.app.okayapi.OkayApi;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.map.map.poi.Poi;
import com.muyu.mapnote.user.activity.LoginActivity;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.controller.ActivityController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class UserController extends ActivityController {
    private View headView;

    @Override
    public void onCreate(BaseActivity activity) {
        super.onCreate(activity);
        EventBus.getDefault().register(this);

        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        headView = navigationView.getHeaderView(0);
        updateLogin();
    }

    private void updateLogin() {
        if (OkayApi.get().isLogined()) {
            ((TextView)headView.findViewById(R.id.nav_username)).setText(OkayApi.get().getCurrentUser().getUserName());
            ((TextView)headView.findViewById(R.id.nav_account)).setText(OkayApi.get().getCurrentUser().getUserName());
        } else {
            ((TextView)headView.findViewById(R.id.nav_username)).setText("未登录");
            ((TextView)headView.findViewById(R.id.nav_account)).setText("---");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapOptEvent(MapOptEvent event) {
        if (event.eventId == MapOptEvent.MAP_EVENT_LOGIN_SUCCESS) {
            updateLogin();
        }
    }
}
