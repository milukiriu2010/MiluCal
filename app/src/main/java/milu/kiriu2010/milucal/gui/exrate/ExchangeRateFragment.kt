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

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateJson
import milu.kiriu2010.milucal.entity.ExRateRecord

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
*/
//private const val ID_EXCHANGE_RATE = "idExchangeRate"


class ExchangeRateFragment : Fragment() {
    /*
    private var param1: String? = null
    private var param2: String? = null
    */

    // 為替データ(Json)
    private var exRateJson: ExRateJson? = null

    // 為替データのリサイクラービュー
    private lateinit var recyclerViewExchangeRate: RecyclerView

    // 為替データのリサイクラービューのアダプタ
    private lateinit var adapter: ExchangeRateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            */
            //exRateJson = it.getParcelable<ExRateJson>(ID_EXCHANGE_RATE)
            val date = it.getString("date") ?: ""
            val base = it.getString("base") ?: ""
            val rateMap: MutableMap<String,Float> = mutableMapOf()
            currencyLst.forEach { key ->
                val currency =  key.substringAfterLast("rateMap")
                //Log.d(javaClass.simpleName,"key[$key]cur[$currency]val[${it.getFloat(key)}]")
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

        /*
        Log.d(javaClass.simpleName,"date[${exRateJson?.date}]base[${exRateJson?.base}]")
        exRateJson?.rateMap?.keys?.forEach { key ->
            Log.d(javaClass.simpleName,"currency[$key]rate[${exRateJson?.rateMap?.get(key)}]")
        }
        */

        // 為替レート(基準貨幣)
        val exRateRecordA = ExRateRecord(exRateJson?.base ?: "","",1f)

        // 為替レート(比較貨幣)のリスト
        val exRateRecordBLst = mutableListOf<ExRateRecord>()
        exRateJson?.rateMap?.keys?.sorted()?.forEach { key ->
            // 為替レート(比較貨幣)
            val exRateRecordB = ExRateRecord(key, "", exRateJson?.rateMap?.get(key) ?: 0f )
            exRateRecordBLst.add(exRateRecordB)
        }

        val ctx = context ?: return view

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


    companion object {
        private val currencyLst: MutableList<String> = mutableListOf()

        @JvmStatic
        fun newInstance(exRateJson: ExRateJson) =
            ExchangeRateFragment().apply {
                arguments = Bundle().apply {
                    /*
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    */
                    //putParcelable(ID_EXCHANGE_RATE,exRateJson)

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
