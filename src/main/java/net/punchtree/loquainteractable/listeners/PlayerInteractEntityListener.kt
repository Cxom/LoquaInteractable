package net.punchtree.loquainteractable.listeners

import net.md_5.bungee.api.ChatColor
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.persistence.PersistentDataType

class PlayerInteractEntityListener : Listener {

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        onLightSwitchInteract(event)
    }

    fun onLightSwitchInteract(event: PlayerInteractEntityEvent) {
        if (event.rightClicked.type != EntityType.ITEM_FRAME) return

        val frame = event.rightClicked as ItemFrame
        val container = frame.persistentDataContainer

        val key = NamespacedKey(LoquaInteractablePlugin.instance, "lightswitch")

        if (!container.has(key, PersistentDataType.INTEGER_ARRAY)) return

        val lights = container.get(key, PersistentDataType.INTEGER_ARRAY)!!
        if (lights.size % 3 != 0) {
            Bukkit.broadcastMessage(ChatColor.RED.toString() + "Wrong number of integers specified")
        }

        event.isCancelled = true

        val on = frame.rotation == Rotation.NONE
        frame.rotation = if (on) Rotation.FLIPPED else Rotation.NONE

        val world = frame.world
        var i = 0
        while (i < lights.size) {
            val loc = Location(world, lights[i].toDouble(), lights[i + 1].toDouble(), lights[i + 1].toDouble())
            loc.block.type = if (on) Material.SMOOTH_QUARTZ else Material.BEACON
            i += 3
        }
    }
}
