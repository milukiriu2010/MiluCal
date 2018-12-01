package milu.kiriu2010.milucal.gui.exrate

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import milu.kiriu2010.gui.drawer.DrawerActivity
import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.gui.menu.CalDrawerMenuFragment
import milu.kiriu2010.milucal.id.FragmentID

class ExchangeRateActivity : DrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_rate)

        // ドロワーレイアウトを表示する
        setupDrawLayout()

        // ドロワーメニューを設定
        if ( savedInstanceState == null ) {
            if ( supportFragmentManager.findFragmentByTag(FragmentID.ID_MENU_DRAWER.id) == null ) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.frameMenuDrawer, CalDrawerMenuFragment.newInstance(), FragmentID.ID_MENU_DRAWER.id)
                    .commit()
            }
        }
    }
}
