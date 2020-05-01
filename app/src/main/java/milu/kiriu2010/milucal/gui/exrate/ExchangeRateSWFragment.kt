package milu.kiriu2010.milucal.gui.exrate


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateRecord
import milu.kiriu2010.milucal.entity.ExRateRecordComp
import kotlin.math.log10
import kotlin.math.pow

class ExchangeRateSWFragment : androidx.fragment.app.Fragment() {

    // 為替レート(基準通貨)
    private lateinit var exRateRecordA: ExRateRecord

    // 為替レート(比較通貨)
    private lateinit var exRateRecordB: ExRateRecord

    // 為替レート(基準通貨)―通貨シンボル
    private lateinit var dataSymbolA: TextView

    // 為替レート(基準通貨)―通貨名
    private lateinit var dataDescA: TextView

    // 為替レート(比較通貨)―通貨シンボル
    private lateinit var dataSymbolB: TextView

    // 為替レート(比較通貨)―通貨名
    private lateinit var dataDescB: TextView

    // 基準通貨と比較通貨の強弱リストを表示するリサイクラービュー
    private lateinit var recyclerViewExchangeRateSW: androidx.recyclerview.widget.RecyclerView

    // 基準通貨と比較通貨の強弱リストを表示するリサイクラービューのアダプタ
    private lateinit var adapter: ExchangeRateSWAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // 為替レート(基準通貨)
            exRateRecordA = it.getParcelable("A")!!
            // 為替レート(比較通貨)
            exRateRecordB = it.getParcelable("B")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exchange_rate_sw, container, false)

        val ctx = context ?: return view

        // 為替レート(基準通貨)―通貨シンボル
        dataSymbolA = view.findViewById(R.id.dataSymbolA)
        dataSymbolA.text = exRateRecordA.symbol

        // 為替レート(基準通貨)―通貨名
        dataDescA = view.findViewById(R.id.dataDescA)
        dataDescA.text = exRateRecordA.desc

        // 為替レート(比較通貨)―通貨シンボル
        dataSymbolB = view.findViewById(R.id.dataSymbolB)
        dataSymbolB.text = exRateRecordB.symbol

        // 為替レート(比較通貨)―通貨名
        dataDescB = view.findViewById(R.id.dataDescB)
        dataDescB.text = exRateRecordB.desc

        // 基準通貨と比較通貨の強弱リストを表示するリサイクラービュー
        recyclerViewExchangeRateSW = view.findViewById(R.id.recyclerViewExchangeRateSW)

        // 基準通貨と比較通貨の強弱リストを表示するリサイクラービューのレイアウトマネージャ
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            ctx,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )
        recyclerViewExchangeRateSW.layoutManager = layoutManager

        // 基準通貨と比較通貨の強弱リストを表示するリサイクラービューのアダプタ
        adapter = ExchangeRateSWAdapter(ctx, createExRateRecordComp())
        recyclerViewExchangeRateSW.adapter = adapter

        // 為替データのリサイクラービューの区切り線
        val itemDecoration = androidx.recyclerview.widget.DividerItemDecoration(
            ctx,
            androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
        )
        recyclerViewExchangeRateSW.addItemDecoration(itemDecoration)

        return view
    }

    // 基準通貨と比較通貨の強弱リストを生成
    private fun createExRateRecordComp(): MutableList<ExRateRecordComp> {
        val exRateRecordCompLst: MutableList<ExRateRecordComp> = mutableListOf()

        // 比較通貨の桁数
        val digitB = log10(exRateRecordB.rate).toInt()

        // 比較通貨の為替レートの+1/1000～10/1000をリストに加える
        // 基準通貨(ドル)比較通貨(円)であれば円安
        (1..10).reversed().forEach {
            val exRateRecordBx = exRateRecordB.copy()
            exRateRecordBx.rate = exRateRecordBx.rate + it.toFloat()*10f.pow((digitB-3).toFloat())
            val exRateRecordComp = ExRateRecordComp(exRateRecordA,exRateRecordBx,it)
            exRateRecordCompLst.add(exRateRecordComp)
        }

        // 基準通貨と比較通貨の元データをリストに加える
        val exRateRecordCompOrg = ExRateRecordComp(exRateRecordA,exRateRecordB,0)
        exRateRecordCompLst.add(exRateRecordCompOrg)

        // 比較通貨の為替レートの-1/1000～10/1000をリストに加える
        // 基準通貨(ドル)比較通貨(円)であれば円高
        (1..10).forEach {
            val exRateRecordBx = exRateRecordB.copy()
            exRateRecordBx.rate = exRateRecordBx.rate - it.toFloat()*10f.pow((digitB-3).toFloat())
            val exRateRecordComp = ExRateRecordComp(exRateRecordA,exRateRecordBx,-1*it)
            exRateRecordCompLst.add(exRateRecordComp)
        }

        return exRateRecordCompLst
    }

    companion object {
        @JvmStatic
        fun newInstance(exRateRecordA: ExRateRecord, exRateRecordB: ExRateRecord) =
            ExchangeRateSWFragment().apply {
                arguments = Bundle().apply {
                    // 為替レート(基準通貨)
                    putParcelable("A", exRateRecordA)
                    // 為替レート(比較通貨)
                    putParcelable("B", exRateRecordB)
                }
            }
    }
}
