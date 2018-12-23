package milu.kiriu2010.milucal.gui.exrate


import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateJson
import milu.kiriu2010.milucal.entity.ExRateRecord

class ExchangeRateFragment : Fragment()
    , ExchangeRateCallback {

    // 為替データ(Json)
    private var exRateJson: ExRateJson? = null

    // 為替レート(基準通貨)
    private lateinit var exRateRecordA: ExRateRecord

    // 為替レート(比較通貨)のリスト
    val exRateRecordBLst = mutableListOf<ExRateRecord>()

    // このフラグメントのレイアウト
    private lateinit var layoutExchangeRate: ConstraintLayout

    // 為替データのリサイクラービュー
    private lateinit var recyclerViewExchangeRate: RecyclerView

    // 為替データのリサイクラービューのアダプタ
    private lateinit var adapter: ExchangeRateAdapter

    // 基準通貨シンボル
    private lateinit var dataCurrencyBaseSymbol: TextView

    // 基準通貨レート
    private lateinit var dataCurrencyBaseRate: EditText

    // 基準通貨名
    private lateinit var dataCurrencyBaseDesc: TextView

    // 為替データの取得日
    private lateinit var dataDate: TextView

    // 為替データ更新ボタン
    private lateinit var btnUpdate: Button

    // ExchangeRateSWCallback
    // 基準通貨と比較通貨の強弱リストを表示するコールバック
    private var exchangeRateSWCallback: ExchangeRateSWCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // 為替レートが計算された日付
            val date = it.getString("date") ?: ""
            // 為替レートの基準通貨
            val base = it.getString("base") ?: ""
            // 為替レートの比較通貨
            val rateMap: MutableMap<String,Float> = mutableMapOf()
            currencyLst.forEach { key ->
                val currency =  key.substringAfterLast("rateMap")
                rateMap[currency] = it.getFloat(key)
            }
            // 為替データ(Json)
            exRateJson = ExRateJson(date,rateMap,base)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exchange_rate, container, false)

        // 基準通貨のシンボル
        val baseSymbol = exRateJson?.base ?: ""
        // 為替レート(基準通貨)
        exRateRecordA = ExRateRecord(baseSymbol,getDescFromSymbol(baseSymbol),1f)

        // 為替レート(比較通貨)のリスト
        exRateRecordBLst.clear()
        exRateJson?.rateMap?.keys?.sorted()?.forEach { key ->
            // 為替レート(比較貨幣)
            val exRateRecordB = ExRateRecord(key, getDescFromSymbol(key), exRateJson?.rateMap?.get(key) ?: 0f )
            exRateRecordBLst.add(exRateRecordB)
        }

        val ctx = context ?: return view

        // このフラグメントのレイアウト
        layoutExchangeRate = view.findViewById(R.id.layoutExchangeRate)

        // 基準通貨シンボル
        dataCurrencyBaseSymbol = view.findViewById(R.id.dataCurrencyBaseSymbol)
        dataCurrencyBaseSymbol.text = exRateRecordA.symbol

        // 基準通貨レート
        dataCurrencyBaseRate = view.findViewById(R.id.dataCurrencyBaseRate)
        dataCurrencyBaseRate.setText(exRateRecordA.rate.toString())

        // 基準通貨名
        dataCurrencyBaseDesc = view.findViewById(R.id.dataCurrencyBaseDesc)
        dataCurrencyBaseDesc.text = exRateRecordA.desc

        // 為替データの取得日
        dataDate = view.findViewById(R.id.dataDate)
        dataDate.text = exRateJson?.date ?: ""

        // 為替データのリサイクラービュー
        recyclerViewExchangeRate = view.findViewById(R.id.recycleViewExchangeRate)

        // 為替データのリサイクラービューのレイアウトマネージャ
        val layoutManager = LinearLayoutManager(ctx,LinearLayoutManager.VERTICAL,false)
        recyclerViewExchangeRate.layoutManager = layoutManager

        // 為替データのリサイクラービューのアダプタ
        adapter = ExchangeRateAdapter(ctx,this,exRateRecordBLst) { exRateRecordB ->
            // 比較通貨をクリックすると
            // 基準通貨と比較通貨の強弱リストを表示する
            exchangeRateSWCallback?.dispBaseComp(exRateRecordA,exRateRecordB)
        }
        recyclerViewExchangeRate.adapter = adapter

        // 為替データのリサイクラービューの区切り線
        val itemDecoration = DividerItemDecoration(ctx,DividerItemDecoration.VERTICAL)
        recyclerViewExchangeRate.addItemDecoration(itemDecoration)

        // 為替データ更新ボタン
        btnUpdate = view.findViewById(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            // 基準通貨のレートを更新
            changeBaseCurrencyRate()
        }

        return view
    }

    // 通貨シンボルから通貨名を取得する
    private fun getDescFromSymbol(symbol: String): String {
        // 通貨名のリソースIDを生成
        val resourceId = resources.getIdentifier("currency_${symbol}", "string", activity?.packageName)
        // リソースから通貨名を取得
        val desc = resources.getString(resourceId) ?: "Unregistered Currency"
        //Log.d(javaClass.simpleName, "desc[$desc]")
        return desc
    }

    // 基準通貨のレートを更新
    private fun changeBaseCurrencyRate() {
        // 基準通貨のレートをエディットテキストから取得
        val baseRate = dataCurrencyBaseRate.text.toString().toFloatOrNull()
        // 入力値が数値フォーマットでない場合エラーを表示
        if ( baseRate == null ) {
            dataCurrencyBaseRate.error = resources.getString(R.string.errmsg_fmt_number)
            return
        }
        // 入力値が0未満の場合エラーを表示
        if ( baseRate <= 0f ) {
            dataCurrencyBaseRate.error = resources.getString(R.string.errmsg_fmt_number)
            return
        }

        // キーボードを閉じる
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(layoutExchangeRate.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        // 基準通貨のレート"現在値"と"入力値"の倍率
        val mag = baseRate/exRateRecordA.rate

        // 基準通貨のレートを入力値に変更する
        exRateRecordA.rate = baseRate

        // 比較通貨のレートを変更する
        exRateRecordBLst.forEach { exRateRecordB ->
            exRateRecordB.rate = exRateRecordB.rate * mag
        }

        // リサイクラービューの表示を更新
        adapter.notifyDataSetChanged()
    }

    // ExchangeRateCallback
    // 基準通貨を変更する
    override fun changeBaseCurrency(nextBaseExRateRecord: ExRateRecord) {
        // 基準通貨(変更前)をコピー
        val prevBaseExRateRecord = exRateRecordA.copy()
        // 基準通貨(変更前)のレートを"基準通貨(変更後)のレート=1.0"で補正する
        val backRate = prevBaseExRateRecord.rate/nextBaseExRateRecord.rate
        val prevRate = prevBaseExRateRecord.rate
        prevBaseExRateRecord.rate = backRate

        // 為替レート(比較通貨)の一覧から、"基準通貨(変更後)"を削除
        exRateRecordBLst.remove(nextBaseExRateRecord)

        // 為替レート(比較通貨)のレートを"基準通貨(変更後)のレート=1.0"で補正する
        exRateRecordBLst.forEach { exRateRecordB ->
            exRateRecordB.rate = exRateRecordB.rate * backRate / prevRate
        }

        // 為替レート(比較通貨)の一覧に"基準通貨(変更前)"を先頭に追加
        exRateRecordBLst.add(0,prevBaseExRateRecord)

        // 基準通貨(変更後)のレート=1.0に更新
        nextBaseExRateRecord.rate = 1f

        // 基準通貨を入れ替える
        exRateRecordA = nextBaseExRateRecord

        // 為替レート(比較通貨)のレートをシンボルでソート
        //exRateRecordBLst.sortBy { it.symbol }

        // -----------------------------------------------------------
        // 以下、表示を変更
        // -----------------------------------------------------------

        // 基準通貨シンボル
        dataCurrencyBaseSymbol.text = exRateRecordA.symbol

        // 基準通貨レート
        dataCurrencyBaseRate.setText(exRateRecordA.rate.toString())

        // 基準通貨名
        dataCurrencyBaseDesc.text = exRateRecordA.desc

        // リサイクラービューの表示を更新
        adapter.notifyDataSetChanged()

        // リサイクラービューの先頭にスクロール
        recyclerViewExchangeRate.post {
            recyclerViewExchangeRate.smoothScrollToPosition(0)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ExchangeRateSWCallback)
        {
            exchangeRateSWCallback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        exchangeRateSWCallback = null
    }

    companion object {
        private val currencyLst: MutableList<String> = mutableListOf()

        @JvmStatic
        fun newInstance(exRateJson: ExRateJson) =
            ExchangeRateFragment().apply {
                arguments = Bundle().apply {
                    putString("date",exRateJson.date)
                    putString("base",exRateJson.base)
                    exRateJson.rateMap.keys.forEach { key ->
                        // Bundleのキー名が、かぶらないよう"rateMap"をくっつけている
                        putFloat("rateMap${key}", exRateJson.rateMap.get(key)!! )
                        currencyLst.add("rateMap${key}")
                    }
                }
            }
    }
}
