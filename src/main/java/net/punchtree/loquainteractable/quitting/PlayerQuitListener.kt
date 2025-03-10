package net.punchtree.loquainteractable.quitting

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.joining.splash.Cinematic
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val playerInputsManager: PlayerInputsManager) : Listener {

    /* The general philosophy around quit management is as follows:
     *  have just this ONE event listener, so that order of quit processing of
     *   all player state is easy to understand and debug
     *
     *  operate from the perspective of expecting high level systems (e.g. splash screen manager)
     *   to clean up their own use of lower-level systems (e.g. cinematics)
     *  but then verify that cinematics and other global managers are actually cleaned up
     *  *trust, but verify*
     */

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        // get the player safely in case they quit before fully connecting
        val loquaPlayer = LoquaPlayerManager.getSafe(event.player) ?: run {
            LoquaInteractablePlugin.instance.logger.info("Player ${event.player.name} quit without a LoquaPlayer object (before fully connecting??)")
            return
        }

        if (loquaPlayer.isInSplashScreen()) {
            LoquaInteractablePlugin.instance.splashScreenManager.onPlayerQuit(loquaPlayer)
            check(Cinematic.getCinematicFor(loquaPlayer) == null) {
                "Player ${event.player.name} still has a cinematic registered after splash screen clean up!"
                Cinematic.stopCinematic(loquaPlayer)
            }
            return
        }

        check(Cinematic.getCinematicFor(loquaPlayer) == null) {
            "Player ${event.player.name} has a cinematic registered but is not in the splash screen and there are no other cutscenes!"
            Cinematic.stopCinematic(loquaPlayer)
        }

        loquaPlayer.saveInventoryIfNotOutOfBody()
        playerInputsManager.destroyInputs(event.player.uniqueId)
        LoquaPlayerManager.destroyPlayer(event.player)
    }
}
