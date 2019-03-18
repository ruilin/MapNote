package com.muyu.mapnote.map.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.MapBaseActivity;
import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkMomentItem;
import com.muyu.mapnote.app.okayapi.OkayApi;
import com.muyu.mapnote.app.okayapi.OkMoment;
import com.muyu.mapnote.app.okayapi.callback.MomentListCallback;
import com.muyu.mapnote.footmark.FootmarkFragment;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.map.map.MapController;
import com.muyu.mapnote.map.map.OnMapEventListener;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.mapnote.map.map.poi.Poi;
import com.muyu.mapnote.map.map.poi.PoiManager;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.mapnote.map.user.UserController;
import com.muyu.mapnote.note.PublishActivity;
import com.muyu.mapnote.user.activity.FeedbackActivity;
import com.muyu.mapnote.user.activity.LoginActivity;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.ShareUtils;
import com.muyu.minimalism.utils.SysUtils;
import com.muyu.minimalism.view.DialogUtils;
import com.muyu.minimalism.view.Msg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MapActivity extends MapBaseActivity
        implements OnMapEventListener, Style.OnStyleLoaded {

    private MapController mMapController;
    private UserController mUserController;
//    private GoogleSearchHelper mSearchPlaceController;
    private BottomNavigationBar bottomNavigationBar;
    private NavigationView leftNavigationView;
    private DrawerLayout mLeftSideView;
    private TextView searchKeyWord;
    private FootmarkFragment footmarkFragment;

    private final int MAIN_MENU_HOME = 0;
    private final int MAIN_MENU_PATH = 1;
    private final int MAIN_MENU_MORE = 2;
    private int lastMemuIndex = MAIN_MENU_HOME;

    private ArrayList<OkMomentItem> mMomentlist = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setStatusBarColor(Color.WHITE);

        EventBus.getDefault().register(this);

        View searchView = findViewById(R.id.view_home_et);
        searchView.setOnClickListener((View v) -> {
            SearchActivity.startSearch(MapActivity.this, searchKeyWord.getText().toString());
//            if (mSearchPlaceController != null)
//                mSearchPlaceController.toSearhMode();
        });
        searchKeyWord = findViewById(R.id.view_home_tv);

        initMenu();
        initFloatButton();
        initLeftSizeMenu();

        initController();
    }

    private void initController() {
        mMapController = new MapController(this);
        mMapController.setMapStyleReloadListener(this);
        addController(mMapController);
        mUserController = new UserController();
        addController(mUserController);
    }

    public void updateMoments() {
//        if (OkayApi.get().isLogined()) {
            OkMoment.getAllMoment(new MomentListCallback() {
                @Override
                public void onSuccess(ArrayList<OkMomentItem> list) {
                    mMomentlist = list;
                    Collections.sort(mMomentlist, new Comparator<OkMomentItem>() {
                        @Override
                        public int compare(OkMomentItem o1, OkMomentItem o2) {
                            return o1.moment_like > o2.moment_like ? 1 : -1;
                        }
                    });
                    showMoments(list);
                }

                @Override
                public void onFail(OkException e) {

                }
            });
//        }
    }

    public void showMoments(ArrayList<OkMomentItem> list) {
        SysUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Msg.show("成功");
                mMapController.showMoments(list);
            }
        });
    }

    /**
     * 主菜单
     */
    private void initMenu() {
        /*1.首先进行fvb*/
        bottomNavigationBar = findViewById(R.id.bottom_nav_bar);
        /*2.进行必要的设置*/
        bottomNavigationBar.setBarBackgroundColor(R.color.mapbox_plugins_white);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);//适应大小
        /*3.添加Tab*/
        bottomNavigationBar.addItem(new BottomNavigationItem(
                        R.mipmap.main_home,
                        R.string.main_menu_home)
                        .setInactiveIconResource(R.mipmap.main_home_disable)
                        .setInActiveColor(R.color.black)
                        .setActiveColorResource(R.color.colorPrimaryDark)
                )
                .addItem(new BottomNavigationItem(
                        R.mipmap.main_path,
                        R.string.main_menu_route)
                        .setInactiveIconResource(R.mipmap.main_path_disable)
                        .setInActiveColor(R.color.black)
                        .setActiveColorResource(R.color.colorPrimaryDark)
                )
                .addItem(new BottomNavigationItem(
                        R.mipmap.main_more,
                        R.string.main_menu_more)
                        .setInactiveIconResource(R.mipmap.main_more_disable)
                        .setInActiveColor(R.color.black)
                        .setActiveColorResource(R.color.colorPrimaryDark)
                )
                .setFirstSelectedPosition(0)//默认显示面板
                .initialise();//初始化

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case MAIN_MENU_HOME:
                        lastMemuIndex = MAIN_MENU_HOME;
                        if (footmarkFragment != null && !footmarkFragment.isHidden()) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .hide(footmarkFragment)
                                    .commit();
                        }
                        break;
                    case MAIN_MENU_PATH:
                        if (!OkayApi.get().isLogined()) {
                            bottomNavigationBar.selectTab(MAIN_MENU_HOME);
                            startActivity(LoginActivity.class);
                            return;
                        }
                        lastMemuIndex = MAIN_MENU_PATH;
                        if (footmarkFragment == null) {
                            footmarkFragment = FootmarkFragment.newInstance();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.map_frag_footmark, footmarkFragment)
                                    .commit();
                        } else {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(footmarkFragment)
                                    .commit();
                        }
                        break;
                    case MAIN_MENU_MORE:
                        openLeftSideView();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
                switch (position) {
                    case MAIN_MENU_MORE:
                        openLeftSideView();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void changeLeftSideView(){
        if (mLeftSideView.isDrawerOpen(Gravity.LEFT)) {
            mLeftSideView.closeDrawer(Gravity.LEFT);
        } else {
            mLeftSideView.openDrawer(Gravity.LEFT);
        }
    }

    private void openLeftSideView(){
        if (!mLeftSideView.isDrawerOpen(Gravity.LEFT)) {
            mLeftSideView.openDrawer(Gravity.LEFT);
        }
    }

    private void initLeftSizeMenu() {
        mLeftSideView = findViewById(R.id.drawer_layout);
        mLeftSideView.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}
            @Override
            public void onDrawerStateChanged(int newState) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                bottomNavigationBar.selectTab(MAIN_MENU_MORE);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                bottomNavigationBar.selectTab(lastMemuIndex);
            }

        });
        leftNavigationView = findViewById(R.id.nav_view);
        leftNavigationView.setItemIconTintList(null);
        leftNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_user) {
                    if (!OkayApi.get().isLogined()) {
                        startActivity(LoginActivity.class);
                    } else {
                        DialogUtils.show(MapActivity.this, "提示", "确定退出登录吗？", new DialogUtils.DialogCallback() {
                            @Override
                            public void onPositiveClick(DialogInterface dialog) {
                                dialog.dismiss();
                                mUserController.logout();
                            }
                        });
                    }
                } else if (id == R.id.nav_gallery) {
                    mMapController.setMapStyle(Style.MAPBOX_STREETS);
                } else if (id == R.id.nav_slideshow) {
                    mMapController.setMapStyle(Style.SATELLITE_STREETS);
                } else if (id == R.id.nav_manage) {
                    mMapController.setMapStyle(Style.TRAFFIC_DAY);
                } else if (id == R.id.nav_share) {
                    ShareUtils.shareToWeChat(MapActivity.this, "下载<" + getResources().getString(R.string.app_name) + ">APP，记录你的旅行足迹！");
                } else if (id == R.id.nav_feedback) {
                    startActivity(FeedbackActivity.class);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    /**
     * 悬浮按钮
     */
    public void initFloatButton() {
        View fab = findViewById(R.id.map_fab_loc);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                mMapController.processLocation();
            }
        });
        fab = findViewById(R.id.map_fab_add);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               toPublishActivity();
            }
        });
    }

    public void toPublishActivity() {
        if (OkayApi.get().isLogined()) {
            if (LocationHelper.INSTANCE.isLocationFresh())
                startActivity(PublishActivity.class);
            else
                Msg.show("无法获取最新定位，请检查系统设置");
        } else {
            startActivity(LoginActivity.class);
        }
    }

    @Override
    public void onMapCreated(MapboxMap map, @SuppressLint("NotChinaMapView") MapView mapView) {
//        mSearchPlaceController = new GoogleSearchHelper(map);
//        addController(mSearchPlaceController);
        updateMoments();
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        if (mMomentlist != null) {
            showMoments(mMomentlist);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapOptEvent(MapOptEvent event) {
        switch (event.eventId) {
            case MapOptEvent.MAP_EVENT_GOTO_LOCATION:
                Poi poi = (Poi) event.object;
                String defText = getResources().getString(R.string.action_search);
                if (!searchKeyWord.getText().equals(defText)) {
                    mMapController.cleanKeywordPois();
                    searchKeyWord.setText(defText);
                }
                if (poi != null) {
                    mMapController.showPoi(poi);
                    searchKeyWord.setText(event.message);
                }
                break;
            case MapOptEvent.MAP_EVENT_DATA_UPDATE:
                updateMoments();
                break;
        }
    }

    long time_interval = 2000;
    long last_back_time = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - last_back_time > time_interval) {
                Msg.show("再按一次退出");
            } else {
                super.onBackPressed();
            }
            last_back_time = currentTime;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
