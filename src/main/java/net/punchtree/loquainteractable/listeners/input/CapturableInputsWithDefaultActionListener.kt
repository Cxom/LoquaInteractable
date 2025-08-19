package net.punchtree.loquainteractable.listeners.input

import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.outofbody.instruments.InstrumentPlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDismountEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class CapturableInputsWithDefaultActionListener(val playerInputsManager: PlayerInputsManager) : Listener {

    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        playerInputsManager[event.player].updateSwapHands(event.player)

        // TODO we need to resolve the conflict between routing the swap event to the player inputs
        //  and the ability/fact of the event being cancelled
        //  detecting the input and deciding whether or not to let the swap proceed are two different things
        //  Update: we still want to remove the coupling to Instruments, it should be handled generically for OutOfBody here, but that's part of input processing refactor
        if (LoquaPlayerManager[event.player].outOfBodyState is InstrumentPlayer) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDismount(event: EntityDismountEvent) {
        val rider = event.entity
        if (rider !is Player) return
        if (event.dismounted.hasMetadata("instrument")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        // TODO see above
        if (LoquaPlayerManager[event.player].outOfBodyState is InstrumentPlayer) {
            event.isCancelled = true
        }
    }

}