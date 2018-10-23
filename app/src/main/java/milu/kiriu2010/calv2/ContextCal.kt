package milu.kiriu2010.calv2

import java.lang.NumberFormatException
import java.util.*

class ContextCal {
    // 計算式
    var eq: String = ""
    // トークンリスト
    val tokenLst: MutableList<String> = mutableListOf()
    // 計算式を解析した位置
    var pos: Int = 0

    enum class CalType {
        // NULL
        NULL,
        // オペランド
        OPERAND,
        // 加算
        OPE_PLUS,
        // 減算
        OPE_MINUS,
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
    fun execute(): Double {
        var num = 0.0

        // 加減算を実施
        num = calPlusMinus()

        return num
    }


    // 加減算を実施
    @Throws(CalException::class)
    private fun calPlusMinus(): Double {
        var num = 0.0

        var prevCalType = CalType.NULL
        try {
            while (tokenLst.size > 0) {
                val str = tokenLst[0]
                when {
                    // 加算
                    (str == "+") -> {
                        prevCalType = CalType.OPE_PLUS
                    }
                    // 減算
                    (str == "-") -> {
                        prevCalType = CalType.OPE_MINUS
                    }
                    else -> {
                        // 式の一番最初
                        if ( prevCalType == CalType.NULL ){
                            num = str.toDouble()
                        }
                        // 加算
                        else if (prevCalType == CalType.OPE_PLUS) {
                            num += str.toDouble()
                        }
                        // 減算
                        else if (prevCalType == CalType.OPE_MINUS) {
                            num -= str.toDouble()
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
