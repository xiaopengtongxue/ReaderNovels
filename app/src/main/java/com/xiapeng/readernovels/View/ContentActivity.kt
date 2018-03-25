package com.xiapeng.readernovels.View

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.xiapeng.readernovels.View.Adapter.ContentAdapter
import com.xiapeng.readernovels.R
import com.xiapeng.readernovels.Utils.Http.NetworkUtils
import com.xiapeng.readernovels.Utils.Http.RetrofitHttp
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.HashMap

class ContentActivity : AppCompatActivity() {
    val mList: MutableList<Map<String, String>> = ArrayList()
    var nameList= java.util.ArrayList<String>()
    var linkList= java.util.ArrayList<String>()
    var call:RetrofitHttp?=null
    var head:Int=0
    var tail:Int=0
    var handler=object : Handler(){
        override fun handleMessage(msg: Message?) {
            if(msg!!.what==5){
                var bundle=msg.data
                var content=bundle.getString("content")
                var chapter=bundle.getString("contentTitle")
                var isHead=bundle.getBoolean("isHead")
                val map= HashMap<String,String>()
                map.set("content",content)
                map.set("contentTitle",chapter)
                if(isHead) {
                    mList.add(map)
                }else{
                    mList.add(0,map)
                }
                recyclerview.adapter.notifyDataSetChanged()
                content_progressbar.visibility=View.GONE
            }
            super.handleMessage(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        val intent=getIntent()
        val chapter=intent.getStringExtra("chapter")
        val link=intent.getStringExtra("link")
        head=intent.getIntExtra("order",0)
        tail=head
        linkList=intent.getStringArrayListExtra("linkList")
        nameList=intent.getStringArrayListExtra("nameList")
        var host=resources.getStringArray(R.array.host)
        call=RetrofitHttp(host[0])

        call!!.queryChapter(link,chapter,mList,recyclerview,true,handler)
        content_progressbar.visibility=View.VISIBLE
        content_progressbar.startAnimation()
        initView()
    }

    fun initView(){
        refreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refresh: RefreshLayout) {
                refresh.finishRefresh(1000)
                if(NetworkUtils().isNetworkAvailiable(this@ContentActivity)) {
                    tail = tail - 1
                    if(tail==-1){
                        //Toast.makeText(this@ContentActivity,R.string.not_pre_chapter,Toast.LENGTH_SHORT).show()
                        tail=0
                        return
                    }
                    if (tail >= 0) {
                        call!!.queryChapter(linkList[tail], nameList[tail], mList, recyclerview, false,handler)
                    }
                }else{
                    Toast.makeText(this@ContentActivity,R.string.fail_net, Toast.LENGTH_LONG).show()
                }
            }
        })
        refreshLayout.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore(refresh: RefreshLayout) {
                refresh.finishLoadMore(1000)
                if(NetworkUtils().isNetworkAvailiable(this@ContentActivity)) {
                   /* head = head + 1
                    if (head <= linkList.size) {
                        call!!.queryChapter(linkList[head], nameList[head], mList, recyclerview, true)
                    }*/
                }else{
                    Toast.makeText(this@ContentActivity,R.string.fail_net, Toast.LENGTH_LONG).show()
                }
            }
        })
        //refreshLayout.setRefreshHeader(BezierRadarHeader(this).setEnableHorizontalDrag(true))
        //refreshLayout.setRefreshFooter(BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale))

        val layoutManager= LinearLayoutManager(this)
        layoutManager.orientation= LinearLayoutManager.VERTICAL
        layoutManager.isAutoMeasureEnabled=true
        recyclerview.layoutManager=layoutManager
        recyclerview.adapter= ContentAdapter(this,mList)
        recyclerview.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var manager=recyclerView!!.layoutManager as LinearLayoutManager
                var currentPos=manager.findFirstVisibleItemPosition()
                Log.d("position",(head-tail-currentPos).toString())
                var gap=head-tail-currentPos
                if (head < linkList.size-1&&gap<1) {
                    head = head + 1
                    call!!.queryChapter(linkList[head], nameList[head], mList, recyclerview, true,handler)
                }
            }
        })
    }
}
