package net.punchtree.loquainteractable.instruments

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.input.PlayerInputs
import net.punchtree.loquainteractable.input.PlayerInputsObserver
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min

class AcousticGuitarPlayer(var player: Player, val playerInputs: PlayerInputs) : PlayerInputsObserver {

    private lateinit var chair: Entity

    init {
        startPlaying()
    }

    private fun startPlaying() {
        this.chair = player.world.spawnEntity(player.location.clone().also {
            it.yaw = 0f
            it.pitch = 0f
            it.y += 1
        }, EntityType.ITEM_DISPLAY, CreatureSpawnEvent.SpawnReason.CUSTOM) {
            it as ItemDisplay
            it.setItemStack(ItemStack(Material.SCAFFOLDING))
            it.addPassenger(player)
            it.transformation = Transformation(Vector3f(0f, -.5f, 0f), Quaternionf(), Vector3f(1f, 1f, 1f), Quaternionf())
            it.setMetadata("instrument", FixedMetadataValue(LoquaInteractablePlugin.getInstance(), "acoustic_guitar"))
        }
        playerInputs.registerObserver(this)
    }

    override fun onUpdate(player: Player, inputs: PlayerInputs, updateType: PlayerInputs.PlayerInputsUpdateType) {
//        doBasicBluesScaleWithHotbar(updateType, inputs)
//        doFifthsWasdStringing(updateType, inputs)
        doFifthsNumberStringing(updateType, inputs)
        // I don't think it's going to get much better than the fifths number stringing,
        //  but one more idea for a stringing is to use
        //  shift A S D F (W) 1 2 3 4 as the frets
        //a
    }

    private var fns_currString = 0
    val isJumpDoesRepeat = DebugVars.getBoolean("guitar-isJumpDoesRepeat", false)
    private fun doFifthsNumberStringing(updateType: PlayerInputs.PlayerInputsUpdateType, inputs: PlayerInputs) {
        /** this value, if true, disables hotbar slot resetting, makes jump repeat the last note, and disables
         *  shifting up and down strings relatively with shift and jump*/

        val quantizeAmount = 10
        val quantizePeriod = 50L

        if (inputs.left) {
            fns_currString = 0
        } else if (inputs.backward) {
            fns_currString = 1
        } else if (inputs.right) {
            fns_currString = 2
        } else if (updateType == PlayerInputs.PlayerInputsUpdateType.SWAP_HANDS) {
            fns_currString = 3
        } else if (inputs.jump && !isJumpDoesRepeat) {
            fns_currString = min(fns_currString + 1, 3)
        } else if (inputs.shift && !isJumpDoesRepeat) {
            fns_currString = max(fns_currString - 1, 0)
        }
        if (updateType == PlayerInputs.PlayerInputsUpdateType.CHANGE_HOTBAR_SLOT && inputs.heldItemSlot != 0 || (inputs.jump && isJumpDoesRepeat)) {
            val fret = inputs.heldItemSlot

            val millisUntilNextPeriod = quantizePeriod - (System.currentTimeMillis() % quantizePeriod)
            if (millisUntilNextPeriod <= quantizeAmount) {
                // TODO quantization would go here, but because it runs at a frequency faster than tick,
                //  it needs to be applied directly to the outgoing packet - all asynchronous
                Thread.sleep(millisUntilNextPeriod) // LOL just freeze the world to make the mortals be on beat
                playNote(notes[fns_currString * 7 + fret - 1])
            } else {
                playNote(notes[fns_currString * 7 + fret - 1])
            }
            if (!isJumpDoesRepeat) {
                player.inventory.heldItemSlot = 0
            }
        }
    }

    var jumpOnLastUpdate = false
    var lastDirectionCode = 0
    private fun doFifthsWasdStringing(updateType: PlayerInputs.PlayerInputsUpdateType, inputs: PlayerInputs) {
//        if (!inputs.jump || jumpOnLastUpdate){
//            jumpOnLastUpdate = inputs.jump
//            return
//        }
        val currDirectionCode = calcDirectionCode(inputs)
        if (currDirectionCode == lastDirectionCode && !inputs.jump) {
            lastDirectionCode = currDirectionCode
            return
        }
        val fret =
        when {
//            !inputs.left && !inputs.forward && !inputs.right && !inputs.backward -> 0
            !inputs.left && !inputs.forward && inputs.right && inputs.backward -> 1
            !inputs.left && !inputs.forward && inputs.right && !inputs.backward -> 2
            !inputs.left && inputs.forward && inputs.right && !inputs.backward -> 3
            !inputs.left && inputs.forward && !inputs.right && !inputs.backward -> 4
            inputs.left && inputs.forward && !inputs.right && !inputs.backward -> 5
            inputs.left && !inputs.forward && !inputs.right && !inputs.backward -> 6
            inputs.left && !inputs.forward && !inputs.right && inputs.backward -> 7
            !inputs.left && !inputs.forward && !inputs.right && inputs.backward -> 0
            else -> -1
        }
        val string =
            when {
                inputs.heldItemSlot == 5 -> 0
                inputs.heldItemSlot == 6 -> 7
                inputs.heldItemSlot == 7 -> 14
                inputs.heldItemSlot == 8 -> 21
                inputs.heldItemSlot == 9 -> 28
                else -> 0
            }
        if (fret != -1) {
            playNote(notes[string + fret])
        }

        jumpOnLastUpdate = inputs.jump
        lastDirectionCode = currDirectionCode
    }

