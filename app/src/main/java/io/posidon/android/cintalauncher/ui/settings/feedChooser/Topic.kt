package io.posidon.android.cintalauncher.ui.settings.feedChooser

import android.content.Context
import org.json.JSONArray
import io.posidon.android.conveniencelib.loadRaw
import io.posidon.android.rsslib.RssSource

class Topic (
    val context: Context,
    val name: String,
    val id: Int
) {

    val sources = context.loadRaw(id) {
        val array = JSONArray(it.readText())
        try {
            Array(array.length()) {
                val obj = array.getJSONObject(it)
                RssSource(
                    obj.getString("name"),
                    obj.getString("url"), "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            arrayOf()
        }
    }

    inline operator fun get(i: Int) = sources[i]
}