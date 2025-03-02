package net.punchtree.loquainteractable.ui

import org.bukkit.NamespacedKey

data object CameraOverlays {
    val BLACK_OUT = CameraOverlay(NamespacedKey("punchtree", "font/special/dark"))
    val LOQUA_SPLASH = CameraOverlay(NamespacedKey("punchtree", "font/special/loqua_splash"))
}

data class CameraOverlay(val namespacedKey: NamespacedKey)