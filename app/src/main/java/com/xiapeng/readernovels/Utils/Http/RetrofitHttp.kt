package com.xiapeng.readernovels.Utils.Http

import android.R
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.xiapeng.readernovels.Model.Chapter
import com.xiapeng.readernovels.View.Adapter.HistoryAdapter
import com.xiapeng.readernovels.Utils.PraseHtml
import okhttp3.ResponseBody
import org.litepal.crud.DataSupport
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.URLEncoder
import java.nio.charset.Charset

/**
 * Created by xiapeng on 2018/3/20.
 */
class RetrofitHttp(url:String){
    var call=Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(GetHttpInterface::class.java)
    var prase=PraseHtml()

    fun querySearch(query:String?,handler: Handler){
        var query1= URLEncoder.encode(query,"gbk")
        val map= mapOf<String,String>("type" to "articlename","s" to query1,"submit" to "")
        URLEncoder.encode(query1,"gbk")
        call.getSearch(map)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                        var resp = String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        var link=prase.praseSearch(resp,query!!)
                        var msg=Message()
                        var bundle=Bundle()
                        bundle.putString("link",link)
                        msg.data= bundle
                        msg.what=1
                        handler.sendMessage(msg)
                        Log.d("response",resp)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                    }
                })
    }

    fun queryCatalog(msg:String,handler: Handler){
        DataSupport.deleteAll(Chapter::class.java)
        call.getData(msg)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                        var resp= String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        Thread(Runnable {
                            var list = prase.praseCatalog(resp, 0)
                            var message=Message()
                            var bundle=Bundle()
                            bundle.putStringArrayList("list",list)
                            message.data= bundle
                            message.what=2
                            handler.sendMessage(message)
                        }).start()
                        Log.d("response",resp)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                    }
                })
    }

    fun queryNovel(msg:String,adapter: HistoryAdapter){
        DataSupport.deleteAll(Chapter::class.java)
        call.getData(msg)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                        var resp:String = String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        var history=prase.praseNovel(resp,msg)
                        adapter.add(history)
                        Log.d("response",resp)
                        Log.d("response", msg)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                    }
                })
    }

    fun queryChapter(link:String,chapter: String,mList:MutableList<Map<String,String>>,recyclerview:RecyclerView){
        call.getData(link)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                        var resp:String = String(response!!.body()!!.bytes(), Charset.forName("gbk"))
                        val content=prase.praseChapter(resp)
                        val map= HashMap<String,String>()
                        map.set("content",content)
                        map.set("contentTitle",chapter)
                        mList.add(map)
                        recyclerview.adapter.notifyDataSetChanged()
                        //Log.d("response",resp)
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("response", "get fail")
                        Log.d("response", t.toString())
                    }
                })
    }

}