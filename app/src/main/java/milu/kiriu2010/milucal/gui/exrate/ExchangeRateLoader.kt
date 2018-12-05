package milu.kiriu2010.milucal.gui.exrate

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import milu.kiriu2010.loader.v2.AsyncResultOK
import milu.kiriu2010.milucal.CalApplication
import milu.kiriu2010.milucal.entity.ExRateData
import milu.kiriu2010.net.v2.MyURLConAbs
import milu.kiriu2010.net.v2.MyURLConFactory
import org.json.JSONObject
import java.net.URL

// ----------------------------------
// 為替レートを取得するローダ
// ----------------------------------
class ExchangeRateLoader(
    context: Context,
    val appl: CalApplication)
    : AsyncTaskLoader<AsyncResultOK<ExRateData>>(context) {

    // ------------------------------------------
    // 為替レートの結果をキャッシュ
    // ------------------------------------------
    private var cache: AsyncResultOK<ExRateData>? = null

    override fun loadInBackground(): AsyncResultOK<ExRateData>? {
        // Loader呼び出し元が受け取るデータ
        val asyncResultOK = AsyncResultOK<ExRateData>()

        // アクセス先URL
        val url = URL(appl.appConf.urlExchangeRate)

        // 接続＆データをGETする
        val urlConAbs = MyURLConFactory.createInstance(url,null)
        try {
            urlConAbs?.apply {
                // 接続
                openConnection()

                // 処理終了後、クローズする
                addSendHeader("Connection", "close")

                // ------------------------------------------
                // 接続＆GETする
                // ------------------------------------------
                doGet()

                // ----------------------------------------------
                // 通信が成功してれば、取得した文字列をJson解析する
                // ----------------------------------------------
                if (responseOK == MyURLConAbs.RESPONSE_OK.OK) {
                    // Jsonルート
                    val jsonRoot = JSONObject(this.responseBuffer.toString())
                    val date = jsonRoot.getString("date")
                    val base = jsonRoot.getString("base")
                    val ratesObj = jsonRoot.getJSONObject("rates")
                    val rates = mutableMapOf<String,Float>()

                    rates["BGN"] = ratesObj.getString("BGN").toFloat()
                    rates["CAD"] = ratesObj.getString("CAD").toFloat()
                    rates["BRL"] = ratesObj.getString("BRL").toFloat()
                    rates["HUF"] = ratesObj.getString("HUF").toFloat()
                    rates["DKK"] = ratesObj.getString("DKK").toFloat()
                    rates["JPY"] = ratesObj.getString("JPY").toFloat()
                    rates["ILS"] = ratesObj.getString("ILS").toFloat()
                    rates["TRY"] = ratesObj.getString("TRY").toFloat()
                    rates["RON"] = ratesObj.getString("RON").toFloat()
                    rates["GBP"] = ratesObj.getString("GBP").toFloat()
                    rates["PHP"] = ratesObj.getString("PHP").toFloat()
                    rates["HRK"] = ratesObj.getString("HRK").toFloat()
                    rates["NOK"] = ratesObj.getString("NOK").toFloat()
                    rates["USD"] = ratesObj.getString("USD").toFloat()
                    rates["MXN"] = ratesObj.getString("MXN").toFloat()
                    rates["AUD"] = ratesObj.getString("AUD").toFloat()
                    rates["IDR"] = ratesObj.getString("IDR").toFloat()
                    rates["KRW"] = ratesObj.getString("KRW").toFloat()
                    rates["HKD"] = ratesObj.getString("HKD").toFloat()
                    rates["ZAR"] = ratesObj.getString("ZAR").toFloat()
                    rates["ISK"] = ratesObj.getString("ISK").toFloat()
                    rates["CZK"] = ratesObj.getString("CZK").toFloat()
                    rates["THB"] = ratesObj.getString("THB").toFloat()
                    rates["MYR"] = ratesObj.getString("MYR").toFloat()
                    rates["NZD"] = ratesObj.getString("NZD").toFloat()
                    rates["PLN"] = ratesObj.getString("PLN").toFloat()
                    rates["SEK"] = ratesObj.getString("SEK").toFloat()
                    rates["RUB"] = ratesObj.getString("RUB").toFloat()
                    rates["CNY"] = ratesObj.getString("CNY").toFloat()
                    rates["SGD"] = ratesObj.getString("SGD").toFloat()
                    rates["CHF"] = ratesObj.getString("CHF").toFloat()
                    rates["INR"] = ratesObj.getString("INR").toFloat()

                    val exRateData = ExRateData(date,rates,base)

                    asyncResultOK.dataOK = exRateData
                }
            }
        }
        catch ( ex: Exception ) {
            asyncResultOK.exception = ex
            ex.printStackTrace()
        }

        return asyncResultOK
    }

    // ----------------------------------------------
    // コールバッククラスに返す前に通る処理
    // ----------------------------------------------
    override fun deliverResult(data: AsyncResultOK<ExRateData>?) {
        // 破棄されていたら結果を返さない
        if (isReset || data == null) return

        // 結果をキャッシュする
        cache = data
        super.deliverResult(data)
    }

    // ----------------------------------------------
    // バックグラウンド処理が開始される前に呼ばれる
    // ----------------------------------------------
    override fun onStartLoading() {
        //super.onStartLoading()
        // キャッシュがあるなら、キャッシュを返す
        if (cache != null) {
            deliverResult(cache)
        }

        // コンテンツが変化している場合やキャッシュがない場合には、バックグラウンド処理を行う
        if (takeContentChanged() || cache == null) {
            // 非同期処理を開始
            forceLoad()
        }
    }

    // ----------------------------------------------
    // ローダーが停止する前に呼ばれる処理
    // ----------------------------------------------
    override fun onStopLoading() {
        //super.onStopLoading()
        cancelLoad()
    }

    // ----------------------------------------------
    // ローダーが破棄される前に呼ばれる処理
    // ----------------------------------------------
    override fun onReset() {
        super.onReset()
        onStopLoading()
        cache = null
    }
}