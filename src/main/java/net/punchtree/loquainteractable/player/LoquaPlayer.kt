package net.punchtree.loquainteractable.player

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.get
import net.punchtree.loquainteractable.data.remove
import net.punchtree.loquainteractable.data.set
import net.punchtree.loquainteractable.ui.CameraOverlay
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class LoquaPlayer(player: Player) : PlayerDecorator(player) {

    // TODO we have a very strong expectation that there is exactly one LoquaPlayer per CraftPlayer
    //  it may be a good idea to enforce this in the constructor, or better yet maybe even make the
    //  constructor private and move the player manager into the same file

    internal var isInSplashScreen = false
    private var isInCharacterSelect = false

    internal fun isStaffMember(): Boolean {
        return hasPermission(LoquaPermissions.STAFF)
    }

    internal fun isInStaffMode(): Boolean {
        if (!isStaffMember()) return false
        return persistentDataContainer.get(LoquaDataKeys.Player.IS_IN_STAFF_MODE)?.let { isInStaffMode ->
            if (!isInStaffMode) {
                persistentDataContainer.remove(LoquaDataKeys.Player.IS_IN_STAFF_MODE)
            }
            isInStaffMode
        } ?: false
    }

    fun saveInventory() {
        persistentDataContainer.set(
            LoquaDataKeys.Player.INVENTORY,
            inventory.contents
        )
    }

    /** Restores the inventory to the last saved state. */
    fun restoreInventoryToLastSave() {
        persistentDataContainer.get(LoquaDataKeys.Player.INVENTORY)?.let {
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
        LoquaInteractablePlugin.instance.logger.info("${name} staff mode: ${isInStaffMode()} isInSplashScreen: $isInSplashScreen isInCharacterSelect: $isInCharacterSelect isPlaying: ${isPlaying()} isOutOfBody: ${isOutOfBody()}")
        if (!isOutOfBody()) {
            LoquaInteractablePlugin.instance.logger.info("Saving inventory for not-out-of-body player $name")
            saveInventory()
        } else {
            LoquaInteractablePlugin.instance.logger.info("Not saving inventory for out-of-body player $name")
        }
    }

    @Suppress("UnstableApiUsage")
    fun setCameraOverlay(cameraOverlay: CameraOverlay) {
        inventory.helmet = ItemStack(Material.BLACK_CONCRETE).also {
            it.editMeta { meta ->
                val equippable = meta.equippable
                equippable.slot = EquipmentSlot.HEAD
                equippable.cameraOverlay = cameraOverlay.namespacedKey
                meta.setEquippable(equippable)
            }
        }
    }
}
