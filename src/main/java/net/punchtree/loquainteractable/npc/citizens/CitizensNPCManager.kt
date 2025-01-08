package net.punchtree.loquainteractable.npc.citizens

import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.npc.NPCRegistry
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.RED

object CitizensNPCManager {

    private lateinit var heistTestingRegisty: NPCRegistry;

    private var npc: NPC? = null

    fun initialize() {
        heistTestingRegisty = CitizensAPI.createInMemoryNPCRegistry("HeistTestingRegistry")
    }

    fun createNPC(player: Player) {
        npc?.destroy()
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Steve").also {
            it.spawn(player.location)
        }
    }

    fun makeMove(player: Player) {
        player.player
        npc?.let {
            it.navigator.setTarget(player.location)
        } ?: player.sendMessage(text("No NPC to move!").color(RED))
    }

    fun onDisable() {
        npc?.destroy()
    }

}
