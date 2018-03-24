package com.xiapeng.readernovels

import com.nrs.utils.tools.CrashHandler
import org.litepal.LitePal
import org.litepal.LitePalApplication

/**
 * Created by Administrator on 2018/3/22.
 */
class MyApplication :LitePalApplication(){
    override fun onCreate() {
        super.onCreate()
        CrashHandler.getInstance().init(this)
        LitePal.initialize(this)
    }
}