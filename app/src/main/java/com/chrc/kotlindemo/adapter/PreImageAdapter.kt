package com.chrc.kotlindemo.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chrc.demo.R
import com.chrc.kotlindemo.KtUtil
import com.chrc.kotlindemo.activity.GPreviewActivity
import com.chrc.kotlindemo.modle.PreImageInfo
import com.chrc.kotlindemo.modle.PreImageInfoResult
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.Gson

/**
 *    @author : chrc
 *    date   : 2021/1/21  2:53 PM
 *    desc   :
 */
class PreImageAdapter: RecyclerView.Adapter<PreImageAdapter.PreViewHolder> {

    private val urls: MutableList<String> = ArrayList()
    private val preImageInfos: MutableList<PreImageInfo> = ArrayList()
    private var layoutManager: LinearLayoutManager
    private lateinit var activity: Activity
    private var spanCount: Int = 1

    constructor(activity: Activity, urls: MutableList<String>, layoutManager: LinearLayoutManager, spanCount: Int) {
        if (!urls.isNullOrEmpty()) {
            this.urls.addAll(urls)
        }
        this.layoutManager = layoutManager
        this.activity = activity
        this.spanCount = spanCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreImageAdapter.PreViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_g_preview, parent, false)
        return PreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return urls.size
    }

    override fun onBindViewHolder(holder: PreViewHolder, position: Int) {
        Log.i("anifrag===", "onBindViewHolder position=$position")
        holder.sdv.setImageURI(urls[position])
        holder.sdv.setOnClickListener {
//            if (preImageInfos.isEmpty()) {
            Log.i("anifrag===", "PreImageAdapter visibile last=${layoutManager.findLastVisibleItemPosition()} first=${layoutManager.findFirstVisibleItemPosition()}")
            preImageInfos.clear()
                preImageInfos.addAll(KtUtil.getPreImageRects(urls, layoutManager, 100F, spanCount))
//            }
            var preImageInfoResult = PreImageInfoResult(position, preImageInfos)
            var intent = Intent(activity, GPreviewActivity::class.java). apply {
                putExtra(GPreviewActivity.PREIMAGE_INFOS_KEY, Gson().toJson(preImageInfoResult))
            }
            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0);
        }
    }

    class PreViewHolder : RecyclerView.ViewHolder {
        lateinit var sdv: SimpleDraweeView

        constructor(itemView: View): super(itemView) {
            sdv = itemView.findViewById(R.id.item_gpre_sdv)
        }
    }

}