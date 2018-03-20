package com.xiapeng.readernovels.View.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xiapeng.readernovels.R


/**
 * Created by xiapeng on 2018/3/16.
 */
class ContentAdapter(context:Context,list:MutableList<Map<String,String>>):RecyclerView.Adapter<ContentAdapter.MyViewHolder>() {
    val mContext=context
    val mList=list


    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        holder!!.content.text=mList[position].get("content")
        holder!!.contentTitle.text=mList[position].get("contentTitle")
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(mContext).inflate(R.layout.item_content,parent,false)
        return MyViewHolder(view)
    }

    class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var content: TextView
        var contentTitle:TextView
        init {
            content = itemView.findViewById(R.id.content)
            contentTitle=itemView.findViewById(R.id.content_title)
        }
    }
}