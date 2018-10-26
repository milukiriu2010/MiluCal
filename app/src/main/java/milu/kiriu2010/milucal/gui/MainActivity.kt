package milu.kiriu2010.milucal.gui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.id.FragmentID

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if ( savedInstanceState == null ) {
            if ( supportFragmentManager.findFragmentByTag(FragmentID.ID_CAL.id) == null ) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.frameCal,Cal01Fragment.newInstance(),FragmentID.ID_CAL.id)
                    .commit()
            }
        }
    }
}
