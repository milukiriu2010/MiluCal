package milu.kiriu2010.milucal.gui.exrate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateJson
import milu.kiriu2010.milucal.entity.ExRateRecord

class ExchangeRateFragment : Fragment() {
    // 為替データ(Json)
    private var exRateJson: ExRateJson? = null

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
        // 為替レート(基準貨幣)
        val exRateRecordA = ExRateRecord(baseSymbol,getDescFromSymbol(baseSymbol),1f)

        // 為替レート(比較貨幣)のリスト
        val exRateRecordBLst = mutableListOf<ExRateRecord>()
        exRateJson?.rateMap?.keys?.sorted()?.forEach { key ->
            // 為替レート(比較貨幣)
            val exRateRecordB = ExRateRecord(key, getDescFromSymbol(key), exRateJson?.rateMap?.get(key) ?: 0f )
            exRateRecordBLst.add(exRateRecordB)
        }

        val ctx = context ?: return view

        // 基準通貨シンボル
        dataCurrencyBaseSymbol = view.findViewById(R.id.dataCurrencyBaseSymbol)
        dataCurrencyBaseSymbol.text = exRateRecordA.symbol

        // 基準通貨レート
        dataCurrencyBaseRate = view.findViewById(R.id.dataCurrencyBaseRate)
        dataCurrencyBaseRate.setText(exRateRecordA.rate.toString())

        // 基準通貨名
        dataCurrencyBaseDesc = view.findViewById(R.id.dataCurrencyBaseDesc)
        dataCurrencyBaseDesc.text = exRateRecordA.desc

        // 為替データのリサイクラービュー
        recyclerViewExchangeRate = view.findViewById(R.id.recycleViewExchangeRate)

        // 為替データのリサイクラービューのレイアウトマネージャ
        val layoutManager = LinearLayoutManager(ctx,LinearLayoutManager.VERTICAL,false)
        recyclerViewExchangeRate.layoutManager = layoutManager

        // 為替データのリサイクラービューのアダプタ
        adapter = ExchangeRateAdapter(ctx,exRateRecordA,exRateRecordBLst)
        recyclerViewExchangeRate.adapter = adapter

        // 為替データのリサイクラービューの区切り線
        val itemDecoration = DividerItemDecoration(ctx,DividerItemDecoration.VERTICAL)
        recyclerViewExchangeRate.addItemDecoration(itemDecoration)

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
