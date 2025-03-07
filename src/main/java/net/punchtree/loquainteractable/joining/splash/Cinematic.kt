package net.punchtree.loquainteractable.joining.splash

import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.player.craftPlayer
import net.punchtree.loquainteractable.ui.Fade
import net.punchtree.loquainteractable.ui.Fade.blackOut
import net.punchtree.loquainteractable.ui.Fade.fadeIn
import net.punchtree.loquainteractable.ui.Fade.fadeOut
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.math.roundToInt

const val ONE_TICK_MILLIS = 50L

const val TEXT_DISPLAY_TEXT_ID = 23

class Cinematic private constructor(val player: CraftPlayer, private val cameraTracks: List<CameraTrack>) {

    // TODO maybe make the fading out and in (to/from black) based on a parameter in the camera tracks?

    private val fakeCameraEntityId = Entity.nextEntityId()
    // It is very tempting to try to make the player ride the camera entity and be the sound source as way to keep
    // it continuous, but this is a bad idea - it is important that the emitter be the actually SPECTATED entity
    // i.e., the camera itself
    // it would be more appropriate to actually spectate the audio listener entity, and then call that the camera!
    // the other one is just the interpolator!
    // TODO this is not a listener! doofus! it's an emitter! the spectated entity (the camera) is the audio listener!
    //  see above note about maybe ACTUALLY spectating this thing
    private val fakeAudioListenerEntityId = Entity.nextEntityId()

    private val fadeOutTimeMillis = DebugVars.getInteger("cinematic-fade-out-time", 750).toLong()
    private val fadeInTimeMillis = DebugVars.getInteger("cinematic-fade-in-time", 750).toLong()

    private var currentTrackIndex = 0

    private var currentTrack = cameraTracks[0]
    private var currentKeyframeIndex = 0
    private var currentKeyframe = currentTrack.keyframes.first()
    private var currentTrackStartTimeMillis = System.currentTimeMillis() + ONE_TICK_MILLIS
    private var endOfCurrentKeyframe = currentTrackStartTimeMillis + currentKeyframe.timeMillis
    private var endOfCurrentTrack = currentTrackStartTimeMillis + currentTrack.keyframes.last().timeMillis
    internal var isFadingOut = false
        private set

    private var isDestroyed = false

    private var hasTeleportedInBlack = false
    private val blackoutToLoadChunksMillis = DebugVars.getInteger("cinematic-load-chunks-buffer-millis", 600).toLong()

    private var startTime = System.currentTimeMillis()
    private fun runningTime() = System.currentTimeMillis() - startTime

    init {
        // the passenger will load chunks on every teleport, but the initial camera and interpolator spawn
        // don't seem to, so this makes sure chunks are loaded for the very first camera track, including
        // when this runs for the splash screen immediately after joining
        player.teleport(currentKeyframe.location)

        validateCameraTracks()

        blackOut(player)

        spawnFakeAudioListener(fakeAudioListenerEntityId, player, currentKeyframe.location)

        CameraUtils.spawnFakeCamera(fakeCameraEntityId, player, currentKeyframe.location, 0, fakeAudioListenerEntityId)

        startTrackToNextKeyframe()

        // TODO formalize this into audio cues that come in as a list as part of the camera tracks
        playMusic()
    }

    private fun playMusic() {
        val musicPacket = ClientboundSoundEntityPacket(
            PaperAdventure.resolveSound(
                net.kyori.adventure.sound.Sound.sound(
                    Sound.MUSIC_DISC_RELIC,
                    SoundCategory.MASTER,
                    1.0f,
                    1.0f
                ).name()
            ),
            SoundSource.MASTER,
            player.craftPlayer().handle,
            1.0f,
            1.0f,
            0L,
        )
        val entityIdField = ClientboundSoundEntityPacket::class.java.getDeclaredField("id")
        entityIdField.isAccessible = true
        entityIdField.setInt(musicPacket, fakeAudioListenerEntityId)

        player.handle.connection.send(musicPacket)
    }

    private fun spawnFakeAudioListener(audioListenerEntityId: Int, player: CraftPlayer, location: Location) {
        val addPacket = ClientboundAddEntityPacket(
            audioListenerEntityId,
            UUID.randomUUID(),
            location.x,
            location.y,
            location.z,
            location.pitch,
            location.yaw,
            net.minecraft.world.entity.EntityType.ITEM_DISPLAY,
            0,
            net.minecraft.world.phys.Vec3.ZERO,
            0.0
        )
        player.handle.connection.send(addPacket)
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

        CameraUtils.teleportFakeCamera(fakeCameraEntityId, fakeAudioListenerEntityId, player, currentKeyframe.location, calculateCurrentTeleportDuration())
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
        CameraUtils.teleportFakeCamera(fakeCameraEntityId, fakeAudioListenerEntityId, player, currentKeyframe.location, 0)
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
        // TODO check for isConnected?

        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis >= endOfCurrentTrack) {
            if (!hasTeleportedInBlack) {
                player.teleport(cameraTracks[nextTrackIndex()].keyframes.first().location)
                hasTeleportedInBlack = true
            }

            if (currentTimeMillis >= endOfCurrentTrack + blackoutToLoadChunksMillis) {
                hasTeleportedInBlack = false
                advanceToNextTrack()
                startTrackToNextKeyframe()
            }
        } else if (currentTimeMillis >= endOfCurrentKeyframe) {
            startTrackToNextKeyframe()
        } else if (!isFadingOut && currentTimeMillis + fadeOutTimeMillis >= endOfCurrentTrack) {
            // TODO would it be a minor improvement to make the fadeOutTimeMillis the actual millis until the end of the track,
            //  instead of the specified, to make the timing perfect to what's expected?
            fadeOut(player, fadeOutTimeMillis)
            isFadingOut = true
        }
    }

    private fun destroy() {
        require(!isDestroyed) { "Cinematic already destroyed" }
        CameraUtils.removeCamera(fakeCameraEntityId, player)
        player.handle.connection.send(ClientboundRemoveEntitiesPacket(fakeAudioListenerEntityId))

        Fade.clear(player)
        isDestroyed = true
    }

    fun getMillisUntilEndOfTrack(): Long {
        return endOfCurrentTrack - System.currentTimeMillis()
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
            activeCinematics.remove(player.craftPlayer())?.destroy()
            activeCinematics[player.craftPlayer()] = Cinematic(player.craftPlayer(), cameraTracks)
        }

        fun getCinematicFor(player: Player): Cinematic? {
            return activeCinematics[player.craftPlayer()]
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
