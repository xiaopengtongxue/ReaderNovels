package com.xiapeng.readernovels.View.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.xiapeng.readernovels.Model.HistoryList
import com.xiapeng.readernovels.R

/**
 * Created by xiapeng on 2018/3/19.
 */
class HistoryAdapter(var context:Context,var list:MutableList<HistoryList>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View?=convertView
        if(view==null) {
           view=LayoutInflater.from(context).inflate(R.layout.item_history,parent,false)
        }
        var avatar=view!!.findViewById<ImageView>(R.id.avatar)
        var title=view.findViewById<TextView>(R.id.title_textview)
        var summary=view.findViewById<TextView>(R.id.summary_textview)

        title.text=list[position].name
        var summaryText=list[position].summary.replace("\n","")
        if(summaryText.length>53){
            summaryText=summaryText.substring(0,53)+"..."
        }
        summary.text=summaryText
        ImageLoader.getInstance().displayImage(list[position].img,avatar)

        return view
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    fun add(history: HistoryList){
        list.add(0,history)
        notifyDataSetChanged()
    }
}