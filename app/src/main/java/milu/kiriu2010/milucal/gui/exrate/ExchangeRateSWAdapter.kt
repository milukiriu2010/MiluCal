package milu.kiriu2010.milucal.gui.exrate

import android.content.Context
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
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
    : androidx.recyclerview.widget.RecyclerView.Adapter<ExchangeRateSWAdapter.ExchangeRateSWViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ExchangeRateSWViewHolder {
        val view = inflater.inflate(R.layout.list_row_exchange_rate_sw,parent,false)
        val viewHolder = ExchangeRateSWViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int = exRateRecordCompLst.size

    override fun onBindViewHolder(viewHolder: ExchangeRateSWViewHolder, pos: Int) {
        // 通貨の比較データ
        val exRateRecordComp = exRateRecordCompLst[pos]

        // バックグラウンドの色
        val colorBG = when {
            (exRateRecordComp.comp >= 10) -> Color.parseColor("#ff0000")
            (exRateRecordComp.comp == 9) -> Color.parseColor("#ff1a1a")
            (exRateRecordComp.comp == 8) -> Color.parseColor("#ff3333")
            (exRateRecordComp.comp == 7) -> Color.parseColor("#ff4d4d")
            (exRateRecordComp.comp == 6) -> Color.parseColor("#ff6666")
            (exRateRecordComp.comp == 5) -> Color.parseColor("#ff8080")
            (exRateRecordComp.comp == 4) -> Color.parseColor("#ff9999")
            (exRateRecordComp.comp == 3) -> Color.parseColor("#ffb3b3")
            (exRateRecordComp.comp == 2) -> Color.parseColor("#ffcccc")
            (exRateRecordComp.comp == 1) -> Color.parseColor("#ffe6e6")
            (exRateRecordComp.comp == -1) -> Color.parseColor("#e6e6ff")
            (exRateRecordComp.comp == -2) -> Color.parseColor("#ccccff")
            (exRateRecordComp.comp == -3) -> Color.parseColor("#b3b3ff")
            (exRateRecordComp.comp == -4) -> Color.parseColor("#9999ff")
            (exRateRecordComp.comp == -5) -> Color.parseColor("#8080ff")
            (exRateRecordComp.comp == -6) -> Color.parseColor("#6666ff")
            (exRateRecordComp.comp == -7) -> Color.parseColor("#4d4dff")
            (exRateRecordComp.comp == -8) -> Color.parseColor("#3333ff")
            (exRateRecordComp.comp == -9) -> Color.parseColor("#1a1aff")
            (exRateRecordComp.comp <= -10) -> Color.parseColor("#0000ff")
            else -> Color.parseColor("#ffffff")
        }

        // レイアウト
        viewHolder.layoutExRateComp.setBackgroundColor(colorBG)
        // 基準通貨レート
        viewHolder.dataRateA.text = exRateRecordComp.exRateRecordA.rate.toString()
        // 比較通貨レート
        viewHolder.dataRateB.text = exRateRecordComp.exRateRecordB.rate.toString()
        // 基準通貨レート(比較通貨で割る)
        viewHolder.dataRateAx.text = (exRateRecordComp.exRateRecordA.rate/exRateRecordComp.exRateRecordB.rate).toString()
        // 比較通貨レート(比較通貨で割る)
        viewHolder.dataRateBx.text = (exRateRecordComp.exRateRecordB.rate/exRateRecordComp.exRateRecordB.rate).toString()
    }

    class ExchangeRateSWViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // レイアウト
        val layoutExRateComp = view.findViewById<ConstraintLayout>(R.id.layoutExRateComp)
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
