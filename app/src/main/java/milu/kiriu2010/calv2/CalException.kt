package milu.kiriu2010.calv2

class CalException(val errType: ErrType, msg: String): Exception(msg) {
    enum class ErrType(val id: Int){
        ERR_FMT_FACTORIAL(10),
        ERR_FMT_POWER(11),
        ERR_FMT_MULTIPLY(12),
        ERR_FMT_DIVIDE(13),
        ERR_FMT_MULTIPLY_DIVIDE(14),
        ERR_FMT_NUMBER(15),
        ERR_DIVIDE_ZERO(20),
        ERR_NO_OPERAND(21)
    }
}
