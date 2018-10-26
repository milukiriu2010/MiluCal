package milu.kiriu2010.milucal.gui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class CalPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    private lateinit var cal01Fragment: Cal01Fragment
    private lateinit var cal02Fragment: Cal02Fragment

    // ページ数
    private val pageCnt = 2

    override fun getItem(pos: Int): Fragment {
        return when(pos) {
            0 -> {
                if ( !this::cal02Fragment.isInitialized ) {
                    cal02Fragment = Cal02Fragment.newInstance()
                }
                return cal02Fragment
            }
            1 -> {
                if ( !this::cal01Fragment.isInitialized ) {
                    cal01Fragment = Cal01Fragment.newInstance()
                }
                return cal01Fragment
            }
            else -> {
                if ( !this::cal02Fragment.isInitialized ) {
                    cal02Fragment = Cal02Fragment.newInstance()
                }
                return cal02Fragment
            }
        }
    }

    override fun getCount(): Int = pageCnt
    // override fun getCount(): Int = Int.MAX_VALUE
}