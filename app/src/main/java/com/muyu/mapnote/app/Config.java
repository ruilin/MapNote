package com.muyu.mapnote.app;

import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseApplication;
import com.muyu.minimalism.utils.FileUtils;

import java.io.File;

public class Config {

    public static final String BASE_PATH = FileUtils.getRootPath() + "/MapNote/";
    public static final String PHOTO_PATH = BASE_PATH + "photo/";
    public static final int colorPrimary = BaseApplication.getInstance().getResources().getColor(R.color.colorPrimary);
    public static final int colorPrimaryDark = BaseApplication.getInstance().getResources().getColor(R.color.colorPrimaryDark);

    public static void init() {
        File file = new File(PHOTO_PATH);
        //是否是文件夹，不是就创建文件夹
        if (!file.exists())
            file.mkdirs();
    }

}
