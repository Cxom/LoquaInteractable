package net.punchtree.loquainteractable.displayutil

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.apache.commons.lang3.text.WordUtils
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.util.EulerAngle

object PrintingObjectUtils {
    @JvmStatic
	fun formatEulerAngle(euler: EulerAngle): String {
        return String.format("[%.5f, %.5f, %.5f]", euler.getX(), euler.getY(), euler.getZ())
    }

    @JvmStatic
	fun formatLocation(loc: Location): String {
        return String.format("%s[%.5f %.5f %.5f]", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ())
    }

    @JvmStatic
	fun formatBlock(block: Block): String {
        return String.format("%s:%d,%d,%d", block.getWorld().getName(), block.getX(), block.getY(), block.getZ())
    }

    @JvmStatic
	fun formatMaterial(material: Material): String {
        return WordUtils.capitalize(material.name.replace('_', ' '))
    }
}

// TODO rename this function to reflect that it is not a string
// TODO minimessage color (USE player::sendRichMessage)?
internal fun Location.toSimpleString(decimalPrecision: Int = 2): Component {
    /** constrained decimal precision */
    val cdp = decimalPrecision.coerceIn(0, 10)
    val text = "[x:%.${cdp}f y:%.${cdp}f z:%.${cdp}f yaw:%.${cdp}f pitch:%.${cdp}f]".format(x, y, z, yaw, pitch)
    val rawNumbers = "%.${cdp}f %.${cdp}f %.${cdp}f  %.${cdp}f %.${cdp}f".format(x, y, z, yaw, pitch)
    return text(text).insertion(rawNumbers)
}
