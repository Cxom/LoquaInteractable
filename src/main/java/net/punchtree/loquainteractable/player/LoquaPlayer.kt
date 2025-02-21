package net.punchtree.loquainteractable.player

import com.jeff_media.morepersistentdatatypes.DataType
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.entity.Player

class LoquaPlayer(player: Player) : PlayerDecorator(player) {
    fun saveInventory() {
        persistentDataContainer.set(
            LoquaDataKeys.INVENTORY,
            DataType.ITEM_STACK_ARRAY,
            inventory.contents
        )
    }

    /** Restores the inventory to the last saved state. */
    fun restoreInventoryToLastSave() {
        persistentDataContainer.get(LoquaDataKeys.INVENTORY, DataType.ITEM_STACK_ARRAY)?.let {
            inventory.contents = it
        } ?: LoquaInteractablePlugin.instance.logger.severe("No saved inventory found when trying to restore inventory for $name.")
    }
}
