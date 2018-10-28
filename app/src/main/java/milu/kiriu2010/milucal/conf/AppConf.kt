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
    // 音声入力に使う言語
    var voiceLang: Locale = Locale.getDefault()
}
