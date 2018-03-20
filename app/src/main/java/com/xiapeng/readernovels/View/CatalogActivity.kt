package com.xiapeng.readernovels.View

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.xiapeng.readernovels.Model.Chapter
import com.xiapeng.readernovels.View.Adapter.HistoryAdapter
import com.xiapeng.readernovels.Model.HistoryList
import com.xiapeng.readernovels.R
import com.xiapeng.readernovels.Utils.Http.RetrofitHttp
import kotlinx.android.synthetic.main.activity_catalog.*
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.crud.DataSupport
import java.util.*

class CatalogActivity : AppCompatActivity(),AdapterView.OnItemClickListener {
    var myCatalog=ArrayList<String>()
    var handler=object : Handler(){
        override fun handleMessage(msg: Message?) {
            if(msg!!.what==2){
                var bundle=msg.peekData()
                myCatalog=bundle.getStringArrayList("list")
                var adapter= ArrayAdapter<String>(this@CatalogActivity, android.R.layout.simple_list_item_1,myCatalog)
                catalog_list.adapter=adapter
                progressbar.stopAnimation()
                progressbar.visibility=View.GONE
            }
            super.handleMessage(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val intent=getIntent()
        val name=intent.getStringExtra("name")
        val link=intent.getStringExtra("link")

        supportActionBar!!.setTitle(name+"目录")
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        catalog_list.setOnItemClickListener(this)
        var host=resources.getStringArray(R.array.host)
        RetrofitHttp(host[0]).queryCatalog(link,handler)
        progressbar.visibility=View.VISIBLE
        progressbar.startAnimation()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId==R.id.menu_order) {
            if (item!!.title.equals("正序")) {
                item!!.setTitle(R.string.des_order)
            } else {
                item!!.setTitle(R.string.right_order)
            }
            Collections.reverse(myCatalog)
            val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myCatalog)
            catalog_list.adapter=adapter
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var cata=myCatalog[position]
        Log.d("catalog", cata)
        val chapterList=DataSupport.select("link","order").where("chapter=?",
                cata).find(Chapter::class.java)
        val intent= Intent(this@CatalogActivity, ContentActivity::class.java)
        intent.putExtra("chapter",cata)
        intent.putExtra("link",chapterList[0].link)
        intent.putExtra("order",chapterList[0].order)
        this@CatalogActivity.startActivity(intent)
    }
}
