package milu.kiriu2010.cal

// S ::= E
// E ::= (E) | E+E | E-E | E*E | E/E | num
// num ::= 0 | 1 | .. | 9

interface ANExpression {
    // データの解析・オブジェクトの生成
    fun interpret(charArray: CharArray, pos: Int): Int

    // 処理の実行
    fun execute(): Int
}