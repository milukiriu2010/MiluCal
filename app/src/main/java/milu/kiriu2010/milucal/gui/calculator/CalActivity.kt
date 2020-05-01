package milu.kiriu2010.milucal.gui.calculator

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import milu.kiriu2010.milucal.R
import kotlinx.android.synthetic.main.activity_cal.*
import milu.kiriu2010.milucal.CalApplication
import milu.kiriu2010.milucal.conf.AppConf
import milu.kiriu2010.milucal.entity.CalData
import milu.kiriu2010.milucal.gui.misc.AboutFragment
import milu.kiriu2010.milucal.gui.misc.ConfFragment
import milu.kiriu2010.milucal.id.FragmentID
import milu.kiriu2010.util.LimitedArrayList

class CalActivity : AppCompatActivity()
    , OnHistoryCallback
    , OnHistoryGetCallback {

    // アプリ設定
    private lateinit var appConf: AppConf

    // スワイプする表示されるページを格納したアダプタ
    private var calPagerAdapter: CalPagerAdapter? = null

    // 計算データの履歴
    private lateinit var calDataLst: LimitedArrayList<CalData>
    // 履歴データを表示するときの履歴位置
    private var historyPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cal)

        // アプリ設定を取得
        appConf = (applicationContext as? CalApplication)?.appConf ?: AppConf()

        // 計算データの履歴
        calDataLst = LimitedArrayList(appConf.historyCnt,appConf.historyCnt)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        calPagerAdapter = CalPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = calPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_cal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
        // "設定"フラグメントを表示
            R.id.action_settings -> {
                val dialog = ConfFragment.newInstance()
                dialog.show(supportFragmentManager, FragmentID.ID_SETTINGS.id)
                true
            }
        // "About Me"フラグメントを表示
            R.id.action_about -> {
                val dialog = AboutFragment.newInstance()
                dialog.show(supportFragmentManager, FragmentID.ID_ABOUT.id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // OnHistoryCallback
    // 履歴に計算データを格納
    override fun put(calData: CalData) {
        // 履歴位置をクリア
        historyPos = 0
        // 履歴に計算データを格納
        calDataLst.add(0,calData)

        // 履歴が追加されたことを関連フラグメントに通知
        for ( i in 0 until calPagerAdapter!!.count ) {
            val fragment = calPagerAdapter?.getItem(i) as? OnHistoryCallback
                ?: continue
            fragment.onUpdate(calDataLst)
        }
    }

    // OnHistoryCallback
    // 計算履歴を通知
    override fun onUpdate(calDataLst: LimitedArrayList<CalData>) {}

    // OnHistoryGetCallback
    // 履歴取得1つ前
    override fun getHistoryPrev(): CalData =
        // 履歴をはじめてたどる場合
        if ( historyPos == -1 ) {
            // 履歴がない場合,空データを返す
            if ( calDataLst.size == 0 ) {
                CalData()
            }
            // 履歴がある場合,最後を返す
            else {
                historyPos = 0
                calDataLst[historyPos]
            }
        }
        // 履歴を連続でたどる場合
        else {
            // 履歴番号を１つ増やす
            historyPos++
            // 履歴サイズより大きい場合,履歴サイズ-1に補正
            if ( historyPos >= calDataLst.size ) {
                historyPos = calDataLst.size-1
            }
            calDataLst[historyPos]
        }
    /*
        // 履歴をはじめてたどる場合
        if ( historyPos == -1 ) {
            // 履歴がない場合,空データを返す
            if ( calDataLst.size == 0 ) {
                CalData()
            }
            // 履歴がある場合,最後を返す
            else {
                historyPos = calDataLst.size-1
                calDataLst.last()
            }
        }
        // 履歴を連続でたどる場合
        else {
            // 履歴番号を１つ減らす
            historyPos--
            // 履歴番号が0より下の場合,0に補正
            if ( historyPos < 0 ) {
                historyPos = 0
            }
            calDataLst[historyPos]
        }
*/

    // OnHistoryGetCallback
    // 履歴取得1つ次
    override fun getHistoryNext(): CalData =
        // 履歴をはじめてたどる場合
        if ( historyPos == -1 ) {
            // 空データを返す
            CalData()
        }
        // 履歴を連続でたどる場合
        else {
            // 履歴番号を１つ減らす
            historyPos--
            // 履歴番号が0より下の場合,0に補正
            if ( historyPos < 0 ) {
                historyPos = 0
            }
            calDataLst[historyPos]
        }
    /*
        // 履歴をはじめてたどる場合
        if ( historyPos == -1 ) {
            // 空データを返す
            CalData()
        }
        // 履歴を連続でたどる場合
        else {
            // 履歴番号を１つ増やす
            historyPos++
            // 履歴番号が履歴サイズより大きい場合,履歴サイズ-1に補正
            if ( historyPos >= calDataLst.size ) {
                historyPos = calDataLst.size-1
            }
            calDataLst[historyPos]
        }
        */
}
