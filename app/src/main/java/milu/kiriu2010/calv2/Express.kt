package milu.kiriu2010.calv2

// S ::= E
// E ::= E1
// E1 ::= E1+E1 | E1-E1
// num ::= (-)dgt+(.)dgt
// dgt ::= 0|1|2|3|4|5|6|7|8|9

interface Express {
    fun execute(): Double
}
