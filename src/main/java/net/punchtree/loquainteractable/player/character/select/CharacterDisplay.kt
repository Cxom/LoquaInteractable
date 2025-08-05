package net.punchtree.loquainteractable.player.character.select

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.player.character.LayerResolvedVisualCharacter
import org.bukkit.Location
import org.bukkit.entity.ItemDisplay

class CharacterDisplay private constructor(
    baseLocation: Location,
    private val visualCharacter: LayerResolvedVisualCharacter
) {
    private val headDisplay: ItemDisplay = visualCharacter.head.spawnDisplayFromCharacterBase(baseLocation)
    private val torsoDisplay: ItemDisplay = visualCharacter.torso.spawnDisplayFromCharacterBase(baseLocation)
    private val leftArmDisplay: ItemDisplay = visualCharacter.leftArm.spawnDisplayFromCharacterBase(baseLocation)
    private val rightArmDisplay: ItemDisplay = visualCharacter.rightArm.spawnDisplayFromCharacterBase(baseLocation)
    private val leftLegDisplay: ItemDisplay = visualCharacter.leftLeg.spawnDisplayFromCharacterBase(baseLocation)
    private val rightLegDisplay: ItemDisplay = visualCharacter.rightLeg.spawnDisplayFromCharacterBase(baseLocation)

    init {
        headDisplay.teleportDuration = 2
        torsoDisplay.teleportDuration = 2
        leftArmDisplay.teleportDuration = 2
        rightArmDisplay.teleportDuration = 2
        leftLegDisplay.teleportDuration = 2
        rightLegDisplay.teleportDuration = 2
    }

    internal fun updateBodyPartModels() {
        headDisplay.setItemStack(visualCharacter.head.generateItemStack())
        torsoDisplay.setItemStack(visualCharacter.torso.generateItemStack())
        leftArmDisplay.setItemStack(visualCharacter.leftArm.generateItemStack())
        rightArmDisplay.setItemStack(visualCharacter.rightArm.generateItemStack())
        leftLegDisplay.setItemStack(visualCharacter.leftLeg.generateItemStack())
        rightLegDisplay.setItemStack(visualCharacter.rightLeg.generateItemStack())
    }

    internal fun updateBaseLocation(baseLocation: Location) {
        headDisplay.teleport(visualCharacter.head.getExactBodyPartLocationFromCharacterBase(baseLocation))
        torsoDisplay.teleport(visualCharacter.torso.getExactBodyPartLocationFromCharacterBase(baseLocation))
        leftArmDisplay.teleport(visualCharacter.leftArm.getExactBodyPartLocationFromCharacterBase(baseLocation))
        rightArmDisplay.teleport(visualCharacter.rightArm.getExactBodyPartLocationFromCharacterBase(baseLocation))
        leftLegDisplay.teleport(visualCharacter.leftLeg.getExactBodyPartLocationFromCharacterBase(baseLocation))
        rightLegDisplay.teleport(visualCharacter.rightLeg.getExactBodyPartLocationFromCharacterBase(baseLocation))
    }

    internal fun remove() {
        LoquaInteractablePlugin.instance.logger.info("Removing character display")
        headDisplay.remove()
        torsoDisplay.remove()
        leftArmDisplay.remove()
        rightArmDisplay.remove()
        leftLegDisplay.remove()
        rightLegDisplay.remove()
    }

    companion object {
        internal fun createAt(baseLocation: Location, visualCharacter: LayerResolvedVisualCharacter): CharacterDisplay {
            return CharacterDisplay(baseLocation, visualCharacter)
        }
    }
}