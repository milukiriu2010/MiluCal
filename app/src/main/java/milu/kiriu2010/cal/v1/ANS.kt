package milu.kiriu2010.cal.v1

// 計算式S
class ANS: ANExpression {
    // 計算式Eを保存するフィールド
    val e = ANE()

    fun interpret(str: String) {
        val charArray = str.toCharArray()
        interpret(charArray,0)
    }

    override fun interpret(charArray: CharArray, pos: Int): Int {
        return e.interpret(charArray,pos)
    }

    override fun execute(): Int {
        return e.execute()
    }
}