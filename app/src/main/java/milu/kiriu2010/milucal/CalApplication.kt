package milu.kiriu2010.milucal

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import milu.kiriu2010.milucal.conf.AppConf

class CalApplication: Application() {
    // アプリ設定
    val appConf = AppConf()

    private enum class SpKey(val id: String) {
        NAME_APP_CONF("appConf"),
        KEY_TAX1("tax1"),
        KEY_TAX2("tax2"),
        KEY_LOGX("logx"),
        KEY_NUM_DECIMAL_PLACES("numDecimalPlaces"),
        KEY_HISTORY_CNT("historyCnt"),
        KEY_SCREEN_ON("screenOn"),
        // --------------------------------------
        // 為替レート用設定
        // --------------------------------------
        // 最後にデータを取得した時刻(UTC)
        KEY_CUR_LAST_ACCESS_TIME("curLastAccessTime"),
        // 最後に取得したデータ(Json)
        KEY_CUR_LAST_ACCESS_DATA("curLastAccessData"),
        // 基準通貨のシンボル
        KEY_BASE_CUR_SYMBOL("baseCurSymbol"),
        // 比較通貨のシンボルリスト(Json形式)
        KEY_COMP_CUR_SYMBOLS("compCurSymbols")
    }

    // -------------------------------------
    // アプリケーションの起動時に呼び出される
    // -------------------------------------
    override fun onCreate() {
        super.onCreate()

        // 共有設定をロードする
        loadSharedPreferences()
    }

    // 共有設定からアプリ設定をロードする
    fun loadSharedPreferences() {
        // 共有設定がない場合のデフォルト設定
        val appConfDef = AppConf()

        // 共有設定を取得
        val sp = getSharedPreferences(SpKey.NAME_APP_CONF.id, Context.MODE_PRIVATE) as SharedPreferences
        // 共有設定から"税率その１"を取得
        appConf.tax1 = sp.getFloat(SpKey.KEY_TAX1.id,appConfDef.tax1)
        // 共有設定から"税率その２"を取得
        appConf.tax2 = sp.getFloat(SpKey.KEY_TAX2.id,appConfDef.tax2)
        // 共有設定から"対数(x)を取得
        appConf.logx = sp.getFloat(SpKey.KEY_LOGX.id,appConfDef.logx)
        // 共有設定から"小数点以下の桁数"を取得
        appConf.numDecimalPlaces = sp.getInt(SpKey.KEY_NUM_DECIMAL_PLACES.id,appConfDef.numDecimalPlaces)
        // 共有設定から"計算データの履歴最大数"を取得
        appConf.historyCnt = sp.getInt(SpKey.KEY_HISTORY_CNT.id,appConfDef.historyCnt)
        // 共有設定から"スクリーンを常にON"を取得
        appConf.screenOn = sp.getBoolean(SpKey.KEY_SCREEN_ON.id,appConfDef.screenOn)
        // --------------------------------------
        // 為替レート用設定
        // --------------------------------------
        // 最後にデータを取得した時刻(UTC)
        appConf.curLastAccessTime = sp.getLong(SpKey.KEY_CUR_LAST_ACCESS_TIME.id,appConfDef.curLastAccessTime)
        // 最後に取得したデータ(Json)
        appConf.curLastAccessData = sp.getString(SpKey.KEY_CUR_LAST_ACCESS_DATA.id,appConfDef.curLastAccessData)!!
        // 基準通貨のシンボル
        appConf.baseCurSymbol = sp.getString(SpKey.KEY_BASE_CUR_SYMBOL.id,appConfDef.baseCurSymbol)!!
        // 比較通貨のシンボルリスト(Json形式)
        appConf.compCurSymbols = sp.getString(SpKey.KEY_COMP_CUR_SYMBOLS.id,appConfDef.compCurSymbols)!!
    }

    // 共有設定へアプリ設定を保存する
    fun saveSharedPreferences() {
        // 共有設定を取得
        val sp = getSharedPreferences(SpKey.NAME_APP_CONF.id, Context.MODE_PRIVATE) as SharedPreferences

        // 共有設定にアプリの設定を保存する
        sp.edit()
            // 共有設定へ"税率その１"を保存
            .putFloat(SpKey.KEY_TAX1.id,appConf.tax1)
            // 共有設定へ"税率その２"を保存
            .putFloat(SpKey.KEY_TAX2.id,appConf.tax2)
            // 共有設定へ"対数(x)"を保存
            .putFloat(SpKey.KEY_LOGX.id,appConf.logx)
            // 共有設定へ"小数点以下の桁数"を保存
            .putInt(SpKey.KEY_NUM_DECIMAL_PLACES.id,appConf.numDecimalPlaces)
            // 共有設定へ"計算データの履歴最大数"を保存
            .putInt(SpKey.KEY_HISTORY_CNT.id,appConf.historyCnt)
            // 共有設定へ"スクリーンを常にON"を保存
            .putBoolean(SpKey.KEY_SCREEN_ON.id,appConf.screenOn)
            // 最後にデータを取得した時刻(UTC)
            .putLong(SpKey.KEY_CUR_LAST_ACCESS_TIME.id,appConf.curLastAccessTime)
            // 最後に取得したデータ(Json)
            .putString(SpKey.KEY_CUR_LAST_ACCESS_DATA.id,appConf.curLastAccessData)
            // 基準通貨のシンボル
            .putString(SpKey.KEY_BASE_CUR_SYMBOL.id,appConf.baseCurSymbol)
            // 比較通貨のシンボルリスト(Json形式)
            .putString(SpKey.KEY_COMP_CUR_SYMBOLS.id,appConf.compCurSymbols)
            .apply()
    }
}