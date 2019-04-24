package com.muyu.mapnote.note;

import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.network.ImageLoader;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.mapnote.map.navigation.location.LocationHelper;

public class MomentPopupView extends PopupWindow {
    private MapPluginController controller;
    private MomentPoi poi;

    public MomentPopupView(MapPluginController controller, MomentPoi poi) {
        super(controller.getActivity());
        this.controller = controller;
        this.poi = poi;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(controller.getActivity());
        View view = inflater.inflate(R.layout.view_dialog_moment, null);

        ImageLoader.loadMoment(controller.getActivity(), poi.pictureUrlLiat.get(0), (ImageView) view.findViewById(R.id.moment_pupup_iv));

        TextView tv = view.findViewById(R.id.footmark_content);
        tv.setText(poi.content);
        tv = view.findViewById(R.id.footmark_username);
        tv.setText(poi.nickname);
        tv = view.findViewById(R.id.footmark_pupup_like);
        tv.setText(String.valueOf(poi.like));

        TextView addr = view.findViewById(R.id.dialog_moment_addr);
        addr.setText(poi.place);

        view.findViewById(R.id.dialog_moment_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = LocationHelper.INSTANCE.getLastLocationCheckChina();
                if (myLocation != null) {
                    LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    Point start = Point.fromLngLat(myLatlng.getLongitude(), myLatlng.getLatitude());
                    Point destination = Point.fromLngLat(poi.lng, poi.lat);
                    controller.getMap().getRoute().route(start, destination);
                }
            }
        });

        ImageLoader.loadHead(controller.getActivity(), poi.headimg, view.findViewById(R.id.dialog_moment_head));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.startDetailPage(controller.getActivity(), poi.data);
                dismiss();
            }
        });
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        DisplayMetrics d = controller.getActivity().getResources().getDisplayMetrics();
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);   // false 点击外面，事件不穿透
        this.setOutsideTouchable(false);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void show(View view) {
        //弹窗位置设置
//        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 10);
        showAtLocation(view, Gravity.TOP | Gravity.CENTER, 0, 10);//有偏差
    }
}
