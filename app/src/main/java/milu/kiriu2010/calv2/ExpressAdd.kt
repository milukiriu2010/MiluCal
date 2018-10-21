package milu.kiriu2010.calv2

// 加算式
class ExpressAdd(
        var expL: Express,
        var expR: Express ): Express {

    override fun execute() = expL.execute() + expR.execute()
}
