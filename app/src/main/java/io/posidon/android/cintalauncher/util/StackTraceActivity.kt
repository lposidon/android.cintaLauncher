package io.posidon.android.cintalauncher.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.widget.TextView
import androidx.core.app.ShareCompat
import androidx.fragment.app.FragmentActivity
import io.posidon.android.cintalauncher.BuildConfig
import io.posidon.android.cintalauncher.R
import io.posidon.android.cintalauncher.ui.color.ColorTheme
import io.posidon.android.cintalauncher.ui.color.DefaultColorTheme
import kotlin.system.exitProcess

class StackTraceActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_stack_trace)

            val title = findViewById<TextView>(R.id.title)
            val stackTrace = findViewById<TextView>(R.id.stack_trace)
            val send = findViewById<TextView>(R.id.send)

            val t = intent.extras!!["throwable"] as Throwable

            val str = buildString {
                appendLine(t.toString())
                appendLine()
                appendLine("Device info:")
                appendLine("    api: " + Build.VERSION.SDK_INT)
                appendLine("    brand: " + Build.BRAND)
                appendLine("    model: " + Build.MODEL)
                appendLine("    ram: " + run {
                    val memInfo = ActivityManager.MemoryInfo()
                    getSystemService(ActivityManager::class.java).getMemoryInfo(memInfo)
                    formatByteAmount(memInfo.totalMem)
                })
                appendLine("Version: " + BuildConfig.VERSION_NAME + " (code: " + BuildConfig.VERSION_CODE + ')')
                appendLine()
                for (tr in t.stackTrace)
                    appendLine().append(format(tr)).appendLine()
                for (throwable in t.suppressed)
                    for (tr in throwable.stackTrace)
                        appendLine().append(format(tr)).appendLine()
                t.cause?.let {
                    for (tr in it.stackTrace)
                        appendLine().append(format(tr)).appendLine()
                }
            }

            stackTrace.text = str

            send.setOnClickListener {
                ShareCompat.IntentBuilder(this)
                    .setType("text/plain")
                    .setText(str)
                    .setSubject("cinta launcher: crash log")
                    .addEmailTo("it@posidon.io")
                    .startChooser()
            }

            try {
                val colorTheme = if (ColorTheme.isInitialized) ColorTheme else DefaultColorTheme(this)
                window.decorView.setBackgroundColor(colorTheme.feedBG)
                send.backgroundTintList = ColorStateList.valueOf(colorTheme.appDrawerItemBase)
                send.setTextColor(colorTheme.titleColorForBG(this, colorTheme.appDrawerItemBase))
                title.setTextColor(colorTheme.titleColorForBG(this, colorTheme.feedBG))
                stackTrace.setTextColor(colorTheme.textColorForBG(this, colorTheme.feedBG))
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun formatByteAmount(bytes: Long): String {
        return when {
            bytes < 1000 -> "$bytes B"
            bytes < 1000_000 -> "${bytes / 1000} KB"
            bytes < 1000_000_000 -> "${bytes / 1000_000} MB"
            else /* bytes < 1000_000_000_000 */ -> "${bytes / 1000_000_000} GB"
        }
    }

    private fun format(e: StackTraceElement) = buildString {
        if (e.isNativeMethod) {
            append("(Native Method)")
        } else if (e.fileName != null) {
            if (e.lineNumber >= 0) {
                append("at ")
                append(e.fileName)
                append(" [")
                append(e.lineNumber)
                append("]")
            } else {
                append("at ")
                append(e.fileName)
            }
        } else {
            if (e.lineNumber >= 0) {
                append("(Unknown Source:")
                append(e.lineNumber)
                append(")")
            } else {
                append("(Unknown Source)")
            }
        }
        appendLine()
        append(e.className)
        append(".")
        append(e.methodName)
    }

    companion object {
        fun init(context: Context) {
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                try {
                    context.startActivity(Intent(context, StackTraceActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("throwable", throwable))
                    Process.killProcess(Process.myPid())
                    exitProcess(0)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
}