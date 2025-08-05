package net.punchtree.loquainteractable.player.character.select

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.world.entity.Entity
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.getOrDefault
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.joining.splash.CameraUtils.spawnFakeCamera
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.character.BodyPart
import net.punchtree.loquainteractable.player.character.LayerResolvedVisualCharacter
import net.punchtree.loquainteractable.player.character.create.CharacterCreationMenu
import net.punchtree.loquainteractable.player.craftPlayer
import net.punchtree.loquainteractable.ui.Fade
import net.punchtree.loquainteractable.ui.Fade.fadeIn
import net.punchtree.util.color.PunchTreeColor
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.GameMode
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.util.*

internal const val PIXEL_TO_BLOCK = 1.0 / 16.0
internal const val BLOCK_TO_PLAYER_SCALE = 1.8 / 2.0

data class Garment(
    val pieces: List<GarmentPiece>
) {
    data class GarmentPiece(
        val bodyPart: BodyPart,
        val layerIndex: Int,
        val layerTextureId: String,
    )
}

/** In the future, there will be more than one character slot.
 *  A character slot shows either an existing character preview that can be selected,
 *  or a transparent gray "empty" slot that where a new character can be created */

// TODO CLEANUP THIS AND OTHER MANAGERS ON SERVER SHUTDOWN

class CharacterSelect(val player: CraftPlayer) {

    /* When a player enters character select, they
     *  1. Are teleported to the location of the character select menu
     *  2. Are put spectating an entity that shows them there character (most recently played)
     *
     *
     *
     *
     */

    private val cameraEntityId = Entity.nextEntityId()

    // TODO replace displays with packets

    private var isDestroyed = false

    private val characterDisplay: CharacterDisplay

    private val characterCreationMenu: CharacterCreationMenu

    init {
        player.gameMode = GameMode.SPECTATOR
        fadeIn(player, 2000)
        val initialCameraLocation = CHARACTER_1_LOCATION.clone().subtract(cameraOffsetFromPlayer).also {
            it.yaw += 180
            it.pitch = -it.pitch
//            it.y += 1.5 * 1.8
        }
        spawnFakeCamera(cameraEntityId, player.craftPlayer(), initialCameraLocation, 0, player.craftPlayer().handle.id)

        characterDisplay = CharacterDisplay.createAt(CHARACTER_1_LOCATION, emptyCharacterSlotCharacter)

        characterCreationMenu = CharacterCreationMenu.createAt(CHARACTER_1_CREATE_MENU_LOCATION)
        // TODO spawn text/menu display(s)
        // TODO create/reference a garment system
        // TODO load a list of garment configurations
    }

//    private fun spawnHeadDisplay() {
//        val skinColor = CharacterOptions.SKIN_COLORS[9]
//        val baseLayer = LayerResolvedVisualCharacter.VisualLayer("skin", skinColor)
//
//        val hairColor = PunchTreeColor(52, 28, 3)
//        val eyeColor = PunchTreeColor.GREEN
//        // TODO make a function of this?
//        val eyebrowColor = PunchTreeColor(hairColor.red() / 2 + eyeColor.red() / 2, hairColor.green() / 2 + eyeColor.green() / 2, hairColor.blue() / 2 + eyeColor.blue() / 2)
//        val layer1 = LayerResolvedVisualCharacter.VisualLayer("eyes_2", eyeColor, eyebrowColor)
//        val layer2 = LayerResolvedVisualCharacter.VisualLayer("hair_efe", hairColor)
//        val layer3 = LayerResolvedVisualCharacter.VisualLayer.EMPTY
//
//        val head = LayerResolvedVisualCharacter.LayerResolvedBodyPart(
//            BodyPart.HEAD,
//            listOf(baseLayer, layer1, layer2, layer3)
//        )
//    }

    // TODO write out in a readme some coding guidelines for this project, such as
    //  - do not name NON-event handlers functions starting with "on"
    //  - do name event handlers functions starting with "on"
    //  - do not have constructors have side effects
    //  - do use private constructors and factory methods for object constructions that should contain side effects

    var currentYaw = CHARACTER_1_LOCATION.yaw

