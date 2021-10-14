package com.chrc.kotlindemo

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chrc.demo.R
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.UpgradeInfo
import com.tencent.bugly.beta.download.DownloadListener
import com.tencent.bugly.beta.download.DownloadTask
import com.tencent.bugly.beta.upgrade.UpgradeListener
import com.tencent.bugly.beta.upgrade.UpgradeStateListener

class BuglyUpdateActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var infoCheckTv: TextView
    private lateinit var infoTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bugly_update)
        initView()
    }

    private fun initView() {
        textView = findViewById(R.id.tv_update)
        infoCheckTv = findViewById(R.id.tv_update_info_check)
        infoTv = findViewById(R.id.tv_update_info)

        textView.setOnClickListener {
            Beta.upgradeListener = UpgradeListener { p0, p1, p2, p3 -> Log.d("upgradeListener==="," p0=$p0 p1=${p1.toString()} p2=$p2 p3=$p3") }
            Beta.upgradeStateListener = object : UpgradeStateListener {
                override fun onUpgradeFailed(p0: Boolean) {
                    Log.d("upgradeListener===","onUpgradeFailed")
                }

                override fun onUpgradeSuccess(p0: Boolean) {
                    Log.d("upgradeListener===","onUpgradeSuccess")
                }

                override fun onUpgradeNoVersion(p0: Boolean) {
                    Log.d("upgradeListener===","onUpgradeNoVersion")
                }

                override fun onUpgrading(p0: Boolean) {
                    Log.d("upgradeListener===","onUpgrading")
                }

                override fun onDownloadCompleted(p0: Boolean) {
                    Log.d("upgradeListener===","onDownloadCompleted")
                }

            }
            Beta.downloadListener = object : DownloadListener {
                override fun onReceive(p0: DownloadTask?) {
                    Log.d("upgradeListener===","onReceive path=${p0?.saveFile?.path} ${p0?.mD5}")
                }

                override fun onCompleted(p0: DownloadTask?) {
                    Log.d("upgradeListener===","onCompleted path=${p0?.saveFile?.path}")
                }

                override fun onFailed(p0: DownloadTask?, p1: Int, p2: String?) {
                    Log.d("upgradeListener===","onFailed p1=$p1 p2=$p2 path=${p0?.saveFile?.path}")
                }

            }
            Beta.checkUpgrade(false, false);
            Log.d("upgradeListener===","dirpath=${Beta.storageDir?.path}")
        }

        infoCheckTv.setOnClickListener {
            loadUpgradeInfo()
        }
    }

    private fun loadUpgradeInfo() {
        if (infoTv == null) return
        /***** 获取升级信息  */
        val upgradeInfo = Beta.getUpgradeInfo()
        if (upgradeInfo == null) {
            infoTv.setText("无升级信息")
            return
        }
        val info = StringBuilder()
        info.append("id: ").append(upgradeInfo.id).append("\n")
        info.append("标题: ").append(upgradeInfo.title).append("\n")
        info.append("升级说明: ").append(upgradeInfo.newFeature).append("\n")
        info.append("versionCode: ").append(upgradeInfo.versionCode).append("\n")
        info.append("versionName: ").append(upgradeInfo.versionName).append("\n")
        info.append("发布时间: ").append(upgradeInfo.publishTime).append("\n")
        info.append("安装包Md5: ").append(upgradeInfo.apkMd5).append("\n")
        info.append("安装包下载地址: ").append(upgradeInfo.apkUrl).append("\n")
        info.append("安装包大小: ").append(upgradeInfo.fileSize).append("\n")
        info.append("弹窗间隔（ms）: ").append(upgradeInfo.popInterval).append("\n")
        info.append("弹窗次数: ").append(upgradeInfo.popTimes).append("\n")
        info.append("发布类型（0:测试 1:正式）: ").append(upgradeInfo.publishType).append("\n")
        info.append("弹窗类型（1:建议 2:强制 3:手工）: ").append(upgradeInfo.upgradeType)
        infoTv.setText(info)
    }
}