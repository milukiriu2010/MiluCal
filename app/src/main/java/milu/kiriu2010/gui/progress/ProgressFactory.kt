package milu.kiriu2010.gui.progress

enum class ProgressID(val id: Int) {
    // 拡大－補完にCycleInterpolatorを利用
    ID_001_SCALE_CYCLE_INTERPOLATOR(1),
    // 拡大－補完にLinearInterpolatorを利用
    ID_002_SCALE_LINEAR_INTERPOLATOR(2),
    // フェードイン・アウト
    ID_003_ALPHA_CYCLE_INTERPOLATOR(3),
    // 直線移動
    ID_004_MOVE_LINEAR(4),
    // 壁で跳ね返る
    ID_005_BOUNCE(5)
}

class ProgressFactory {
    companion object {
        fun createInstance( id: Int ): ProgressAbstract {
            var progressAbs = when ( id ) {
                //ProgressID.ID_001_SCALE_CYCLE_INTERPOLATOR.id -> Progress001ScaleCycleInterpolator()
                //ProgressID.ID_002_SCALE_LINEAR_INTERPOLATOR.id -> Progress002ScaleLinearInterpolator()
                //ProgressID.ID_003_ALPHA_CYCLE_INTERPOLATOR.id -> Progress003AlphaCycleInterpolator()
                ProgressID.ID_004_MOVE_LINEAR.id -> Progress004MoveLinear()
                //ProgressID.ID_005_BOUNCE.id -> Progress005Bounce()
                //else -> Progress005Bounce()
                else -> Progress004MoveLinear()
            }
            return progressAbs
        }
    }
}