    fun destroy() {
        require(!isDestroyed) { "Character select already destroyed!" }
        player.handle.connection.send(ClientboundRemoveEntitiesPacket(cameraEntityId))

        characterDisplay.remove()
        characterCreationMenu.remove()

        // TODO clear all displays

        // TODO really make the playerInputsManager track weak references as a backup defense against memory leaks
        // TODO also look into and understand the default observable mechanism (used for parallelism)

        Fade.clear(player)
        isDestroyed = true
    }

    @Suppress("UnstableApiUsage")
    fun tick() {
        if (player.currentInput.isJump) {
            currentYaw += DebugVars.getDecimalAsFloat("character_model_rotate_speed", 5f)
            characterDisplay.updateBaseLocation(
                CHARACTER_1_LOCATION.clone().also {
                    it.yaw = currentYaw
                }
            )
        }
        if (player.currentInput.isSneak) {
            currentYaw -= DebugVars.getDecimalAsFloat("character_model_rotate_speed", 5f)
            characterDisplay.updateBaseLocation(
                CHARACTER_1_LOCATION.clone().also {
                    it.yaw = currentYaw
                }
            )
        }
    }

    companion object {
        // TODO data drive
        private val cameraOffsetFromPlayer = Vector(4.0, -0.5, 0.6)

        private val CHARACTER_1_LOCATION by lazy {
            LoquaInteractablePlugin.world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.CHARACTER_SELECT_CHARACTER_1_LOCATION, Housings.DEFAULT_HOUSING_SPAWN)
        }
        private val CHARACTER_1_CREATE_MENU_LOCATION by lazy {
            LoquaInteractablePlugin.world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.CHARACTER_SELECT_CHARACTER_1_CREATE_MENU_LOCATION, Housings.DEFAULT_HOUSING_SPAWN)
        }

        val emptyCharacterSlotCharacter = run {
            val skinLayer = LayerResolvedVisualCharacter.VisualLayer("skin", PunchTreeColor.GRAY)
            val grayOnly = listOf(skinLayer)
            LayerResolvedVisualCharacter(
                grayOnly,
                grayOnly,
                grayOnly,
                grayOnly,
                grayOnly,
                grayOnly
            )
        }
    }
}

class CharacterSelectManager {

    // TODO SPLIT CHARACTER SELECT AND CHARACTER CREATION INTO TWO SEPARATE THINGS

    // use lateinit to fail fast if this is not set
    internal lateinit var onSelectCharacter: ((LoquaPlayer) -> Unit)

    private lateinit var characterSelectTick: BukkitTask

    private val characterSelectPlayers = mutableMapOf<UUID, CharacterSelect>()

    internal fun showCharacterSelect(loquaPlayer: LoquaPlayer) {
        require(!characterSelectPlayers.contains(loquaPlayer.uniqueId)) {
            "Player ${loquaPlayer.name} is already in character select!"
        }

        characterSelectPlayers[loquaPlayer.uniqueId] = CharacterSelect(loquaPlayer.craftPlayer())

        if (!::characterSelectTick.isInitialized) {
            characterSelectTick = object : BukkitRunnable() {
                override fun run() {
                    characterSelectPlayers.values.forEach { it.tick() }
                }
            }.runTaskTimer(LoquaInteractablePlugin.instance, 0, 1)
        }
    }

    internal fun isInCharacterSelect(uniqueId: UUID): Boolean {
        return characterSelectPlayers.containsKey(uniqueId)
    }

    // NOT AN EVENT LISTENER
    internal fun handlePlayerQuit(player: LoquaPlayer) {
        cleanupCharacterSelect(player)
    }

    private fun cleanupCharacterSelect(loquaPlayer: LoquaPlayer) {
        characterSelectPlayers.remove(loquaPlayer.uniqueId)?.destroy()
    }

    fun onDisable() {
        // TODO cleanup all character selects
        characterSelectPlayers.values.forEach { it.destroy() }
        characterSelectPlayers.clear()
        if (this::characterSelectTick.isInitialized) {
            characterSelectTick.cancel()
        }
    }
}
