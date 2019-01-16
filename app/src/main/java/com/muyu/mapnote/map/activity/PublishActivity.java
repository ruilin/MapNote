package com.muyu.mapnote.map.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.util.Msg;
import com.muyu.minimalism.view.imagebox.ZzImageBox;

public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

//        final ZzImageBox imageBox = (ZzImageBox) findViewById(R.id.zz_image_box);
//        //如果需要加载网络图片，添加此监听。
//        imageBox.setOnlineImageLoader(new ZzImageBox.OnlineImageLoader() {
//            @Override
//            public void onLoadImage(ImageView iv, String url) {
//                Log.d("ZzImageBox", "url=" + url);
//                Glide.with(PublishActivity.this).load(url).into(iv);
//            }
//        });
//        imageBoxAddMode.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {
//            @Override
//            public void onImageClick(int position, String filePath, String tag, int type, ImageView iv) {
//                Msg.show("你点击了+" + position + "的图片:url=" + filePath + ", tag=" + tag);
//            }
//
//            @Override
//            public void onDeleteClick(int position, String filePath, String tag, int type) {
//                Msg.show("tag=" + tag + ", type=" + type);
//                //移除position位置的图片
//                imageBoxAddMode.removeImage(position);
//            }
//
//            @Override
//            public void onAddClick() {
//                //添加网络图片
////                imageBoxAddMode.addImageOnline("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
//                imageBoxAddMode.addImageOnlineWithRealPathAndType("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg", "tag" + imageBoxAddMode.getCount(), imageBoxAddMode.getCount());
//            }
//        });
    }
}
