package net.punchtree.loquainteractable.outofbody.drone

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.input.PlayerInputs
import net.punchtree.loquainteractable.input.PlayerInputsObserver
import net.punchtree.loquainteractable.input.observeInputsWith
import net.punchtree.loquainteractable.input.unobserveInputsWith
import net.punchtree.loquainteractable.outofbody.OutOfBodyState
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector

class DroneOperator(val player: Player, val drone: Drone) : OutOfBodyState, PlayerInputsObserver {

    // TODO cancel dismounting!!!!!!
    // TODO block interactions - make the ridden entity a flying slime and use velocity?
    // TODO camera overlay
    // TODO range limitation

    private val locationFlyingFrom = player.location.clone()

    private lateinit var displayToRide: Entity
    private lateinit var droneEntity: ItemDisplay

    private lateinit var task: BukkitTask

    @Volatile private var forward = false
    @Volatile private var backward = false
    @Volatile private var left = false
    @Volatile private var right = false
    @Volatile private var jump = false
    @Volatile private var sprint = false

    private var velocity = Vector(0.0, 0.0, 0.0)

    // TODO make this a private constructor and make startFlying a static factory method
    init {
        startFlying()
    }

    private fun startFlying() {
        this.displayToRide = player.world.spawnEntity(
            player.location.clone().add(0.0, 1.0, 0.0),
            EntityType.ITEM_DISPLAY,
            CreatureSpawnEvent.SpawnReason.CUSTOM) {
            it.isVisibleByDefault = false
            it.addPassenger(player)
        }
        this.droneEntity = player.world.spawnEntity(
            player.eyeLocation.clone().add(0.0, 1.0, 0.0).also {
                it.yaw = 0f
                it.pitch = 0f
            },
            EntityType.ITEM_DISPLAY,
            CreatureSpawnEvent.SpawnReason.CUSTOM) {
            it as ItemDisplay
            it.setItemStack(drone.getItemStack())
            it.interpolationDuration = DebugVars.getInteger("loquainteractable.drone.interpolation-duration", 1)
            it.teleportDuration = DebugVars.getInteger("loquainteractable.drone.teleport-duration", 1)
        } as ItemDisplay
        player.gameMode = GameMode.SPECTATOR
        player.spectatorTarget = droneEntity
        player.observeInputsWith(this)

        // TODO hide the player! might need a manager for that? but should someone flying a drone be visible? probably, need to think about how to handle it

        startTask()
    }

    private fun startTask() {
        object : BukkitRunnable() {
            override fun run() {
                val speed = DebugVars.getDecimalAsFloat("loquainteractable.drone.speed", 0.1f)
                val turnSpeed = DebugVars.getDecimalAsFloat("loquainteractable.drone.turn-speed", 1.0f)
                val vertSpeed = DebugVars.getDecimalAsDouble("loquainteractable.drone.vert-speed", 0.1)
                if (forward && !backward) {
                    droneEntity.teleport(droneEntity.location.clone().add(droneEntity.location.direction.multiply(speed)))
                } else if (backward && !forward) {
                    droneEntity.teleport(droneEntity.location.clone().subtract(droneEntity.location.direction.multiply(speed)))
                }
                if (left && !right) {
                    droneEntity.teleport(droneEntity.location.clone().also {
                        it.yaw -= turnSpeed
                    })
                } else if (right && !left) {
                    droneEntity.teleport(droneEntity.location.clone().also {
                        it.yaw += turnSpeed
                    })
                }
                if (jump && !sprint) {
                    droneEntity.teleport(droneEntity.location.clone().add(0.0, vertSpeed, 0.0))
                } else if (sprint && !jump) {
                    droneEntity.teleport(droneEntity.location.clone().subtract(0.0, vertSpeed, 0.0))
                }
            }
        }.runTaskTimer(LoquaInteractablePlugin.instance, 0, 1)
    }

    fun stopFlying() {
        player.gameMode = GameMode.ADVENTURE
        displayToRide.remove()
        droneEntity.remove()
        player.unobserveInputsWith(this)
    }

    // TODO add a sync onInputsUpdate so we can teleport without worrying about stuff? or do this all async? in any case label it on the func(s)
    override fun onInputsUpdate(player: Player, inputs: PlayerInputs, updateType: PlayerInputs.PlayerInputsUpdateType) {
        forward = inputs.forward
        backward = inputs.backward
        left = inputs.left
        right = inputs.right
        jump = inputs.jump
        sprint = inputs.sprint
    }

    override fun exit() {
        stopFlying()
    }

}
