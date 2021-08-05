package io.posidon.android.cintalauncher.ui.feed.home.summary

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.toXfermode
import io.posidon.android.cintalauncher.R
import io.posidon.android.cintalauncher.color.ColorTheme
import io.posidon.android.cintalauncher.data.feed.summary.SummaryItem
import io.posidon.android.cintalauncher.data.feed.summary.media.MediaSummary
import io.posidon.android.cintalauncher.ui.acrylicBlur
import io.posidon.android.cintalauncher.ui.view.SeeThoughView
import posidon.android.conveniencelib.Colors
import posidon.android.conveniencelib.toBitmap
import posidon.android.conveniencelib.vibrate

class MediaSummaryViewHolder(
    itemView: View
) : SummaryViewHolder(itemView) {

    val card = itemView.findViewById<CardView>(R.id.card)!!
    val title = card.findViewById<TextView>(R.id.title)!!
    val description = card.findViewById<TextView>(R.id.description)!!

    val cover = card.findViewById<ImageView>(R.id.cover)!!

    val previous = card.findViewById<ImageView>(R.id.button_previous)!!
    val play = card.findViewById<ImageView>(R.id.button_play)!!
    val next = card.findViewById<ImageView>(R.id.button_next)!!

    val blurBG = card.findViewById<SeeThoughView>(R.id.blur_bg)!!

    override fun onBind(summary: SummaryItem) {
        summary as MediaSummary
        title.text = summary.description
        description.text = summary.subtitle


        val c = summary.cover
        if (c == null)
            cover.setImageDrawable(null)
        else {
            val b = c.toBitmap()
            val paint = Paint().apply {
                this.shader = LinearGradient(
                    0f,
                    0f,
                    b.width.toFloat() / 2f,
                    0f,
                    0,
                    0xff000000.toInt(),
                    Shader.TileMode.CLAMP
                )
                this.xfermode = PorterDuff.Mode.DST_IN.toXfermode()
            }
            val bitmap = Bitmap.createBitmap(b).applyCanvas {
                drawRect(0f, 0f, width.toFloat(), width.toFloat(), paint)
            }
            cover.setImageBitmap(bitmap)
        }


        cover.setOnClickListener(summary.onTap)
        title.setOnClickListener(summary.onTap)
        description.setOnClickListener(summary.onTap)

        val backgroundColor = Colors.blend(summary.color, ColorTheme.appDrawerItemBase, .4f)
        val titleColor = ColorTheme.titleColorForBG(itemView.context, backgroundColor)
        val textColor = ColorTheme.textColorForBG(itemView.context, backgroundColor)

        val titleTintList = ColorStateList.valueOf(titleColor)

        card.setCardBackgroundColor(backgroundColor)
        title.setTextColor(titleColor)
        title.setShadowLayer(title.shadowRadius, title.shadowDx, title.shadowDy, backgroundColor)
        description.setShadowLayer(description.shadowRadius, description.shadowDx, description.shadowDy, backgroundColor)
        description.setTextColor(textColor)
        previous.imageTintList = titleTintList
        play.imageTintList = titleTintList
        next.imageTintList = titleTintList

        blurBG.drawable = acrylicBlur?.fullBlur?.let { BitmapDrawable(itemView.resources, it) }

        play.setImageResource(if (summary.isPlaying()) R.drawable.ic_pause else R.drawable.ic_play)

        previous.setOnClickListener {
            it.context.vibrate(14)
            summary.previous(it)
        }
        next.setOnClickListener {
            it.context.vibrate(14)
            summary.next(it)
        }
        play.setOnClickListener {
            it as ImageView
            it.context.vibrate(14)
            summary.togglePause(it)
        }
    }
}