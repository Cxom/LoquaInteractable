package net.punchtree.loquainteractable.input

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.punchtree.loquainteractable.instruments.InstrumentManager
import org.bukkit.plugin.Plugin

class ChangeHeldItemInputPacketAdapter(
    plugin: Plugin?,
    private val playerInputsManager: PlayerInputsManager
) : PacketAdapter(
    plugin, ListenerPriority.LOWEST, *arrayOf(
        PacketType.Play.Client.HELD_ITEM_SLOT
    )
) {
    override fun onPacketReceiving(packetEvent: PacketEvent) {
        val packetContainer = packetEvent.packet

        val newSlot = packetContainer.integers.readSafely(0)
        val player = packetEvent.player

        val playerInputs = playerInputsManager.getInputsForPlayer(player)
        playerInputs?.updateHeldItemSlot(player, newSlot)
    }
}
