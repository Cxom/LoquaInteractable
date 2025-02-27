package net.punchtree.loquainteractable.player

import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.craftPlayer(): CraftPlayer {
    return when (this) {
        is CraftPlayer -> this
        is PlayerDecorator -> this.craftPlayer()
        else -> throw IllegalStateException("Player is not a CraftPlayer!")
    }
}