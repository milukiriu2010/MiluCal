package milu.kiriu2010.milucal.gui.func

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_row_history.view.*
import milu.kiriu2010.milucal.CalApplication
import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.conf.AppConf
import milu.kiriu2010.milucal.entity.CalData
import milu.kiriu2010.util.MyTool

class CalHistoryAdapter(
    context: Context,
    // 計算データの履歴
    val calDataLst: MutableList<CalData> = mutableListOf() )
    : RecyclerView.Adapter<CalHistoryAdapter.CalHistoryViewHolder>(){

    private val inflater = LayoutInflater.from(context)

    private val appConf = (context as? CalApplication)?.appConf ?: AppConf()

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): CalHistoryViewHolder {
        val view = inflater.inflate(R.layout.list_row_history,parent,false)
        val viewHolder = CalHistoryViewHolder(view)
        return viewHolder
    }

    override fun getItemCount() = calDataLst.size

    override fun onBindViewHolder(viewHolder: CalHistoryViewHolder, pos: Int) {
        // 計算データ
        val calData = calDataLst[pos]

        // 計算式
        viewHolder.dataFormula.text = calData.formula
        // 計算結果
        viewHolder.dataNum.text = MyTool.fromBigDeimal2String(calData.num,appConf.numDecimalPlaces)
    }

    class CalHistoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // 計算式
        val dataFormula = view.findViewById<TextView>(R.id.dataFormula)
        // 計算結果
        val dataNum = view.findViewById<TextView>(R.id.dataNum)
    }
}