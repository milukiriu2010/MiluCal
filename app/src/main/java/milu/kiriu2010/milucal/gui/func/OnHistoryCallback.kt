package milu.kiriu2010.milucal.gui.func

import milu.kiriu2010.milucal.entity.CalData

interface OnHistoryCallback {
    // 履歴に計算データを格納
    public fun put(calData: CalData)
}
