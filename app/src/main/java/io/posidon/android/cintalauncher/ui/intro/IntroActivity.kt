package io.posidon.android.cintalauncher.ui.intro

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import io.posidon.android.cintalauncher.R
import io.posidon.android.cintalauncher.providers.color.ColorThemeOptions
import io.posidon.android.cintalauncher.providers.color.theme.ColorTheme
import java.util.*

class IntroActivity : FragmentActivity() {

    val stack = LinkedList<FragmentWithNext>().apply {
        push(SplashFragment())
    }

    fun setFragment(fragment: FragmentWithNext) {
        stack.push(fragment)
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.intro_fragment_slide_in_right, R.anim.intro_fragment_slide_out_left)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun next(v: View) {
        stack.peek()!!.next(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, stack.peek()!!)
                .commit()
        }

        updateColorTheme()
    }

    override fun onResume() {
        super.onResume()
        (stack.peek() as? PermissionsFragment)?.updatePermissionStatus()
    }

    override fun onBackPressed() {
        stack.pop()
        if (stack.isEmpty())
            super.onBackPressed()
        else supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.intro_fragment_slide_in_left, R.anim.intro_fragment_slide_out_right)
            .replace(R.id.fragment_container, stack.peek()!!)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            (stack.peek() as? PermissionsFragment)?.updatePermissionStatus()
        }
    }

    fun updateColorTheme() {
        findViewById<ImageView>(R.id.button_next)!!.run {
            backgroundTintList = ColorStateList.valueOf(ColorTheme.buttonColorCallToAction)
            imageTintList = ColorStateList.valueOf(ColorTheme.titleColorForBG(ColorTheme.buttonColorCallToAction))
        }
        window.decorView.setBackgroundColor(ColorTheme.uiBG)
    }
}