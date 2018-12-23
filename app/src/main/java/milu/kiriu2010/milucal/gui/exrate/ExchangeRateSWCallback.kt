package milu.kiriu2010.milucal.gui.exrate

import milu.kiriu2010.milucal.entity.ExRateRecord

interface ExchangeRateSWCallback {
    // 基準通貨と比較通貨の強弱リストを表示する
    fun dispBaseComp( exRateRecordA: ExRateRecord, exRateRecordB: ExRateRecord )
}
