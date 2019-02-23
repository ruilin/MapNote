package com.muyu.minimalism.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * @author ruilin
 */
public class CanvasUtils {

    /**
     * 把图片画成圆形
     * @param bitmap
     * @return
     */
    public static Bitmap drawCircleBitmap(Bitmap bitmap) {
        int radius = (bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight()) / 2;
        Bitmap newBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        BitmapShader shape = new BitmapShader(bitmap, Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        paint.setShader(shape);
        Canvas canvas = new Canvas(newBmp);
        canvas.drawCircle(radius, radius, radius, paint);
        canvas.drawBitmap(newBmp, 0, 0, paint);
        paint.reset();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        canvas.drawCircle(radius, radius, radius - 1, paint);
        return newBmp;
    }
}
