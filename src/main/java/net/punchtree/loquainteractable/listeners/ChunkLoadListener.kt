package net.punchtree.loquainteractable.listeners

import net.punchtree.loquainteractable.displayutil.ArmorStandChunkLoadingReglow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent

class ChunkLoadListener : Listener {

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        ArmorStandChunkLoadingReglow.doArmorStandReglow(event)
    }

}