    private fun calcDirectionCode(inputs: PlayerInputs) = (if (inputs.left) 1 else 0 shl 3) or
                (if (inputs.forward) 1 else 0 shl 2) or
                (if (inputs.right) 1 else 0 shl 1) or
                (if (inputs.backward) 1 else 0)

    private fun doBasicBluesScaleWithHotbar(
        updateType: PlayerInputs.PlayerInputsUpdateType,
        inputs: PlayerInputs
    ) {
        if (updateType == PlayerInputs.PlayerInputsUpdateType.CHANGE_HOTBAR_SLOT) {
            when (inputs.heldItemSlot) {
                0 -> playNote(c_3)
                1 -> playNote(d_3)
                2 -> playNote(f_3)
                3 -> playNote(g_3)
                4 -> playNote(g_sharp_3)
                5 -> playNote(a_3)
                6 -> playNote(c_4)
                7 -> playNote(d_4)
                8 -> playNote(f_4)
            }
        }
    }

    var playingSound: String? = null
    private fun playNote(sound: String) {
//        playingSound?.let { player.stopSound(it) }
//        playingSound = sound
        player.playSound(player.location, sound, 1f, 1f)
    }

    fun stopPlaying() {
        chair.remove()
        playerInputs.unregisterObserver(this)
    }

    companion object {
        val e_2 =       "punchtree:instrument.guitar.e_2"
        val f_2 =       "punchtree:instrument.guitar.f_2"
        val f_sharp_2 = "punchtree:instrument.guitar.f_sharp_2"
        val g_2 =       "punchtree:instrument.guitar.g_2"
        val g_sharp_2 = "punchtree:instrument.guitar.g_sharp_2"
        val a_2 =       "punchtree:instrument.guitar.a_2"
        val a_sharp_2 = "punchtree:instrument.guitar.a_sharp_2"
        val b_2 =       "punchtree:instrument.guitar.b_2"
        val c_3 =       "punchtree:instrument.guitar.c_3"
        val c_sharp_3 = "punchtree:instrument.guitar.c_sharp_3"
        val d_3 =       "punchtree:instrument.guitar.d_3"
        val d_sharp_3 = "punchtree:instrument.guitar.d_sharp_3"
        val e_3 =       "punchtree:instrument.guitar.e_3"
        val f_3 =       "punchtree:instrument.guitar.f_3"
        val f_sharp_3 = "punchtree:instrument.guitar.f_sharp_3"
        val g_3 =       "punchtree:instrument.guitar.g_3"
        val g_sharp_3 = "punchtree:instrument.guitar.g_sharp_3"
        val a_3 =       "punchtree:instrument.guitar.a_3"
        val a_sharp_3 = "punchtree:instrument.guitar.a_sharp_3"
        val b_3 =       "punchtree:instrument.guitar.b_3"
        val c_4 =       "punchtree:instrument.guitar.c_4"
        val c_sharp_4 = "punchtree:instrument.guitar.c_sharp_4"
        val d_4 =       "punchtree:instrument.guitar.d_4"
        val d_sharp_4 = "punchtree:instrument.guitar.d_sharp_4"
        val e_4 =       "punchtree:instrument.guitar.e_4"
        val f_4 =       "punchtree:instrument.guitar.f_4"
        val f_sharp_4 = "punchtree:instrument.guitar.f_sharp_4"
        val g_4 =       "punchtree:instrument.guitar.g_4"
        val g_sharp_4 = "punchtree:instrument.guitar.g_sharp_4"
        val a_4 =       "punchtree:instrument.guitar.a_4"
        val a_sharp_4 = "punchtree:instrument.guitar.a_sharp_4"
        val b_4 =       "punchtree:instrument.guitar.b_4"
        val notes = listOf(
            e_2, f_2, f_sharp_2, g_2, g_sharp_2, a_2, a_sharp_2, b_2,
            c_3, c_sharp_3, d_3, d_sharp_3, e_3, f_3, f_sharp_3, g_3, g_sharp_3, a_3, a_sharp_3, b_3,
            c_4, c_sharp_4, d_4, d_sharp_4, e_4, f_4, f_sharp_4, g_4, g_sharp_4, a_4, a_sharp_4, b_4
        )
    }
}
