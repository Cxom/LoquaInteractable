package net.punchtree.loquainteractable.instruments

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

data object Instruments {

    val GUITAR = ItemStack(Material.PAPER).also {
        it.editMeta { itemMeta ->
            itemMeta.itemModel = NamespacedKey("punchtree", "acoustic_guitar")
            itemMeta.customName(Component.text("Acoustic Guitar").decoration(TextDecoration.ITALIC, false))
        }
    }

}