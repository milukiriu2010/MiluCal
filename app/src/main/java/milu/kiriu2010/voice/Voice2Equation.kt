package milu.kiriu2010.voice

class Voice2Equation {
    // 単純な変換マップ
    private val map = mapOf(
        "GO" to "5",
        "点" to ".",
        "," to ".",
        // "x" => "*"の変換が後ろにあるため、最初の方にもってきている
        "exponential" to "exp(",
        "かっことじ" to ")",
        "かっこ" to "(",
        "足す" to "+",
        "たす" to "+",
        "＋" to "+",
        "más" to "+",
        "＋" to "+",
        "引く" to "-",
        "ひく" to "-",
        "マイナス" to "-",
        "menos" to "-",
        "－" to "-",
        "かける" to "*",
        "かけ" to "*",
        "por" to "*",
        "X" to "*",
        "x" to "*",
        "×" to "*",
        "割る" to "/",
        "わる" to "/",
        "entre" to "/",
        "÷" to "/",
        "の階乗" to "!",
        "ルート" to "sqrt(",
        "cos" to "cos(",
        "コサイン" to "cos(",
        "sin" to "sin(",
        "サイン" to "sin(",
        "tan" to "tan(",
        "タンジェント" to "tan(",
        "自然対数" to "ln(",
        "常用対数" to "log(",
        "指数関数" to "exp(",
        "=" to "",
        "イコール" to "",
        "は" to "",
        // "exp(" => "e*p(" に変換されてしまうため
        "e*p(" to "exp("
    )

    // 正規表現を使った変換マップ
    val mapReg = mapOf<String,String>(
        "の(\\d+)乗" to "^$1"
    )

    fun correct( strOrg: String ): String {
        var strNew: String = strOrg
        // 単純な変換
        map.iterator().forEach {
            strNew = strNew.replace(it.key,it.value)
        }
        // 正規表現を使った変換
        mapReg.iterator().forEach {
            strNew = strNew.replace(it.key.toRegex(),it.value)
        }
        return strNew
    }
}
