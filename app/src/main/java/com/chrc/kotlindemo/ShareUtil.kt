package com.chrc.kotlindemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode

/**
 *    @author : chrc
 *    date   : 2021/4/6  11:04 AM
 *    desc   :
 */
object ShareUtil {

    fun shareText(context: Context, text: String) {
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setPackage("com.facebook.katana");//packageName 为需要分享到的包名
        intent.setPackage("com.twitter.android");//packageName 为需要分享到的包名
//        intent.setPackage("jp.naver.line.android");//packageName 为需要分享到的包名
        context.startActivity(intent)
//        context.startActivity(Intent.createChooser(intent, "分享到"))
    }

    fun shareImage(context: Context, uri: Uri) {
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(intent, "分享到"))
    }

    fun sendMoreImage(context: Context, imageUris: ArrayList<Uri>) {
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        var mulIntent = Intent(Intent.ACTION_SEND_MULTIPLE);
        mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
        mulIntent.type = "image/*"
        context.startActivity(Intent.createChooser(mulIntent, "分享到"))
    }

    fun sendEmail(context: Context, emailAddress: String, title: String, content: String) {
        var intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddress"))
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        context.startActivity(Intent.createChooser(intent, "分享到"))
    }

    fun shareVideo(context: Context, uri: Uri) {
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(intent, "分享到"))
    }

    fun shareAudio(context: Context, uri: Uri) {
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(intent, "分享到"))
    }

    fun shareAudioVideo(context: Context, uri: Uri) {
        var builder = StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build())
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/;audio/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(intent, "分享到"))
    }
}