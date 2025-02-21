package net.punchtree.loquainteractable.player

import net.punchtree.loquainteractable.player.LoquaPlayerManager.get
import org.bukkit.Bukkit
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

    // TODO HANDLE JOIN AND LEAVING IN EXACTLY ONE PLACE - as a readable entrypoint
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

    fun getSafe(player: Player): LoquaPlayer? {
        return playersToLoquaPlayers[player.uniqueId]
    }

    operator fun get(player: Player): LoquaPlayer {
        return get(player.uniqueId)
    }

    operator fun get(uuid: UUID): LoquaPlayer {
        return checkNotNull(playersToLoquaPlayers[uuid]) {
            val playerName = Bukkit.getOfflinePlayer(uuid).name
            "Tried fetching LoquaPlayer for player [$playerName:${uuid}] and it didn't work! Are they not online?"
        }
    }
}

fun Player.toLoquaPlayer(): LoquaPlayer? {
    return LoquaPlayerManager[uniqueId]
}