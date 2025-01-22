package net.punchtree.loquainteractable.instruments

import org.bukkit.entity.Player
import java.util.*

object InstrumentManager {

    private val playersPlayingInstruments = mutableMapOf<UUID, AcousticGuitarPlayer>()

    internal fun isPlayingInstrument(player: Player): Boolean {
        return playersPlayingInstruments.containsKey(player.uniqueId)
    }

    fun startPlayerPlaying(player: Player, acousticGuitarPlayer: AcousticGuitarPlayer) {
        stopPlayerPlaying(player)
        playersPlayingInstruments[player.uniqueId] = acousticGuitarPlayer
    }

    fun stopPlayerPlaying(player: Player) {
        playersPlayingInstruments.remove(player.uniqueId)?.stopPlaying()
    }

    fun onDisable() {
        playersPlayingInstruments.values.forEach { it.stopPlaying() }
    }

}