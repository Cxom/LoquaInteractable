package net.punchtree.loquainteractable.transit.streetcar

import org.bukkit.entity.ItemDisplay

data class StreetcarCar(val itemDisplay: ItemDisplay) {

    enum class Type(val customModelData: Int) {
        START(1000),
        MIDDLE(1001),
        END(1002)
    }

    internal var d1 = 0.0
    internal var d2 = 0.0

    internal fun remove() {
        itemDisplay.remove()
    }

}
