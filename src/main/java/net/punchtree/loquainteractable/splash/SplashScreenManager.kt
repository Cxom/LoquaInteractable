package net.punchtree.loquainteractable.splash

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.getOrDefault
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.loquainteractable.ui.CameraOverlays
import net.punchtree.loquainteractable.ui.Fade
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInputEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class SplashScreenManager {

    // TODO block any commands and use of chat while in splash screen

    // use lateinit to fail fast if this is not set
    internal lateinit var onExitSplashScreen : ((Player) -> Unit)

    private val splashScreenPlayers = mutableSetOf<UUID>()

    internal fun startSplashScreen(player: LoquaPlayer) {
        require(!isInSplashScreen(player.uniqueId)) {
            "Player ${player.name} is already in the splash screen!"
        }

        splashScreenPlayers.add(player.uniqueId)
        Cinematic.startCinematic(player, splashCameraTracks)
        player.setCameraOverlay(CameraOverlays.BLACK_OUT)

        // TODO write an action bar utility for sending long running action bars, flashing bars
        //  but before writing it, look into preexisting solutions
        // TODO make flashing text using subtitles and custom fonts for height offsetting
        player.sendActionBar(Component.text("Press Space to Play!"))
    }

    fun isInSplashScreen(uniqueId: UUID) = splashScreenPlayers.contains(uniqueId)

    internal fun fadeInOnClientLoadedWorld(player: LoquaPlayer) {
        // The cinematic has already started - do the fade in and set the overlay

        val splashCinematic = checkNotNull(Cinematic.getCinematicFor(player.craftPlayer())) {
            "Player ${player.name} is in splash screen but has no cinematic!"
        }
        if (splashCinematic.isFadingOut) {
            // wait to set the overlay until the current camera track is done fading out
            // no need to call fadeIn, start of the next camera track will do that
            object : BukkitRunnable() {
                override fun run() {
                    // isConnected references the same connection, so this won't cause a bug for players that have reconnected in the meantime
                    // player.isOnline WOULD cause that bug
                    if (player.isConnected && isInSplashScreen(player.uniqueId)) {
                        player.setCameraOverlay(CameraOverlays.LOQUA_SPLASH)
                    }
                }
            }.runTaskLater(LoquaInteractablePlugin.instance, splashCinematic.getMillisUntilEndOfTrack() / 50L)
        } else {
            Fade.fadeIn(player, DebugVars.getInteger("splash-fade-in-millis", 3000).toLong())
            player.setCameraOverlay(CameraOverlays.LOQUA_SPLASH)
            return
        }
    }

    internal fun handlePlayerInput(event: PlayerInputEvent) {
        // TODO really REALLY be careful about player tracking and all the places it happens, and that we're not leaking memory
        //  really think about it, don't guess. I think we're on the right track with the player manager, the join and quit listeners,
        //  and the equality implementation on PlayerDecorator, but really think about it and make sure it all makes sense
        //  maybe write tests, if you can figure out what needs to be assured
//        if (event.input.isJump) {
//            Bukkit.broadcastMessage("Detected jump input from ${event.player.name}")
//            Bukkit.broadcastMessage("Player is in splash screen: ${splashScreenPlayers.contains(event.player.uniqueId)}")
//            Bukkit.broadcastMessage("Loqua Player is in splash screen: ${splashScreenPlayers.contains(loquaPlayer.uniqueId)}")
//            Bukkit.broadcastMessage("LoquaPlayer equals CraftPlayer: ${loquaPlayer == event.player}")
//            Bukkit.broadcastMessage("CraftPlayer equals LoquaPlayer: ${event.player == loquaPlayer}")
//            return
//        }
        if (event.input.isJump && splashScreenPlayers.contains(event.player.uniqueId)) {
            val loquaPlayer = LoquaPlayerManager[event.player]
            cleanupSplashScreen(loquaPlayer)
            onExitSplashScreen?.invoke(loquaPlayer)
        }
    }

    private fun cleanupSplashScreen(player: LoquaPlayer) {
        Cinematic.stopCinematic(player)
        player.stopSound(SPLASH_SCREEN_MUSIC)
        player.removeCameraOverlay()
        splashScreenPlayers.remove(player.uniqueId)
    }

    /* This is NOT an eventListener, so that we have a single quit listener, that can be referenced
     * to understand the order in which cleanup happens. It calls this method (and others like it) */
    fun handlePlayerQuit(quitPlayer: LoquaPlayer) {
        if (splashScreenPlayers.contains(quitPlayer.uniqueId)) {
            cleanupSplashScreen(quitPlayer)
        }
    }

    fun onDisable() {
        splashScreenPlayers.forEach { uuid ->
            LoquaPlayerManager.getSafe(uuid)?.let(::cleanupSplashScreen) ?: run {
                LoquaInteractablePlugin.instance.logger.warning("Player with UUID $uuid was in the splash screen but not found in the player manager!")
            }
        }
        splashScreenPlayers.clear()
    }

    companion object {
        val SPLASH_SCREEN_MUSIC = Sound.sound(org.bukkit.Sound.MUSIC_DISC_WAIT, Sound.Source.MASTER, 1f, 1f)

        // TODO make this private once done testing
        internal val splashCameraTracks by lazy {
            val world = LoquaInteractablePlugin.world
            val defaultLocation = Housings.DEFAULT_HOUSING_SPAWN
            val track1Start = world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.SPLASH_CINEMATIC_TRACK_1_START, defaultLocation)
            val track1End = world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.SPLASH_CINEMATIC_TRACK_1_END, defaultLocation)
            val track1Duration = world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.SPLASH_CINEMATIC_TRACK_1_DURATION, 100000).toLong()
            val track2Start = world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.SPLASH_CINEMATIC_TRACK_2_START, defaultLocation)
            val track2End = world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.SPLASH_CINEMATIC_TRACK_2_END, defaultLocation)
            val track2Duration = world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.SPLASH_CINEMATIC_TRACK_2_DURATION, 100000).toLong()
            val track1 = CameraTrack(
                sortedSetOf(
                    CameraKeyframe(track1Start, 0),
                    CameraKeyframe(track1End, track1Duration)
                )
            )
            val track2 = CameraTrack(
                sortedSetOf(
                    CameraKeyframe(track2Start, 0),
                    CameraKeyframe(track2End, track2Duration)
                )
            )
            listOf(track1, track2)
        }
    }
}