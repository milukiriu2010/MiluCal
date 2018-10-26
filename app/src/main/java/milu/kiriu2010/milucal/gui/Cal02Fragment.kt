package milu.kiriu2010.milucal.gui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import milu.kiriu2010.calv2.ContextCal
import milu.kiriu2010.milucal.CalApplication

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.conf.AppConf


class Cal02Fragment : Fragment() {
    // アプリ設定
    private lateinit var appConf: AppConf

    // 式を入力するビュー
    private lateinit var dataEQ: EditText
    // 結果を表示するビュー
    private lateinit var dataResult: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cal02, container, false)

        // アプリ設定を取得
        appConf = (context?.applicationContext as? CalApplication)?.appConf ?: AppConf()

        // 式を入力するビュー
        dataEQ = view.findViewById(R.id.dataEQ)
        // 結果を表示するビュー
        dataResult = view.findViewById(R.id.dataResult)

        // 一文字削除"DEL"
        val btnDEL = view.findViewById<Button>(R.id.btnDEL)
        btnDEL.setOnClickListener { delStrByDEL() }

        // 一文字削除"BS"
        val btnBS = view.findViewById<Button>(R.id.btnBS)
        btnBS.setOnClickListener { delStrByBS() }

        // クリア
        val btnAC = view.findViewById<Button>(R.id.btnAC)
        btnAC.setOnClickListener { dataEQ.setText( "" ) }

        // 計算"="
        val btnEQ = view.findViewById<Button>(R.id.btnEQ)
        btnEQ.setOnClickListener {
            // 計算を実施
            val ctxCal = ContextCal(dataEQ.text.toString())
            // 対数(x)を設定
            ctxCal.logx = appConf.logx.toDouble()

            try {
                val num = ctxCal.execute()

                // 計算結果を表示
                dataResult.setText(num.toString())
            }
            catch ( ex: Exception ) {
                // エラー結果を表示
                dataResult.setText(ex.message)
            }
        }

        // 左かっこ"("
        val btnLEFT = view.findViewById<Button>(R.id.btnLEFT)
        btnLEFT.setOnClickListener { insertStr("(") }

        // 右かっこ")"
        val btnRIGHT = view.findViewById<Button>(R.id.btnRIGHT)
        btnRIGHT.setOnClickListener { insertStr(")" ) }

        // カーソル移動(左:←)
        val btnCurLeft = view.findViewById<Button>(R.id.btnCurLeft)
        btnCurLeft.setOnClickListener { moveCursor(-1) }

        // カーソル移動(右:→)
        val btnCurRight = view.findViewById<Button>(R.id.btnCurRight)
        btnCurRight.setOnClickListener { moveCursor(1) }

        // 0
        val btn0 = view.findViewById<Button>(R.id.btn0)
        btn0.setOnClickListener { insertStr("0" ) }

        // 1
        val btn1 = view.findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener { insertStr("1" ) }

        // 2
        val btn2 = view.findViewById<Button>(R.id.btn2)
        btn2.setOnClickListener { insertStr("2" ) }

        // 3
        val btn3 = view.findViewById<Button>(R.id.btn3)
        btn3.setOnClickListener { insertStr("3" ) }

        // 4
        val btn4 = view.findViewById<Button>(R.id.btn4)
        btn4.setOnClickListener { insertStr("4" ) }

        // 5
        val btn5 = view.findViewById<Button>(R.id.btn5)
        btn5.setOnClickListener { insertStr("5" ) }

        // 6
        val btn6 = view.findViewById<Button>(R.id.btn6)
        btn6.setOnClickListener { insertStr("6" ) }

        // 7
        val btn7 = view.findViewById<Button>(R.id.btn7)
        btn7.setOnClickListener { insertStr("7" ) }

        // 8
        val btn8 = view.findViewById<Button>(R.id.btn8)
        btn8.setOnClickListener { insertStr("8" ) }

        // 9
        val btn9 = view.findViewById<Button>(R.id.btn9)
        btn9.setOnClickListener { insertStr("9" ) }

        // 徐算"/"
        val btnDIV = view.findViewById<Button>(R.id.btnDIV)
        btnDIV.setOnClickListener { insertStr("/" ) }

        // 乗算"*"
        val btnMUL = view.findViewById<Button>(R.id.btnMUL)
        btnMUL.setOnClickListener { insertStr("*" ) }

        // 減算"-"
        val btnMINUS = view.findViewById<Button>(R.id.btnMINUS)
        btnMINUS.setOnClickListener { insertStr("-" ) }

        // 加算"+"
        val btnPLUS = view.findViewById<Button>(R.id.btnPLUS)
        btnPLUS.setOnClickListener { insertStr("+" ) }

        // 小数点"."
        val btnDOT = view.findViewById<Button>(R.id.btnDOT)
        btnDOT.setOnClickListener { insertStr("." ) }

        // 平方根"sqrt"
        val btnSQRT = view.findViewById<Button>(R.id.btnSQRT)
        btnSQRT.setOnClickListener { insertStr("sqrt(" ) }

        // 累乗"pow"
        val btnPOW = view.findViewById<Button>(R.id.btnPOW)
        btnPOW.setOnClickListener { insertStr("^" ) }

        // 階乗"factorial"
        val btnFACTORIAL = view.findViewById<Button>(R.id.btnFACTORIAL)
        btnFACTORIAL.setOnClickListener { insertStr("!" ) }

        // 税率その１
        val btnTAX1 = view.findViewById<Button>(R.id.btnTAX1)
        btnTAX1.setOnClickListener { insertStr("*%.2f".format(((100+appConf.tax1)/100).toFloat()) ) }

        // 税率その２
        val btnTAX2 = view.findViewById<Button>(R.id.btnTAX2)
        btnTAX2.setOnClickListener { insertStr("*%.2f".format(((100+appConf.tax2)/100).toFloat()) ) }

        // sin
        val btnSIN = view.findViewById<Button>(R.id.btnSIN)
        btnSIN.setOnClickListener { insertStr("sin(" ) }

        // cos
        val btnCOS = view.findViewById<Button>(R.id.btnCOS)
        btnCOS.setOnClickListener { insertStr("cos(" ) }

        // tan
        val btnTAN = view.findViewById<Button>(R.id.btnTAN)
        btnTAN.setOnClickListener { insertStr("tan(" ) }

        // 指数"exp"
        val btnEXP = view.findViewById<Button>(R.id.btnEXP)
        btnEXP.setOnClickListener { insertStr("exp(" ) }

        // 対数"log(e)"
        val btnLOGE = view.findViewById<Button>(R.id.btnLOGE)
        btnLOGE.setOnClickListener { insertStr("ln(" ) }

        // 対数"log10"
        val btnLOG10 = view.findViewById<Button>(R.id.btnLOG10)
        btnLOG10.setOnClickListener { insertStr("log(" ) }

        // 対数"log(x)"
        val btnLOGX = view.findViewById<Button>(R.id.btnLOGX)
        btnLOGX.setOnClickListener { insertStr("logx(" ) }

        return view
    }

    // 式に文字挿入
    private fun insertStr(str: String) {
        // 現在のカーソル位置
        val posS = dataEQ.selectionStart
        // 文字挿入
        dataEQ.setText(dataEQ.text.toString() + str)
        // カーソル位置を移動
        dataEQ.setSelection(posS+str.length)
    }

    // カーソル移動
    private fun moveCursor(dv:Int) {
        // 式に設定されている文字
        val str = dataEQ.text.toString()
        // 現在のカーソル位置
        val posS = dataEQ.selectionStart

        val pos = when {
            // 現在カーソル位置が左端より左にくることはない
            ((posS + dv) < 0) -> {
                0
            }
            // 現在カーソル位置が右端より右にくることはない
            ((posS + dv) > str.length) -> {
                str.length
            }
            // 現在カーソル位置が文字範囲内にある場合
            else -> {
                posS+dv
            }
        }
        dataEQ.setSelection(pos)
    }

    // 式から一文字削除
    // 現在カーソル位置の文字を削除
    private fun delStrByDEL() {
        // 式に設定されている文字
        val str = dataEQ.text.toString()
        // 現在のカーソル位置
        val posS = dataEQ.selectionStart
        // 現在のカーソル位置が最後尾なら何もしない
        //if ( posS == (str.length-1) ) return
        if ( posS == str.length ) return

        if (str.length > 0) {
            // カーソル位置の文字を一文字削除
            dataEQ.setText( str.removeRange(posS,posS+1) )
            // カーソル位置を１つ前にする
            dataEQ.setSelection(posS)
        }
    }

    // 式から一文字削除
    // 現在カーソルの１つ前を削除
    private fun delStrByBS() {
        // 式に設定されている文字
        val str = dataEQ.text.toString()
        // 現在のカーソル位置
        val posS = dataEQ.selectionStart
        // 現在のカーソル位置が先頭なら何もしない
        if ( posS == 0 ) return

        if (str.length > 0) {
            // カーソル位置の１つ前を一文字削除
            dataEQ.setText( str.removeRange(posS-1,posS) )
            // カーソル位置を１つ前にする
            dataEQ.setSelection(posS-1)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            Cal02Fragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
