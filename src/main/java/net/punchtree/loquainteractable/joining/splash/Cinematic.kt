package net.punchtree.loquainteractable.joining.splash

import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component.text
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.phys.Vec3
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.craftPlayer
import net.punchtree.loquainteractable.ui.Fade.blackOut
import net.punchtree.loquainteractable.ui.Fade.fadeIn
import net.punchtree.loquainteractable.ui.Fade.fadeOut
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.math.roundToInt

const val ONE_TICK_MILLIS = 50L

const val TEXT_DISPLAY_TEXT_ID = 23

class Cinematic private constructor(val player: CraftPlayer, private val cameraTracks: List<CameraTrack>) {

    private val fakeCameraEntityId = Entity.nextEntityId()

    private val fadeOutTimeMillis = DebugVars.getInteger("cinematic-fade-out-time", 500).toLong()

    /** How long after a full black out before teleporting the player (adjust to prevent visual hiccups where the teleport is visible) **/
//    private val fullBlackOutBufferTime = DebugVars.getInteger("cinematic-full-black-out-buffer-time", 50).toLong()

    private val fadeInTimeMillis = DebugVars.getInteger("cinematic-fade-in-time", 500).toLong()

    private var currentTrackIndex = 0

    private var currentTrack = cameraTracks[0]
    private var currentKeyframeIndex = 0
    private var currentKeyframe = currentTrack.keyframes.first()
    private var currentTrackStartTimeMillis = System.currentTimeMillis() + ONE_TICK_MILLIS
    private var endOfCurrentKeyframe = currentTrackStartTimeMillis + currentKeyframe.timeMillis
    private var endOfCurrentTrack = currentTrackStartTimeMillis + currentTrack.keyframes.last().timeMillis
    private var isFadingOut = false

    private var isDestroyed = false

    private var startTime = System.currentTimeMillis()
    private fun runningTime() = System.currentTimeMillis() - startTime

    init {
        validateCameraTracks()

        blackOut(player)

        player.gameMode = GameMode.SPECTATOR

        spawnFakeCamera(currentKeyframe.location, 0)

        startTrackToNextKeyframe()
    }

    private fun validateCameraTracks() {
        // We must have at least one track and each track must have at least two keyframes
        require(cameraTracks.isNotEmpty()) { "Cinematic must have at least one track" }
        cameraTracks.withIndex().forEach {(index, it) ->
            require(it.keyframes.size >= 2) { "Each track must have at least two keyframes. Track $index doesn't." }
            require(it.keyframes.first().timeMillis == 0L) { "The first keyframe of each track must occur at time 0. Track $index's first keyframe doesn't." }
            require(it.keyframes.none { it.timeMillis < 0L }) { "Keyframes must occur at time >= 0. Track $index has a keyframe that doesn't." }
        }
    }

    private fun teleportFakeCamera(location: Location, durationTicks: Int) {
        if (durationTicks == 0) {
            // There is no way to avoid packets potentially being processed on the client out of order, so create a new camera on instant teleport
            // I'm pretty sure the reusing of the entity id prevents this from actually leaving behind old entities on the client, but not sure
            spawnFakeCamera(location, 0)
            return
        }

        val metadataPacket = ClientboundSetEntityDataPacket(
            fakeCameraEntityId,
            listOf(SynchedEntityData.DataValue(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID.id, EntityDataSerializers.INT, durationTicks))
        )
        val teleportPacket = ClientboundTeleportEntityPacket(
            fakeCameraEntityId,
            PositionMoveRotation(
                Vec3(location.x, location.y, location.z),
                Vec3.ZERO,
                location.yaw,
                location.pitch,
            ),
            emptySet(),
            false
        )

        val packetBundle = ClientboundBundlePacket(listOf(metadataPacket, teleportPacket))
        player.handle.connection.send(packetBundle)
    }

    private fun spawnFakeCamera(
        location: Location,
        durationTicks: Int
    ) {
//        player.sendMessage("Creating a new fake camera in teleportFakeCamera")

        val addPacket = ClientboundAddEntityPacket(
            fakeCameraEntityId,
            UUID.randomUUID(),
            location.x,
            location.y,
            location.z,
            location.pitch,
            location.yaw,
            EntityType.TEXT_DISPLAY,
            0,
            Vec3.ZERO,
            0.0
        )
        val metadataPacket = ClientboundSetEntityDataPacket(
            fakeCameraEntityId,
            listOf(
                SynchedEntityData.DataValue(
                    Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID.id,
                    EntityDataSerializers.INT,
                    durationTicks
                ),
                // TODO right now this is just given a name so we can see it, but we should use a transformation combined with a big flat black square or two to prevent disabling the HUD from preventing the fade out
                SynchedEntityData.DataValue(
                    TEXT_DISPLAY_TEXT_ID,
                    EntityDataSerializers.COMPONENT,
                    PaperAdventure.asVanilla(text("camera"))
                )
            )
        )
        val gameModeChangePacket = ClientboundGameEventPacket(
            ClientboundGameEventPacket.CHANGE_GAME_MODE,
            GameMode.SPECTATOR.value.toFloat()
        )
        // constructor takes an entity but we only have an entityId, so we need reflection - ignore the passing of the player to the constructor
        val spectateTheCameraPacket = ClientboundSetCameraPacket(player.handle)
        val cameraIdField = ClientboundSetCameraPacket::class.java.getDeclaredField("cameraId")
        cameraIdField.isAccessible = true
        cameraIdField.setInt(spectateTheCameraPacket, fakeCameraEntityId)

        val packetBundle =
            ClientboundBundlePacket(listOf(addPacket, metadataPacket, gameModeChangePacket, spectateTheCameraPacket))

        // TODO extensions for packets
        player.handle.connection.send(packetBundle)
    }

