package milu.kiriu2010.milucal.gui.exrate

import milu.kiriu2010.milucal.entity.ExRateRecord

interface ExchangeRateCallback {
    // 基準通貨を変更する
    fun changeBaseCurrency( nextBaseExRateRecord: ExRateRecord )
}
