package com.chrc.kotlindemo.activity_contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.chrc.kotlindemo.activity.AndroidGrammarTestActivity

/**
 *    @author : chrc
 *    date   : 2021/1/12  11:22 AM
 *    desc   :
 */
class GrammarTestaActivityResultContract: ActivityResultContract<String, String>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context,AndroidGrammarTestActivity::class.java).apply {
            putExtra("name",input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        val data = intent?.getStringExtra("result")
        return if (resultCode == Activity.RESULT_OK && data != null) data
        else null
    }
}