    private fun startTrackToNextKeyframe() {
        // Two big approaches to try for getting to the next keyframe
        //  1. Do the interpolation ourselves each tick, teleport the entity, let it be smoothed out
        //  2. If the interpolation is linear, simply set the teleport duration to the millis of the next keyframe minus the millis of this one, and then teleport
        //  We'll start by testing approach 2. We will need to do additional work like 1 for bezier/hermite

        if (currentKeyframeIndex == 0) {
            fadeIn(player, fadeInTimeMillis)
            isFadingOut = false
        }

        currentKeyframeIndex += 1
        currentKeyframe = currentTrack.keyframes.elementAt(currentKeyframeIndex)
        endOfCurrentKeyframe = currentTrackStartTimeMillis + currentKeyframe.timeMillis

//        player.sendMessage("Advancing to track ${currentTrackIndex}, keyframe $currentKeyframeIndex (${currentKeyframe.timeMillis})")

        teleportFakeCamera(currentKeyframe.location, calculateCurrentTeleportDuration())
    }

    private fun calculateCurrentTeleportDuration(): Int {
        val currentKeyframeDuration = currentKeyframe.timeMillis - (currentTrack.keyframes.elementAt(currentKeyframeIndex - 1).timeMillis)
        return (currentKeyframeDuration / 50.0).roundToInt()
    }

    private fun advanceToNextTrack() {
        currentTrackIndex = nextTrackIndex()
        currentTrack = cameraTracks[currentTrackIndex]
        currentKeyframeIndex = 0
        currentKeyframe = currentTrack.keyframes.elementAt(currentKeyframeIndex)
        currentTrackStartTimeMillis = System.currentTimeMillis() + ONE_TICK_MILLIS // Add one tick to account for the teleport duration change delay
        endOfCurrentTrack = currentTrackStartTimeMillis + currentTrack.keyframes.last().timeMillis

//        player.sendMessage("Advancing to track $currentTrackIndex")
        teleportFakeCamera(currentKeyframe.location, 0)
    }

    private fun nextTrackIndex(): Int {
        return if (currentTrackIndex == cameraTracks.size - 1) {
            0
        } else {
            currentTrackIndex + 1
        }
    }

    private fun update() {
        // TODO because we've modified the implementation to be packet based, we can do this all async, and via kotlin coroutines

        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis >= endOfCurrentTrack) {
            advanceToNextTrack()
        }

        if (currentTimeMillis >= endOfCurrentKeyframe) {
            startTrackToNextKeyframe()
        }

        if (!isFadingOut && currentTimeMillis + fadeOutTimeMillis /*+ fullBlackOutBufferTime*/ >= endOfCurrentTrack) {
            fadeOut(player, fadeOutTimeMillis)
            isFadingOut = true
        }
    }

    private fun destroy() {
        player.handle.connection.send(ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, player.gameMode.value.toFloat()))
        player.handle.connection.send(ClientboundSetCameraPacket(player.handle))
        player.handle.connection.send(ClientboundRemoveEntitiesPacket(fakeCameraEntityId))

        isDestroyed = true
    }

    companion object {
        private lateinit var cinematicTick: BukkitTask

        private val activeCinematics by lazy {
            mutableMapOf<CraftPlayer, Cinematic>().also { activeCinematics ->
                cinematicTick = object : BukkitRunnable() {
                    override fun run() {
                        val iterator = activeCinematics.iterator()
                        while (iterator.hasNext()) {
                            val (player, cinematic) = iterator.next()
                            if (!player.isConnected) {
                                cinematic.destroy()
                                iterator.remove()
                            }
                        }
                        // TODO there's a weird discrepancy in player objects, where isOnline checks any for the same player and isConnected checks exact connection
                        //  need to understand how joining is processing players - and if it would cause a separate splash cinematic to be started
                        //  in the meantime, removing if the current exact one disconnected is safer than accidentally scheduling two cinematics for the same player
                        activeCinematics.values.forEach { it.update() }
                    }
                }.runTaskTimer(LoquaInteractablePlugin.instance, 0, 1)
            }
        }

        fun stopCinematic(player: Player) {
            activeCinematics.remove(player.craftPlayer())?.destroy()
        }

        fun startCinematic(player: Player, cameraTracks: List<CameraTrack>) {
            activeCinematics.remove(player.player)?.destroy()
            activeCinematics[player.craftPlayer()] = Cinematic(player.craftPlayer(), cameraTracks)
        }
    }

}

class CameraTrack(val keyframes: SortedSet<CameraKeyframe>) {
}

class CameraKeyframe(
    val location: Location,
    /** when the keyframe occurs in the camera track, in millis **/
    val timeMillis: Long
) : Comparable<CameraKeyframe> {
    override fun compareTo(other: CameraKeyframe): Int {
        return (timeMillis - other.timeMillis).toInt()
    }
}
