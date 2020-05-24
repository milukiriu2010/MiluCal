package milu.kiriu2010.milucal.gui.menu

import android.os.Parcel
import android.os.Parcelable

// メニュータイプ
enum class CalDrawerMenuType(val viewType: Int) {
    // メインメニュー
    TYPE_MAIN(0),
    // サブメニュー
    TYPE_SUB(1)
}

enum class CalDrawerMenuID() {
    // 計算
    ID_MENU_SUB_CALCULATOR,
    // 為替レート
    ID_MENU_SUB_EXCHANGE_RATE;
}

// 左に表示するメニュー
data class CalDrawerMenu(
    val menuType: CalDrawerMenuType,
    val menuID: CalDrawerMenuID,
    val menuName: String
)