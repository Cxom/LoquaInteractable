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

    fun getInputsForPlayer(player: Player): PlayerInputs? {
        return getInputsForPlayer(player.uniqueId)
    }

    fun getInputsForPlayer(uuid: UUID): PlayerInputs? {
        return playerInputsMap[uuid]
    }

    fun initializeInputsForPlayer(uuid: UUID) {
        playerInputsMap[uuid] = PlayerInputs(uuid)
    }

    fun destroyInputsForPlayer(uuid: UUID) {
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
