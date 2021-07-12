package com.chrc.kotlindemo

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.util.Log
import androidx.core.app.ActivityCompat

/**
 *    @author : chrc
 *    date   : 2021/7/9  3:49 PM
 *    desc   :
 */
class PermissionUtil {

    companion object {

        private const val PERMISSION_NOT_HAVE = 1
        private const val PERMISSION_GRANTED = 2
        private const val PERMISSION_DENIED = 3
        private const val PERMISSION_NOT_REQUEST = 4
        private const val INSTALL_PERMISSION = "com.android.permission.GET_INSTALLED_APPS"

        fun requestPermission(context: Context, packageManager: PackageManager, appPackageName: String?, callback: ((granted: Boolean)->Unit)?) {
            when (hasGranted(context, packageManager, appPackageName)) {

                PERMISSION_NOT_HAVE,
                PERMISSION_GRANTED -> {
                    callback?.invoke(true)
                }
                PERMISSION_DENIED -> {
                    callback?.invoke(false)
                }
                PERMISSION_NOT_REQUEST -> {
                    showPermissionTipDialog()
                }
            }
        }

        /**
         * 弹 权限 说明弹框
         */
        private fun showPermissionTipDialog() {

        }

        /**
         * @return true:
         * 1.系统不支持com.android.permission.GET_INSTALLED_APPS
         * 2.
         */
        private fun hasGranted(context: Context, packageManager: PackageManager, appPackageName: String?): Int {
            var granted = PERMISSION_NOT_HAVE
            if (isRunningBackGround()) {
                granted = PERMISSION_DENIED
                return granted
            }
            try {
                var permissionInfo: PermissionInfo? = packageManager.getPermissionInfo(INSTALL_PERMISSION, 0)
                permissionInfo?.apply {
                    if (packageName == appPackageName && ((protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS)) {
                        //支持已安装应用列表权限动态授权
                        Log.d("permission===", "支持")
                        granted = if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, INSTALL_PERMISSION)) {
                            //允许权限
                            PERMISSION_GRANTED
                        } else {
                            PERMISSION_NOT_REQUEST
                        }
                    } else {
                        //不支持已安装应用列表权限动态授权
                        Log.d("permission===", "不支持")
                    }
                }
            } catch (e: Exception) {
                Log.d("permission===","msg=${e.fillInStackTrace()}")
                e.fillInStackTrace()
            }
            return granted
        }

        /**
         * app是否在后台运行
         */
        private fun isRunningBackGround(): Boolean {
            return false
        }

        /**
         * 检测是否安装微信
         * @param context
         * @return
         */
        fun isInstallApp(context: Context, appPackageName: String): Boolean {
            val packageManager = context.packageManager // 获取packagemanager
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = packageManager.getPackageInfo(appPackageName, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return packageInfo != null

        }
    }
}