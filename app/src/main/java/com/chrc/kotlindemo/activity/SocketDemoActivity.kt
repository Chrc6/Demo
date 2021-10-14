package com.chrc.kotlindemo.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chrc.demo.R
import com.chrc.demo.util.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Socket

class SocketDemoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var unionTv: TextView
    private lateinit var telHttpTv: TextView
    private lateinit var telHttpsTv: TextView
    private lateinit var tvTestTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket_demo)
        initView()
    }

    private fun initView() {
        unionTv = findViewById(R.id.tv_union_connect)
        telHttpTv = findViewById(R.id.tv_tel_http_connect)
        telHttpsTv = findViewById(R.id.tv_tel_https_connect)
        tvTestTv = findViewById(R.id.tv_test)

        unionTv.setOnClickListener(this)
        telHttpTv.setOnClickListener(this)
        telHttpsTv.setOnClickListener(this)

        tvTestTv.text = "width=${Util.px2dip(this, Util.getDeviceWidthPixels(this).toDouble())} height=${Util.px2dip(this, Util.getDeviceHeightPixels(this).toDouble())}"
    }

    fun unionRequest() {
        GlobalScope.launch {
            Log.d("union===", " thread=${Thread.currentThread()}")
            val request = async { unionSend() }
            var data = request.await()
            unionTv.text = data
        }
    }

    inline fun unionSend(): String {
        var responseData = "has no request"
        try {
//            var proxyIP = "lrts.gzproxy.10155.com";  //代理服务器地址
//            var proxyPort = 8070;  //代理服务器端口
            var proxyIP = "14.146.228.46";  //代理服务器地址
            var proxyPort = 80;  //代理服务器端口
            //创建代理
            var proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress(proxyIP, proxyPort))
            var sk = Socket(proxy)
//            var sk = Socket()
//            sk.connect(InetSocketAddress("153.35.121.2", 8070))
            sk.connect(InetSocketAddress.createUnresolved("14.215.177.38", 443))
//            sk.connect(InetSocketAddress("14.215.177.38", 443))

            var printMsg = "/s?wd=socket%E4%BD%BF%E7%94%A8&rsv_spt=1&rsv_iqid=0xd254fa0c00087cf2&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=1&rsv_dl=tb&oq=socket%25E8%25AF%25B7%25E6%25B1%2582%25E4%25B8%2580%25E4%25B8%25AA%25E5%259C%25B0%25E5%259D%2580&rsv_btype=t&inputT=8102&rsv_t=a9a3lO6yax9V6CRiZFpuV5bjQvm5sS9VhPmKjga%2FFykhu6Br2CArmpctwgb5VLNNDG8y&rsv_pq=f1a0d626000a5b0a&rsv_sug3=52&rsv_sug1=47&rsv_sug7=100&rsv_sug2=0&rsv_sug4=8776"
//            var printMsg = "http://ps.mting.info/yyting/navigation/navigationBarAttach.action?id=90441&token=fP3Ig0-sqkLP1SZKF6GjFg**_rt2JW9TnzLHzDiGw1f6YWQ**&imei=bHJpZGFqYXJvci9XSTJJT255RDRjZTFHbmVBPT0%3D&nwt=1&q=228&mode=0&lrid=bHJpZGFqYXJvci9XSTJJT255RDRjZTFHbmVBPT0%3D&sc=50d8e5a8d1510b04f14f9b2840832955"

            // 通过 Socket 的流对象创建 PrintWriter 用于发送请求数据，创建 BufferedReader 用于接收服务端响应数据
            var printWriter = PrintWriter(sk.getOutputStream())
            var bufferedReader = BufferedReader(InputStreamReader(sk.getInputStream()))
            // 发送数据
            var requestData = " 客户端请求数据 "
            printWriter.println(printMsg)
            printWriter.flush()
            // 接收服务端响应数据``
            responseData = bufferedReader.readLine() ;
            System . out .println(" 接收到服务端的响应数据为： " + responseData)
            // 关闭资源
            printWriter.close()
            bufferedReader.close()
            sk.close()
        } catch (e: Exception) {
            Log.d("union===", " exception=${e.fillInStackTrace()}")
        }
        return responseData
    }

    fun telHttpRequest() {

    }

    fun telHttpsRequest() {

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_union_connect -> {
                unionRequest()
            }
            R.id.tv_tel_http_connect -> {
                telHttpRequest()
            }
            R.id.tv_tel_https_connect -> {
                telHttpsRequest()
            }
        }
    }
}