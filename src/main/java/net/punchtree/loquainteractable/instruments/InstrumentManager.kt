package net.punchtree.loquainteractable.instruments

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.entity.Player
import java.util.*

object InstrumentManager {

    private val playersPlayingInstruments = mutableMapOf<UUID, InstrumentPlayer>()

    internal fun isPlayingInstrument(player: Player): Boolean {
        return playersPlayingInstruments.containsKey(player.uniqueId)
    }

    fun startPlayerPlaying(player: Player, instrument: Instruments.Instrument) {
        stopPlayerPlaying(player)
        val playerInputs = LoquaInteractablePlugin.instance.playerInputsManager[player]
        val instrumentPlayer = InstrumentPlayer(player, instrument, playerInputs)
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