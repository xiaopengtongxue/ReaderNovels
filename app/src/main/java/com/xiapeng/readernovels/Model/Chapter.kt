package com.xiapeng.readernovels.Model

import org.litepal.crud.DataSupport

/**
 * Created by xiapneg on 2018/3/14.
 * */

class Chapter :DataSupport(){
    var chapter: String=""
    var order: Int=0
    var link:String=""
}