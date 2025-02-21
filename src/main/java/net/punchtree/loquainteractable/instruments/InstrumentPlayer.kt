package net.punchtree.loquainteractable.instruments

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.input.PlayerInputs
import net.punchtree.loquainteractable.input.PlayerInputsObserver
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Material
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

class InstrumentPlayer(
    val player: Player,
    val instrument: Instruments.Instrument,
    private val playerInputs: PlayerInputs
) : PlayerInputsObserver {

    /* Next steps for instruments
     * - Make playingSound happen on the world, and stop playing happen for all nearby entities
     * - Fill the hotbar slot when playing with an instrument, restore on quit/death/stop playing
     * - Really think about how player death interacts with these mechanics. We handle server disabling well, but not quitting or death
     * - Add instructions for how to play to the lore of instruments
     * - Add actionbar visuals to help with understanding note layout and current string
     * - Look at adding default minecraft instruments!
     * - Make the commands for playing instruments generated from the available instruments
     * - Add a way to play an instrument and stop playing without using commands
     */

    /** this value, if true, disables hotbar slot resetting, makes jump repeat the last note, and disables
     *  shifting up and down strings relatively with shift and jump*/
    private val isJumpDoesRepeat = DebugVars.getBoolean("instrument-isJumpDoesRepeat", false)

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
            it.setMetadata("instrument", FixedMetadataValue(LoquaInteractablePlugin.instance, "instrument_stool")) // the metadata value here is unused
        }
        playerInputs.registerObserver(this)
        if (!isJumpDoesRepeat) {
            player.inventory.heldItemSlot = 0
        }
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
    private val quantizeAmount = DebugVars.getInteger("instrument-quantizeAmount", 10)
    private val quantizePeriod = DebugVars.getInteger("instrument-quantizePeriod", 50)
    private fun doFifthsNumberStringing(updateType: PlayerInputs.PlayerInputsUpdateType, inputs: PlayerInputs) {

        if (inputs.left) {
            fns_currString = 0
        } else if (inputs.backward) {
            fns_currString = 1
        } else if (inputs.right) {
            fns_currString = 2
        } else if (updateType == PlayerInputs.PlayerInputsUpdateType.SWAP_HANDS) {
            fns_currString = 3
        }
        if (inputs.jump && !isJumpDoesRepeat) {
            fns_currString = min(fns_currString + 1, 3)
        }
        if (inputs.shift && !isJumpDoesRepeat) {
            fns_currString = max(fns_currString - 1, 0)
        }
        if (inputs.forward) {
            stopNote()
        }
        if (updateType == PlayerInputs.PlayerInputsUpdateType.CHANGE_HOTBAR_SLOT && inputs.heldItemSlot != 0 || (inputs.jump && isJumpDoesRepeat)) {
            val fret = inputs.heldItemSlot

            val noteIndex = (fns_currString * 7 + fret - 1)
                .coerceAtMost(instrument.notes().lastIndex)

            val millisUntilNextPeriod = quantizePeriod - (System.currentTimeMillis() % quantizePeriod)
            if (DebugVars.getBoolean("instrument-do-quantizing", false)
                && millisUntilNextPeriod <= quantizeAmount) {
                // TODO quantization would go here, but because it runs at a frequency faster than tick,
                //  it needs to be applied directly to the outgoing packet - all asynchronous
                Thread.sleep(millisUntilNextPeriod) // LOL just freeze the world to make the mortals be on beat
                playNote(instrument.notes()[noteIndex])
            } else {
                playNote(instrument.notes()[noteIndex])
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
            playNote(instrument.notes()[string + fret])
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
        val keyOffset = 8
        if (updateType == PlayerInputs.PlayerInputsUpdateType.CHANGE_HOTBAR_SLOT) {
            when (inputs.heldItemSlot) {
                0 -> playNote(instrument.notes()[0 + keyOffset])
                1 -> playNote(instrument.notes()[2 + keyOffset])
                2 -> playNote(instrument.notes()[5 + keyOffset])
                3 -> playNote(instrument.notes()[7 + keyOffset])
                4 -> playNote(instrument.notes()[8 + keyOffset])
                5 -> playNote(instrument.notes()[9 + keyOffset])
                6 -> playNote(instrument.notes()[12 + keyOffset])
                7 -> playNote(instrument.notes()[14 + keyOffset])
                8 -> playNote(instrument.notes()[17 + keyOffset])
            }
        }
    }

    private var playingSound: String? = null
    private fun playNote(sound: String) {
        if (!instrument.letRing()) {
            stopNote()
            playingSound = sound
        }
        player.playSound(player.location, sound, 1f, 1f)
    }

    private fun stopNote() {
        playingSound?.let { player.stopSound(it) }
        playingSound = null
    }

    fun stopPlaying() {
        chair.remove()
        playerInputs.unregisterObserver(this)
    }
}
