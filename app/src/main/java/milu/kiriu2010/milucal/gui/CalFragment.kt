package milu.kiriu2010.milucal.gui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import milu.kiriu2010.cal.ANS
import milu.kiriu2010.calv2.ContextCal

import milu.kiriu2010.milucal.R
import milu.kiriu2010.util.MyTool
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 * Use the [CalFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CalFragment : Fragment() {

    // 計算バージョン
    private var calVer = "v2"

    // 計算バージョンを設定するグループ
    private lateinit var radioGroup: RadioGroup
    // 計算式を選択するビュー
    private lateinit var spinCal: Spinner
    // 計算式を入力するビュー
    private lateinit var editTextCal: EditText
    // 計算結果を表示するビュー
    private lateinit var textViewResult: TextView
    // エラーを表示するビュー
    private lateinit var textViewExp: TextView

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
        val view = inflater.inflate(R.layout.fragment_cal, container, false)

        // 計算バージョンを設定するグループ
        radioGroup = view.findViewById(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            calVer = when (checkedId) {
                R.id.rbtnV1 -> "v1"
                R.id.rbtnV2 -> "v2"
                else -> "v2"
            }
        }

        // 計算式を選択するビュー
        spinCal = view.findViewById(R.id.spinCal)
        /*
        val calArray =
            arrayOf(
                "2*(1+2)",
                "2+((2-3)*(4/2))+1",
                "-1*5"
            )
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,calArray)
        */
        val adapter = ArrayAdapter.createFromResource(context,R.array.func_array,android.R.layout.simple_spinner_item)
        spinCal.adapter = adapter
        spinCal.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 選択された値
                val selectedData = parent?.selectedItem as String
                editTextCal.setText( selectedData )
            }
        }

        // 計算式を入力するビュー
        editTextCal = view.findViewById(R.id.editTextCal)
        // 計算結果を表示するビュー
        textViewResult = view.findViewById(R.id.textViewResult)
        // エラーを表示するビュー
        textViewExp = view.findViewById(R.id.textViewExp)

        // 計算を実施
        val btnCal = view.findViewById<Button>(R.id.btnCal)
        btnCal.setOnClickListener {
            try {
                var res = 0.0
                if ( calVer == "v1") {
                    val ans = ANS()
                    // 解析
                    ans.interpret(editTextCal.text.toString())
                    // 計算
                    res = ans.execute().toDouble()
                }
                else {
                    val ctxCal = ContextCal(editTextCal.text.toString())
                    ctxCal.tokenLst.forEachIndexed { index, s ->
                        Log.d( javaClass.simpleName, "token[$index][$s]")
                    }
                    res = ctxCal.execute()
                }
                // 値を反映
                textViewResult.text = res.toString()
                textViewExp.text = ""
            }
            catch ( ex: Exception ) {
                // 値を反映
                textViewResult.text = ""
                textViewExp.text = MyTool.exp2str(ex)
            }
        }

        return view
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            CalFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
