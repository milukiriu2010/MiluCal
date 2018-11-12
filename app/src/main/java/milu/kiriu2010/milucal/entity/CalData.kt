package milu.kiriu2010.milucal.entity

import java.math.BigDecimal

// 計算データ
data class CalData(
    // 計算式
    var formula: String = "",
    // 値
    var num: BigDecimal = 0.toBigDecimal()
) {
}
