package milu.kiriu2010.gui.progress

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import milu.kiriu2010.milucal.R

abstract class ProgressAbstract {

    // レイアウトの大きさを計算済みかどうか
    //   計算済み => true
    //   未計算   => false
    protected var isCalculated = false

    // ハンドラ
    val handler = Handler()
    lateinit var runnable: Runnable

    abstract fun start(inflater: LayoutInflater, container: ViewGroup?,
                       savedInstanceState: Bundle?): View?


    // 経過時間を更新する
    fun updateElapsedTime(view: View) {
        // 経過時間を表示
        val tvElapsedTime = view.findViewById<TextView>(R.id.tvProgress)

        // 経過時間
        var elapsedTime = -1

        // 1秒ごとに経過時間を更新
        runnable = object: Runnable {
            override fun run() {
                elapsedTime++
                tvElapsedTime.text = elapsedTime.toString()
                // 1秒後に再び、自分自身(Runnnable)を呼び出す
                handler.postDelayed(this,1000)
            }
        }

        // ハンドラへ送信
        handler.post(runnable)
    }

    fun removeHandler() {
        // ハンドラ削除
        handler.removeCallbacks(runnable)
    }
}