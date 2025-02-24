package net.punchtree.loquainteractable.listeners

import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val playerInputsManager: PlayerInputsManager) : Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        playerInputsManager.destroyInputs(event.player.uniqueId)
        LoquaPlayerManager.destroyPlayer(event.player)
    }
}
