package net.punchtree.loquainteractable.input

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * Manages PlayerInputs objects
 * @author Cxom
 */
class PlayerInputsManager {
    // Don't make this a weakhashmap without also making the uuid member of PlayerInputs a weakreference
    private val playerInputsMap: MutableMap<UUID, PlayerInputs> = HashMap()

    operator fun get(player: Player): PlayerInputs {
        return get(player.uniqueId)
    }

    operator fun get(uuid: UUID): PlayerInputs {
        return checkNotNull(playerInputsMap[uuid]) {
            val playerName = Bukkit.getOfflinePlayer(uuid).name
            "Tried fetching PlayerInputs for player [$playerName:${uuid}] and it didn't work! Are they not online?"
        }
    }

    fun getSafe(uuid: UUID): PlayerInputs? {
        return playerInputsMap[uuid]
    }

    fun initializeInputs(uuid: UUID) {
        playerInputsMap[uuid] = PlayerInputs(uuid)
    }

    fun destroyInputs(uuid: UUID) {
        playerInputsMap.remove(uuid)
    }

    // TODO actually run this test (some sort of test command probs)
    fun verifyAllPlayersAreOnline(): Boolean {
        for (uuid in playerInputsMap.keys) {
            val p = Bukkit.getPlayer(uuid)
            if (p == null || !p.isOnline) {
                return false
            }
        }
        return true
    }
}
