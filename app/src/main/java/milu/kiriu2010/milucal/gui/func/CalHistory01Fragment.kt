package milu.kiriu2010.milucal.gui.func


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
import milu.kiriu2010.milucal.entity.CalData
import milu.kiriu2010.util.LimitedArrayList

class CalHistory01Fragment : Fragment()
    , OnHistoryCallback {

    // 計算データの履歴リサイクラービュー
    private lateinit var recyclerViewHistory: RecyclerView

    // 計算データの履歴リサイクラービューのアダプタ
    private lateinit var adapter: CalHistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cal_history01, container, false)

        val ctx = context ?: return view

        // 計算データの履歴リサイクラービュー
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory)

        // 計算データの履歴リサイクラービューのレイアウトマネージャ
        val layoutManager = LinearLayoutManager(ctx,LinearLayoutManager.VERTICAL,false)
        recyclerViewHistory.layoutManager = layoutManager

        // 計算データの履歴リサイクラービューのアダプタ
        adapter = CalHistoryAdapter(ctx)
        recyclerViewHistory.adapter = adapter

        // 計算データの履歴リサイクラービューの区切り線
        val itemDecoration = DividerItemDecoration(ctx,DividerItemDecoration.VERTICAL)
        recyclerViewHistory.addItemDecoration(itemDecoration)

        return view
    }

    // OnHistoryCallback
    // 履歴に計算データを格納
    override fun put(calData: CalData) {}

    // OnHistoryCallback
    // 計算履歴を通知
    override fun onUpdate(calDataLst: LimitedArrayList<CalData>) {
        Log.d(javaClass.simpleName, "onUpdate:calDataLst[${calDataLst.size}]")
        // アダプタの内容を再描画
        adapter.calDataLst.clear()
        adapter.calDataLst.addAll(calDataLst)
        adapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CalHistory01Fragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
