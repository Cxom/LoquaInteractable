package net.punchtree.loquainteractable.input

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Input
import org.bukkit.plugin.Plugin

class SteerVehicleInputPacketAdapter(
    plugin: Plugin,
    private val playerInputsManager: PlayerInputsManager
) : PacketAdapter(
    plugin,
    ListenerPriority.LOWEST,
    *arrayOf(
        PacketType.Play.Client.STEER_VEHICLE
    )
) {
    override fun onPacketReceiving(packetEvent: PacketEvent) {
        val packetContainer = packetEvent.packet

        packetContainer.serializeToBuffer()

        val buffer = packetContainer.serializeToBuffer() as FriendlyByteBuf
        val input = Input.STREAM_CODEC.decode(buffer)

        val player = packetEvent.player

        val playerInputs = playerInputsManager.getInputsForPlayer(player)
        playerInputs?.updateVehicleInput(player, input)
    }
}
