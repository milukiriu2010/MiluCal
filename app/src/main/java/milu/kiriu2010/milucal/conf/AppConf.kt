package milu.kiriu2010.milucal.conf

import java.util.*

// アプリ設定
class AppConf {
    // 税率その１
    var tax1: Float = 8.0f
    // 税率その２
    var tax2: Float = 10.0f
    // 対数(x)
    var logx: Float = 2.0f
    // 小数点以下の桁数
    var numDecimalPlaces: Int = 10
    // 計算データの履歴最大数
    var historyCnt: Int = 10
    // スクリーンを常にON(true:ON/false:OFF)
    var screenOn: Boolean = false
    // 音声入力に使う言語(現在：未使用)
    var voiceLang: Locale = Locale.getDefault()

    // デフォルト設定にする
    fun goDefault() {
        val appConfDef = AppConf()
        // 税率その１
        tax1 = appConfDef.tax1
        // 税率その２
        tax2 = appConfDef.tax2
        // 対数(x)
        logx = appConfDef.logx
        // スクリーンを常にON(true:ON/false:OFF)
        screenOn = appConfDef.screenOn
        // 音声入力に使う言語
        voiceLang = appConfDef.voiceLang
    }

}
