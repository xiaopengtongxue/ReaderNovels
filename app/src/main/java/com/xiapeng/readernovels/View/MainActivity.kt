package com.xiapeng.readernovels.View

import android.content.DialogInterface
import android.content.Intent
import android.os.*
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import com.xiapeng.readernovels.View.Adapter.HistoryAdapter
import com.xiapeng.readernovels.Model.HistoryList
import com.xiapeng.readernovels.R
import com.xiapeng.readernovels.Utils.Http.NetworkUtils
import com.xiapeng.readernovels.Utils.Http.RetrofitHttp
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.LitePal
import org.litepal.crud.DataSupport
import java.util.*

class MainActivity() : AppCompatActivity() ,AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener,SearchView.OnQueryTextListener{
    var httpQuery :RetrofitHttp?=null
    var adapter:HistoryAdapter?=null
    var historyList:MutableList<HistoryList> = ArrayList()
    var handler=object : Handler(){
        override fun handleMessage(msg: Message?) {
            if(msg!!.what==1){
                Toast.makeText(this@MainActivity,R.string.no_novel,Toast.LENGTH_LONG).show()
            }
            progressbar_main.visibility=View.GONE
            super.handleMessage(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        initView()
        //NetworkUtils().testHttp()
    }

    override fun onResume() {
        adapter!!.notifyDataSetChanged()
        super.onResume()
    }

    fun init(){
        //数据库初始化
        LitePal.getDatabase()

        var host=resources.getStringArray(R.array.host)
        httpQuery=RetrofitHttp(host[0])
    }

    fun initView(){
        history.setOnItemClickListener(this)
        history.setOnItemLongClickListener(this)
        search.setOnQueryTextListener(this)
        historyList= DataSupport.select("name","summary","img").order("id desc").find(HistoryList::class.java)
        adapter= HistoryAdapter(this,historyList)
        history.adapter=adapter
    }

    fun jump(link:String?,name:String){
        if(NetworkUtils().isNetworkAvailiable(this)){
            val intent= Intent(this,CatalogActivity::class.java)
            intent.putExtra("link",link)
            intent.putExtra("name",name)
            startActivity(intent)
        }else{
            Toast.makeText(this,R.string.fail_net,Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val link= DataSupport.select("link","name").where("name = ?",
                historyList[position].name).find(HistoryList::class.java)
        jump(link[0].link,link[0].name)
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        AlertDialog.Builder(this)
                .setTitle(R.string.alert)
                .setMessage(R.string.deleteItem)
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener{
                    dialogInterface: DialogInterface, i: Int ->Unit

                })
                .setPositiveButton(R.string.delete,DialogInterface.OnClickListener{
                    dialogInterface: DialogInterface, i: Int ->Unit
                    DataSupport.delete(HistoryList::class.java,historyList[position].id)
                    adapter!!.deleteItem(position)
                }).create().show()

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        var j=0
        for(i in historyList){
            if(i.name.equals(query)){
                Collections.swap(historyList,0,j)
                adapter!!.notifyDataSetChanged()
                return false
            }
            j=j+1
        }
        httpQuery!!.querySearch(query,handler,history.adapter as HistoryAdapter)
        progressbar_main.visibility=View.VISIBLE
        progressbar_main.startAnimation()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}
