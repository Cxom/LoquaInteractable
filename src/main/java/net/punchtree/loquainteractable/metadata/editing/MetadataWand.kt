package net.punchtree.loquainteractable.metadata.editing

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.punchtree.loquainteractable.lighting.LightSwitchMetadataEditingMode
import net.punchtree.loquainteractable.lighting.ToggleMetadataEditingMode
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

// TODO is this more a session manager class?
// TODO per player instances for data accumulation? (this comment was next to event registration for the metadata wand)
object MetadataWand {
    private val METADATA_WAND_ITEM = ItemStack(Material.BLAZE_ROD)

    init {
        val im = METADATA_WAND_ITEM.getItemMeta()
        im.displayName(text(ChatColor.RESET.toString() + "" + ChatColor.WHITE + "Metadata Wand"))
        im.lore(
            listOf<TextComponent?>(
                text("Right click to inspect"),
                text("Left click to edit")
            )
        )
        METADATA_WAND_ITEM.setItemMeta(im)
    }

    private fun isMetadataWandItem(itemStack: ItemStack?): Boolean {
        return METADATA_WAND_ITEM == itemStack
    }

	fun giveMetadataWandItem(player: Player) {
        player.inventory.addItem(METADATA_WAND_ITEM.clone())
    }

    private val INSPECT_ACTION = Action.RIGHT_CLICK_BLOCK
    private val OPEN_EDIT_MODE_MENU_ACTION = Action.LEFT_CLICK_AIR
    private val APPLY_EDIT_MODE_ACTION = Action.LEFT_CLICK_BLOCK

    private val editingModes: MutableList<MetadataEditingMode?> = ArrayList<MetadataEditingMode?>()

    init {
        editingModes.add(InspectMetadataEditingMode())
        editingModes.add(LightSwitchMetadataEditingMode())
        editingModes.add(ToggleMetadataEditingMode())
    }

    fun addEditingMode(editingMode: MetadataEditingMode?) {
        editingModes.add(editingMode)
    }

    var editModeMenu: MetadataEditingModeMenu? = null

    fun handleMetadataWand(event: PlayerInteractEvent) {
        // TODO tie to sessions
        if (event.hand != EquipmentSlot.HAND || !isMetadataWandItem(event.item)) return
        event.isCancelled = true

        val session = MetadataEditingSessionManager.getSessionFor(event.getPlayer())
        val editingMode = session.editingMode
        if (event.action == Action.RIGHT_CLICK_AIR) {
            editingMode.onRightClickAir(event, event.getPlayer(), session)
            //			onInspect(event);
        } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
            editingMode.onRightClickBlock(event, event.getPlayer(), session)
        } else if (event.action == Action.LEFT_CLICK_BLOCK) {
            editingMode.onLeftClickBlock(event, event.getPlayer(), session)
            //			onInspectAllRaw(event);
        } else if (event.action == OPEN_EDIT_MODE_MENU_ACTION) {
            onOpenEditModeMenu(event.getPlayer())
        }

        //		else if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
//			onEdit(event);
//		}
    }

    private fun onOpenEditModeMenu(player: Player) {
        if (editModeMenu == null) {
            editModeMenu = MetadataEditingModeMenu(editingModes)
        }
        editModeMenu!!.showTo(player)
    }
}
