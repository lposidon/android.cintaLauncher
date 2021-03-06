package io.posidon.android.cintalauncher.ui.pinned.viewHolders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.cintalauncher.providers.color.theme.ColorTheme
import io.posidon.android.cintalauncher.data.items.LauncherItem
import io.posidon.android.cintalauncher.providers.feed.suggestions.SuggestionsManager
import io.posidon.android.cintalauncher.ui.popup.appItem.ItemLongPress

class PinnedViewHolder(
    val icon: ImageView
) : RecyclerView.ViewHolder(icon) {
}

fun bindPinnedViewHolder(
    holder: PinnedViewHolder,
    item: LauncherItem,
    navbarHeight: Int,
    onDragStart: (view: View) -> Unit = {},
) {
    holder.icon.setImageDrawable(item.icon)

    holder.itemView.setOnClickListener {
        SuggestionsManager.onItemOpened(it.context, item)
        item.open(it.context.applicationContext, it)
    }
    holder.itemView.setOnLongClickListener {
        val backgroundColor = ColorTheme.tintWithColor(ColorTheme.cardBG, item.getColor())
        ItemLongPress.onItemLongPress(
            it,
            backgroundColor,
            ColorTheme.titleColorForBG(backgroundColor),
            item,
            navbarHeight,
        )
        onDragStart(it)
        true
    }
}