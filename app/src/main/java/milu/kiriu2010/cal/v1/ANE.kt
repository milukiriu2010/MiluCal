package milu.kiriu2010.cal.v1

// 計算式E
class ANE: ANExpression {
    enum class PATTERN {
        // 未決定
        X,
        // 計算式E
        E,
        // 足し算
        ADD,
        // 引き算
        SUB,
        // 掛け算
        MUL,
        // 割り算
        DIV
    }

    // 現在の状態
    private var state: PATTERN = PATTERN.X

    // 右辺
    private lateinit var nextE: ANExpression
    // 左辺
    private lateinit var nowE: ANExpression

    override fun interpret(charArray: CharArray, pos: Int): Int {
        if ( charArray.size <= pos ) {
            throw NoSuchElementException()
        }
        var pos2 = pos

        when (charArray[pos2]) {
            // かっこの中にEがある
            '(' -> {
                nowE = ANE()
                pos2 = nowE.interpret(charArray,pos2+1)
            }
            else -> {
                nowE = ANNum()
                pos2 = nowE.interpret(charArray,pos2)
            }
        }
        // 終端
        if ( charArray.size <= pos2 ) {
            state = PATTERN.E
            return pos2
        }

        state = when (charArray[pos2]) {
            // かっこが終わる
            ')' -> PATTERN.E
            // 足し算
            '+' -> PATTERN.ADD
            // 引き算
            '-' -> PATTERN.SUB
            // 掛け算
            '*' -> PATTERN.MUL
            // 割り算
            '/' -> PATTERN.DIV
            else -> throw NoSuchElementException()
        }

        // かっこが終わる
        if ( state == PATTERN.E) {
            return pos2+1
        }

        // 右辺を作って解析続行
        nextE = ANE()
        return nextE.interpret(charArray,pos2+1)
    }

    override fun execute(): Int {
        return when(state) {
            // 左辺調査
            PATTERN.E -> nowE.execute()
            // 足し算
            PATTERN.ADD -> nowE.execute() + nextE.execute()
            // 引き算
            PATTERN.SUB -> nowE.execute() - nextE.execute()
            // 掛け算
            PATTERN.MUL -> nowE.execute() * nextE.execute()
            // 割り算
            PATTERN.DIV -> nowE.execute() / nextE.execute()
            else -> throw NoSuchElementException()
        }
    }
}