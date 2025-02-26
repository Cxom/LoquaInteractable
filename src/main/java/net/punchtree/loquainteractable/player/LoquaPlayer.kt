package net.punchtree.loquainteractable.player

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class LoquaPlayer(player: Player) : PlayerDecorator(player) {

    internal var isInSplashScreen = false
    private var isInCharacterSelect = false

    internal fun isStaffMember(): Boolean {
        return hasPermission(LoquaPermissions.STAFF)
    }

    internal fun isInStaffMode(): Boolean {
        if (!isStaffMember()) return false
        return persistentDataContainer.get(LoquaDataKeys.IS_IN_STAFF_MODE)?.let { isInStaffMode ->
            if (!isInStaffMode) {
                persistentDataContainer.remove(LoquaDataKeys.IS_IN_STAFF_MODE)
            }
            isInStaffMode
        } ?: false
    }

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

    fun isPlaying(): Boolean {
        return !isInStaffMode() && !isInSplashScreen && !isInCharacterSelect
    }

    private fun isOutOfBody(): Boolean {
        // TODO isPlaying is insufficient - there are instances where the user will not
        //  have their characters inventory and we need to not save what they do have
        //  examples: in-game cutscenes, minigames, jail, dead, flying drones, literally anything that takes them out-of-body
        //  we ought to have a general practice of saving their inventory before they enter any of these states
        return !isPlaying()
    }

    fun saveInventoryIfNotOutOfBody() {
        if (!isOutOfBody()) {
            LoquaInteractablePlugin.instance.logger.info("Saving inventory for not-out-of-body player $name")
            Bukkit.broadcastMessage("Saving inventory for not-out-of-body player $name")
            saveInventory()
        } else {
            LoquaInteractablePlugin.instance.logger.info("Not saving inventory for out-of-body player $name")
            Bukkit.broadcastMessage("Not saving inventory for out-of-body player $name")
        }
    }
}
