package net.punchtree.loquainteractable.joining

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
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInputEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

class SplashScreenManager : Listener {

    // TODO block any commands and use of chat while in splash screen

    internal var onExitSplashScreen : ((Player) -> Unit)? = null

    private val splashCameraTracks by lazy {
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

    private val splashScreenPlayers = mutableSetOf<UUID>()

    internal fun showSplashScreen(player: LoquaPlayer) {
        splashScreenPlayers.add(player.uniqueId)
        player.isInSplashScreen = true
        player.inventory.helmet = ItemStack(Material.BLUE_CONCRETE).also {
            it.editMeta { meta ->
                val equippable = meta.equippable
                equippable.slot = EquipmentSlot.HEAD
                equippable.cameraOverlay = NamespacedKey("punchtree", "font/special/loqua_splash")
                meta.setEquippable(equippable)
            }
        }
        Cinematic.startCinematic(player, splashCameraTracks)
        // TODO write an action bar utility for sending long running action bars, flashing bars
        //  but before writing it, look into preexisting solutions
        // TODO make flashing text using subtitles and custom fonts for height offsetting
        player.sendActionBar(Component.text("Press Space to Play!"))
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
        splashScreenPlayers.remove(player.uniqueId)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        cleanupSplashScreen(event.player)
        // Don't reset loquaPlayer.isInSplashScreen,
        // because we don't want other quit listeners to think they were actually playing
    }
}