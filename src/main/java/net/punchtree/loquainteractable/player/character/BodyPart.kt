package net.punchtree.loquainteractable.player.character

import net.punchtree.loquainteractable.player.character.select.BLOCK_TO_PLAYER_SCALE
import net.punchtree.loquainteractable.player.character.select.PIXEL_TO_BLOCK
import org.bukkit.NamespacedKey
import org.bukkit.util.Vector
import org.joml.Vector3f

enum class BodyPart(
    internal val itemModel: NamespacedKey,
    jointOffsetFromFeetPixels: Vector,
    jointOffsetFromModelCenter: Vector3f
) {
    HEAD(
        NamespacedKey("punchtree", "player/head"),
        Vector(0, 24, 0),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    TORSO(
        NamespacedKey("punchtree", "player/torso"),
        Vector(0, 24, 0),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    LEFT_ARM(
        NamespacedKey("punchtree", "player/left_arm"),
        Vector(-5, 22, 0),
        Vector3f(-1f, 0.0f, 0.0f)
    ),
    RIGHT_ARM(
        NamespacedKey("punchtree", "player/right_arm"),
        Vector(5, 22, 0),
        Vector3f(1f, 0.0f, 0.0f)
    ),
    LEFT_LEG(
        NamespacedKey("punchtree", "player/left_leg"),
        Vector(-1.9, 12.0, 0.0),
        Vector3f(-.1f, 0.0f, 0.0f)
    ),
    RIGHT_LEG(
        NamespacedKey("punchtree", "player/right_leg"),
        Vector(1.9, 12.0, 0.0),
        Vector3f(.1f, 0.0f, 0.0f)
    );

    internal val worldOffsetFromFeet = jointOffsetFromFeetPixels.multiply(PIXEL_TO_BLOCK * BLOCK_TO_PLAYER_SCALE)
    internal val worldOffsetFromModelCenter = jointOffsetFromModelCenter.mul((PIXEL_TO_BLOCK * BLOCK_TO_PLAYER_SCALE).toFloat())
}