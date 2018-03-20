package com.xiapeng.readernovels.View

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.xiapeng.readernovels.Model.Chapter
import com.xiapeng.readernovels.View.Adapter.ContentAdapter
import com.xiapeng.readernovels.R
import com.xiapeng.readernovels.Utils.Http.RetrofitHttp
import kotlinx.android.synthetic.main.activity_content.*
import org.litepal.crud.DataSupport

class ContentActivity : AppCompatActivity() {
    val mList: MutableList<Map<String, String>> = ArrayList()

    var call:RetrofitHttp?=null
    var order:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        val intent=getIntent()
        val chapter=intent.getStringExtra("chapter")
        val link=intent.getStringExtra("link")
        order=intent.getIntExtra("order",0)
        var host=resources.getStringArray(R.array.host)
        call=RetrofitHttp(host[0])

        call!!.queryChapter(link,chapter,mList,recyclerview)
        initView()
    }

    fun initView(){
        refreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refresh: RefreshLayout) {
                refresh.finishRefresh(1000)
                /*val chapterList=DataSupport.select("link","order").where("chapter=?",
                        (order-1).toString()).find(Chapter::class.java)
                if(chapterList.size!==0){
                    getData(chapterList[0].link,chapterList[0].chapter)
                }*/
            }
        })
        refreshLayout.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore(refresh: RefreshLayout) {
                refresh.finishLoadMore(1000)
                val chapterList= DataSupport.select("link","chapter").where("order=?",
                        (order+1).toString()).find(Chapter::class.java)
                if(chapterList.size!==0){
                    call!!.queryChapter(chapterList[0].link,chapterList[0].chapter,mList,recyclerview)
                    order=order+1
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
    }
}
