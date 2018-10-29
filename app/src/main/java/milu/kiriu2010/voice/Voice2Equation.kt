package milu.kiriu2010.voice

class Voice2Equation {
    // 単純な変換マップ
    private val map = mapOf(
        "GO" to "5",
        "かっことじ" to ")",
        "かっこ" to "(",
        "たす" to "+",
        "＋" to "+",
        "引く" to "-",
        "－" to "-",
        "かける" to "*",
        "かけ" to "*",
        "×" to "*",
        "わる" to "/",
        "÷" to "/",
        "の階乗" to "!",
        "ルート" to "sqrt(",
        "sin" to "sin(",
        "cos" to "cos(",
        "tan" to "tan(",
        "タンジェント" to "tan(",
        "=" to "",
        "イコール" to "",
        "は" to ""
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
