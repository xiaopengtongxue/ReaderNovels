package com.xiapeng.readernovels.Utils.Http

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.xiapeng.readernovels.View.Adapter.HistoryAdapter
import com.xiapeng.readernovels.Utils.PraseHtml
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by xiapeng on 2018/3/20.
 */
class RetrofitHttp(var url:String){
    var call=Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(GetHttpInterface::class.java)
    var prase=PraseHtml()

    fun querySearch(query:String?,handler: Handler,adapter: HistoryAdapter){
        var query1= URLEncoder.encode(query,"gbk")
        val map= mapOf<String,String>("searchtype" to "articlename","searchkey" to query1,"submit" to "")
        URLEncoder.encode(query1,"gbk")
        call.getSearch(map)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                        var resp = String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        var novel=prase.praseSearch2(resp)
                        if(novel.name.isEmpty()) {
                            handler.sendEmptyMessage(1)
                        }else{
                            adapter.add(novel)
                            handler.sendEmptyMessage(0)
                        }
                        Log.d("response",resp)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                    }
                })
    }

    fun queryCatalog(msg:String,handler: Handler){
        call.getData(msg)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                        var resp= String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        //var resp=response!!.body().toString()
                        Thread(Runnable {
                            var start= Date(System.currentTimeMillis())
                            var list = prase.praseCatalog3(resp, 0)
                            var nameList=ArrayList<String>()
                            var linkList=ArrayList<String>()
                            for(i in list){
                                var name=i.replace(Regex("<.*?>"),"")
                                var link=i.replace(Regex("<a href=\"/"),"")
                                        .replace(Regex("\" class=\"xbk\">.*?</a><span class=\"listspan\"></span>"),"")
                                nameList.add(name)
                                linkList.add(link)
                            }
                            var message=Message()
                            var bundle=Bundle()
                            bundle.putStringArrayList("name",nameList)
                            bundle.putStringArrayList("link",linkList)
                            message.data= bundle
                            message.what=2
                            handler.sendMessage(message)
                            var end= Date(System.currentTimeMillis())
                            var diff=end.time-start.time
                            Log.d("time",diff.toString())
                        }).start()
                        Log.d("response",resp)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                        handler.sendEmptyMessage(3)
                    }
                })
    }

    fun queryNovel(msg:String, adapter: HistoryAdapter, progressbar: CircularProgressView){
        call.getData(msg)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                        var resp:String = String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        var history=prase.praseNovel(resp,msg)
                        adapter.add(history)
                        progressbar.visibility= View.GONE
                        Log.d("response",resp)
                        Log.d("response", msg)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                    }
                })
    }

    fun queryChapter(link:String,chapter: String,mList:MutableList<Map<String,String>>,recyclerview:RecyclerView,isHead: Boolean){
        call.getData(link)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                        var resp:String = String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        val content=prase.praseChapter(resp)
                        val map= HashMap<String,String>()
                        map.set("content",content)
                        map.set("contentTitle",chapter)
                        if(isHead) {
                            mList.add(map)
                        }else{
                            mList.add(0,map)
                        }
                        recyclerview.adapter.notifyDataSetChanged()
                        //Log.d("response",resp)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                    }
                })
    }

    fun queryTest(){
        call.getURL("http://m.qududu.net")
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                        var resp:String = String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        Log.d("response",resp)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                    }
                })
    }
}