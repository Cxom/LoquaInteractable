package net.punchtree.loquainteractable.outofbody.drone

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Drone {
    fun getItemStack(): ItemStack {
        return ItemStack.of(Material.IRON_TRAPDOOR)
    }

}
