package milu.kiriu2010.gui.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class ProgressAbstract {

    // レイアウトの大きさを計算済みかどうか
    //   計算済み => true
    //   未計算   => false
    protected var isCalculated = false


    abstract fun start(inflater: LayoutInflater, container: ViewGroup?,
                       savedInstanceState: Bundle?): View?
}