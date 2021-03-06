package milu.kiriu2010.milucal.gui.exrate


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import milu.kiriu2010.milucal.CalApplication

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.conf.AppConf
import milu.kiriu2010.milucal.entity.ExRateJson
import milu.kiriu2010.milucal.entity.ExRateRecord
import org.json.JSONArray
import org.json.JSONObject

// 為替レートを取得できたら、表示されるフラグメント
class ExchangeRateFragment : Fragment()
    , ExchangeRateCallback {

    // 為替データ(Json)
    private var exRateJson: ExRateJson? = null

    // 為替レート(基準通貨)
    private lateinit var exRateRecordA: ExRateRecord

    // 為替レート(比較通貨)のリスト
    val exRateRecordBLst = mutableListOf<ExRateRecord>()

    // このフラグメントのレイアウト
    private lateinit var layoutExchangeRate: ConstraintLayout

    // 為替データのリサイクラービュー
    private lateinit var recyclerViewExchangeRate: RecyclerView

    // 為替データのリサイクラービューのアダプタ
    private lateinit var adapter: ExchangeRateAdapter

    // 基準通貨シンボル
    private lateinit var dataCurrencyBaseSymbol: TextView

    // 基準通貨レート
    private lateinit var dataCurrencyBaseRate: EditText

    // 基準通貨名
    private lateinit var dataCurrencyBaseDesc: TextView

    // 為替データの取得日
    private lateinit var dataDate: TextView

    // 為替データ更新ボタン
    private lateinit var btnUpdate: Button

    // ExchangeRateSWCallback
    // 基準通貨と比較通貨の強弱リストを表示するコールバック
    private var exchangeRateSWCallback: ExchangeRateSWCallback? = null

    // アプリケーションオブジェクト
    private lateinit var calApp: CalApplication

    // アプリ設定
    private lateinit var appConf: AppConf

    // ロングタッチで移動できるようにするためのおまじない
    // https://medium.com/@yfujiki/drag-and-reorder-recyclerview-items-in-a-user-friendly-manner-1282335141e9
    private val itemTouchHelper by lazy {
        // 1. Note that I am specifying all 4 directions.
        //    Specifying START and END also allows
        //    more organic dragging than just specifying UP and DOWN.
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
                0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as ExchangeRateAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    // 2. Update the backing model. Custom implementation in
                    //    MainRecyclerViewAdapter. You need to implement
                    //    reordering of the backing model inside the method.
                    adapter.moveItem(from, to)
                    // 3. Tell adapter to render the model update.
                    adapter.notifyItemMoved(from, to)

                    // 通貨リストのシンボルをJson配列にする
                    val jsonCur = JSONObject()
                    val jsonCurs = JSONArray()
                    exRateRecordBLst.forEach {
                        jsonCurs.put(it.symbol)
                    }
                    jsonCur.put( "curs", jsonCurs)

                    // -----------------------------
                    // アプリ設定として保存
                    // -----------------------------
                    // 比較通貨のシンボルリスト(Json形式)
                    appConf.compCurSymbols = jsonCur.toString()
                    calApp.saveSharedPreferences()

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // 4. Code block for horizontal swipe.
                    //    ItemTouchHelper handles horizontal swipe as well, but
                    //    it is not relevant with reordering. Ignoring here.
                }

                // onSelectedChanged と clearView で
                // ロングタッチでドラッグ＆ドロップができることを知らせるおまじない

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                               actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                // 2. This callback is called when the ViewHolder is
                //    unselected (dropped). We unhighlight the ViewHolder here.
                override fun clearView(recyclerView: RecyclerView,
                                       viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }
            }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // 為替レートが計算された日付
            val date = it.getString("date") ?: ""
            // 為替レートの基準通貨
            val base = it.getString("base") ?: ""
            // 為替レートの比較通貨
            val rateMap: MutableMap<String,Float> = mutableMapOf()
            currencyLst.forEach { key ->
                val currency =  key.substringAfterLast("rateMap")
                rateMap[currency] = it.getFloat(key)
            }

            // アプリ設定を読み込む
            calApp = context?.applicationContext as CalApplication
            appConf = calApp.appConf

            // 為替データ(Json)
            exRateJson = ExRateJson(date,rateMap,base).copy().changeBase(appConf.baseCurSymbol)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exchange_rate, container, false)

        // 基準通貨のシンボル
        val baseSymbol = exRateJson?.base ?: ""
        // 為替レート(基準通貨)
        exRateRecordA = ExRateRecord(baseSymbol,getDescFromSymbol(baseSymbol),1f)

        // 共有プリファレンスに保存された比較通貨シンボルのリスト
        //Log.d(javaClass.simpleName,"appConf.comCurSymbos=${appConf.compCurSymbols}")
        val jSonCompCurSymbols = JSONObject(appConf.compCurSymbols)
            .getJSONArray("curs")
        val compCurSymbols = mutableListOf<String>()
        (0 until jSonCompCurSymbols.length()).forEach {
            compCurSymbols.add(jSonCompCurSymbols.get(it) as String)
            //Log.d( javaClass.simpleName, "compCurSymbol[${it}]=${jSonCompCurSymbols.get(it)}")
        }

        // 為替レート(比較通貨)のリスト
        exRateRecordBLst.clear()
        // 共有プリファレンスの比較通貨シンボルのリストを用いて、まず構築する
        compCurSymbols.forEach { key ->
            //Log.d(javaClass.simpleName, "compCurSymbols:${key}")
            // 為替レート(比較貨幣)
            val exRateRecordB = ExRateRecord(key, getDescFromSymbol(key), exRateJson?.rateMap?.get(key) ?: 0f )
            exRateRecordBLst.add(exRateRecordB)
        }
        // キーでソートしたい場合
        exRateJson?.rateMap?.keys?.sorted()?.forEach { key ->
            // すでにリストに加えているシンボルの登録はスキップする
            if ( compCurSymbols.contains(key) ) return@forEach

            // 為替レート(比較貨幣)
            val exRateRecordB = ExRateRecord(key, getDescFromSymbol(key), exRateJson?.rateMap?.get(key) ?: 0f )
            exRateRecordBLst.add(exRateRecordB)
        }

        val ctx = context ?: return view

        // このフラグメントのレイアウト
        layoutExchangeRate = view.findViewById(R.id.layoutExchangeRate)

        // 基準通貨シンボル
        dataCurrencyBaseSymbol = view.findViewById(R.id.dataCurrencyBaseSymbol)
        dataCurrencyBaseSymbol.text = exRateRecordA.symbol

        // 基準通貨レート
        dataCurrencyBaseRate = view.findViewById(R.id.dataCurrencyBaseRate)
        dataCurrencyBaseRate.setText(exRateRecordA.rate.toString())

        // 基準通貨名
        dataCurrencyBaseDesc = view.findViewById(R.id.dataCurrencyBaseDesc)
        dataCurrencyBaseDesc.text = exRateRecordA.desc

        // 為替データの取得日
        dataDate = view.findViewById(R.id.dataDate)
        dataDate.text = exRateJson?.date ?: ""

        // 為替データのリサイクラービュー
        recyclerViewExchangeRate = view.findViewById(R.id.recycleViewExchangeRate)

        // 為替データのリサイクラービューのレイアウトマネージャ
        val layoutManager = LinearLayoutManager(
            ctx,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerViewExchangeRate.layoutManager = layoutManager

        // 為替データのリサイクラービューのアダプタ
        adapter = ExchangeRateAdapter(ctx,this,exRateRecordBLst) { exRateRecordB ->
            // 比較通貨をクリックすると
            // 基準通貨と比較通貨の強弱リストを表示する
            exchangeRateSWCallback?.dispBaseComp(exRateRecordA,exRateRecordB)
        }
        recyclerViewExchangeRate.adapter = adapter

        // 為替データのリサイクラービューの区切り線
        val itemDecoration = DividerItemDecoration(
            ctx,
            DividerItemDecoration.VERTICAL
        )
        recyclerViewExchangeRate.addItemDecoration(itemDecoration)

        // ロングタッチによるアイテム移動をするために、補助とリサイクラビューを結びつける
        itemTouchHelper.attachToRecyclerView(recyclerViewExchangeRate)

        // 為替データ更新ボタン
        btnUpdate = view.findViewById(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            // 基準通貨のレートを更新
            changeBaseCurrencyRate()
        }

        return view
    }

    // 通貨シンボルから通貨名を取得する
    private fun getDescFromSymbol(symbol: String): String {
        // 通貨名のリソースIDを生成
        val resourceId = resources.getIdentifier("currency_${symbol}", "string", activity?.packageName)
        // リソースから通貨名を取得
        val desc = resources.getString(resourceId)
        //Log.d(javaClass.simpleName, "desc[$desc]")
        return desc
    }

    // 基準通貨のレートを更新
    private fun changeBaseCurrencyRate() {
        // 基準通貨のレートをエディットテキストから取得
        val baseRate = dataCurrencyBaseRate.text.toString().toFloatOrNull()
        // 入力値が数値フォーマットでない場合エラーを表示
        if ( baseRate == null ) {
            dataCurrencyBaseRate.error = resources.getString(R.string.errmsg_fmt_number)
            return
        }
        // 入力値が0未満の場合エラーを表示
        if ( baseRate <= 0f ) {
            dataCurrencyBaseRate.error = resources.getString(R.string.errmsg_fmt_number)
            return
        }

        // キーボードを閉じる
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(layoutExchangeRate.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        // 基準通貨のレート"現在値"と"入力値"の倍率
        val mag = baseRate/exRateRecordA.rate

        // 基準通貨のレートを入力値に変更する
        exRateRecordA.rate = baseRate

        // 比較通貨のレートを変更する
        exRateRecordBLst.forEach { exRateRecordB ->
            exRateRecordB.rate = exRateRecordB.rate * mag
        }

        // リサイクラービューの表示を更新
        adapter.notifyDataSetChanged()
    }

    // ExchangeRateCallback
    // 基準通貨を変更する
    override fun changeBaseCurrency(nextBaseExRateRecord: ExRateRecord) {
        // 基準通貨(変更前)をコピー
        val prevBaseExRateRecord = exRateRecordA.copy()
        // 基準通貨(変更前)のレートを"基準通貨(変更後)のレート=1.0"で補正する
        val backRate = prevBaseExRateRecord.rate/nextBaseExRateRecord.rate
        val prevRate = prevBaseExRateRecord.rate
        prevBaseExRateRecord.rate = backRate

        // 為替レート(比較通貨)の一覧から、"基準通貨(変更後)"を削除
        exRateRecordBLst.remove(nextBaseExRateRecord)

        // 為替レート(比較通貨)のレートを"基準通貨(変更後)のレート=1.0"で補正する
        exRateRecordBLst.forEach { exRateRecordB ->
            exRateRecordB.rate = exRateRecordB.rate * backRate / prevRate
        }

        // 為替レート(比較通貨)の一覧に"基準通貨(変更前)"を先頭に追加
        exRateRecordBLst.add(0,prevBaseExRateRecord)

        // 基準通貨(変更後)のレート=1.0に更新
        nextBaseExRateRecord.rate = 1f

        // 基準通貨を入れ替える
        exRateRecordA = nextBaseExRateRecord

        // 為替レート(比較通貨)のレートをシンボルでソート
        //exRateRecordBLst.sortBy { it.symbol }

        // -----------------------------------------------------------
        // 以下、表示を変更
        // -----------------------------------------------------------

        // 基準通貨シンボル
        dataCurrencyBaseSymbol.text = exRateRecordA.symbol

        // 基準通貨レート
        dataCurrencyBaseRate.setText(exRateRecordA.rate.toString())

        // 基準通貨名
        dataCurrencyBaseDesc.text = exRateRecordA.desc

        // リサイクラービューの表示を更新
        adapter.notifyDataSetChanged()

        // リサイクラービューの先頭にスクロール
        recyclerViewExchangeRate.post {
            recyclerViewExchangeRate.smoothScrollToPosition(0)
        }

        // 通貨リストのシンボルをJson配列にする
        val jsonCur = JSONObject()
        val jsonCurs = JSONArray()
        exRateRecordBLst.forEach {
            jsonCurs.put(it.symbol)
        }
        jsonCur.put( "curs", jsonCurs)

        // -----------------------------
        // アプリ設定として保存
        // -----------------------------
        // 基準通貨のシンボル
        appConf.baseCurSymbol = nextBaseExRateRecord.symbol
        // 比較通貨のシンボルリスト(Json形式)
        appConf.compCurSymbols = jsonCur.toString()
        calApp.saveSharedPreferences()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ExchangeRateSWCallback)
        {
            exchangeRateSWCallback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        exchangeRateSWCallback = null
    }

    companion object {
        private val currencyLst: MutableList<String> = mutableListOf()

        @JvmStatic
        fun newInstance(exRateJson: ExRateJson) =
            ExchangeRateFragment().apply {
                arguments = Bundle().apply {
                    // データを取得した日付
                    putString("date",exRateJson.date)
                    // 基準となる通貨
                    putString("base",exRateJson.base)
                    // 比較される通貨とレートのマップ
                    exRateJson.rateMap.keys.forEach { key ->
                        // Bundleのキー名が、かぶらないよう"rateMap"をくっつけている
                        putFloat("rateMap${key}", exRateJson.rateMap.get(key)!! )
                        currencyLst.add("rateMap${key}")
                    }
                }
            }
    }
}
