package com.xiapeng.readernovels.Utils

import android.util.Log
import com.xiapeng.readernovels.Model.Chapter
import com.xiapeng.readernovels.Model.HistoryList
import java.util.regex.Pattern

/**
 * Created by xiapeng on 2018/3/20.
 */
class PraseHtml{
    fun praseNovel(msg:String?,link:String):HistoryList{
        val catalog= HistoryList()
        catalog.link=link
        var regex = "<a ><h2>.*?</h2></a>"
        catalog.name=matcher(regex,msg!!).replace(Regex("<.*?>"),"")
        Log.d("catalog",catalog.name)

        regex = "<div class=\"intro_info\">[^<]*</div>"
        catalog.summary=matcher(regex,msg).replace(Regex("<.*?>"),"")
        Log.d("catalog",catalog.summary)

        regex = "<div class=\"block_img2\"><img[^>]*></div>"
        catalog.img=matcher(regex,msg).replace(Regex("<div class=\"block_img2\"><img src=\""),"")
                .replace(Regex("\".*?/></div>"),"")
        Log.d("catalog",catalog.img)
        catalog.save()
        return catalog
    }

    fun praseCatalog(msg:String?,countStep:Int):ArrayList<String>{
        var list= ArrayList<String>()
        val msgArray=msg!!.split("<li>")
        var j:Int=0
        for(i in msgArray){
            val cp=Chapter()
            var regex = "<a[^>]*>.*?<span></span></a>"
            var cat = Pattern.compile(regex)
                    .matcher(i)
            if(cat.find()) {
                j=j+1
                //Log.d("catalog", cat.group())
                val chap=cat.group().replace(Regex("<.*?>"),"")
                //Log.d("catalog",chap )
                list.add(chap)
                cp.chapter=chap
                Log.d("catalog", cat.group().replace(Regex("<a href=\'/"),"")
                        .replace(Regex("\'.*>.*?<span></span></a>"),""))
                cp.link=cat.group().replace(Regex("<a href=\'/"),"")
                        .replace(Regex("\'.*>.*?<span></span></a>"),"")
                cp.order =j+countStep*20
                cp.save()
            }
        }
        return list
    }

    fun praseChapter(content:String):String{
        val regex = "<div[^>]id=\"nr1\">.*?</div>"
        var con = Pattern.compile(regex)
                .matcher(content)
        if(con.find()) {
            Log.d("catalog", con.group())
            val chap=con.group().replace(Regex("<br/>"),"\n")
                    .replace(Regex("<.*>"),"")
            Log.d("catalog",chap )
            return chap
        }
        return "文章读取出错！"
    }

    fun praseSearch(query:String,name:String):String{
        val regex = "<a[^>]*>"+name+"</a>"
        var con = Pattern.compile(regex)
                .matcher(query)
        if(con.find()) {
            Log.d("catalog", con.group())
            val chap=con.group().replace(Regex("<a href=\"/"),"")
                    .replace(Regex("\".*>.*?</a>"),"")
            Log.d("catalog",chap )
            return chap
        }
        return "无结果"
    }

    fun matcher(regex: String,msg: String):String{
        var con = Pattern.compile(regex)
                .matcher(msg)
        if(con.find()) {
            return con.group()
        }
        return ""
    }
}