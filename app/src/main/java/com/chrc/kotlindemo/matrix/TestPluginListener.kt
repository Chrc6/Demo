package com.chrc.kotlindemo.matrix

import android.content.Context
import com.tencent.matrix.plugin.DefaultPluginListener
import com.tencent.matrix.report.Issue
import com.tencent.matrix.util.MatrixLog

/**
 *    @author : chrc
 *    date   : 12/28/21  7:08 PM
 *    desc   :https://github.com/Tencent/matrix#matrix_android_cn
 */
class TestPluginListener(context: Context?) : DefaultPluginListener(context) {

    val TAG = "Matrix.TestPluginListener"

    override fun onReportIssue(issue: Issue?) {
        super.onReportIssue(issue);
        MatrixLog.e(TAG, issue.toString());

        //add your code to process data
    }
}