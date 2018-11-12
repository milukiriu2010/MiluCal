package milu.kiriu2010.milucal.gui.func

import android.support.v7.app.AppCompatActivity

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
    , OnHistoryCallback {

    // アプリ設定
    private lateinit var appConf: AppConf

    // スワイプする表示されるページを格納したアダプタ
    private var calPagerAdapter: CalPagerAdapter? = null

    // 計算データの履歴
    private lateinit var calDataLst: LimitedArrayList<CalData>


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
        calDataLst.add(calData)
    }
}
