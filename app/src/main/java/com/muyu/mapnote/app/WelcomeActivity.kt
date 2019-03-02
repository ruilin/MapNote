package com.muyu.mapnote.app

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.ms.banner.Banner
import com.ms.banner.holder.BannerViewHolder
import com.ms.banner.holder.HolderCreator
import com.muyu.mapnote.R
import com.muyu.minimalism.framework.app.BaseActivity
import android.widget.TextView
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.muyu.mapnote.map.activity.MapActivity
import com.muyu.minimalism.utils.SysUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation




class WelcomeActivity : MapBaseActivity() {
    var list = mutableListOf<String>()
    lateinit var adView: View
    lateinit var banner: Banner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        setStatusBarColor(Color.WHITE)

        adView = findViewById<View>(R.id.welcome_ad)
        banner = findViewById<Banner>(R.id.welcome_banner)

        adView.visibility = View.VISIBLE
        banner.visibility = View.GONE
        SysUtils.runOnUiThreadDelayed( {
            enter()
        }, 2000)

    }

    fun enter() {
        if (MapApplication.getInstance().isFirshLaunch) {
            var hiddenAction = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
            adView.startAnimation(hiddenAction)
            adView.visibility = View.GONE

            banner.visibility = View.VISIBLE
            list.add("你好")
            list.add("欢迎")
            banner.setPages(list) { CustomViewHolder() }
                    .setAutoPlay(true)
                    .setLoop(false)
                    .start()
            var showAction = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            banner.startAnimation(showAction)
        } else {
            enterMap()
        }
    }

    fun enterMap() {
        startActivity(MapActivity::class.java)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    internal inner class CustomViewHolder : BannerViewHolder<String> {

        private var mImageView: ImageView? = null
        private var mTextView: TextView? = null
        private var mButton: Button? = null

        override fun createView(context: Context): View {
            val view = LayoutInflater.from(context).inflate(R.layout.item_welcome_banner, null)
            mImageView = view.findViewById(R.id.welcome_banner_item_img)
            mTextView = view.findViewById(R.id.welcome_banner_item_text)
            mButton = view.findViewById(R.id.welcome_banner_item_btn)
            mButton!!.setOnClickListener{
                enterMap()
            }
            return view
        }

        override fun onBind(context: Context, position: Int, data: String) {
            // 数据绑定
            mTextView!!.text = data
            if (position == list.size - 1) {
                mButton!!.visibility = View.VISIBLE
            } else {
                mButton!!.visibility = View.GONE
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
