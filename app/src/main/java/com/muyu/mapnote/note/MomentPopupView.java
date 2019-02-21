package com.muyu.mapnote.note;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.mapnote.map.map.poi.PoiManager;
import com.muyu.minimalism.framework.app.BaseActivity;

import org.w3c.dom.Text;

public class MomentPopupView extends PopupWindow {
    private BaseActivity mContext;
    private MomentPoi poi;

    public MomentPopupView(BaseActivity context, MomentPoi poi) {
        super(context);
        this.mContext = context;
        this.poi = poi;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.view_dialog_moment, null);

        if (!poi.pictureUrlLiat.isEmpty()) {
            Glide.with(mContext).load(poi.pictureUrlLiat.get(0)).into((ImageView) view.findViewById(R.id.moment_pupup_iv));
        }
        TextView tv = view.findViewById(R.id.moment_pupup_content);
        tv.setText(poi.content);
        tv = view.findViewById(R.id.moment_pupup_username);
        tv.setText(poi.nickname);
        tv = view.findViewById(R.id.moment_pupup_like);
        tv.setText(String.valueOf(poi.like));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.startDetailPage(mContext, poi.id);
                dismiss();
            }
        });
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        DisplayMetrics d = mContext.getResources().getDisplayMetrics();
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);   // false 点击外面，事件不穿透
        this.setOutsideTouchable(true);
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
        showAtLocation(view, Gravity.TOP | Gravity.CENTER, 0, 180);//有偏差
    }
}
