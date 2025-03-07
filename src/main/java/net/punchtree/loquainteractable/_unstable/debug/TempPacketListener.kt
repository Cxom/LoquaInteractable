package net.punchtree.loquainteractable._unstable.debug

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Bukkit

class TempPacketListener : PacketListener {

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.user.profile.uuid?.let { Bukkit.getPlayer(it) }
        if (event.packetType == PacketType.Play.Client.PLAYER_LOADED) {
//            val playerLoadedPacket = WrapperPlayClientPlayerLoaded(event)
            if (player != null) {
//                Bukkit.broadcastMessage("Player ${player.name} loaded packet event!");
            } else {
//                Bukkit.broadcastMessage("Player ${event.user.profile.uuid} loaded packet event, but player is null!");
            }
        } else if (DebugVars.getBoolean("packet-debug", false) && player?.name == "Cxom" && event.packetType != PacketType.Play.Client.CLIENT_TICK_END) {
            Bukkit.broadcastMessage("***Received from ${player.name} packet ${event.packetType}!")
        }
    }

    override fun onPacketSend(event: PacketSendEvent) {
        val player = event.user.profile.uuid?.let { Bukkit.getPlayer(it) } ?: return
        if (event.packetType == PacketType.Play.Server.UNLOAD_CHUNK) {
            val wrapper = WrapperPlayServerUnloadChunk(event)
//            Bukkit.broadcastMessage("Player ${player.name} is being told to unload chunk ${wrapper.chunkX}, ${wrapper.chunkZ}!")
        }
    }

}