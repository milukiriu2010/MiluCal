package milu.kiriu2010.gui.drawer

import android.content.res.Configuration
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import milu.kiriu2010.milucal.R

open class DrawerActivity : AppCompatActivity() {

    // ナビゲーションドロワーの状態操作用オブジェクト
    protected var drawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // アクティビティの生成が終わった後に呼ばれる
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Log.d( javaClass.simpleName, "========================" )
        Log.d( javaClass.simpleName, "onPostCreate" )
        // ドロワーのトグルの状態を同期する
        drawerToggle?.syncState()
    }

    // 画面が回転するなど、状態が変化したときに呼ばれる
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 状態の変化をドロワーに伝える
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    // アクションバーのアイコンがタップされると呼ばれる
    // このときドロワートグルにイベントを伝えることで
    // ナビゲーションドロワーを開閉する
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // ドロワーに伝える
        if ( drawerToggle?.onOptionsItemSelected(item) == true ) {
            return true
        }
        else {
            return super.onOptionsItemSelected(item)
        }
    }

    // ドロワーレイアウトを表示する
    protected fun setupDrawLayout() {
        // レイアウトからドロワーを探す
        //   Portrait  => ドロワーあり
        //   Landscape => ドロワーなし
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)

        // レイアウト中にドロワーがある場合設定を行う
        if ( drawerLayout != null ) {
            setupDrawer(drawerLayout)
        }
    }

    // ナビゲーションドロワーを開閉するためのアイコンをアクションバーに配置する
    private fun setupDrawer( drawer: androidx.drawerlayout.widget.DrawerLayout) {
        val toggle = ActionBarDrawerToggle( this, drawer, R.string.app_name, R.string.app_name )
        // ドロワーのトグルを有効にする
        toggle.isDrawerIndicatorEnabled = true
        // 開いたり閉じたりのコールバックを設定する
        drawer.addDrawerListener(toggle)

        drawerToggle = toggle

        // アクションバーの設定を行う
        supportActionBar?.apply {
            // ドロワー用のアイコンを表示
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }
}
