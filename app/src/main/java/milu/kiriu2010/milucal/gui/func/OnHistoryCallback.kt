package milu.kiriu2010.milucal.gui.func

import milu.kiriu2010.milucal.entity.CalData
import milu.kiriu2010.util.LimitedArrayList

interface OnHistoryCallback {
    // 履歴に計算データを格納
    public fun put(calData: CalData)
    // 計算履歴を通知
    public fun onUpdate(calDataLst: LimitedArrayList<CalData>)
}
