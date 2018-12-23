package milu.kiriu2010.milucal.gui.exrate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateRecord

class ExchangeRateSWFragment : Fragment() {

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // 為替レート(基準通貨)
            exRateRecordA = it.getParcelable("A")
            // 為替レート(比較通貨)
            exRateRecordB = it.getParcelable("B")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exchange_rate_sw, container, false)

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

        return view
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
