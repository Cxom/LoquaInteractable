package net.punchtree.loquainteractable

import net.punchtree.util.color.PunchTreeColor
import java.awt.Color
import kotlin.math.min

data object LoquaConstants {

    data object Colors {
        // TODO these are the same as MaterialColors.CONCRETE_<COLOR>, maybe just replace with those, or at least define in terms of those
        //  blue and yellow are off by 1 on a value or two
        val LoquaFlagBlue = PunchTreeColor(45, 47, 144)
        val LoquaFlagWhite = PunchTreeColor(207, 213, 214)
        val LoquaFlagRed = PunchTreeColor(143, 33, 33)
        val LoquaFlagYellow = PunchTreeColor(242, 176, 21)

        val LoquaFlagBlueLight = LoquaFlagBlue.lerpLuminance(1f, 0.5f)
        val LoquaFlagRedLight = LoquaFlagRed.lerpLuminance(1f, 0.5f)
        val LoquaFlagYellowLight = LoquaFlagYellow.lerpLuminance(1f, 0.5f)

        private fun PunchTreeColor.lerpLuminance(endValue: Float, t: Float): PunchTreeColor {
            endValue.coerceIn(0f, 1f)

            // Convert RGB to HSV (using Color.RGBtoHSB)
            val hsv = FloatArray(3)
            Color.RGBtoHSB(javaColor.red, javaColor.green, javaColor.blue, hsv)
            // Convert HSV to HSL
            val hsl = hsvToHsl(hsv[0], hsv[1], hsv[2])
            // Extract current luminance (lightness) and apply interpolation
            val currentLuminance = hsl[2]
            val lerpedLuminance = currentLuminance + t * (endValue - currentLuminance)
            // Set the luminance to the lerped value, keeping hue and saturation intact
            hsl[2] = lerpedLuminance
            // Convert HSL back to HSV
            val finalHsv = hslToHsv(hsl[0], hsl[1], hsl[2])
            return PunchTreeColor(
                // Use Color.getHSBColor to convert HSV back to RGB
                Color.getHSBColor(finalHsv[0], finalHsv[1], finalHsv[2])
            )
        }

        // TODO formalize/relocate color utils (punchtree-util)
        private fun hslToHsv(h: Float, s: Float, l: Float): FloatArray {
            // Calculate the Value (V) in HSV
            val v = l + s * min(l, 1 - l)  // The equivalent of the Value in HSV
            val newS = if (v == 0f || v == 1f) 0f else 2 * (1 - l / v)
            return floatArrayOf(h, newS, v)
        }

        private fun hsvToHsl(h: Float, s: Float, v: Float): FloatArray {
            // Calculate the Lightness (L) in HSL
            val l = v * (1 - s / 2)  // The equivalent of the Lightness in HSL
            val newS = if (l == 0f || l == 1f) 0f else (v - l) / min(l, 1 - l)
            return floatArrayOf(h, newS, l)
        }

    }

}