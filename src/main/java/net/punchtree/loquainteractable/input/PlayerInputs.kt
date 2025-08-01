package net.punchtree.loquainteractable.input

import net.minecraft.world.entity.player.Input
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

// TODO resolve memory concerns reason for using a uuid when a player would be far more convenient
class PlayerInputs(val uuid: UUID) {

    enum class PlayerInputsUpdateType {
        GENERAL_INPUT,
        SWAP_HANDS,
        CHANGE_HOTBAR_SLOT
    }

    // TODO take this in as a constructor value?
    var heldItemSlot = checkNotNull(Bukkit.getPlayer(uuid)).inventory.heldItemSlot
        private set
    var heldItemSlotLastModified = 0L
        private set
    fun updateHeldItemSlot(
        player: Player,
        newSlot: Int
    ) {
        heldItemSlot = newSlot
        heldItemSlotLastModified = System.currentTimeMillis()
        notifyObservers(player, this, PlayerInputsUpdateType.CHANGE_HOTBAR_SLOT)
    }

    var lastSwappedHands = 0L
        private set
    fun updateSwapHands(player: Player) {
        lastSwappedHands = System.currentTimeMillis()
        notifyObservers(player, this, PlayerInputsUpdateType.SWAP_HANDS)
    }

    var forward: Boolean = false
        private set
    var backward: Boolean = false
        private set
    var left: Boolean = false
        private set
    var right: Boolean = false
        private set

    var jump: Boolean = false
        private set
    var shift: Boolean = false
        private set
    var sprint: Boolean = false
        private set

    fun updateVehicleInput(
        player: Player,
        forward: Boolean,
        backward: Boolean,
        left: Boolean,
        right: Boolean,
        jump: Boolean,
        shift: Boolean,
        sprint: Boolean
    ) {
        this.forward = forward
        this.backward = backward
        this.left = left
        this.right = right
        this.jump = jump
        this.shift = shift
        this.sprint = sprint
        notifyObservers(player, this, PlayerInputsUpdateType.GENERAL_INPUT)
    }

    // XXX TODO We need to figure out how reseting inputs happens. If a packet does
    // not line up with the loop,
    // then for which controls do we maintain what was last input and which do we
    // reset to their default values

    // TODO we now have the ability to take in inputs both directly from packets and through the paper api
    //  it would be useful to do some sort of overhead test where we measure if there's a delay in the api
    //  processing. If not, switch everything to use events

    override fun toString(): String {
        return "PlayerInputs(uuid=$uuid, forward=$forward, backward=$backward, left=$left, right=$right, jump=$jump, sneak=$shift, sprint=$sprint, heldItemSlot=$heldItemSlot, heldItemSlotLastModified=$heldItemSlotLastModified, lastSwappedHands=$lastSwappedHands)"
    }

    // TODO This is just a freakin event. Use that
    fun updateVehicleInput(player: Player, input: Input) {
        this.forward = input.forward
        this.backward = input.backward
        this.left = input.left
        this.right = input.right
        this.jump = input.jump
        this.shift = input.shift
        this.sprint = input.sprint
        notifyObservers(player, this, PlayerInputsUpdateType.GENERAL_INPUT)
    }

//    fun updateVehicleInput(player: Player, input: org.bukkit.Input) {
//        this.forward = input.isForward
//        this.backward = input.isBackward
//        this.left = input.isLeft
//        this.right = input.isRight
//        this.jump = input.isJump
//        this.shift = input.isSneak
//        this.sprint = input.isSprint
//        notifyObservers(player, this, PlayerInputsUpdateType.GENERAL_INPUT)
//    }

    val observers: MutableList<PlayerInputsObserver> = mutableListOf()
    fun registerObserver(observer: PlayerInputsObserver) {
        observers.add(observer)
    }

    internal fun notifyObservers(player: Player, inputs: PlayerInputs, updateType: PlayerInputsUpdateType) {
        observers.forEach {
            it.onInputsUpdate(player, inputs, updateType)
        }
    }

    // TODO if objects are not careful to unregister themselves, we will leak memory
    //  perhaps observers should be weakly referenced
    fun unregisterObserver(observer: PlayerInputsObserver) {
        observers.remove(observer)
    }

}

interface PlayerInputsObserver {
    fun onInputsUpdate(player: Player, inputs: PlayerInputs, updateType: PlayerInputs.PlayerInputsUpdateType)
}
