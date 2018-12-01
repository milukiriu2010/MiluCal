package milu.kiriu2010.cal.v1

import android.util.Log

// 数字のオブジェクト
class ANNum: ANExpression {
    // 数値を保存するフィールド
    var x = 0

    override fun interpret(charArray: CharArray, pos: Int): Int {
        x = charArray[pos] - '0'
        Log.d(javaClass.simpleName,"pos[$pos]x[$x]")
        return pos+1
    }

    override fun execute(): Int {
        return x
    }
}