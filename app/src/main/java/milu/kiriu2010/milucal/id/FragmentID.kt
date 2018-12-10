package milu.kiriu2010.milucal.id

enum class FragmentID(val id: String) {
    // ドロワーメニューを表示するフラグメント
    ID_MENU_DRAWER("fragmentMenuDrawer"),
    // 計算機を表示するフラグメント
    ID_CALCULATOR("fragmentCalculator"),
    // 為替レートを表示するフラグメント
    ID_EXCHANGE_RATE("fragmentExchangeRate"),
    // "設定"を表示するフラグメント
    ID_SETTINGS("fragmentSettings"),
    // "About"を表示するフラグメント
    ID_ABOUT("fragmentAbout"),
    // エラーを表示するフラグメント
    ID_EXCEPTION("fragmentException"),
    // 進捗状況を表示するフラグメント
    ID_PROGRESS("fragmentProgress")

}
