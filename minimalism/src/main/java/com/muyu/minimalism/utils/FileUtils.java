package com.muyu.minimalism.utils;

import android.content.Context;
import android.os.Environment;

import com.muyu.minimalism.framework.app.BaseApplication;

import java.io.File;

public class FileUtils {

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDir.toString();
    }

    public static String getRootPath() {
        String path = getSDPath();
        if (!StringUtils.isEmpty(path)) {
            return path;
        } else {
            return BaseApplication.getInstance().getFilesDir().getAbsolutePath();
        }
    }
}
