package net.punchtree.loquainteractable.joining

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.getOrDefault
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.joining.splash.CameraKeyframe
import net.punchtree.loquainteractable.joining.splash.CameraTrack
import net.punchtree.loquainteractable.joining.splash.Cinematic
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.loquainteractable.ui.CameraOverlays
import net.punchtree.loquainteractable.ui.Fade
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInputEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class SplashScreenManager : Listener {

    // TODO block any commands and use of chat while in splash screen

    internal var onExitSplashScreen : ((Player) -> Unit)? = null

    private val splashScreenPlayers = mutableSetOf<UUID>()

    internal fun startSplashScreen(player: LoquaPlayer) {
        require(!splashScreenPlayers.contains(player.uniqueId)) {
            "Player ${player.name} is already in the splash screen!"
        }

        splashScreenPlayers.add(player.uniqueId)
        player.isInSplashScreen = true
        Cinematic.startCinematic(player, splashCameraTracks)
        // TODO make a convenience method for this
        player.setCameraOverlay(CameraOverlays.BLACK_OUT)

        // TODO write an action bar utility for sending long running action bars, flashing bars
        //  but before writing it, look into preexisting solutions
        // TODO make flashing text using subtitles and custom fonts for height offsetting
        player.sendActionBar(Component.text("Press Space to Play!"))
    }

    internal fun fadeInOnClientLoadedWorld(player: LoquaPlayer) {
        // The cinematic has already started - do the fade in, and then set the helmet

        // TODO make sure that we don't call fadeIn
        //  if the cinematic is actively fading out - instead, just wait to remove the helmet
        //  until the end of the current track
        val splashCinematic = checkNotNull(Cinematic.getCinematicFor(player.craftPlayer())) {
            "Player ${player.name} is in splash screen but has no cinematic!"
        }
        if (splashCinematic.isFadingOut) {
            // TODO maybe we should just wait to set the overlay until the cinematic is done fading out
            //  but then we need to make sure that we don't call fadeIn again
            return
        }

        Fade.fadeIn(player, DebugVars.getInteger("splash-fade-in-millis", 3000).toLong())
        player.setCameraOverlay(CameraOverlays.LOQUA_SPLASH)
    }

    @Suppress("UnstableApiUsage")
    @EventHandler
    fun onPlayerInput(event: PlayerInputEvent) {
        val loquaPlayer = LoquaPlayerManager[event.player]
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
            cleanupSplashScreen(loquaPlayer)
            loquaPlayer.isInSplashScreen = false
            onExitSplashScreen?.invoke(loquaPlayer)
        }
    }

    private fun cleanupSplashScreen(player: Player) {
        Cinematic.stopCinematic(player)
        player.stopSound(SPLASH_SCREEN_MUSIC)
        splashScreenPlayers.remove(player.uniqueId)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (splashScreenPlayers.contains(event.player.uniqueId)) {
            // TODO do we maybe need to handle this in the cinematic itself?????
            //  what if we forget somewhere else?????
            //  maybe we should just have a single quit manager, that handles all possible player states???
            //  but then something still ought to check on the cinematic manager
            //  we should probably operate from the perspective of expecting everything that uses a cinematic to
            //  clean up the cinematic, but then verify that cinematics and other global managers are actually cleaned up
            //  trust, but verify
            cleanupSplashScreen(event.player)
        }
        // Don't reset loquaPlayer.isInSplashScreen,
        // because we don't want other quit listeners to think they were actually playing
    }

    companion object {
        val SPLASH_SCREEN_MUSIC = Sound.sound(org.bukkit.Sound.MUSIC_DISC_WAIT, Sound.Source.MASTER, 1f, 1f)

        // TODO make this private once done testing
        internal val splashCameraTracks by lazy {
            // TODO replace this with data stored on the world PDC not in debugvars
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