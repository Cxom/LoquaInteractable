package net.punchtree.loquainteractable.instruments

import org.bukkit.entity.Player
import java.util.*

object InstrumentManager {

    // TODO using a manager here is a pattern we don't want to replicate
    //  there will be many out-of-body statuses a player could have, so much so
    //  that one single manager, or an object instance attached to the LoquaPlayer
    //  is a much better way to handle it than a bunch of different managers
    //  build drones, and then try abstracting the two different options


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