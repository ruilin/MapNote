package com.muyu.mapnote.user.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.muyu.mapnote.R
import com.muyu.mapnote.app.okayapi.OkException
import com.muyu.mapnote.app.okayapi.OkFeedback
import com.muyu.mapnote.app.okayapi.callback.CommonCallback
import com.muyu.minimalism.view.Loading
import com.muyu.minimalism.view.Msg

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        var content = findViewById<EditText>(R.id.feedback_content)
        var contact = findViewById<EditText>(R.id.feedback_contact)
        var button = findViewById<Button>(R.id.feedback_commit)

        button.setOnClickListener{
            if (content.text.isEmpty()) {
                Msg.show("请输入内容")
            } else {
                var loading = Loading(this)
                loading.show("提交中，请耐心等待~")
                OkFeedback(content.text.toString(), if (contact.text.isEmpty()) "" else contact.text.toString())
                        .postInBackground(object : CommonCallback {
                            override fun onSuccess(result: String?) {
                                loading.dismiss()
                                Msg.show("提交成功，谢谢反馈")
                                finish()
                            }

                            override fun onFail(e: OkException?) {
                                loading.dismiss()
                                Msg.show(e!!.message)
                            }
                        })
            }

        }
    }
}
