package net.punchtree.loquainteractable.player

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.input.PlayerInputs
import org.bukkit.entity.Player

class LoquaPlayer(player: Player) : PlayerDecorator(player) {

    fun saveInventory() {
        persistentDataContainer.set(
            LoquaDataKeys.INVENTORY,
            inventory.contents
        )
    }

    /** Restores the inventory to the last saved state. */
    fun restoreInventoryToLastSave() {
        persistentDataContainer.get(LoquaDataKeys.INVENTORY)?.let {
            inventory.contents = it
        } ?: LoquaInteractablePlugin.instance.logger.severe("No saved inventory found when trying to restore inventory for $name.")
    }
}
