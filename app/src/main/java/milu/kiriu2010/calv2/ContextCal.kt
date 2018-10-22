package milu.kiriu2010.calv2

import java.util.*

class ContextCal {
    // 計算式
    var eq: String = ""
    // トークンリスト
    val tokenLst: MutableList<String> = mutableListOf()
    // 計算式を解析した位置
    var pos: Int = 0

    constructor(eq: String) {
        this.eq = eq

        val tokenizer = StringTokenizer(this.eq,"+-*/()",true)
        while ( tokenizer.hasMoreTokens() ) {
            tokenLst.add( tokenizer.nextToken() )
        }
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
