package net.punchtree.loquainteractable.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

// TODO dependency inject? it'd be nice to do it with a proper framework
//  ...It'd also be nice to test such that I actually need to avoid using singletons...
object LoquaPlayerManager {

    /**
     * Player objects needed to be created when a player joins
     * and destroyed when a player leaves
     */

    private val playersToLoquaPlayers = mutableMapOf<UUID, LoquaPlayer>()

    // TODO HANDLE JOIN AND LEAVING IN EXACTLY ONE PLACE - as a readable entrypoint
    internal fun initializePlayer(player: Player): LoquaPlayer {
        val loquaPlayer = LoquaPlayer(player)
        playersToLoquaPlayers[player.uniqueId] = loquaPlayer
        return loquaPlayer
    }

    internal fun destroyPlayer(player: Player) {
        playersToLoquaPlayers.remove(player.uniqueId)
    }

    fun getSafe(player: Player): LoquaPlayer? {
        return getSafe(player.uniqueId)
    }

    fun getSafe(uuid: UUID): LoquaPlayer? {
        return playersToLoquaPlayers[uuid]
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