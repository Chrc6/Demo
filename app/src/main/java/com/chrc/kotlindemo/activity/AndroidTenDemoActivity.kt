package com.chrc.kotlindemo.activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.chrc.demo.R
import com.chrc.kotlindemo.kt.Person
import com.google.gson.Gson
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class AndroidTenDemoActivity : KotlinBaseActivity(), View.OnClickListener {
    private val PICK_FILE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android_ten_demo)

        initView()
        initTestData()
    }

    private fun initTestData() {
        println("initTestData start")
//        for (i in 1..10) {
//            println("initTestData i=$i")
//            if (i == 8) {
//                return
//            }
//        }
        println("initTestData end")
//        var fromJson = Gson().fromJson("", Person::class.java)
//        println("initTestData fromJson is null = ${fromJson == null}")
    }

    private fun initView() {
        findViewById<View>(R.id.tv_get_pic).setOnClickListener(this)
        findViewById<View>(R.id.tv_pick_file).setOnClickListener(this)
        findViewById<View>(R.id.tv_create_old_file).setOnClickListener(this)
        findViewById<View>(R.id.tv_create_new_file).setOnClickListener(this)
        findViewById<View>(R.id.tv_request_permission).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.tv_get_pic -> {
                getPhonePic()
            }
            R.id.tv_pick_file -> {
                pickFile()
//                createFile()
//                selectDir()
            }
            R.id.tv_create_old_file -> {
                var absolutePath = Environment.getExternalStorageDirectory().absolutePath
                var dir = File(absolutePath, "tingshu/onevents")
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                var file = File(dir, "1.txt")
                if (!file.exists()) {
                    file.createNewFile()
                }
                println("old file is null = ${file == null} absolutePath=$absolutePath")
                var outputStream = FileOutputStream(file)
                outputStream.write("123456".toByteArray())
                outputStream.flush()
                outputStream.close()

            }
            R.id.tv_create_new_file -> {
                var dir = getExternalFilesDir("demo")
                if (!dir!!.exists()) {
                    dir.mkdirs()
                }
                var file = File(dir!!.path, "demo.java")
                if (!file.exists()) {
                    file.createNewFile()
                }
                println("new file is null = ${file == null} path=${dir.path}")
                var outputStream = FileOutputStream(file, true)
                outputStream.write("123456".toByteArray())
                outputStream.flush()
                outputStream.close()
            }

            R.id.tv_request_permission -> {
                var intent = Intent().apply {
//                    action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    action = "android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION"
                    data = Uri.parse("package:com.chrc.demo")
                }
                startActivity(intent)
            }
        }
    }

    private fun getPhonePic() {
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                println("image uri is $uri")

//                val fd = contentResolver.openFileDescriptor(uri, "r")
//                if (fd != null) {
//                    val bitmap = BitmapFactory.decodeFileDescriptor(fd.fileDescriptor)
//                    fd.close()
//                    imageView.setImageBitmap(bitmap)
//                }
            }
            cursor.close()
        }
    }

    // 通过uri获取bitmap
    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        var fileDescriptor: FileDescriptor? = null
        var bitmap: Bitmap? = null
        try {
            parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            if (parcelFileDescriptor?.fileDescriptor != null) {
                fileDescriptor = parcelFileDescriptor.fileDescriptor
                //转换uri为bitmap类型
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                parcelFileDescriptor?.close()
            }
            catch (e: IOException) {
            }
            return bitmap
        }
    }

    fun addBitmapToAlbum(bitmap: Bitmap, displayName: String, mimeType: String, compressFormat: Bitmap.CompressFormat) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName")
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(compressFormat, 100, outputStream)
                outputStream.close()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    fun downloadFile(fileUrl: String, fileName: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(this, "You must use device running Android 10 or higher", Toast.LENGTH_SHORT).show()
            return
        }
        thread {
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val inputStream = connection.inputStream
                val bis = BufferedInputStream(inputStream)
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                //android10以上才有
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    val outputStream = contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        val bos = BufferedOutputStream(outputStream)
                        val buffer = ByteArray(1024)
                        var bytes = bis.read(buffer)
                        while (bytes >= 0) {
                            bos.write(buffer, 0 , bytes)
                            bos.flush()
                            bytes = bis.read(buffer)
                        }
                        bos.close()
                    }
                }
                bis.close()
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Android 11中，ACTION_OPEN_DOCUMENT访问受限：
     * Downloads根目录。
     * 设备制造商认为可靠的各个 SD 卡根目录，无论该卡是模拟卡还是可移除的卡。
     * 内部存储根目录
     */
    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_FILE -> {

                val IMAGE_PROJECTION = arrayOf(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media._ID)
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri = data.data
                    if (uri != null) {
                        val inputStream = contentResolver.openInputStream(uri)
                        // 执行文件读取操作
                        println("file uri is $uri")

                        val cursor: Cursor? = this.contentResolver
                                .query(uri, IMAGE_PROJECTION, null, null, null, null)

                        if (cursor != null && cursor.moveToFirst()) {
                            val displayName: String = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                            val size: String = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))

                            cursor.close()

                            println("file size is $size displayName is $displayName")
                        }
                    }
                }
            }
        }
    }

    /**
     * SAF创建文件
     */
    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // 文件类型
        intent.type = "text/plain"
        // 文件名称
        intent.putExtra(Intent.EXTRA_TITLE, System.currentTimeMillis().toString() + ".txt")
        startActivityForResult(intent, 2)
    }

    /**
     * 将目录下的子文件(夹)的读写权限授予app
     * Intent.ACTION_OPEN_DOCUMENT_TREE在Android 11被禁止了
     */
    private fun selectDir() {
        // 用户可以选择任意文件夹，将它及其子文件夹的读写权限授予APP。
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, 3)
    }

    //上传图片时，一般传的是file而不是uri，所以把其他文件夹下的文件copy至媒体库某个目录下
    fun copyPic() {
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")
//        var id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
//        var uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        var uri = Uri.parse("")
        val imgFile: File? = getExternalFilesDir("image")
        if (imgFile?.exists() == false) {
            imgFile.mkdir()
        }
        try {
            val file = File(imgFile?.absolutePath + File.separator +
                    System.currentTimeMillis().toString() + ".jpg")
            // 使用openInputStream(uri)方法获取字节输入流
            val fileInputStream: InputStream? = contentResolver.openInputStream(uri)
            val fileOutputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var byteRead: Int
            while (-1 != fileInputStream?.read(buffer).also { byteRead = it!! }) {
                fileOutputStream.write(buffer, 0, byteRead)
            }
            fileInputStream?.close()
            fileOutputStream.flush()
            fileOutputStream.close()
            // 文件可用新路径 file.getAbsolutePath()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}