package milu.kiriu2010.milucal.gui.exrate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_row_exchange_rate.view.*
import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateRecord

class ExchangeRateAdapter(
    context: Context,
    // 為替レート(基準貨幣)
    val exRateRecordA: ExRateRecord,
    // 為替レート(比較貨幣)リスト
    val exRateRecordBLst: MutableList<ExRateRecord> = mutableListOf() )
    : RecyclerView.Adapter<ExchangeRateAdapter.ExchangeRateViewHolder>(){

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ExchangeRateViewHolder {
        val view = inflater.inflate(R.layout.list_row_exchange_rate,parent,false)
        val viewHolder = ExchangeRateViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int = exRateRecordBLst.size

    override fun onBindViewHolder(viewHolder: ExchangeRateViewHolder, pos: Int) {
        // 貨幣レコード(基準貨幣)―シンボル
        viewHolder.dataCurrencyA.text = exRateRecordA.symbol
        // 貨幣レコード(基準貨幣)―レート
        viewHolder.dataRateA.text = exRateRecordA.rate.toString()

        // 為替レコード(比較貨幣)
        val exRateRecordB = exRateRecordBLst[pos]
        // 為替レコード(比較貨幣)―シンボル
        viewHolder.dataCurrencyB.text = exRateRecordB.symbol
        // 為替レコード(比較貨幣)―レート
        viewHolder.dataRateB.text = exRateRecordB.rate.toString()
    }


    class ExchangeRateViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // 貨幣シンボル(基準)
        val dataCurrencyA = view.findViewById<TextView>(R.id.dataCurrencyA)
        // 貨幣名(基準)
        val dataRateA = view.findViewById<TextView>(R.id.dataRateA)
        // 貨幣シンボル(比較)
        val dataCurrencyB = view.findViewById<TextView>(R.id.dataCurrencyB)
        // 貨幣名(比較)
        val dataRateB = view.findViewById<TextView>(R.id.dataRateB)
    }
}
