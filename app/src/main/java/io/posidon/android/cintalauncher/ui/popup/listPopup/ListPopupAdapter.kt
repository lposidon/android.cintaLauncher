package io.posidon.android.cintalauncher.ui.popup.listPopup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.cintalauncher.R
import io.posidon.android.cintalauncher.ui.popup.listPopup.viewHolders.ListPopupItemViewHolder
import io.posidon.android.cintalauncher.ui.popup.listPopup.viewHolders.ListPopupSwitchItemViewHolder
import io.posidon.android.cintalauncher.ui.popup.listPopup.viewHolders.ListPopupTitleViewHolder
import io.posidon.android.cintalauncher.ui.popup.listPopup.viewHolders.ListPopupViewHolder

class ListPopupAdapter : RecyclerView.Adapter<ListPopupViewHolder>() {

    override fun getItemViewType(i: Int): Int {
        return when {
            items[i].onToggle != null -> 2
            items[i].isTitle -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPopupViewHolder {
        return when (viewType) {
            1 -> ListPopupTitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_popup_title, parent, false))
            2 -> ListPopupSwitchItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_popup_switch_item, parent, false))
            else -> ListPopupItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_popup_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ListPopupViewHolder, i: Int) {
        holder.onBind(items[i])
    }

    override fun getItemCount() = items.size

    private var items: List<ListPopupItem> = emptyList()

    fun updateItems(items: List<ListPopupItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}
