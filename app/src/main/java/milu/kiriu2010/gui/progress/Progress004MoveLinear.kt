package milu.kiriu2010.gui.progress

import android.animation.Animator
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextView
import milu.kiriu2010.milucal.R

class Progress004MoveLinear: ProgressAbstract() {

    override fun start(inflater: LayoutInflater, container: ViewGroup?,
                       savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_progress, container, false)

        // 進捗状況を表示する画像ビューをインスタンス化
        val imageView = view.findViewById<ImageView>(R.id.imageViewProgress)
        // 表示する画像を設定
        imageView.setImageResource(R.drawable.ic_launcher)

        // 進捗状況を表示するテキストビュー
        //val textViewProgress = view.findViewById<TextView>(R.id.textViewProgress)

        // レイアウト・画像サイズ取得
        // エミュレータ(1080x1584) => ButtonNavigationなし
        // エミュレータ(1038x1542) => ButtonNavigationあり
        // 64x64 => 168x168
        view.viewTreeObserver.addOnGlobalLayoutListener {
            if ( isCalculated == true ) return@addOnGlobalLayoutListener
            isCalculated = true
            Log.d( javaClass.simpleName, "W:w[${view.width}]h[${view.height}]/I:w[${imageView.width}]h[${imageView.height}]")

            // レイアウト幅・高さ
            val lw = view.width.toFloat()
            val lh = view.height.toFloat()
            // 画像幅・高さ
            val iw = imageView.width.toFloat()
            val ih = imageView.height.toFloat()

            // 初期配置
            //   横 = 0
            //   縦 = 中央
            imageView.x = 0f
            imageView.y = lh/2 - ih/2

            // 画像の幅分右に移動
            val duration = 100L
            val animator = imageView.animate()
                    .setDuration(duration)
                    .xBy(iw)
            // リピートする
            animator.setListener( object: Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    Log.d( javaClass.simpleName, "onAnimationEnd")
                    // 次の位置がレイアウトを超える場合は
                    // 初期値に戻す
                    if ( (imageView.x+imageView.width) >= lw ) {
                        imageView.x = 0.0f
                    }

                    // 位置を右にずらす
                    imageView.animate()
                            .setDuration(duration)
                            .xBy(iw)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })


        }


        return view
    }
}