package io.posidon.android.cintalauncher.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import posidon.android.conveniencelib.Device

class SeeThoughView : View {
    constructor(c: Context) : super(c)
    constructor(c: Context, a: AttributeSet?) : this(c, a, 0, 0)
    constructor(c: Context, a: AttributeSet?, da: Int) : this(c, a, da, 0)
    constructor(c: Context, a: AttributeSet?, da: Int, dr: Int) : super(c, a, da, dr)

    var drawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    private val lastScreenLocation = IntArray(2)

    override fun isDirty(): Boolean {
        return super.isDirty() || run {
            val location = IntArray(2)
            getLocationOnScreen(location)
            !lastScreenLocation.contentEquals(location)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val d = drawable
        if (d != null) {
            val w = Device.screenWidth(context)
            val h = Device.screenHeight(context)
            getLocationOnScreen(lastScreenLocation)
            val l = lastScreenLocation[0]
            val t = lastScreenLocation[1]
            d.setBounds(-l, -t, w - l, h - t)
            d.draw(canvas)
        }
    }
}