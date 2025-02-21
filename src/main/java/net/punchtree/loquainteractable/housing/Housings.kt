package net.punchtree.loquainteractable.housing

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.Location

data object Housings {

    // TODO data drive this
    val DEFAULT_HOUSING_SPAWN by lazy {
        Location(LoquaInteractablePlugin.world, 2548.5, 71.1, 950.5, 180f, 0f)
    }

}
