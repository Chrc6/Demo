package com.chrc.kotlindemo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.chrc.demo.R
import com.chrc.kotlindemo.ShareUtil
import org.w3c.dom.Text

class ShareDemoActivity : KotlinBaseActivity(), View.OnClickListener {

    private lateinit var systemShareTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_demo)
        initView()
    }

    private fun initView() {
        systemShareTv = findViewById(R.id.tv_share_system)

        systemShareTv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_share_system -> {
                ShareUtil.shareText(this, "分享测试111")
            }
        }
    }
}