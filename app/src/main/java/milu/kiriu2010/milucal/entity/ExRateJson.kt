package milu.kiriu2010.milucal.entity

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.lang.Exception
import java.util.*

// 為替レートのJsonデータ
/*
{
  "date": "2018-11-30",
  "rates": {
    "BGN": 1.9558,
    "CAD": 1.5116,
    "BRL": 4.3843,
    "HUF": 323.62,
    "DKK": 7.4622,
    "JPY": 128.99,
    "ILS": 4.2167,
    "TRY": 5.8753,
    "RON": 4.6598,
    "GBP": 0.89068,
    "PHP": 59.536,
    "HRK": 7.4055,
    "NOK": 9.74,
    "USD": 1.1359,
    "MXN": 23.091,
    "AUD": 1.5565,
    "IDR": 16246.21,
    "KRW": 1274.04,
    "HKD": 8.8851,
    "ZAR": 15.6258,
    "ISK": 139.4,
    "CZK": 25.957,
    "THB": 37.348,
    "MYR": 4.7532,
    "NZD": 1.6556,
    "PLN": 4.29,
    "SEK": 10.3195,
    "RUB": 76.0734,
    "CNY": 7.8897,
    "SGD": 1.5581,
    "CHF": 1.134,
    "INR": 79.0815
  },
  "base": "EUR"
}
 */
data class ExRateJson(
    // 日付
    // 2018-11-30
    val date: String,
    // レート一覧
    // key:比較通貨のシンボル
    // val:比較通貨のレート
    val rateMap: MutableMap<String,Float>,
    // 基準となる通貨
    var base: String
) {
    // 基準となる通貨を変更
    // nBase: 次に基準となる通貨のシンボル
    fun changeBase( nBase: String ): ExRateJson {
        // 次に基準となる通貨のレート
        val nRate = rateMap[nBase] ?: throw Exception("not found next currency")
        // レート一覧から次に基準となる通貨を削除
        rateMap.remove(nBase)
        // レート一覧に前の基準通貨を追加
        rateMap[base] = 1f
        // レート一覧を、次に基準となる通貨のレートで更新
        rateMap.keys.forEach { k ->
            rateMap[k] = rateMap[k]!!.div(nRate)
        }

        // 基準通貨を変更
        base = nBase

        return this
    }
}

// 各通貨ごとのデータ
data class ExRateRecord(
    // 通貨シンボル
    val symbol: String,
    // 通貨名
    val desc: String,
    // 金額
    var rate: Float
): Parcelable {
    constructor(parcel: Parcel) : this(
        // 通貨シンボル
        parcel.readString()!!,
        // 通貨名
        parcel.readString()!!,
        // 金額
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            // 通貨シンボル
            it.writeString(symbol)
            // 通貨名
            it.writeString(desc)
            // 金額
            it.writeFloat(rate)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ExRateRecord> {
        override fun createFromParcel(parcel: Parcel): ExRateRecord {
            return ExRateRecord(parcel)
        }

        override fun newArray(size: Int): Array<ExRateRecord?> {
            return arrayOfNulls(size)
        }
    }

}


// 通貨の比較データ
data class ExRateRecordComp(
    // 基準通貨
    val exRateRecordA: ExRateRecord,
    // 比較通貨
    val exRateRecordB: ExRateRecord,
    // 現在値との比較(比較通貨側からみて)
    // ---------------------------------------
    // 基準通貨:ドル
    // 比較通貨:円
    // の場合
    //   > 0: 円安
    //   = 0: 同じ
    //   < 0: 円高
    val comp: Int
)

// --------------------------
// Realmに保存するデータ
// ・最新データ取得日付
// ・基準通貨のシンボル
// --------------------------
open class RealmExBase: RealmObject() {
    var date: Date = Date()
    var base: String = ""
}

// --------------------------
// Realmに保存するデータ
// ・比較通貨のシンボル
// ・レート
// --------------------------
open class RealmExComp: RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var compCurr: String = ""
    var compRate: Float = 0f
}