package milu.kiriu2010.milucal.gui.menu

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import milu.kiriu2010.milucal.R

class CalDrawerMenuAdapter(
    context: Context,
    private val calDrawerMenuLst: MutableList<CalDrawerMenu> = mutableListOf(),
    private val onItemClick: (CalDrawerMenu) -> Unit )
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val viewHolder: RecyclerView.ViewHolder
        when (viewType) {
            CalDrawerMenuType.TYPE_SUB.viewType -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.list_row_cal_drawer_menu_sub, parent,false)
                viewHolder = CalDrawerMenuViewHolderSub(view)
                // セルをクリックしたら
                // 当該セルの"CalDrawerMenu"をコールバックに渡す
                view.setOnClickListener {
                    val pos = viewHolder.adapterPosition
                    val calDrawerMenu = calDrawerMenuLst[pos]
                    onItemClick(calDrawerMenu)
                }
            }
            else -> {
                throw RuntimeException("No match for $viewType")
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int = calDrawerMenuLst.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, pos: Int) {
        val calDrawerMenu = calDrawerMenuLst[pos]
        when (viewHolder) {
            is CalDrawerMenuViewHolderSub -> {
                // メニュー名
                viewHolder.textViewMenuSub.text = calDrawerMenu.menuName
            }
        }
    }

    // セルを表示するときのXMLリソースを切り替えるために
    // ビュータイプを返す
    override fun getItemViewType(pos: Int): Int {
        //return super.getItemViewType(position)
        val calDrawerMenu = calDrawerMenuLst[pos]
        return calDrawerMenu.menuType.viewType
    }

    // サブメニュー
    class CalDrawerMenuViewHolderSub(view: View): RecyclerView.ViewHolder(view) {
        // メニュー名
        val textViewMenuSub = view.findViewById<TextView>(R.id.textViewMenuSub)
    }
}
