package net.punchtree.loquainteractable.outofbody.instruments

import org.bukkit.entity.Player
import java.util.*

object InstrumentManager {

    // TODO using a manager here is a pattern we don't want to replicate
    //  there will be many out-of-body statuses a player could have, so much so
    //  that one single manager, or an object instance attached to the LoquaPlayer
    //  is a much better way to handle it than a bunch of different managers
    //  build drones, and then try abstracting the two different options
    //  also singleton bad
    //  .
    //  moreover, this idea of out-of-body state can be applied to splash screen manager????????
    //  maybe not, only because that is a very special case of a non-gameplay state
    //  BUT, the idea of dispatching to it through the LoquaClient class is not a bad idea
    //  or, we can just treat it as a play state. that might be better, it is really only
    //  *conceptually* not a play state, but the limitations it has around what the player can do
    //  while it's happening (nothing) are pretty similar to the limitations of some other
    //  out-of-body states, like drone-flying, watching a cinematic, being dead (corpse), etc


    /*
     * Instrument Manager lets forward:
     * - Swap, Dismount, and Drop Item events (for cancelling)
     * - Quit event (for stopping)
     * - Server shutdown (for stopping)
     *
     * Drone lets forward the same
     *
     * Splash Screen Manager:
     *  -
     *
     */

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