package io.posidon.android.cintalauncher.ui.settings.iconPackPicker.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.cintalauncher.R
import io.posidon.android.cintalauncher.providers.color.theme.ColorTheme

class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val text = itemView.findViewById<TextView>(R.id.text)

    fun bind(string: String) {
        text.text = string
        text.setTextColor(ColorTheme.adjustColorForContrast(ColorTheme.uiBG, ColorTheme.accentColor))
    }
}