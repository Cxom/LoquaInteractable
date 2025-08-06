package net.punchtree.loquainteractable.listeners

import net.punchtree.loquainteractable.displayutil.ArmorStandUtilsTesting
import net.punchtree.loquainteractable.metadata.editing.MetadataWand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractListener : Listener {

    @EventHandler
    fun onInteractWithWand(event: PlayerInteractEvent) {
        MetadataWand.handleMetadataWand(event)
        if (event.isCancelled) return
        // TODO probably delete this shit if no longer relevant
        ArmorStandUtilsTesting.onPlayerStaticPlace(event)
    }

}