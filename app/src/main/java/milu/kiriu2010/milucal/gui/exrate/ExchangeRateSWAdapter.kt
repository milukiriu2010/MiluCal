package milu.kiriu2010.milucal.gui.exrate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_row_exchange_rate_sw.view.*
import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateRecordComp

class ExchangeRateSWAdapter(
    val context: Context,
    // 通貨の比較データのリスト
    val exRateRecordCompLst: MutableList<ExRateRecordComp> = mutableListOf() )
    : RecyclerView.Adapter<ExchangeRateSWAdapter.ExchangeRateSWViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ExchangeRateSWViewHolder {
        val view = inflater.inflate(R.layout.list_row_exchange_rate,parent,false)
        val viewHolder = ExchangeRateSWViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int = exRateRecordCompLst.size

    override fun onBindViewHolder(viewHolder: ExchangeRateSWViewHolder, pos: Int) {
        // 通貨の比較データ
        val exRateRecordComp = exRateRecordCompLst[pos]

        // 基準通貨レート
        viewHolder.dataRateA.text = exRateRecordComp.exRateRecordA.rate.toString()
        // 比較通貨レート
        viewHolder.dataRateB.text = exRateRecordComp.exRateRecordB.rate.toString()
        // 基準通貨レート(比較通貨で割る)
        viewHolder.dataRateAx.text = (exRateRecordComp.exRateRecordA.rate/exRateRecordComp.exRateRecordB.rate).toString()
        // 比較通貨レート(比較通貨で割る)
        viewHolder.dataRateBx.text = (exRateRecordComp.exRateRecordB.rate/exRateRecordComp.exRateRecordB.rate).toString()
    }

    class ExchangeRateSWViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // 基準通貨レート
        val dataRateA = view.findViewById<TextView>(R.id.dataRateA)
        // 比較通貨レート
        val dataRateB = view.findViewById<TextView>(R.id.dataRateB)
        // 基準通貨レート(比較通貨で割る)
        val dataRateAx = view.findViewById<TextView>(R.id.dataRateAx)
        // 比較通貨レート(比較通貨で割る)
        val dataRateBx = view.findViewById<TextView>(R.id.dataRateBx)

    }
}
