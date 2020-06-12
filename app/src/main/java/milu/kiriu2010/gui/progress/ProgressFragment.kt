package milu.kiriu2010.gui.progress


import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView

import milu.kiriu2010.milucal.R

class ProgressFragment : androidx.fragment.app.Fragment() {

    lateinit var progressAbs: ProgressAbstract

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // キーボードを閉じる
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(container?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        // アニメーション付き進捗状況を表示
        // ---------------------------------------------
        // 実行するアニメーションをランダムに決定する
        val randomInteger = (1..ProgressID.values().size).shuffled().first()
        progressAbs = ProgressFactory.createInstance(randomInteger)
        return progressAbs.start(inflater,container,savedInstanceState)
    }

    override fun onDestroyView() {
        progressAbs.removeHandler()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProgressFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
