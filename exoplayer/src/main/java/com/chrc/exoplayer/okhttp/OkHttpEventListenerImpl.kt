package com.chrc.exoplayer.okhttp

import android.util.Log
import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

/**
 *    @author : chrc
 *    date   : 2021/8/27  10:40 AM
 *    desc   :
 */
class OkHttpEventListenerImpl: EventListener() {

    private var callStartTime: Long = 0
    private var dnsStartTime: Long = 0
    private var connectStartTime: Long = 0
    private var connectionAcquiredTime: Long = 0
    private var secureConnectStartTime: Long = 0
    private var requestBodyStartTime: Long = 0
    private var requestHeadersStartTime: Long = 0

    override fun callEnd(call: Call) {
        super.callEnd(call)
        Log.i("exoplayer===oe", "callEnd inteval=${System.currentTimeMillis() - callStartTime}")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        super.callFailed(call, ioe)
        Log.i("exoplayer===oe", "callFailed cause=${ioe.cause} inteval=${System.currentTimeMillis() - callStartTime}")
    }

    override fun callStart(call: Call) {
        super.callStart(call)
        callStartTime = System.currentTimeMillis()
        Log.i("exoplayer===oe", "callStart")
    }

    override fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol)
        Log.i("exoplayer===oe", "connectEnd inteval=${System.currentTimeMillis() - connectStartTime}")
    }

    override fun connectFailed(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: IOException) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
        Log.i("exoplayer===oe", "connectFailed inteval=${System.currentTimeMillis() - connectStartTime}")
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        super.connectStart(call, inetSocketAddress, proxy)
        connectStartTime = System.currentTimeMillis()
        Log.i("exoplayer===oe", "connectStart")
    }

    override fun connectionAcquired(call: Call, connection: Connection) {
        super.connectionAcquired(call, connection)
        connectionAcquiredTime = System.currentTimeMillis();
        Log.i("exoplayer===oe", "connectionAcquired receive length="+connection.socket().receiveBufferSize)
    }

    override fun connectionReleased(call: Call, connection: Connection) {
        super.connectionReleased(call, connection)
        Log.i("exoplayer===oe", "connectionReleased inteval=${System.currentTimeMillis() - connectionAcquiredTime}")
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        super.secureConnectEnd(call, handshake)
        Log.i("exoplayer===oe", "secureConnectEnd inteval=${System.currentTimeMillis() - connectionAcquiredTime}")
    }

    override fun secureConnectStart(call: Call) {
        super.secureConnectStart(call)
        secureConnectStartTime = System.currentTimeMillis()
        Log.i("exoplayer===oe", "secureConnectStart")
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: MutableList<InetAddress>) {
        super.dnsEnd(call, domainName, inetAddressList)
        Log.i("exoplayer===oe", "dnsEnd inteval=${System.currentTimeMillis() - dnsStartTime}")
    }

    override fun dnsStart(call: Call, domainName: String) {
        super.dnsStart(call, domainName)
        dnsStartTime = System.currentTimeMillis()
        Log.i("exoplayer===oe", "dnsStart")
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        super.requestBodyEnd(call, byteCount)
        Log.i("exoplayer===oe", "requestBodyEnd inteval=${System.currentTimeMillis() - requestBodyStartTime} byteCount="+byteCount)
    }

    override fun requestBodyStart(call: Call) {
        super.requestBodyStart(call)
        requestBodyStartTime = System.currentTimeMillis()
        Log.i("exoplayer===oe", "requestBodyStart")
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        super.requestHeadersEnd(call, request)
        Log.i("exoplayer===oe", "requestHeadersEnd inteval=${System.currentTimeMillis() - requestHeadersStartTime}")
    }

    override fun requestHeadersStart(call: Call) {
        super.requestHeadersStart(call)
        requestHeadersStartTime = System.currentTimeMillis()
        Log.i("exoplayer===oe", "requestHeadersStart")
    }
}