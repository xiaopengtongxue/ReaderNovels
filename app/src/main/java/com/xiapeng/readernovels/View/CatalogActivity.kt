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
import android.widget.Toast
import com.xiapeng.readernovels.R
import com.xiapeng.readernovels.Utils.Http.NetworkUtils
import com.xiapeng.readernovels.Utils.Http.RetrofitHttp
import kotlinx.android.synthetic.main.activity_catalog.*
import java.util.*

class CatalogActivity : AppCompatActivity(),AdapterView.OnItemClickListener {
    var isDesc=false
    var myCatalog=ArrayList<String>()
    var linkList=ArrayList<String>()
    var handler=object : Handler(){
        override fun handleMessage(msg: Message?) {
            if(msg!!.what==2){
                var bundle=msg.peekData()
                myCatalog=bundle.getStringArrayList("name")
                linkList=bundle.getStringArrayList("link")
                isDesc=true
                var adapter= ArrayAdapter<String>(this@CatalogActivity, android.R.layout.simple_list_item_1,myCatalog)
                catalog_list.adapter=adapter
                progressbar.stopAnimation()
                progressbar.visibility=View.GONE
            }
            if(msg!!.what==3){
                progressbar.stopAnimation()
                progressbar.visibility=View.GONE
                Toast.makeText(this@CatalogActivity,R.string.request_fail,Toast.LENGTH_LONG).show()
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

    override fun onResume() {
        super.onResume()
        if(isDesc){
            Collections.reverse(myCatalog)
            Collections.reverse(linkList)
        }
        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myCatalog)
        catalog_list.adapter=adapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId==R.id.menu_order) {
            if (item!!.title.equals("正序")) {
                item!!.setTitle(R.string.des_order)
                isDesc=true
            } else {
                item!!.setTitle(R.string.right_order)
                isDesc=false
            }
            Collections.reverse(myCatalog)
            Collections.reverse(linkList)
            val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myCatalog)
            catalog_list.adapter=adapter
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var location=position
        if(NetworkUtils().isNetworkAvailiable(this)) {
            var cata = myCatalog[position]
            var link = linkList[position]
            Log.d("catalog", cata)
            if(isDesc){
                Collections.reverse(myCatalog)
                Collections.reverse(linkList)
                location = linkList.size-position-1
            }
            val intent = Intent(this@CatalogActivity, ContentActivity::class.java)
            intent.putStringArrayListExtra("linkList", linkList)
            intent.putStringArrayListExtra("nameList", myCatalog)
            intent.putExtra("chapter", cata)
            intent.putExtra("link", link)
            intent.putExtra("order", location)
            this@CatalogActivity.startActivity(intent)
        }else{
            Toast.makeText(this,R.string.fail_net, Toast.LENGTH_LONG).show()
        }
    }
}
