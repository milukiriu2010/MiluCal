package milu.kiriu2010.milucal.gui.menu


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.gui.calculator.MainActivity
import milu.kiriu2010.milucal.gui.exrate.ExchangeRateActivity

class CalDrawerMenuFragment : Fragment() {

    // メニューを表示するリサイクラービュー
    private lateinit var recyclerViewMenu: RecyclerView

    // メニューを表示するためのアダプタ
    private lateinit var adapter: CalDrawerMenuAdapter

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
        val view = inflater.inflate(R.layout.fragment_cal_drawer_menu, container, false)
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu)

        // コンテキストのnullチェック
        val ctx = context ?: return view

        // メニューを縦方向に並べて表示
        val layoutManager = LinearLayoutManager( ctx, LinearLayoutManager.VERTICAL, false )
        recyclerViewMenu.layoutManager = layoutManager

        // メニューを表示するためのアダプタ
        adapter = CalDrawerMenuAdapter( ctx, loadMenuLst() ) { calDrawerMenu ->
            // メニューがタップされたらコールバックを呼ぶ
            onItemClick(calDrawerMenu)
        }
        recyclerViewMenu.adapter = adapter

        // 区切り線を入れる
        val itemDecoration = DividerItemDecoration(ctx,DividerItemDecoration.VERTICAL)
        recyclerViewMenu.addItemDecoration(itemDecoration)

        return view
    }

    // メニュー一覧を作成
    private fun loadMenuLst(): MutableList<CalDrawerMenu> {
        val calDrawerMenuLst = mutableListOf<CalDrawerMenu>()

        calDrawerMenuLst.add(CalDrawerMenu(CalDrawerMenuType.TYPE_SUB,CalDrawerMenuID.ID_MENU_SUB_CALCULATOR,resources.getString(R.string.menu_calculator)))
        calDrawerMenuLst.add(CalDrawerMenu(CalDrawerMenuType.TYPE_SUB,CalDrawerMenuID.ID_MENU_SUB_EXCHANGE_RATE,resources.getString(R.string.menu_exchange_rate)))

        return calDrawerMenuLst
    }

    // メニューをクリックしたときに呼び出されるコールバック
    private fun onItemClick( calDrawerMenu: CalDrawerMenu ) {
        // レイアウトからドロワーを探す
        //   Portrait  => ドロワーあり
        //   Landscape => ドロワーなし
        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawerLayout)

        // タップ時にドロワーを閉じる
        if ( drawerLayout != null ) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // 対応するメニューの画面へ遷移する
        val intent = when (calDrawerMenu.menuID) {
            // "計算機"をクリック
            CalDrawerMenuID.ID_MENU_SUB_CALCULATOR -> Intent(activity, MainActivity::class.java)
            // "為替レート"をクリック
            CalDrawerMenuID.ID_MENU_SUB_EXCHANGE_RATE -> Intent(activity,ExchangeRateActivity::class.java)
            else -> return
        }

        startActivity(intent)
        activity?.finish()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CalDrawerMenuFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
