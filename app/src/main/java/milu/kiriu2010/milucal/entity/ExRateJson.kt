package milu.kiriu2010.milucal.entity

import android.os.Parcel
import android.os.Parcelable

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
    // レート
    val rateMap: MutableMap<String,Float>,
    // 基準となる通貨
    val base: String
)

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
