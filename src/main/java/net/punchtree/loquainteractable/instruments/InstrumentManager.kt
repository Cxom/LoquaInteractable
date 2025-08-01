package net.punchtree.loquainteractable.instruments

import org.bukkit.entity.Player
import java.util.*

object InstrumentManager {

    private val playersPlayingInstruments = mutableMapOf<UUID, InstrumentPlayer>()

    internal fun isPlayingInstrument(player: Player): Boolean {
        return playersPlayingInstruments.containsKey(player.uniqueId)
    }

    fun startPlayerPlaying(player: Player, instrument: Instruments.Instrument) {
        stopPlayerPlaying(player)
        val instrumentPlayer = InstrumentPlayer(player, instrument)
        playersPlayingInstruments[player.uniqueId] = instrumentPlayer
    }

    fun stopPlayerPlaying(player: Player) {
        playersPlayingInstruments.remove(player.uniqueId)?.stopPlaying()
    }

    fun onDisable() {
        playersPlayingInstruments.values.forEach { it.stopPlaying() }
        playersPlayingInstruments.clear()
    }

}