package com.muyu.minimalism.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.muyu.minimalism.R;

public class BottomMenu {

    public static void show(Activity activity, String[] items, OnItemClickedListener listener) {
        Dialog dialog = new Dialog(activity, R.style.BottomMenuStyle);
        // 填充对话框的布局
        View view = LayoutInflater.from(activity).inflate(R.layout.view_bottom_menu, null);

        ListView listView = view.findViewById(R.id.bottom_menu_list);
        listView.setAdapter(new ArrayAdapter<>(activity, R.layout.view_bottom_menu_item, items));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.OnItemClicked(position);
                }
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.bottom_menu_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        // 设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.y = 0;//设置Dialog距离底部的距离

        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    public interface OnItemClickedListener {
        void OnItemClicked(int position);
    }
}
