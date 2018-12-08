package milu.kiriu2010.milucal.gui.exrate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateRecord

class ExchangeRateAdapter(
    val context: Context,
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
        /*
        // 貨幣レコード(基準貨幣)―シンボル
        viewHolder.dataCurrencyA.text = exRateRecordA.symbol
        // 貨幣レコード(基準貨幣)―レート
        viewHolder.dataRateA.text = exRateRecordA.rate.toString()
        */

        // 為替レコード(比較通貨)
        val exRateRecordB = exRateRecordBLst[pos]
        //Log.d(javaClass.simpleName,"symbol[${exRateRecordB.symbol}]desc[${exRateRecordB.desc}]")
        // 為替レコード(比較通貨)―シンボル
        viewHolder.dataCurrencyCompSymbol.text = exRateRecordB.symbol
        // 為替レコード(比較通貨)―レート
        viewHolder.dataCurrencyCompRate.text = exRateRecordB.rate.toString()
        // 為替レコード(比較通貨)―通貨名
        viewHolder.dataCurrencyCompDesc.text = exRateRecordB.desc
        // オプションメニュー
        viewHolder.imageViewMore.setOnClickListener {
            // ポップアップメニュー
            val popupMenu = PopupMenu(context, viewHolder.imageViewMore)
            // XMLからメニューを生成
            popupMenu.inflate(R.menu.menu_exrate)
            // ポップアップメニューをクリック
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuItemToBase -> {
                        Toast.makeText(context,"test",Toast.LENGTH_LONG).show()
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
            // ポップアップメニューを表示
            popupMenu.show()
        }

    }


    class ExchangeRateViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // 比較通貨シンボル
        val dataCurrencyCompSymbol= view.findViewById<TextView>(R.id.dataCurrencyCompSymbol)
        // 比較通貨レート
        val dataCurrencyCompRate = view.findViewById<TextView>(R.id.dataCurrencyCompRate)
        // 比較通貨名
        val dataCurrencyCompDesc = view.findViewById<TextView>(R.id.dataCurrencyCompDesc)
        // オプションメニュー
        val imageViewMore = view.findViewById<ImageView>(R.id.imageViewMore)
    }
}
