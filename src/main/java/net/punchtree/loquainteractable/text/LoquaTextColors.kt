package net.punchtree.loquainteractable.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.punchtree.util.color.PunchTreeColor

// TODO propagate use of this to the entire project
object LoquaTextColors {
    val SUCCESS = NamedTextColor.GREEN
    val FAILURE = NamedTextColor.RED
    val WARNING = NamedTextColor.GOLD
    val INFO = PunchTreeColor(200, 200, 200)
    val DEBUG = NamedTextColor.GRAY

    // TODO evaluate if we should propagate this instead
    fun success(message: String): Component {
        return (text(message)).color(SUCCESS)
    }

    fun warning(message: String): Component {
        return (text(message)).color(WARNING)
    }

    fun failure(message: String): Component {
        return (text(message)).color(FAILURE)
    }
}

