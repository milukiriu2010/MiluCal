package milu.kiriu2010.milucal.gui.misc


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import milu.kiriu2010.milucal.CalApplication

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.conf.AppConf
import java.util.*

class ConfFragment : androidx.fragment.app.DialogFragment()
    , SeekBar.OnSeekBarChangeListener {

    // アプリケーションの設定
    private lateinit var appConf: AppConf

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

    // 小数点以下の桁数を選択するシークバー
    private lateinit var seekBarNumDecimalPlaces: SeekBar
    // 小数点以下の桁数を表示するビュー
    private lateinit var dataNumDecimalPlaces: TextView

    // スクリーンONスイッチ
    private lateinit var switchScreenOn: Switch


    // 音声入力に使う言語のスピン
    private lateinit var spinVoice: Spinner
    // 音声入力に使う言語のアダプタ
    private lateinit var adapterVoice: ArrayAdapter<Locale>
    // 音声入力に使う言語の配列
    private var arrayVoiceLocale: Array<Locale> = arrayOf(Locale.getDefault(), Locale.ENGLISH,Locale.JAPANESE)

    // デフォルトボタン
    private lateinit var btnDefault: Button

    // OKボタン
    private lateinit var btnOK: Button

    // Cancelボタン
    private lateinit var btnCancel: Button

    // OKボタンを押下したことを通知するために用いるリスナー
    private lateinit var listener: OnUpdateConfListener

    // OKボタンを押下したことを通知するために用いるリスナー
    interface OnUpdateConfListener {
        fun updateConf()
    }

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
        appConf = appl.appConf

        // Tax1を選択するシークバー
        seekBarTax1 = view.findViewById(R.id.seekBarTax1)
        seekBarTax1.setOnSeekBarChangeListener(this)
        // Tax1の値を表示するビュー
        dataTax1 = view.findViewById(R.id.dataTax1)

        // Tax2を選択するシークバー
        seekBarTax2 = view.findViewById(R.id.seekBarTax2)
        seekBarTax2.setOnSeekBarChangeListener(this)
        // Tax2の値を表示するビュー
        dataTax2 = view.findViewById(R.id.dataTax2)

        // log(x)を選択するシークバー
        seekBarLogx = view.findViewById(R.id.seekBarLogx)
        seekBarLogx.setOnSeekBarChangeListener(this)
        // log(x)の値を表示するビュー
        dataLogx = view.findViewById(R.id.dataLogx)

        // 小数点以下の桁数を選択するシークバー
        seekBarNumDecimalPlaces = view.findViewById(R.id.seekBarNumDecimalPlaces)
        seekBarNumDecimalPlaces.setOnSeekBarChangeListener(this)
        // 小数点以下の桁数を表示するビュー
        dataNumDecimalPlaces = view.findViewById(R.id.dataNumDecimalPlaces)

        // スクリーンONスイッチ
        switchScreenOn = view.findViewById(R.id.switchScreenOn)

        // 音声入力に使う言語のアダプタ
        //adapterVoice = ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.voice_lang_array))
        adapterVoice = ArrayAdapter<Locale>(ctx, android.R.layout.simple_spinner_item, arrayVoiceLocale)
        // 音声入力に使う言語スピン
        spinVoice = view.findViewById(R.id.spinVoice)
        spinVoice.adapter = adapterVoice

        // アプリ設定の値をビューに反映する
        appConf2View()

        // デフォルトボタン
        btnDefault = view.findViewById(R.id.btnDefault)
        btnDefault.setOnClickListener {
            // デフォルト設定にする
            appConf.goDefault()
            // アプリ設定をビューへ反映
            appConf2View()
        }

        // OKボタン
        btnOK = view.findViewById(R.id.btnOK)
        btnOK.setOnClickListener {
            // 税率その１
            appConf.tax1 = dataTax1.text.toString().toFloat()
            // 税率その２
            appConf.tax2 = dataTax2.text.toString().toFloat()
            // 対数(x)
            appConf.logx = dataLogx.text.toString().toFloat()
            // 小数点以下の桁数
            appConf.numDecimalPlaces = dataNumDecimalPlaces.text.toString().toInt()

            // "スクリーンON"に対応する更新を実施
            appConf.screenOn = switchScreenOn.isChecked
            appConf.screenControl(activity!!)
            /*
            // ON
            if ( appConf.screenOn ) {
                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            // OFF
            else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            */


            // 音声入力に使う言語
            appConf.voiceLang = spinVoice.selectedItem as Locale

            // 共有設定へアプリ設定を保存する
            appl.saveSharedPreferences()
            // ダイアログを閉じる
            dismiss()
        }

        // Cancelボタン
        btnCancel = view.findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            // ダイアログを閉じる
            dismiss()
        }


        return view
    }

    // アプリ設定の値をビューに反映する
    fun appConf2View() {
        // Tax1を選択するシークバー
        seekBarTax1.progress = appConf.tax1.toInt()
        // Tax1の値を表示するビュー
        dataTax1.text = appConf.tax1.toInt().toString()

        // Tax2を選択するシークバー
        seekBarTax2.progress = appConf.tax2.toInt()
        // Tax2の値を表示するビュー
        dataTax2.text = appConf.tax2.toInt().toString()

        // log(x)を選択するシークバー
        //   シークバーは最小値が必ず0だが、
        //   log(x)の最小値は2としたいため、2引き算している
        seekBarLogx.progress = appConf.logx.toInt()-2
        // log(x)の値を表示するビュー
        dataLogx.text = appConf.logx.toInt().toString()

        // 小数点以下の桁数を選択するシークバー
        //   シークバーは最小値が必ず0だが、
        //   小数点以下の桁数の最小値は1としたいため、1引き算している
        seekBarNumDecimalPlaces.progress = appConf.numDecimalPlaces-1
        // 小数点以下の桁数を表示するビュー
        dataNumDecimalPlaces.text = appConf.numDecimalPlaces.toString()

        // スクリーンONスイッチを設定する
        switchScreenOn.isChecked = appConf.screenOn

        // 音声入力に使う言語スピン
        val locale = appConf.voiceLang
        val pos = arrayVoiceLocale.indexOf(locale)
        spinVoice.setSelection(pos)
    }

    // SeekBar.OnSeekBarChangeListener
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // シークバーの値をビューに反映
        seekBar2View()
    }

    // SeekBar.OnSeekBarChangeListener
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    // SeekBar.OnSeekBarChangeListener
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // シークバーの値をビューに反映
        seekBar2View()
    }

    // シークバーの値をビューに反映
    private fun seekBar2View() {
        // Tax1の値を表示するビュー
        dataTax1.text = seekBarTax1.progress.toString()
        // Tax2の値を表示するビュー
        dataTax2.text = seekBarTax2.progress.toString()
        // log(x)の値を表示するビュー
        //   シークバーは最小値が必ず0だが、
        //   log(x)の最小値は2としたいため、2足し算している
        dataLogx.text = (seekBarLogx.progress+2).toString()
        // 小数点以下の桁数を選択するシークバー
        //   シークバーは最小値が必ず0だが、
        //   小数点以下の桁数の最小値は1としたいため、1引き算している
        dataNumDecimalPlaces.text = (seekBarNumDecimalPlaces.progress+1).toString()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // WindowManager.LayoutParams
        val lp = dialog?.window?.attributes ?: return

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
