package net.punchtree.loquainteractable.listeners

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.instruments.InstrumentManager
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.loquainteractable.splash.Cinematic
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
        val player = event.player
        val loquaPlayer = LoquaPlayerManager.getSafe(player) ?: run {
            LoquaInteractablePlugin.Companion.instance.logger.info("Player ${player.name} quit without a LoquaPlayer object (before fully connecting??)")
            return
        }

        // TODO call some sort of loqua player state validator
        //  OR BETTER YET make these states impossible
        //  check things like: not in splash screen and also in character select at same time, extended to other cutscenes, etc
        if (loquaPlayer.isInSplashScreen()) {
            LoquaInteractablePlugin.Companion.instance.splashScreenManager.handlePlayerQuit(loquaPlayer)
            check(Cinematic.Companion.getCinematicFor(loquaPlayer) == null) {
                "Player ${player.name} still has a cinematic registered after splash screen clean up!"
                Cinematic.Companion.stopCinematic(loquaPlayer)
            }
            return
        } else if (loquaPlayer.isInCharacterSelect()) {
            LoquaInteractablePlugin.Companion.instance.characterSelectManager.handlePlayerQuit(loquaPlayer)
            return
        }

        check(Cinematic.Companion.getCinematicFor(loquaPlayer) == null) {
            "Player ${player.name} has a cinematic registered but is not in the splash screen and there are no other cutscenes!"
            Cinematic.Companion.stopCinematic(loquaPlayer)
        }

        // TODO is this necessary? Is instrument cleanup handled anywhere else?
        //  moreover the oversight of having not implemented this in the first place highlights the need to
        //  really carefully think through how to close out all the different possible alternate input situations
        //  a player may be in the midst of when they quit
        InstrumentManager.stopPlayerPlaying(player)

        // Similarly, is metadata wand even properly placed? It's a flippin' build tool, TODO put it in a different plugin
        MetadataEditingSessionManager.getInstance().handlePlayerQuit(player)

        loquaPlayer.saveInventoryIfNotOutOfBody()
        playerInputsManager.destroyInputs(player.uniqueId)
        LoquaPlayerManager.destroyPlayer(player)
    }
}