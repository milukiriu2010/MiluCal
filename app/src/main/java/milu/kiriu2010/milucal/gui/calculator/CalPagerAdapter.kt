package milu.kiriu2010.milucal.gui.calculator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class CalPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    private lateinit var cal01Fragment: Cal01Fragment
    private lateinit var cal02Fragment: Cal02Fragment
    private lateinit var calHistory01Fragment: CalHistory01Fragment

    // ページ数
    private val pageCnt = 2

    override fun getItem(pos: Int): Fragment {
        return when(pos) {
            0 -> {
                if ( !this::cal02Fragment.isInitialized ) {
                    cal02Fragment = Cal02Fragment.newInstance()
                }
                cal02Fragment
            }
            1 -> {
                if ( !this::calHistory01Fragment.isInitialized ) {
                    calHistory01Fragment = CalHistory01Fragment.newInstance()
                }
                calHistory01Fragment
            }
            /*
            1 -> {
                if ( !this::cal01Fragment.isInitialized ) {
                    cal01Fragment = Cal01Fragment.newInstance()
                }
                cal01Fragment
            }
            */
            else -> {
                if ( !this::cal02Fragment.isInitialized ) {
                    cal02Fragment = Cal02Fragment.newInstance()
                }
                cal02Fragment
            }
        }
    }

    override fun getCount(): Int = pageCnt
    // override fun getCount(): Int = Int.MAX_VALUE
}