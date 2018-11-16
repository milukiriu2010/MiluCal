package milu.kiriu2010.milucal.gui.func

import milu.kiriu2010.milucal.entity.CalData

interface OnHistoryGetCallback {
    // 履歴取得1つ前
    fun getHistoryPrev(): CalData
    // 履歴取得1つ次
    fun getHistoryNext(): CalData
}
