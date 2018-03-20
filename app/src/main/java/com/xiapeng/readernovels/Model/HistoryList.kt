package com.xiapeng.readernovels.Model

import org.litepal.crud.DataSupport
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Administrator on 2018/3/14.
 */
class HistoryList:DataSupport() {
    var id:Long=0
    var name:String=""
    var img:String=""
    var summary:String=""
    val lastestChapter:String=""
    var link = ""
    val date:String= ""
}