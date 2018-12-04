package milu.kiriu2010.net.v2

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.*

// =======================================
// ver 0.0.1 2018.08.28  no cache
// ver 0.0.2 2018.09.03  GET parameter
// ver 0.0.3 2018.09.14  redirect
// ver 0.0.4 2018.10.12  doPost(method)
// ---------------------------------------
// =======================================
abstract class MyURLConAbs: Cloneable {
    // 接続先URL
    lateinit var url: URL
    // 接続オブジェクト
    var conAbs: URLConnection? = null
    // 接続しているかどうか
    var connected = false

    // 送信ヘッダ
    val sendHeaderMap = mutableMapOf<String,String>()
    // 送信データ
    val sendDataMap = mutableMapOf<String,String>()

    // HTTP受信コード
    var responseCode = -1
    // HTTPレスポンス結果
    var responseOK = RESPONSE_OK.NG

    // 受信ヘッダ
    val responseHeaderMap = mutableMapOf<String,MutableList<String>>()

    // 受信バッファ
    val responseBuffer = StringBuffer();

    // URLにGETパラメータを付与する
    enum class HAS_GET_PARAM{
        YES,
        NO
    }

    // レスポンス結果
    enum class RESPONSE_OK {
        OK,
        NG,
        REDIRECT
    }

    // 送信ヘッダ追加
    fun addSendHeader(k: String, v: String ) {
        sendHeaderMap.put(k,v)
    }

    // 送信データ追加
    fun addSendData(k: String, v: String ) {
        sendDataMap.put(k,v)
    }

    // -----------------------------
    // メンバ変数クリア
    // -----------------------------
    // "受信バッファ"
    fun clear() {
        this.responseCode = -1
        this.responseOK = RESPONSE_OK.OK
        this.responseBuffer.delete( 0, this.responseBuffer.length )
    }

    // ------------------------------------
    // コネクションを開く
    // ------------------------------------
    // 接続していなかったら接続する
    // 接続していたらスキップ
    // ------------------------------------
    @Throws(IOException::class)
    public fun openConnection( hasGetParam: HAS_GET_PARAM = HAS_GET_PARAM.NO ): URLConnection? {
        try {
            if ( connected == false ) {
                // GETパラメータがある場合、URLにGETパラメータを付与する
                if ( hasGetParam == HAS_GET_PARAM.YES ) {
                    val strURL = url.toString()
                    val getData = StringBuilder()
                    this.sendDataMap.forEach{ k, v ->
                        if ( getData.length != 0 ) {
                            getData.append("&")
                        }
                        getData.append(URLEncoder.encode(k,"UTF-8"))
                        getData.append("=")
                        getData.append(URLEncoder.encode(v,"UTF-8"))
                    }
                    url = URL( strURL + "?" + getData.toString() )
                    Log.d(javaClass.simpleName, "add get to URL[${url.toString()}]")
                }

                conAbs = url.openConnection()
                setSocketOption()
                //connected = true
            }
        }
        catch ( ioEx: IOException ) {
            conAbs = null
            //connected = false
        }
        return conAbs
    }

    @Throws(ConnectException::class,IOException::class)
    fun doGet() {
        val con = this.conAbs as? HttpURLConnection ?: return

        // クリア
        clear()

        // 送信するHTTPヘッダをセットする
        sendHeaderMap.forEach { k, v->
            con.setRequestProperty(k,v)
        }

        // キャッシュを使用しない
        con.useCaches = false

        // -------------------------------------
        // メソッドとしてGETをセットする
        // URLConnectionではなく
        // HttpURLConnectionである必要がある
        // -------------------------------------
        con.setRequestMethod("GET")

        // -------------------------------------
        // 接続
        // -------------------------------------
        if ( connected == false ) {
            try {
                con.connect()
                connected = true
            }
            catch ( conEx: ConnectException ) {
                connected = false
                throw conEx
            }
            catch ( ioEx: IOException ) {
                connected = false
                throw ioEx
            }
        }

        // -------------------------------------
        // HTTP受信コード取得
        // -------------------------------------
        this.responseCode = con.responseCode

        // -------------------------------------
        // 全ヘッダ取得
        // -------------------------------------
        //responseHeaderMap = con.headerFields
        responseHeaderMap.clear()
        con.headerFields.forEach { k, v ->
            if ( k != null ) {
                responseHeaderMap.put(k, v)
            }
            else {
                Log.d( javaClass.simpleName, "headerFields:k[${k}]")
                v.forEach {
                    Log.d( javaClass.simpleName, "headerFields:k[${k}]v[$it]")
                }
            }
        }
        responseHeaderMap.forEach{ k,v ->
            Log.d( javaClass.simpleName, "responseHeaderMap:k[${k}]")
            v.forEach {
                Log.d( javaClass.simpleName, "responseHeaderMap:k[${k}]v[$it]")
            }
            Log.d( javaClass.simpleName, "=========================")
        }

        // -------------------------------------
        // レスポンスを受信バッファに詰め込む
        // -------------------------------------
        // HTTP 200-299はinputStream
        // 上記以外はerrorStream
        // から取得するらしい
        // -------------------------------------
        val isr = if ( this.responseCode in 200..299 ) {
            this.responseOK = RESPONSE_OK.OK
            InputStreamReader( con.inputStream, "UTF-8" )
        }
        else if ( this.responseCode in 301..302 ) {
            this.responseOK = RESPONSE_OK.REDIRECT
            return
        }
        else {
            this.responseOK = RESPONSE_OK.NG
            InputStreamReader( con.errorStream, "UTF-8" )
        }

        /*
        // Content-Lengthがないと、発生する？
        // java.net.ProtocolException: unexpected end of stream
        val br = BufferedReader(isr)
        br.readLines().forEach {
            this.responseBuffer.append(it)
        }
        */

        // chunkedでも動作する？
        // https://grokonez.com/android/kotlin-http-call-with-asynctask-example-android
        val br = BufferedReader(isr)
        var line: String?
        do {
            line = br.readLine()
            if ( line != null ) {
                this.responseBuffer.append(line)
            }
        } while ( line != null )

        isr.close()
        br.close()
    }

