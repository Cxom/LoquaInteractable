package net.punchtree.loquainteractable.player

import net.punchtree.loquainteractable.player.LoquaPlayerManager.getLoquaPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

object LoquaPlayerManager : Listener {

    /**
     * Player objects needed to be created when a player joins
     * and destroyed when a player leaves
     */

    private val playersToLoquaPlayers = mutableMapOf<UUID, LoquaPlayer>()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val loquaPlayer = LoquaPlayer(player)
        playersToLoquaPlayers[player.uniqueId] = loquaPlayer
    }

    @EventHandler
    fun onLeave(event: org.bukkit.event.player.PlayerQuitEvent) {
        val player = event.player
        playersToLoquaPlayers.remove(player.uniqueId)
    }

    fun getLoquaPlayer(player: Player): LoquaPlayer? {
        return getLoquaPlayer(player.uniqueId)
    }

    fun getLoquaPlayer(uuid: UUID): LoquaPlayer? {
        return playersToLoquaPlayers[uuid]
    }
}

fun Player.toLoquaPlayer(): LoquaPlayer? {
    return getLoquaPlayer(this.uniqueId)
}