package io.posidon.android.cintalauncher.ui.feed.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.posidon.android.cintalauncher.R
import io.posidon.android.cintalauncher.color.ColorTheme
import io.posidon.android.cintalauncher.providers.summary.NotificationSummariesProvider
import io.posidon.android.cintalauncher.ui.LauncherActivity
import io.posidon.android.cintalauncher.ui.acrylicBlur
import io.posidon.android.cintalauncher.ui.feed.home.summary.SummaryAdapter
import io.posidon.android.cintalauncher.ui.popup.home.HomeLongPressPopup
import io.posidon.android.cintalauncher.ui.view.SeeThoughView
import io.posidon.android.cintalauncher.util.InvertedRoundRectDrawable
import io.posidon.android.lookerupper.ui.SearchActivity
import posidon.android.conveniencelib.dp
import posidon.android.conveniencelib.getNavigationBarHeight

class HomeViewHolder(
    val scrollIndicator: ImageView,
    parentView: ViewGroup,
    val launcherActivity: LauncherActivity,
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    val weekDay = itemView.findViewById<TextView>(R.id.week_day)!!
    val time = itemView.findViewById<TextView>(R.id.time)!!
    val date = itemView.findViewById<TextView>(R.id.date)!!

    val summaryCard = itemView.findViewById<View>(R.id.summary_card)!!
    val summaryAdapter = SummaryAdapter()
    val summaryRecycler = summaryCard.findViewById<RecyclerView>(R.id.summary_recycler)!!.apply {
        layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)
        adapter = summaryAdapter
    }
    val recentlyOpenedAdapter = RecentlyOpenedItemsAdapter(launcherActivity)
    val recentlyOpenedRecycler = summaryCard.findViewById<RecyclerView>(R.id.recents_recycler)!!.apply {
        layoutManager = GridLayoutManager(itemView.context, 3, RecyclerView.VERTICAL, false)
        adapter = recentlyOpenedAdapter
    }

    val searchCard = itemView.findViewById<CardView>(R.id.search_bar_container)!!.apply {
        setOnClickListener {
            val context = it.context
            context.startActivity(
                Intent(context, SearchActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
    val searchIcon = searchCard.findViewById<ImageView>(R.id.search_bar_icon)!!
    val searchText = searchCard.findViewById<TextView>(R.id.search_bar_text)!!
    val blurBG = searchCard.findViewById<SeeThoughView>(R.id.blur_bg)!!

    val vertical = itemView.findViewById<LinearLayout>(R.id.vertical)!!

    init {
        NotificationSummariesProvider.init(itemView.context) {
            itemView.post(::updateSummary)
        }
        itemView.layoutParams.apply {
            height = parentView.measuredHeight - parentView.paddingBottom - parentView.paddingTop
        }
        val s = itemView.dp(24).toInt()
        vertical.addView(scrollIndicator, 4, LinearLayout.LayoutParams(s, s).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        })
        val r = itemView.context.resources.getDimension(R.dimen.dock_corner_radius)
        itemView.background = InvertedRoundRectDrawable(
            floatArrayOf(0f, 0f, 0f, 0f, r, r, r, r), 0f, 0)
    }

    fun updateSummary() {
        val ns = NotificationSummariesProvider.get()
        if (ns.isEmpty()) {
            summaryRecycler.isVisible = false
        } else {
            summaryRecycler.isVisible = true
            summaryAdapter.updateSummaries(ns)
        }
    }

    fun updateRecents() {
        val recent = launcherActivity.suggestionsManager.getLast3()
        if (recent.isEmpty()) {
            recentlyOpenedRecycler.isVisible = false
        } else {
            recentlyOpenedRecycler.isVisible = true
            recentlyOpenedAdapter.updateItems(recent)
        }
    }

    fun onScroll() {
        blurBG.invalidate()
        summaryAdapter.onScroll()
        recentlyOpenedAdapter.onScroll()
    }
}

@SuppressLint("ClickableViewAccessibility")
fun bindHomeViewHolder(
    holder: HomeViewHolder
) {
    holder.updateSummary()
    holder.updateRecents()
    (holder.itemView.background as InvertedRoundRectDrawable).outerColor = ColorTheme.uiBG
    holder.searchCard.setCardBackgroundColor(ColorTheme.searchBarBG)
    holder.searchIcon.imageTintList = ColorStateList.valueOf(ColorTheme.searchBarFG)
    holder.searchText.setTextColor(ColorTheme.searchBarFG)
    holder.blurBG.drawable = acrylicBlur?.smoothBlur?.let { BitmapDrawable(holder.itemView.resources, it) }
    holder.time.setTextColor(ColorTheme.uiTitle)
    holder.date.setTextColor(ColorTheme.uiDescription)
    holder.weekDay.setTextColor(ColorTheme.uiDescription)
    holder.scrollIndicator.imageTintList = ColorStateList.valueOf(ColorTheme.uiHint)
    var x = 0f
    var y = 0f
    holder.itemView.setOnTouchListener { _, e ->
        val action: Int = e.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                x = e.rawX
                y = e.rawY
            }
        }
        false
    }
    holder.itemView.setOnLongClickListener {
        HomeLongPressPopup.show(it, x, y, holder.launcherActivity.getNavigationBarHeight(), holder.launcherActivity.settings, holder.launcherActivity::reloadColorThemeSync)
        true
    }
}