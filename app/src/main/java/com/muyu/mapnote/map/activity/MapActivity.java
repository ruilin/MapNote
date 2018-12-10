package com.muyu.mapnote.map.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.MapController;
import com.muyu.mapnote.map.map.OnMapEventListener;
import com.muyu.mapnote.map.search.SearchPlaceController;
import com.muyu.minimalism.framework.app.BaseActivity;

public class MapActivity extends BaseActivity
        implements OnMapEventListener, NavigationView.OnNavigationItemSelectedListener {

    private MapController mMapController;
    private SearchPlaceController mSearchPlaceController;
    private BottomNavigationBar bottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initMenu();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initController();
    }

    private void initController() {
        mMapController = new MapController(this);
        addController(mMapController);
    }

    private void initMenu() {
        /*1.首先进行fvb*/
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_nav_bar);
        /*2.进行必要的设置*/
        bottomNavigationBar.setBarBackgroundColor(R.color.mapbox_plugins_white);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);//适应大小
        /*3.添加Tab*/
        bottomNavigationBar.addItem(new BottomNavigationItem(
                R.drawable.ic_arrow_head_casing,R.string.mapbox_attributionsDialogTitle)
                .setInactiveIconResource(R.drawable.ic_arrow_up)
                .setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(
                        R.drawable.ic_arrow_head_casing,R.string.action_search)
                        .setInactiveIconResource(R.drawable.ic_close)
                        .setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(
                        R.drawable.ic_arrow_head_casing,R.string.action_search)
                        .setInactiveIconResource(R.drawable.ic_launcher_foreground)
                        .setActiveColorResource(R.color.mapbox_navigation_route_alternative_congestion_yellow))
                .addItem(new BottomNavigationItem(
                        R.drawable.ic_arrow_head_casing,R.string.action_search)
                        .setInactiveIconResource(R.drawable.ic_launcher_background)
                        .setActiveColorResource(R.color.colorAccent))
                .setFirstSelectedPosition(0)//默认显示面板
                .initialise();//初始化
    }

    @Override
    public void onMapCreated(MapboxMap map, MapView mapView) {
        mSearchPlaceController = new SearchPlaceController(map);
        addController(mSearchPlaceController);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            if (mSearchPlaceController != null)
                mSearchPlaceController.toSearhMode();
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
