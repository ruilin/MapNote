package com.muyu.mapnote.map.user;

import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.network.okayapi.OkUser;
import com.muyu.mapnote.app.network.okayapi.OkayApi;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.controller.ActivityController;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.view.Msg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class UserController extends ActivityController {
    private View headView;
    private NavigationView navigationView;
    @Override
    public void onCreate(BaseActivity activity) {
        super.onCreate(activity);
        EventBus.getDefault().register(this);

        navigationView = activity.findViewById(R.id.nav_view);
        headView = navigationView.getHeaderView(0);
        updateLogin();
    }

    private void updateLogin() {
        if (OkayApi.get().isLogined()) {
            OkUser user = OkayApi.get().getCurrentUser();
            ((TextView)headView.findViewById(R.id.nav_username)).setText(user.getNickname());
            ((TextView)headView.findViewById(R.id.nav_account)).setText(user.getUserName());
            if (!StringUtils.isEmpty(user.getHeadimg())) {
                Glide.with(getActivity()).load(user.getHeadimg()).into((ImageView)headView.findViewById(R.id.nav_head_iv));
            } else {
                ((ImageView) headView.findViewById(R.id.nav_head_iv)).setImageResource(
                        user.getSex() == 0 ? R.drawable.head_girl_1 : R.drawable.head_def);
            }
            navigationView.getMenu().findItem(R.id.nav_user).setTitle("退出登录");
        } else {
            ((TextView)headView.findViewById(R.id.nav_username)).setText("未登录");
            ((TextView)headView.findViewById(R.id.nav_account)).setText("---");
            navigationView.getMenu().findItem(R.id.nav_user).setTitle("登录");
            ((ImageView) headView.findViewById(R.id.nav_head_iv)).setImageResource(R.drawable.head_def);
        }
    }

    public void logout() {
        OkayApi.get().logOut();
        EventBus.getDefault().post(new MapOptEvent<>(MapOptEvent.MAP_EVENT_LOGOUT, null, "logout"));
        Msg.show("已退出登录");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapOptEvent(MapOptEvent event) {
        switch (event.eventId){
            case MapOptEvent.MAP_EVENT_LOGIN_SUCCESS:
            case MapOptEvent.MAP_EVENT_LOGOUT:
                updateLogin();
            break;
        }
    }
}
