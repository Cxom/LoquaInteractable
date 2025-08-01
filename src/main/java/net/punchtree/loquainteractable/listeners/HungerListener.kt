package net.punchtree.loquainteractable.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

class HungerListener : Listener {

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        // The game simply handles food/hunger entirely differently
        event.isCancelled = true
    }

}
