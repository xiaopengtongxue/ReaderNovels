package com.xiapeng.readernovels.Utils.Http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Administrator on 2018/3/20.
 */
interface GetHttpInterface{
    //查询主站下的path部分
    @GET("{path}")
    fun getData(@Path("path") path:String): Call<ResponseBody>

    //停止URL编码（默认utf-8，但要求的网址以gbk编码）
    @GET("modules/article/search.php")
    fun getNovel(@Query(value="searchkey",encoded = true) searchkey: String): Call<ResponseBody>

    //要求完整的地址
    @GET
    fun getURL(@Url url:String): Call<ResponseBody>

    //form形式请求，停止URL编码（默认utf-8，但要求的网址以gbk编码）
    @POST("s.php")
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=GBK")
    fun getSearch(@FieldMap(encoded = true) map:Map<String, String>): Call<ResponseBody>
}