    @Throws(ConnectException::class,IOException::class)
    fun doPost(method: String = "POST") {
        val con = this.conAbs as? HttpURLConnection ?: return

        // クリア
        clear()

        // http://blog.officekoma.co.jp/2016/07/urlconnectionhttpurlconnectionget.html
        // リクエストボディ送信を許可
        con.doOutput = true

        // 送信するHTTPヘッダをセットする
        sendHeaderMap.forEach { k, v->
            con.setRequestProperty(k,v)
        }

        // ---------------------------------------------------------
        // POSTするデータを生成
        // ---------------------------------------------------------
        val postData = StringBuilder()
        val contentType = this.sendHeaderMap.get("Content-type") ?: ""

        // ---------------------------------------------------------
        // Content-typeがjsonの場合、そのままPOSTする
        // Content-typeがjson以外の場合、key=val形式のデータを生成する
        // ---------------------------------------------------------
        if ( contentType.contains("application/json")) {
            this.sendDataMap.forEach{ _, v ->
                postData.append(v)
            }
        }
        else {
            this.sendDataMap.forEach{ k, v ->
                if ( postData.length != 0 ) {
                    postData.append("&")
                }
                postData.append(URLEncoder.encode(k,"UTF-8"))
                postData.append("=")
                postData.append(URLEncoder.encode(v,"UTF-8"))
            }
        }

        // ---------------------------------------------------------
        // Content-Lengthを計算し、設定する
        // ---------------------------------------------------------
        val postDataBytes = postData.toString().toByteArray()
        con.setRequestProperty("Content-Length", postDataBytes.size.toString())

        // キャッシュを使用しない
        con.useCaches = false

        // -------------------------------------
        // メソッドとしてPOSTをセットする
        // URLConnectionではなく
        // HttpURLConnectionである必要がある
        // -------------------------------------
        con.setRequestMethod(method)

        // -------------------------------------
        // 接続
        // -------------------------------------
        if ( connected == false ) {
            try {
                con.connect()
                connected = true
            }
            catch ( conEx: ConnectException ) {
                connected = false
                throw conEx
            }
            catch ( ioEx: IOException ) {
                connected = false
                throw ioEx
            }
        }

        // -------------------------------------
        // POSTデータ送信
        // -------------------------------------
        val wr = DataOutputStream(con.outputStream)
        wr.write(postDataBytes)
        wr.flush()
        wr.close()

        // -------------------------------------
        // HTTP受信コード取得
        // -------------------------------------
        this.responseCode = con.responseCode

        // -------------------------------------
        // 全ヘッダ取得
        // -------------------------------------
        //responseHeaderMap = con.headerFields
        responseHeaderMap.clear()
        con.headerFields.forEach { k, v ->
            if ( k != null ) {
                responseHeaderMap.put(k, v)
            }
            else {
                Log.d( javaClass.simpleName, "headerFields:k[${k}]")
                v.forEach {
                    Log.d( javaClass.simpleName, "headerFields:k[${k}]v[$it]")
                }
            }
        }
        responseHeaderMap.forEach{ k,v ->
            Log.d( javaClass.simpleName, "responseHeaderMap:k[${k}]")
            v.forEach {
                Log.d( javaClass.simpleName, "responseHeaderMap:k[${k}]v[$it]")
            }
            Log.d( javaClass.simpleName, "=========================")
        }

        // -------------------------------------
        // レスポンスを受信バッファに詰め込む
        // -------------------------------------
        // HTTP 200-299はinputStream
        // 上記以外はerrorStream
        // から取得するらしい
        // -------------------------------------
        val isr = if ( this.responseCode in 200..299 ) {
            this.responseOK = RESPONSE_OK.OK
            InputStreamReader( con.inputStream, "UTF-8" )
        }
        else {
            this.responseOK = RESPONSE_OK.NG
            InputStreamReader( con.errorStream, "UTF-8" )
        }

        /*
        val br = BufferedReader(isr)
        br.readLines().forEach {
            this.responseBuffer.append(it)
        }
        */

        // chunkedでも動作する？
        // https://grokonez.com/android/kotlin-http-call-with-asynctask-example-android
        val br = BufferedReader(isr)
        var line: String?
        do {
            line = br.readLine()
            if ( line != null ) {
                this.responseBuffer.append(line)
            }
        } while ( line != null )

        isr.close()
        br.close()
    }

    abstract fun setSocketOption()

    public override fun clone(): Any {
        return super.clone()
    }
}
