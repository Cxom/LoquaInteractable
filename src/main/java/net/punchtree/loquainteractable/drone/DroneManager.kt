package net.punchtree.loquainteractable.drone

import org.bukkit.entity.Player
import java.util.*

object DroneManager {

    private val playersFlyingDrones = mutableMapOf<UUID, DroneOperator>()

    fun isPlayerFlyingDrone(player: Player): Boolean {
        return playersFlyingDrones.containsKey(player.uniqueId)
    }

    fun startFlyingDrone(player: Player) {
        stopFlyingDrone(player)
        playersFlyingDrones[player.uniqueId] = DroneOperator(player, Drone())
    }

    fun stopFlyingDrone(player: Player) {
        playersFlyingDrones.remove(player.uniqueId)?.stopFlying()
    }

    fun onDisable() {
        playersFlyingDrones.values.forEach { it.stopFlying() }
        playersFlyingDrones.clear()
    }

}