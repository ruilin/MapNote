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
import com.muyu.mapnote.app.network.ImageLoader
import com.muyu.mapnote.app.configure.Styles
import com.muyu.mapnote.app.network.okayapi.OkException
import com.muyu.mapnote.app.network.okayapi.OkMessage
import com.muyu.mapnote.app.network.okayapi.OkayApi
import com.muyu.mapnote.app.network.okayapi.been.OkMessageItem
import com.muyu.mapnote.app.network.okayapi.callback.CommonCallback
import com.muyu.mapnote.app.network.okayapi.callback.MessageListCallback
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
    var list = ArrayList<OkMessageItem>()

    interface OnNewMessageListener {
        fun onNewMessage(newCount : Int)
    }

    companion object {
        var newCount = 0
        fun newInstance() = MessageFragment()
        fun updateNewMessageCount(l : OnNewMessageListener): Boolean {
            if (!OkayApi.get().isLogined) {
                return false
            }
            OkMessage.requestMessageCount(object : CommonCallback {
                override fun onSuccess(result: String?) {
                    var originalCount = SPUtils.get(OkayApi.SP_KEY_MESSAGE_COUNT, 0)
                    var count = result!!.toInt()
                    if (count > 0 && count != originalCount) {
                        l.onNewMessage(count - originalCount)
                        newCount = count
                    }
                    if (count == 0) {
                        OkMessage.postSystemMessage("你的故事，值得与世界分享!!!\n<地图笔记>开发团队欢迎你🌹")
                    }
//                    if (list!!.size > 0 && list[0].read_status == 0) {
//                        l.onNewMessage(count - originalCount)
//                        newCount = count
//                    }
                }

//                override fun onSuccess(list: ArrayList<OkMessageItem>?) {
//                    var originalCount = SPUtils.get(OkayApi.SP_KEY_MESSAGE_COUNT, 0)
//                    var count = list!!.size
////                    if (count > 0 && count != originalCount) {
////                        l.onNewMessage(count - originalCount)
////                        newCount = count
////                    }
//                    if (list!!.size > 0 && list[0].read_status == 0) {
//                        l.onNewMessage(count - originalCount)
//                        newCount = count
//                    }
//                }
                override fun onFail(e: OkException?) {
                    Logs.e(e!!.message)
                }
            })
            return true
        }
        fun saveCount() {
            SPUtils.put(OkayApi.SP_KEY_MESSAGE_COUNT, newCount)
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
                ImageLoader.loadMessage(activity, msg.icon, icon)
            }
        }
        adapter.setDataList(list)
        listView.setAdapterWithDivider(adapter)

        mRefreshView!!.setOnRefreshListener {
            update()
        }

        update()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && adapter.itemCount == 0) {
            update()
        }
    }


    fun update() {
        if (!OkayApi.get().isLogined) {
            mRefreshView!!.isRefreshing = false
            return
        }
        list.clear()
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
