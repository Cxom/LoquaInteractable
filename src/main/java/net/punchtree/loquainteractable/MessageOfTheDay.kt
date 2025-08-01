package net.punchtree.loquainteractable

import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import net.punchtree.loquainteractable.LoquaInteractablePlugin.Companion.instance
import java.util.*

internal object MessageOfTheDay {
    private fun getMessageOfTheDay(): String =
        requireNotNull(instance.getConfig().getString("Message Of The Day"))
            .replace("&", "§").replace("\\§", "&")

    private fun getSplash(): String {
        val splashes = instance.getConfig().getStringList("Splashes")
        return splashes[Random().nextInt(splashes.size)]
    }

    internal fun getFullServerListingText() = Component.text(getMessageOfTheDay() + "\n" + ChatColor.DARK_GRAY + getSplash())
}
