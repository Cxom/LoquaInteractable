package net.punchtree.loquainteractable.player.character

import net.kyori.adventure.text.Component
import net.punchtree.util.color.PunchTreeColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Quaternionf
import org.joml.Vector3f

class LayerResolvedVisualCharacter(
    head: List<VisualLayer>,
    torso: List<VisualLayer>,
    leftArm: List<VisualLayer>,
    rightArm: List<VisualLayer>,
    leftLeg: List<VisualLayer>,
    rightLeg: List<VisualLayer>,
) {

    internal val head = LayerResolvedBodyPart(BodyPart.HEAD, head)
    internal val torso = LayerResolvedBodyPart(BodyPart.TORSO, torso)
    internal val leftArm = LayerResolvedBodyPart(BodyPart.LEFT_ARM, leftArm)
    internal val rightArm = LayerResolvedBodyPart(BodyPart.RIGHT_ARM, rightArm)
    internal val leftLeg = LayerResolvedBodyPart(BodyPart.LEFT_LEG, leftLeg)
    internal val rightLeg = LayerResolvedBodyPart(BodyPart.RIGHT_LEG, rightLeg)

    data class LayerResolvedBodyPart(
        val bodyPart: BodyPart,
        val layers: List<VisualLayer>
    ) {
        internal val name = bodyPart.name.lowercase().replaceFirstChar(Char::titlecase)
        internal fun generateItemStack(): ItemStack {
            val strings = layers.map { it.id }
            val colors = layers.flatMap { listOf(it.tint1.bukkitColor, it.tint2.bukkitColor, it.tint3.bukkitColor) }

            @Suppress("UnstableApiUsage")
            val itemStack = ItemStack(Material.BONE).also {
                it.editMeta { itemMeta ->
                    itemMeta.itemModel = bodyPart.itemModel
                    val customModelDataComponent = itemMeta.customModelDataComponent
                    customModelDataComponent.strings = strings
                    customModelDataComponent.colors = colors
                    itemMeta.setCustomModelDataComponent(customModelDataComponent)
                    itemMeta.itemName(Component.text(name))
                }
            }

            return itemStack
        }

        fun spawnDisplayFromCharacterBase(baseLocation: Location): ItemDisplay {
            val displayLocation = getExactBodyPartLocationFromCharacterBase(baseLocation)
            return spawnDisplayExact(displayLocation)
        }

        internal fun getExactBodyPartLocationFromCharacterBase(baseLocation: Location): Location {
            val forward = baseLocation.direction
            val right = Vector(0, 1, 0).crossProduct(forward)
            val displayLocation = baseLocation.clone()
                .add(right.multiply(bodyPart.worldOffsetFromFeet.x))
                .add(Vector(0, 1, 0).multiply(bodyPart.worldOffsetFromFeet.y))
                .add(forward.multiply(bodyPart.worldOffsetFromFeet.z))
                .also {
                    if (bodyPart != BodyPart.HEAD) {
                        it.pitch = 0f
                    }

                    // TODO arm rotation won't work if we're positioning exact (without use of transform) because they're
                    //  not centered - handle!!
                }
            return displayLocation
        }

        private fun spawnDisplayExact(location: Location): ItemDisplay {
            return location.world.spawnEntity(location, EntityType.ITEM_DISPLAY, CreatureSpawnEvent.SpawnReason.CUSTOM) {
                it as ItemDisplay
                it.setItemStack(generateItemStack())
                it.itemDisplayTransform = ItemDisplay.ItemDisplayTransform.NONE
                it.transformation = Transformation(
                    bodyPart.worldOffsetFromModelCenter,
                    Quaternionf(),
                    Vector3f(1f, 1f, 1f),
                    Quaternionf()
                )
            } as ItemDisplay
        }

        internal val worldOffsetFromFeet get() = bodyPart.worldOffsetFromFeet
    }

    data class VisualLayer(
        val id: String,
        val tint1: PunchTreeColor = PunchTreeColor.WHITE,
        val tint2: PunchTreeColor = PunchTreeColor.WHITE,
        val tint3: PunchTreeColor = PunchTreeColor.WHITE
    ) {
        companion object {
            val EMPTY = VisualLayer("empty")
        }
    }

    // TODO is there any reuse potential in right arm/left arm and legs?
}