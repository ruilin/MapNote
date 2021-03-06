package com.muyu.minimalism.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    /**
     * 改变图片尺寸
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap changeBitmapSize(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int widthCut = width;
        int heightCut = height;

        float radOrigin = width / height;
        float radNew = newWidth / newHeight;
        if (radOrigin < radNew) {
            // 保持宽度 按高度截取
            float rad2 = newHeight / newWidth;
            heightCut = (int)(width * rad2);
        } else {
            float rad2 = newWidth / newHeight;
            widthCut = (int)(height * rad2);
        }
        // 计算压缩的比率
        float scaleWidth = ((float) newWidth) / widthCut;
        float scaleHeight = ((float) newHeight) / heightCut;

        // 获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // 获取新的bitmap
        bitmap = Bitmap.createBitmap(bitmap, (width - widthCut) >> 1, (height - heightCut) >> 1, widthCut, heightCut, matrix,true);
        bitmap.getWidth();
        bitmap.getHeight();
        return bitmap;
    }

    /**
     * @param quality 要压缩到的质量（0-100）
     * @return
     */
    public static String compressImage(String filePath, String targetPath, int quality, boolean isBigImage)  {
        int width = 512;
        int height = 512;
        if (isBigImage) {
            width = 720;
            height = 720;
        }
        Bitmap bm = getSmallBitmap(filePath, width, height);       // 获取一定尺寸的图片
        int degree = readPictureDegree(filePath);   // 获取相片拍摄角度
        if (degree != 0) {                          // 旋转照片角度，防止头像横着显示
            bm = rotateBitmap(bm,degree);
        }
        File outputFile = new File(targetPath);
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
            } else {
                outputFile.delete();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.WEBP, quality, out);
        } catch (Exception e) {}
        return outputFile.getPath();
    }

    public static String compressPNG(String filePath, String targetPath)  {
        Bitmap bm = getSmallBitmap(filePath, 480, 800);       // 获取一定尺寸的图片
        int degree = readPictureDegree(filePath);   // 获取相片拍摄角度
        if (degree != 0) {                          // 旋转照片角度，防止头像横着显示
            bm = rotateBitmap(bm,degree);
        }
        File outputFile = new File(targetPath);
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
            } else {
                outputFile.delete();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {}
        return outputFile.getPath();
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 获取照片角度
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     * @param bitmap
     * @param degress
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap,int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
