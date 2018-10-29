package milu.kiriu2010.milucal.gui

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.milucal.R
import kotlinx.android.synthetic.main.activity_cal.*
import kotlinx.android.synthetic.main.fragment_cal02.view.*
import milu.kiriu2010.milucal.gui.misc.AboutFragment
import milu.kiriu2010.milucal.gui.misc.ConfFragment
import milu.kiriu2010.milucal.id.FragmentID

class CalActivity : AppCompatActivity() {

    // スワイプする表示されるページを格納したアダプタ
    private var calPagerAdapter: CalPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cal)

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
}
