package net.punchtree.loquainteractable.displayutil

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.scheduler.BukkitRunnable

object ArmorStandChunkLoadingReglow : Listener {

    internal fun doArmorStandReglow(event: ChunkLoadEvent) {
        // TODO move this into latching functions onto one loop off the main plugin instance?
        for (entity in event.getChunk().entities.filter {
            it.type == EntityType.ARMOR_STAND
                    && it.scoreboardTags.contains("loqinttempglowing")
        }) {
            val stand = entity as ArmorStand
            stand.setCanTick(true)
            stand.setFireTicks(1000)
            object : BukkitRunnable() {
                override fun run() {
                    stand.setCanTick(false)
                }
            }.runTaskLater(LoquaInteractablePlugin.instance, 20)
        }
    }

}
