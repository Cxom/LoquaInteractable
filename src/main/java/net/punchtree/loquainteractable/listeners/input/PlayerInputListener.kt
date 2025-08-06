package net.punchtree.loquainteractable.listeners.input

import net.punchtree.loquainteractable.splash.SplashScreenManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInputEvent

@Deprecated("USE PACKETS INSTEAD")
class PlayerInputListener(val splashScreenManager: SplashScreenManager) : Listener {

    // TODO replace this when player input processing is fully designed and ready
    @EventHandler
    fun onPlayerInput(event: PlayerInputEvent) {
        splashScreenManager.handlePlayerInput(event)
    }

}