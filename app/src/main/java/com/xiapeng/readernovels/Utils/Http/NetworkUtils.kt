package com.xiapeng.readernovels.Utils.Http

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Administrator on 2018/3/22.
 */
class NetworkUtils{
    fun isNetworkAvailiable(context: Context):Boolean{
        var service= context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(service==null){
            return false
        }else{
            var networkInfo=service.activeNetworkInfo
            if(networkInfo==null){
                return false
            }else {
                var type = networkInfo.type
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    Log.d("network", "Mobile")
                } else if (type == ConnectivityManager.TYPE_WIFI) {
                    Log.d("network", "WiFi")
                }
                if (networkInfo.isConnected) {
                    return true
                } else {
                    return false
                }
            }
        }
    }

    fun testHttp(){
        Thread(Runnable {
            var url=URL("https://m.555zw.com")
            var connetion=url.openConnection() as HttpURLConnection
            var read=BufferedReader(InputStreamReader(connetion.inputStream))
            var strBuffer=StringBuffer()
            while (true) {
                var line = read.readLine()
                if(line==null){
                    break
                }
                strBuffer.append(line)
            }
            Log.d("responseBuffer",strBuffer.toString())
        }).start()
    }
}