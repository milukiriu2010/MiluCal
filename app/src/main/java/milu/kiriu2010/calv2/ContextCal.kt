package milu.kiriu2010.calv2

import java.lang.NumberFormatException
import java.math.BigDecimal
import java.util.*

class ContextCal {
    // 計算式
    var eq: String = ""
    // トークンリスト
    val tokenLst: MutableList<String> = mutableListOf()
    // 計算式を解析した位置
    var pos: Int = 0

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

    constructor(eq: String) {
        this.eq = eq

        val tokenizer = StringTokenizer(this.eq,"+-*/^()",true)
        while ( tokenizer.hasMoreTokens() ) {
            tokenLst.add( tokenizer.nextToken() )
        }
    }

    @Throws(CalException::class)
    fun execute(): BigDecimal {
        // 符号(正負)を数値へマージ
        calSignMerge()

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


        /*
        tokenLst.forEachIndexed { index, s ->
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
                    val num = s.toDoubleOrNull()
                    if ( num != null ) {
                        // 1つ前が符号の場合マージし、
                        // トークンリストから符号を削除
                        if ( prevCalType == CalType.SIGN_PLUS ) {
                            tokenLst.removeAt(index-1)
                        }
                        else if ( prevCalType == CalType.SIGN_MINUS ) {
                            tokenLst[index] = (-1 * num).toString()
                            tokenLst.removeAt(index-1)
                        }
                        prevCalType = CalType.NUM
                    }
                    else {
                        prevCalType = CalType.UNKNOWN
                    }
                }
            }
        }
        */
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
