package com.chrc.kotlindemo.activity

import android.content.Intent
import android.content.pm.PermissionInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chrc.demo.R
import com.chrc.kotlindemo.PermissionUtil
import com.chrc.kotlindemo.kt.Studen

class PermissionTestDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_test_demo)

        initData()
        test()
        checkInstallApp()
    }

    private fun checkInstallApp() {
        var packageName = "com.tencent.mm"
        Log.d("installapp===", "packageName = $packageName has install = ${PermissionUtil.isInstallApp(this, packageName)}")

        try {
            var installedPackages = packageManager.getInstalledPackages(0)

            var parse = Uri.parse("file://${Environment.getExternalStorageDirectory().absolutePath}/tingshu/heiftojpeg/")
            var intent: Intent? = null
            intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //如果是4.4及以上版本
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            } else {
                Intent(Intent.ACTION_MEDIA_MOUNTED)
            }
            intent.data = parse
            sendBroadcast(intent)
        } catch (e: Exception) {
            Log.d("installapp===", "error="+e.fillInStackTrace())
            e.printStackTrace()
        }
    }

    private fun test() {
        var person: Studen? = null
        person?.msg?.let {
            Log.d("null===", "test not null")
        } ?: emptyLog()
    }

    private fun emptyLog() {
        Log.d("null===", "test null")
    }

    /**
     *
     * mBase: PackageManagerWrapper ? ApplicationPackageManager
     */
    private fun initData() {
//        packageManager.getInstalledApplications()
//        packageManager.getInstalledApplications()
//        packageManager.getInstalledPackages()
//        (ApplicationPackageManager)packageManager.getInstalledPackagesAsUser()

        PermissionInfo.PROTECTION_MASK_BASE
        try {
            var permissionInfo: PermissionInfo? = packageManager.getPermissionInfo("com.android.permission.GET_INSTALLED_APPS", 0)
            permissionInfo?.apply {
                if (packageName == "android" && ((protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS)) {
                    //支持已安装应用列表权限动态授权
                    Log.d("permission===", "支持")
                } else {
                    //不支持已安装应用列表权限动态授权
                    Log.d("permission===", "不支持")
                }
            }
        } catch (e: Exception) {
            Log.d("permission===","msg=${e.fillInStackTrace()}")
            e.fillInStackTrace()
        }
    }
}