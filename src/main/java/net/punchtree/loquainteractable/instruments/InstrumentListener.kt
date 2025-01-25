package net.punchtree.loquainteractable.instruments

import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.instruments.Instruments.isInstrument
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDismountEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class InstrumentListener(val playerInputsManager: PlayerInputsManager) : Listener {

    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        playerInputsManager.getInputsForPlayer(event.player)?.updateSwapHands(event.player)

        // TODO we need to resolve the conflict between routing the swap event to the player inputs
        //  and the ability/fact of the event being cancelled
        //  detecting the input and deciding whether or not to let the swap proceed are two different things
        if (InstrumentManager.isPlayingInstrument(event.player)) {
            event.isCancelled = true
        }
    }

//    @EventHandler
//    fun onInput(event: PlayerInputEvent) {
//        playerInputsManager.getInputsForPlayer(event.player)?.updateVehicleInput(event.player, event.input)
//    }

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
        if (InstrumentManager.isPlayingInstrument(event.player)) {
            event.isCancelled = true
        }
    }

}
