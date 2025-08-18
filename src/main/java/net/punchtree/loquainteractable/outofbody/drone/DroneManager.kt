package net.punchtree.loquainteractable.outofbody.drone

import net.punchtree.loquainteractable.player.LoquaPlayer
import org.bukkit.entity.Player
import java.util.*

object DroneManager {

    private val playersFlyingDrones = mutableMapOf<UUID, DroneOperator>()

    fun isPlayerFlyingDrone(player: Player): Boolean {
        return playersFlyingDrones.containsKey(player.uniqueId)
    }

    fun startFlyingDrone(player: LoquaPlayer) {

        player.enterOutOfBodyState(DroneOperator(player, Drone()))

//        stopFlyingDrone(player)
//        playersFlyingDrones[player.uniqueId] = DroneOperator(player, Drone())
    }

    fun stopFlyingDrone(player: LoquaPlayer) {
        playersFlyingDrones.remove(player.uniqueId)?.stopFlying()
    }

    fun onDisable() {
        playersFlyingDrones.values.forEach { it.stopFlying() }
        playersFlyingDrones.clear()
    }

}