package milu.kiriu2010.milucal.gui.exrate

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import milu.kiriu2010.loader.v2.AsyncResultOK
import milu.kiriu2010.milucal.CalApplication
import milu.kiriu2010.milucal.entity.ExRateData

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
        return null
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