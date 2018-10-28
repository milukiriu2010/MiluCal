package milu.kiriu2010.milucal.gui.misc


import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import milu.kiriu2010.milucal.CalApplication

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.conf.AppConf
import java.util.*

class ConfFragment : DialogFragment() {
    // Tax1を選択するシークバー
    private lateinit var seekBarTax1: SeekBar
    // Tax1の値を表示するビュー
    private lateinit var dataTax1: TextView

    // Tax2を選択するシークバー
    private lateinit var seekBarTax2: SeekBar
    // Tax2の値を表示するビュー
    private lateinit var dataTax2: TextView

    // log(x)を選択するシークバー
    private lateinit var seekBarLogx: SeekBar
    // log(x)の値を表示するビュー
    private lateinit var dataLogx: TextView

    // 音声入力に使う言語のスピン
    private lateinit var spinVoice: Spinner
    // 音声入力に使う言語のアダプタ
    private lateinit var adapterVoice: ArrayAdapter<Locale>
    // 音声入力に使う言語の配列
    private var arrayVoiceLocale: Array<Locale> = arrayOf(Locale.getDefault(), Locale.ENGLISH,Locale.JAPANESE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_conf, container, false)

        val ctx = context ?: return view

        // アプリケーションの設定
        val appl = ctx.applicationContext as CalApplication
        val appConf = appl.appConf

        // Tax1を選択するシークバー
        seekBarTax1 = view.findViewById(R.id.seekBarTax1)
        // Tax1の値を表示するビュー
        dataTax1 = view.findViewById(R.id.dataTax1)

        // Tax2を選択するシークバー
        seekBarTax2 = view.findViewById(R.id.seekBarTax2)
        // Tax2の値を表示するビュー
        dataTax2 = view.findViewById(R.id.dataTax2)

        // log(x)を選択するシークバー
        seekBarLogx = view.findViewById(R.id.seekBarLogx)
        // log(x)の値を表示するビュー
        dataLogx = view.findViewById(R.id.dataLogx)

        // 音声入力に使う言語のアダプタ
        //adapterVoice = ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.voice_lang_array))
        adapterVoice = ArrayAdapter<Locale>(ctx, android.R.layout.simple_spinner_item, arrayVoiceLocale)
        // 音声入力に使う言語スピン
        spinVoice = view.findViewById(R.id.spinVoice)
        spinVoice.adapter = adapterVoice

        // アプリ設定の値をビューに反映する
        appConf2View(ctx,appConf)

        return view
    }

    // アプリ設定の値をビューに反映する
    fun appConf2View(ctx: Context, appConf: AppConf) {
        // Tax1を選択するシークバー
        seekBarTax1.progress = appConf.tax1.toInt()
        // Tax1の値を表示するビュー
        dataTax1.text = appConf.tax1.toInt().toString()

        // Tax2を選択するシークバー
        seekBarTax2.progress = appConf.tax2.toInt()
        // Tax1の値を表示するビュー
        dataTax2.text = appConf.tax2.toInt().toString()

        // log(x)を選択するシークバー
        seekBarLogx.progress = appConf.logx.toInt()
        // log(x)の値を表示するビュー
        dataLogx.text = appConf.logx.toInt().toString()

        // 音声入力に使う言語スピン
        val locale = appConf.voiceLang
        val pos = arrayVoiceLocale.indexOf(locale)
        spinVoice.setSelection(pos)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // WindowManager.LayoutParams
        val lp = dialog.window.attributes

        // DisplayMetrics
        val metrics = resources.displayMetrics

        // ダイアログの幅だけ端末の幅まで広げる
        lp.width = metrics.widthPixels
        //lp.height = metrics.heightPixels
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ConfFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
