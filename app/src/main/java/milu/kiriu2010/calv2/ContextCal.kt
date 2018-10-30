package milu.kiriu2010.calv2

import android.util.Log
import milu.kiriu2010.util.MyTool
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.MathContext
import java.util.StringTokenizer
import kotlin.math.log
import kotlin.math.log10
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.exp
import kotlin.math.sqrt

class ContextCal(
    // トークンリスト
    val tokenLst: MutableList<String> = mutableListOf() ) {

    // 対数(x)の値
    var logx = 2.0

    private enum class CalType {
        // 未確定
        UNKNOWN,
        // 未計算(式の一番最初)
        YET,
        // 演算子
        //OPERATOR,
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
        // 階乗
        OPERATOR_FACTORIAL,
        // 平方根
        FUNC_SQRT,
        // 指数
        FUNC_EXP,
        // 対数(e)
        FUNC_LN,
        // 対数(10)
        FUNC_LOG,
        // 対数(x)
        FUNC_LOGX,
        // cos
        FUNC_COS,
        // sin
        FUNC_SIN,
        // tan
        FUNC_TAN,
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
        // スペースは除く
        val eqNew = eq.replace("\\s".toRegex(),"")
        // 演算子で分割
        val tokenizer = StringTokenizer(eqNew,"+-*/!^()",true)
        while ( tokenizer.hasMoreTokens() ) {
            tokenLst.add( tokenizer.nextToken() )
        }
    }

    // トークンを数式文字列に変換したものを返す
    fun toEquation(): String {
        return tokenLst.joinToString(separator = "")
    }

    // 計算を実施
    @Throws(CalException::class)
    fun execute(): BigDecimal {
        // 符号(正負)を数値へマージ
        calSignMerge()

        // かっこ"()"内の計算を実施
        calBracket()

        // 関数計算を実施
        calFunc()

        // 階乗計算を実施
        calFactorial()

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
                    if ( ( prevCalType == CalType.YET ) or ( prevCalType == CalType.BRACKET_LEFT ) or ( prevCalType == CalType.UNKNOWN ) ) {
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
                    if ( ( prevCalType == CalType.YET ) or ( prevCalType == CalType.BRACKET_LEFT ) or ( prevCalType == CalType.UNKNOWN ) ) {
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
                            tokenLst[index-1] = num.toString()
                            ite.remove()
                            index--
                        }
                        else if ( prevCalType == CalType.SIGN_MINUS ) {
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
            // 対数(x)を設定する
            calCtx.logx = logx
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
        // 左かっこ"("しかみつからない場合
        // 一番右に右かっこ"("をつけて再計算してみる
        else if ( ( posL != -1 ) and ( posR == -1 ) ) {
            // 一番右に右かっこ"("を付与
            tokenLst.add(")")
            // 再度かっこ"()"内の計算を実施
            calBracket()
        }
        // 右かっこ")"しかみつからない場合
        // 一番左に左かっこ"("を付与
        else {
            //throw CalException("Bracket'()' should be pair")
            // 一番左に左かっこ"("を付与
            tokenLst.add(0,"(")
            // 再度かっこ"()"内の計算を実施
            calBracket()
        }
    }

    // 関数計算を実施
    @Throws(CalException::class)
    private fun calFunc() {
        var prevCalType = CalType.YET

        var index = 0
        val ite = tokenLst.iterator()
        ite.forEach {
            when (it) {
                // 平方根
                "sqrt" -> {
                    prevCalType = CalType.FUNC_SQRT
                }
                // 指数
                "exp" -> {
                    prevCalType = CalType.FUNC_EXP
                }
                // 対数(e)
                "ln" -> {
                    prevCalType = CalType.FUNC_LN
                }
                // 対数(10)
                "log" -> {
                    prevCalType = CalType.FUNC_LOG
                }
                // 対数(x)
                "logx" -> {
                    prevCalType = CalType.FUNC_LOGX
                }
                // cos
                "cos" -> {
                    prevCalType = CalType.FUNC_COS
                }
                // sin
                "sin" -> {
                    prevCalType = CalType.FUNC_SIN
                }
                // tan
                "tan" -> {
                    prevCalType = CalType.FUNC_TAN
                }
                // 上記以外
                else -> {
                    val num = it.toBigDecimalOrNull()
                    // 数字の場合
                    if ( num != null ) {
                        when (prevCalType) {
                            CalType.FUNC_SQRT -> {
                                tokenLst[index-1] = sqrt(num.toDouble()).toString()
                                ite.remove()
                                index--
                            }
                            CalType.FUNC_EXP -> {
                                tokenLst[index-1] = exp(num.toDouble()).toString()
                                ite.remove()
                                index--
                            }
                            CalType.FUNC_LN -> {
                                tokenLst[index-1] = ln(num.toDouble()).toString()
                                ite.remove()
                                index--
                            }
                            CalType.FUNC_LOG -> {
                                tokenLst[index-1] = log10(num.toDouble()).toString()
                                ite.remove()
                                index--
                            }
                            CalType.FUNC_LOGX -> {
                                tokenLst[index-1] = log(num.toDouble(),logx).toString()
                                ite.remove()
                                index--
                            }
                            CalType.FUNC_COS -> {
                                //tokenLst[index-1] = cos(num.toDouble()/180.0* PI).toString()
                                //tokenLst[index-1] = Math.cos( Math.toRadians(num.toDouble()) ).toString()
                                //tokenLst[index-1] = cos( (num*PI.toBigDecimal()).divide(180.toBigDecimal(),MathContext.DECIMAL128).toDouble() ).toString()
                                //tokenLst[index-1] = Math.cos( Math.toRadians(num.toDouble()) ).toString()
                                tokenLst[index-1] = "%.10f".format(Math.cos( Math.toRadians(num.toDouble()) ))
                                ite.remove()
                                index--
                            }
                            CalType.FUNC_SIN -> {
                                //tokenLst[index-1] = sin(num.toDouble()/180.0* PI).toString()
                                //tokenLst[index-1] = Math.sin( Math.toRadians(num.toDouble()) ).toString()
                                // sin(30) = 0.49999999
                                //tokenLst[index-1] = sin( (num*PI.toBigDecimal()).divide(180.toBigDecimal(),MathContext.DECIMAL128).toDouble() ).toString()
                                tokenLst[index-1] = "%.10f".format(Math.sin( Math.toRadians(num.toDouble()) ))
                                //Log.d( javaClass.simpleName, "num=%.7f".format(num.toDouble()))
                                //Log.d( javaClass.simpleName, "sin(30org)=%.7f".format(Math.sin( Math.toRadians(num.toDouble()) )))
                                //Log.d( javaClass.simpleName, "sin(30)=%.7f".format(Math.sin(Math.toRadians(30.0))))
                                ite.remove()
                                index--
                            }
                            CalType.FUNC_TAN -> {
                                //tokenLst[index-1] = tan(num.toDouble()/180.0* PI).toString()
                                //tokenLst[index-1] = Math.tan( Math.toRadians(num.toDouble()) ).toString()
                                //tokenLst[index-1] = tan( (num*PI.toBigDecimal()).divide(180.toBigDecimal(),MathContext.DECIMAL128).toDouble() ).toString()
                                //tokenLst[index-1] = Math.tan( Math.toRadians(num.toDouble()) ).toString()
                                tokenLst[index-1] = "%.10f".format(Math.tan( Math.toRadians(num.toDouble()) ))
                                ite.remove()
                                index--
                            }
                        }

                        prevCalType = CalType.NUM
                    }
                    // 数字以外の場合
                    else {
                        prevCalType = CalType.UNKNOWN
                    }
                }
            }

            index++
        }

    }

    // 階乗計算を実施
    // 3! = 1*2*3 = 6
    // 3!! = (1*2*3)! = 6! = 1*2*3*4*5*6
    private fun calFactorial() {
        var prevCalType = CalType.YET

        var index = 0
        val ite = tokenLst.iterator()
        ite.forEach {
            when (it) {
                "!" -> {
                    // 左となりの演算子が数字の場合、
                    // 階乗の計算を実行し、配列をつめる
                    // 数字は整数に丸める
                    if ( prevCalType == CalType.NUM ) {
                        val num = tokenLst[index-1].toInt()
                        // 階乗の左となりの数値を上書き
                        tokenLst[index-1] = MyTool.factorial(num).toString()
                        // 階乗部分を削除
                        ite.remove()
                        index--
                        prevCalType = CalType.NUM
                    }
                    // 左となりの演算子が数値でない場合、
                    // エラー
                    else {
                        throw CalException(CalException.ErrType.ERR_FMT_FACTORIAL,"the value should be number before factorial mark'!'")
                    }
                }
                else -> {
                    val num = it.toBigDecimalOrNull()
                    // 数字
                    if ( num != null ) {
                        prevCalType = CalType.NUM
                    }
                    // 数字以外
                    else {
                        prevCalType = CalType.UNKNOWN
                    }
                }
            }
            index++
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
        var prevCalType = CalType.YET

        var index = tokenLst.size-1
        val ite = tokenLst.reversed().iterator()
        ite.forEach {
            Log.d( javaClass.simpleName, "index[$index]token[$it]")
            when (it) {
                "^" -> {
                    // 右となりの演算子が数値でない場合
                    // 数式がおかしいということでエラーとする
                    if ( prevCalType != CalType.NUM ) {
                        throw CalException(CalException.ErrType.ERR_FMT_POWER,"the right side of the operator '^' is something weird")
                    }
                    prevCalType = CalType.OPERATOR_POW
                }
                else -> {
                    val num = it.toBigDecimalOrNull()
                    // 数字
                    if ( num != null ) {
                        // 右となりの演算子が累乗の場合、
                        // 累乗の計算を実行し、配列をつめる
                        if ( prevCalType == CalType.OPERATOR_POW ) {
                            // 累乗の左となりの数値を上書き
                            tokenLst[index] = (num.toDouble().pow(tokenLst[index+2].toDouble())).toString()
                            // 累乗の右となりの数値を削除
                            tokenLst.removeAt(index+2)
                            // 累乗部分を削除
                            tokenLst.removeAt(index+1)
                        }
                        // 右となりの演算子が累乗でない場合、
                        // 何もしない
                        else {
                        }
                        prevCalType = CalType.NUM
                    }
                    // 数字以外
                    else {
                        //throw CalException("the left side of the operator '^' is something weird")
                        prevCalType = CalType.UNKNOWN
                    }
                }
            }
            index--
        }

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
                        throw CalException(CalException.ErrType.ERR_FMT_MULTIPLY,"value should be number before operator'*'")
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
                        throw CalException(CalException.ErrType.ERR_FMT_DIVIDE,"value should be number before operator'/'")
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
                                // https://stackoverflow.com/questions/4591206/arithmeticexception-non-terminating-decimal-expansion-no-exact-representable
                                // ArithmeticException
                                // Non-terminating decimal expansion
                                //tokenLst[index-1] = tokenLst[index-1].toBigDecimal().divide(num).toString()


                                tokenLst[index-1] = tokenLst[index-1].toBigDecimal().divide(num, MathContext.DECIMAL128).toString()
                            }
                            else {
                                throw CalException(CalException.ErrType.ERR_DIVIDE_ZERO,"divide by 0")
                            }
                            ite.remove()
                            index--
                        }
                        prevCalType = CalType.NUM
                    }
                    else {
                        throw CalException(CalException.ErrType.ERR_FMT_MULTIPLY_DIVIDE,"format error for multiple / divide")
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
                            throw CalException(CalException.ErrType.ERR_NO_OPERAND,"${tokenLst[0]} has no operand.")
                        }

                        prevCalType = CalType.NUM
                    }
                }
                tokenLst.removeAt(0)
            }
        }
        catch ( ex: NumberFormatException ) {
            throw CalException(CalException.ErrType.ERR_FMT_NUMBER,"${tokenLst[0]} is not number.")
        }

        return num
    }
}
