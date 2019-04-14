package com.muyu.mapnote.message

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.muyu.mapnote.R
import com.muyu.mapnote.app.ImageLoader
import com.muyu.mapnote.app.Styles
import com.muyu.mapnote.app.okayapi.OkException
import com.muyu.mapnote.app.okayapi.OkMessage
import com.muyu.mapnote.app.okayapi.OkayApi
import com.muyu.mapnote.app.okayapi.been.OkCommentItem
import com.muyu.mapnote.app.okayapi.been.OkMessageItem
import com.muyu.mapnote.app.okayapi.callback.CommentCallback
import com.muyu.mapnote.app.okayapi.callback.CommonCallback
import com.muyu.mapnote.app.okayapi.callback.MessageListCallback
import com.muyu.mapnote.footmark.FootmarkFragment
import com.muyu.minimalism.utils.Logs
import com.muyu.minimalism.utils.SPUtils
import com.muyu.minimalism.utils.SysUtils
import com.muyu.minimalism.view.Msg
import com.muyu.minimalism.view.recyclerview.CommonRecyclerAdapter
import com.muyu.minimalism.view.recyclerview.CommonViewHolder
import com.muyu.minimalism.view.recyclerview.VerticalRecyclerView

class MessageFragment : Fragment() {
    lateinit var listView : VerticalRecyclerView
    lateinit var adapter : CommonRecyclerAdapter<OkMessageItem>
    lateinit var emptyView : TextView
    private var mRefreshView: SwipeRefreshLayout? = null

    private lateinit var viewModel: MessageViewModel

    interface OnNewMessageListener {
        fun onNewMessage(newCount : Int)
    }

    companion object {
        var key = "message_count"
        var newCount = 0
        fun newInstance() = MessageFragment()
        fun updateNewMessageCount(l : OnNewMessageListener): Boolean {
            OkMessage.requestMessageCount(object : CommonCallback {
                override fun onSuccess(result: String?) {
                    var originalCount = SPUtils.get(key, 0)
                    var count = Integer.parseInt(result)
                    if (count != originalCount) {
                        l.onNewMessage(count - originalCount)
                        newCount = count
                    } else {
                    }
                }

                override fun onFail(e: OkException?) {
                    Logs.e(e!!.message)
                }
            })
            return false
        }
        fun saveCount() {
            SPUtils.put(key, newCount)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.message_fragment, container, false)
        listView = view.findViewById<VerticalRecyclerView>(R.id.frag_msg_list)
        emptyView = view.findViewById<TextView>(R.id.item_msg_empty)
        mRefreshView = view.findViewById(R.id.item_msg_refresh)
        Styles.refreshView(activity, mRefreshView);
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java)
        viewModel.getMessage().observe(this, object : Observer<ArrayList<OkMessageItem>> {
            override fun onChanged(list: ArrayList<OkMessageItem>?) {
                emptyView.visibility = if (list!!.size > 0) View.GONE else View.VISIBLE
                adapter.setDataList(list)
            }
        })

        adapter = object : CommonRecyclerAdapter<OkMessageItem>(activity,
                viewModel.getMessage().getValue(),
                R.layout.item_message) {

            override fun bindData(holder: CommonViewHolder, msg: OkMessageItem, position: Int) {
                val view = holder.itemView
                var text = view.findViewById<TextView>(R.id.item_msg_content)
                var time = view.findViewById<TextView>(R.id.item_msg_time)
                var icon = view.findViewById<ImageView>(R.id.item_msg_icon)
                text.text = msg.message
                time.text = msg.add_time
                ImageLoader.loadMoment(activity, msg.icon, icon)
            }
        }
        var list = ArrayList<OkMessageItem>()
        adapter.setDataList(list)
        listView.setAdapterWithDivider(adapter)


        mRefreshView!!.setOnRefreshListener {
            update()
        }

        update()
    }


    fun update() {
        if (!OkayApi.get().isLogined) {
            mRefreshView!!.isRefreshing = false
            return
        }
        OkMessage.requestMessages(object : MessageListCallback{
            override fun onSuccess(list: ArrayList<OkMessageItem>?) {
                SysUtils.runOnUiThread {
                    mRefreshView!!.isRefreshing = false
                    viewModel.getMessage().postValue(list)
                }
            }
            override fun onFail(e: OkException?) {
                SysUtils.runOnUiThread{
                    mRefreshView!!.isRefreshing = false
                    Msg.show("网络连接失败")
                }
            }
        })
    }
}
