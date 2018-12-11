package milu.kiriu2010.milucal.gui.exrate

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import milu.kiriu2010.gui.common.ExceptionFragment
import milu.kiriu2010.gui.drawer.DrawerActivity
import milu.kiriu2010.gui.exp.OnRetryListener
import milu.kiriu2010.gui.progress.ProgressFragment
import milu.kiriu2010.loader.v2.AsyncResultOK
import milu.kiriu2010.milucal.CalApplication
import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateJson
import milu.kiriu2010.milucal.gui.menu.CalDrawerMenuFragment
import milu.kiriu2010.milucal.id.FragmentID
import milu.kiriu2010.milucal.id.LoaderID

class ExchangeRateActivity : DrawerActivity()
    , LoaderManager.LoaderCallbacks<AsyncResultOK<ExRateJson>>
    , OnRetryListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_rate)

        // ドロワーレイアウトを表示する
        setupDrawLayout()

        // "為替レートを取得するローダ"の初期化と開始
        LoaderManager.getInstance<FragmentActivity>(this).initLoader(LoaderID.ID_EXCHANGE_RATE.id, null, this)

        // ドロワーメニューを設定
        if (savedInstanceState == null) {
            if (supportFragmentManager.findFragmentByTag(FragmentID.ID_MENU_DRAWER.id) == null) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.frameMenuDrawer, CalDrawerMenuFragment.newInstance(), FragmentID.ID_MENU_DRAWER.id)
                    .commit()
            }
        }
    }

    // LoaderManager.LoaderCallbacks
    // ----------------------------------------------------
    // 非同期処理開始
    // ----------------------------------------------------
    // ローダが要求されたときによばれる
    // 非同期処理を行うLoaderを生成する
    // getLoaderManager().initLoaderで一回のみ呼び出される
    // ----------------------------------------------------
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<AsyncResultOK<ExRateJson>> {
        return when (id) {
            LoaderID.ID_EXCHANGE_RATE.id -> {
                // 進捗状況を表示するフラグメントを追加
                if (supportFragmentManager.findFragmentByTag(FragmentID.ID_PROGRESS.id) == null) {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.frameExchangeRate, ProgressFragment.newInstance(), FragmentID.ID_PROGRESS.id)
                        .commit()
                }
                // 為替レートを取得するローダを起動
                ExchangeRateLoader(this, application as CalApplication)
            }
            else -> throw RuntimeException("no supported loader for this id[$id]")
        }
    }

    // LoaderManager.LoaderCallbacks
    // ---------------------------------------------------
    // 非同期処理終了
    // ---------------------------------------------------
    override fun onLoadFinished(loader: Loader<AsyncResultOK<ExRateJson>>, data: AsyncResultOK<ExRateJson>?) {
        if (data == null) return
        if (loader.id != LoaderID.ID_EXCHANGE_RATE.id) return

        // 進捗状況を表示するフラグメントを削除
        val fragmentProgress = supportFragmentManager.findFragmentByTag(FragmentID.ID_PROGRESS.id)
        if ( fragmentProgress != null ) {
            supportFragmentManager.beginTransaction()
                .remove(fragmentProgress)
                .commit()
        }

        // --------------------------------------
        // 為替レートの結果を表示
        // --------------------------------------
        // データ取得OK
        // --------------------------------------
        if (data.dataOK != null) {
            val exRateData = data.dataOK
            Log.d(javaClass.simpleName, "date:{${exRateData?.date}}")
            // 為替レートの結果を表示するフラグメントを追加
            if (supportFragmentManager.findFragmentByTag(FragmentID.ID_EXCHANGE_RATE.id) == null) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.frameExchangeRate, ExchangeRateFragment.newInstance(exRateData!!), FragmentID.ID_EXCHANGE_RATE.id)
                    .commit()
            }
        }
        // --------------------------------------
        // 通信エラー
        // --------------------------------------
        else {
            // エラーを表示するフラグメントを追加
            if (supportFragmentManager.findFragmentByTag(FragmentID.ID_EXCEPTION.id) == null) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.frameExchangeRate, ExceptionFragment.newInstance("Network Error",data.exception!!), FragmentID.ID_EXCEPTION.id)
                    .commit()
            }
        }
    }

    // LoaderManager.LoaderCallbacks
    // ローダがリセットされたときに呼ばれる
    override fun onLoaderReset(loader: Loader<AsyncResultOK<ExRateJson>>) {
    }

    // OnRetryListener
    override fun onRetry(id: String) {
        Log.d(javaClass.simpleName, "onRetry[$id]")

        // "為替レートを取得するロード"の再初期化と開始
        LoaderManager.getInstance<FragmentActivity>(this).restartLoader(LoaderID.ID_EXCHANGE_RATE.id, null, this)
    }
}
