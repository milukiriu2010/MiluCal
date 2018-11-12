package milu.kiriu2010.util

import android.content.res.Resources
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// 2018.11.12  数値(BigDecimal)→文字列変換
// 2018.10.25  階乗
// 2018.10.06  rawファイルの中身をString型に変換
// 2018.09.25  今日の日付をYYYYMMDD形式で取得
// 2018.09.15  String(ISO8601+RFC3339)をDateへ変換
class MyTool {
    companion object {
        // ----------------------------------------
        // 2018.11.12
        // ----------------------------------------
        // 数値(BigDecimal)→文字列変換
        // ----------------------------------------
        // num: 変換元数値(BigDecimal)
        // numDecimalPlaces: 小数点以下の桁数
        fun fromBigDeimal2String(num: BigDecimal, numDecimalPlaces: Int): String {
            val numScale = num.scale()
            val numPrecision = num.precision()
            Log.d( javaClass.simpleName, "ans[$num]scale[$numScale]precision[$numPrecision]")

            return when {
                // 整数部のみ
                ( numScale <= 0 ) -> { "%d".format(num.toInt()) }
                // 小数部の桁数が、設定"小数点以下の桁数"より短い場合
                ( numScale > 0 ) and ( numDecimalPlaces > numScale ) -> {
                    var strScale = StringBuffer()
                    (1..numScale).forEach { strScale.append("#") }
                    val decimalFmt = DecimalFormat("###." + strScale )
                    decimalFmt.format(num)
                }
                // 小数部の桁数が、設定"小数点以下の桁数"より長い場合
                else -> {
                    var strScale = StringBuffer()
                    (1..numDecimalPlaces).forEach { strScale.append("#") }
                    val decimalFmt = DecimalFormat("###." + strScale )
                    decimalFmt.format(num)
                }
            }
        }


        // ----------------------------------------
        // 2018.10.25
        // ----------------------------------------
        // 階乗
        // ----------------------------------------
        // https://ja.wikipedia.org/wiki/%E9%9A%8E%E4%B9%97
        fun factorial( num: Int ): Int {
            var x: Int
            if ( num > 0 ) {
                x = num * factorial(num-1)
            }
            else {
                // 0! = 1
                x = 1
            }
            return x
        }

        // ----------------------------------------
        // 2018.10.06
        // ----------------------------------------
        // rawファイルの中身をString型に変換
        // ----------------------------------------
        fun loadRawFile(resources: Resources, id: Int ): String {
            val sb = StringBuffer()

            val istream = resources.openRawResource(id)
            val reader = istream.bufferedReader()
            val iterator = reader.lineSequence().iterator()
            while ( iterator.hasNext() ) {
                sb.append( iterator.next() )
            }
            reader.close()
            istream.close()

            return sb.toString()
        }
        
        // ----------------------------------------
        // 2018.09.25
        // ----------------------------------------
        // 今日の日付をYYYYMMDD形式で取得
        // ----------------------------------------
        fun getToday( zone: String = "Asia/Tokyo" ): String {
            // Date()はUTCなので、タイムゾーンを"Asia/Tokyo"にして変換する
            val date = Date()
            val dateFormat = SimpleDateFormat("yyyyMMdd")
            dateFormat.timeZone = TimeZone.getTimeZone(zone)
            return dateFormat.format(date)
        }

        // ----------------------------------------
        // 2018.09.15
        // ----------------------------------------
        // RFC3339
        //   2018-08-28T19:00:00+09:00
        //   2018-09-14T21:46:00Z
        // ----------------------------------------
        fun rfc3339date(str: String): Date {
            try {
                val formatterRFC3339_1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US)
                return formatterRFC3339_1.parse(str)
            } catch ( parseEx: ParseException) {
                val formatterRFC3339_2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                return formatterRFC3339_2.parse(str)
            }
        }

        // ----------------------------------------
        // Exceptionを文字列に変換
        // ----------------------------------------
        fun exp2str( ex: Exception ): String {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            ex.printStackTrace(pw)
            pw.flush()
            return sw.toString()
        }
        
    }
}
