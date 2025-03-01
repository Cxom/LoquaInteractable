package net.punchtree.loquainteractable.listeners

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val playerInputsManager: PlayerInputsManager) : Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        // get the player safely in case they quit before fully connecting
        LoquaPlayerManager.getSafe(event.player)?.let {
            it.saveInventoryIfNotOutOfBody()
        } ?: run {
            LoquaInteractablePlugin.instance.logger.info("Player ${event.player.name} quit without a LoquaPlayer object (before fully connecting??)")
            return
        }
        playerInputsManager.destroyInputs(event.player.uniqueId)
        LoquaPlayerManager.destroyPlayer(event.player)
    }
}
