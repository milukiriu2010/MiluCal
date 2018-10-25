package milu.kiriu2010.calv2

import android.util.Log
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.util.*

class ContextCal(
    // トークンリスト
    val tokenLst: MutableList<String> = mutableListOf() ) {

    enum class CalType {
        // 未確定
        UNKNOWN,
        // 未計算(式の一番最初)
        YET,
        // 演算子
        OPERATOR,
        // 加算
        OPERATOR_PLUS,
        // 減算
        OPERATOR_MINUS,
        // 乗算
        OPERATOR_MULTI,
        // 除算
        OPERATOR_DIV,
        // 累乗
        OPERATOR_POW,
        // 左かっこ"(",
        BRACKET_LEFT,
        // 右かっこ")",
        BRACKET_RIGHT,
        // 符号(プラス),
        SIGN_PLUS,
        // 符号(マイナス)
        SIGN_MINUS,
        // 数字
        NUM
    }

    constructor(eq: String) : this() {
        val tokenizer = StringTokenizer(eq,"+-*/^()",true)
        while ( tokenizer.hasMoreTokens() ) {
            tokenLst.add( tokenizer.nextToken() )
        }
    }

    fun toEquation(): String {
        return tokenLst.joinToString(separator = "")
    }

    @Throws(CalException::class)
    fun execute(): BigDecimal {
        // 符号(正負)を数値へマージ
        calSignMerge()

        // かっこ"()"内の計算を実施
        calBracket()

        // 累乗計算を実施
        calPow()

        // 乗除算を実施
        calMultiDiv()

        // 加減算を実施
        return calPlusMinus()
    }

    // 符号(正負)を数値へマージ
    // -------------------------------
    //   -1+5
    //   token[0] = -
    //   token[1] = 1
    //   token[2] = +
    //   token[3] = 5
    // -------------------------------
    //   → 以下のようにマージする
    // -------------------------------
    //   token[0] = -1
    //   token[1] = +
    //   token[2] = 5
    // -------------------------------
    @Throws(CalException::class)
    private fun calSignMerge() {
        var prevCalType = CalType.YET

        var index = 0
        val ite = tokenLst.iterator()
        ite.forEach { s ->
            when  {
                // プラス符号
                ( s == "+" ) -> {
                    // 式の先頭 or カッコ内の先頭
                    if ( ( prevCalType == CalType.YET ) or ( prevCalType == CalType.BRACKET_LEFT ) ) {
                        prevCalType = CalType.SIGN_PLUS
                    }
                    // 上記以外
                    else {
                        prevCalType = CalType.OPERATOR_PLUS
                    }
                }
                // マイナス符号
                ( s == "-" ) -> {
                    // 式の先頭 or カッコ内の先頭
                    if ( ( prevCalType == CalType.YET ) or ( prevCalType == CalType.BRACKET_LEFT ) ) {
                        prevCalType = CalType.SIGN_MINUS
                    }
                    // 上記以外
                    else {
                        prevCalType = CalType.OPERATOR_MINUS
                    }
                }
                // 左かっこ"("
                ( s == "(" ) -> {
                    prevCalType = CalType.BRACKET_LEFT
                }
                // 右かっこ")"
                ( s == ")" ) -> {
                    prevCalType = CalType.BRACKET_RIGHT
                }
                // 上記以外
                else -> {
                    val num = s.toBigDecimalOrNull()
                    if ( num != null ) {
                        // 1つ前が符号の場合マージし、
                        // トークンリストから符号を削除
                        if ( prevCalType == CalType.SIGN_PLUS ) {
                            // ConcurrentModificationException
                            //tokenLst.removeAt(index-1)
                            tokenLst[index-1] = num.toString()
                            ite.remove()
                            index--
                        }
                        else if ( prevCalType == CalType.SIGN_MINUS ) {
                            // ConcurrentModificationException
                            //tokenLst[index] = (-1 * num).toString()
                            //tokenLst.removeAt(index-1)
                            tokenLst[index-1] = (-1.toBigDecimal() * num).toString()
                            ite.remove()
                            index--
                        }
                        prevCalType = CalType.NUM
                    }
                    else {
                        prevCalType = CalType.UNKNOWN
                    }
                }
            }
            index++
        }

    }

    // かっこ"()"内の計算を実施
    @Throws(CalException::class)
    private fun calBracket() {
        // 左かっこ"("の位置
        val posL = tokenLst.indexOf("(")
        // 右かっこ")"の位置
        var posR = -1
        // トークンの長さ
        val size = tokenLst.size
        // かっこのレベル
        // -------------------------------------
        //   (1+2)
        //      =>
        //      かっこレベルは0
        // -------------------------------------
        //   ((1+2)*(3+4))
        //      =>
        //      一番外側のかっこレベルは0
        //      (1+2)と(3+4)のかっこレベルは1
        var level = 0

        // 左かっこ"("に対応する右かっこ")"を探す
        (posL+1 until size).forEach bracket@{
            when (tokenLst[it]) {
                ")" -> {
                    // 同一レベルの右かっこがみつかった場合
                    if (level == 0) {
                        posR = it
                        return@bracket
                    }
                    // 異なるレベルの右かっこがみつかった場合
                    // レベルを下げる
                    else {
                        level--
                    }
                }
                "(" -> {
                    // 左かっこが見つかった場合
                    // レベルを上げる
                    level++
                }
            }
        }

        // 左かっこ"("右かっこ")"ともに見つかれば、
        // 該当部分を配列から取り出して、解析処理に回す
        if ( ( posL != -1 ) and ( posR != -1 ) and (posL < posR) ) {
            val tmpTokenLst = mutableListOf<String>()
            // かっこ"()"内の式をテンポラリ配列に詰め込む
            (posL+1..posR-1).forEach { tmpTokenLst.add(tokenLst[it]) }
            // かっこ"()"で囲まれた配列を現在のトークンリストから外す
            (posL..posR).forEach { tokenLst.removeAt(posL) }

            // かっこ"()"内の式の値を取得
            val calCtx = ContextCal(tmpTokenLst)
            Log.d(javaClass.simpleName, "bra:eq[${calCtx.toEquation()}]")
            val num = calCtx.execute()
            Log.d(javaClass.simpleName, "bra:num[$num]")

            // かっこ"()"内の式の値を現在のトークンリストに入れ込む
            tokenLst.add(posL,num.toString())
            Log.d(javaClass.simpleName, "modify:eq[${toEquation()}]")

            // まだ、かっこがあるかもしれないので、
            // 再度かっこ"()"内の計算を実施
            calBracket()
        }
        // 左かっこ"("右かっこ")"ともに見つからなければ、
        // 何もせず処理を終了
        else if ( ( posL == -1 ) and ( posR == -1 ) ) {
        }
        // 左かっこ"("しかみつからない場合エラー
        // 右かっこ")"しかみつからない場合エラー
        else {
            throw CalException("Bracket'()' should be pair")
        }
    }

    // 累乗計算を実施
    // -----------------------
    // 累乗は、後ろ優先っぽい
    // -----------------------
    // 2^2 = 4
    // 2^2^2 = 16
    // 2^2^3 = 256
    @Throws(CalException::class)
    private fun calPow() {
        // 累乗記号(^)を後ろから見つける
        val pos = tokenLst.lastIndexOf("^")

    }


    // 乗除算を実施
    // -------------------------------
    //   1+2*5
    //   token[0] = 1
    //   token[1] = +
    //   token[2] = 2
    //   token[3] = *
    //   token[4] = 5
    // -------------------------------
    //   → 以下のようにマージする
    // -------------------------------
    //   token[0] = 1
    //   token[1] = +
    //   token[2] = 10
    // -------------------------------
    @Throws(CalException::class)
    private fun calMultiDiv() {
        var prevCalType = CalType.YET

        var index = 0
        val ite = tokenLst.iterator()
        ite.forEach { s ->
            when {
                // 乗算
                ( s == "*" ) -> {
                    // 演算子の前が数値
                    if ( prevCalType == CalType.NUM ) {
                        ite.remove()
                        index--
                        prevCalType = CalType.OPERATOR_MULTI
                    }
                    else {
                        throw CalException("value should be number before operator'*'")
                    }
                }
                // 除算
                ( s == "/" ) -> {
                    // 演算子の前が数値
                    if ( prevCalType == CalType.NUM ) {
                        ite.remove()
                        index--
                        prevCalType = CalType.OPERATOR_DIV
                    }
                    else {
                        throw CalException("value should be number before operator'/'")
                    }
                }
                // 加算
                ( s == "+" ) -> {
                    prevCalType = CalType.OPERATOR_PLUS
                }
                // 減算
                ( s == "-" ) -> {
                    prevCalType = CalType.OPERATOR_MINUS
                }
                // 上記以外
                else -> {
                    val num = s.toBigDecimalOrNull()
                    if ( num != null ) {
                        // 乗算
                        if ( prevCalType == CalType.OPERATOR_MULTI ) {
                            tokenLst[index-1] = (tokenLst[index-1].toBigDecimal() * num).toString()
                            ite.remove()
                            index--
                        }
                        // 除算
                        else if ( prevCalType == CalType.OPERATOR_DIV ) {
                            if ( num != 0.0.toBigDecimal() ) {
                                tokenLst[index-1] = tokenLst[index-1].toBigDecimal().divide(num).toString()
                            }
                            else {
                                throw CalException("divide by 0")
                            }
                            ite.remove()
                            index--
                        }
                        prevCalType = CalType.NUM
                    }
                    else {
                        throw CalException("format error for multiple/divide")
                    }
                }
            }
            index++
        }
    }

    // 加減算を実施
    @Throws(CalException::class)
    private fun calPlusMinus(): BigDecimal {
        var num = 0.0.toBigDecimal()

        var prevCalType = CalType.YET
        try {
            while (tokenLst.size > 0) {
                val str = tokenLst[0]
                when {
                    // 加算
                    (str == "+") -> {
                        prevCalType = CalType.OPERATOR_PLUS
                    }
                    // 減算
                    (str == "-") -> {
                        prevCalType = CalType.OPERATOR_MINUS
                    }
                    // 数値 or それ以外
                    else -> {
                        // 未計算(式の一番最初)
                        if ( prevCalType == CalType.YET ){
                            num = str.toBigDecimal()
                        }
                        // 加算
                        else if (prevCalType == CalType.OPERATOR_PLUS) {
                            num += str.toBigDecimal()
                        }
                        // 減算
                        else if (prevCalType == CalType.OPERATOR_MINUS) {
                            num -= str.toBigDecimal()
                        }
                        else {
                            throw CalException("${tokenLst[0]} has no operand.")
                        }

                        prevCalType = CalType.NUM
                    }
                }
                tokenLst.removeAt(0)
            }
        }
        catch ( ex: NumberFormatException ) {
            throw CalException("${tokenLst[0]} is not number.")
        }

        return num
    }

    /*
    fun currToken(): String {
        return if ( pos >= tokenLst.size ) {
            ""
        }
        else{
            tokenLst[pos]
        }
    }

    fun nextToken(): String {
        return if ( pos >= tokenLst.size ) {
            ""
        }
        else{
            tokenLst[pos]
        }
    }
    */
}
