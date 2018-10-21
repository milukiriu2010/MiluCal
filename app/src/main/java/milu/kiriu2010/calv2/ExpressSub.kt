package milu.kiriu2010.calv2

// 減算式
class ExpressSub(
    var expL: Express,
    var expR: Express ): Express {

    override fun execute() = expL.execute() - expR.execute()
}
