package io.posidon.android.cintalauncher.providers.color.theme

import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.core.graphics.luminance
import io.posidon.android.cintalauncher.providers.color.pallete.ColorPalette
import io.posidon.android.cintalauncher.providers.color.theme.ColorTheme.Companion.hueTintClosest
import io.posidon.android.cintalauncher.providers.color.theme.ColorTheme.Companion.labClosestVibrant

class DarkColorTheme(
    val palette: ColorPalette,
) : ColorTheme {

    override val accentColor = palette.primary

    override val uiBG = palette.neutralVeryDark
    override val uiTitle = palette.neutralVeryLight
    override val uiDescription = palette.neutralLight
    override val uiHint = palette.neutralMedium

    override val cardBG = palette.neutralDark
    override val cardTitle = palette.neutralVeryLight
    override val cardDescription = palette.neutralLight
    override val cardHint = palette.neutralMedium

    override val buttonColor = palette.neutralMedium
    override val buttonColorCallToAction = palette.secondary

    override val searchBarBG = palette.neutralDark
    override val searchBarFG = palette.neutralLight

    override fun adjustColorForContrast(base: Int, tint: Int): Int {
        return if (base.luminance > .7f) {
            val lab = DoubleArray(3)
            ColorUtils.colorToLAB(tint, lab)
            lab[0] = lab[0].coerceAtMost(20.0)
            ColorUtils.LABToColor(lab[0], lab[1], lab[2])
        } else {
            val lab = DoubleArray(3)
            ColorUtils.colorToLAB(tint, lab)
            lab[0] = 100.0
            lab[1] *= .75
            lab[2] *= .75
            ColorUtils.LABToColor(lab[0], lab[1], lab[2])
        }
    }

    override fun tileColor(iconBackgroundColor: Int) = when {
        iconBackgroundColor == 0 -> palette.neutralMedium
        iconBackgroundColor.alpha == 0 -> labClosestVibrant(iconBackgroundColor, arrayOf(
            palette.neutralVeryDark,
            palette.neutralDark,
            palette.neutralMedium,
            palette.neutralLight,
            palette.neutralVeryLight,
            palette.primary,
            palette.secondary,
            ColorPalette.wallColor,
        ))
        else -> hueTintClosest(iconBackgroundColor, arrayOf(
            palette.neutralVeryDark,
            palette.neutralDark,
            palette.neutralMedium,
            palette.neutralLight,
            palette.neutralVeryLight,
            palette.primary,
            palette.secondary,
            ColorPalette.wallColor,
        ))
    }
}