package io.posidon.android.cintalauncher.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

object ImageLoader {
    const val AUTO = -1

    fun loadNullableBitmap(
        url: String,
        width: Int = AUTO,
        height: Int = AUTO,
        scaleIfSmaller: Boolean = true,
        onFinished: (img: Bitmap?) -> Unit
    ) = thread(name = "Image loading thread") {
        onFinished(loadNullableBitmapOnCurrentThread(url, width, height, scaleIfSmaller))
    }

    fun loadNullableBitmapOnCurrentThread(
        url: String,
        width: Int = AUTO,
        height: Int = AUTO,
        scaleIfSmaller: Boolean = true
    ) : Bitmap? {
        var w = width
        var h = height
        var img: Bitmap? = null
        try {
            val tmp = URL(url).openConnection().getInputStream().use {
                BitmapFactory.decodeStream(it) ?: return null
            }
            when {
                w == AUTO && h == AUTO -> img = tmp
                !scaleIfSmaller && (w > tmp.width || h > tmp.height) && w > tmp.width && h == AUTO || h > tmp.height && w == AUTO -> img = tmp
                else -> {
                    if (w == AUTO) w = h * tmp.width / tmp.height else if (h == AUTO) h = w * tmp.height / tmp.width
                    img = Bitmap.createScaledBitmap(tmp, w, h, true)
                }
            }
        }
        catch (e: IOException) {}
        catch (e: Exception) { e.printStackTrace() }
        catch (e: OutOfMemoryError) {
            img?.recycle()
            img = null
            System.gc()
        }
        return img
    }

    inline fun loadBitmap(
        url: String,
        width: Int = AUTO,
        height: Int = AUTO,
        scaleIfSmaller: Boolean = true,
        crossinline onFinished: (img: Bitmap) -> Unit
    ) = loadNullableBitmap(url, width, height, scaleIfSmaller) {
        if (it != null) {
            onFinished(it)
        }
    }
}