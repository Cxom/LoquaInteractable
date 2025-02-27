package net.punchtree.loquainteractable.joining

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.getOrDefault
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.joining.splash.CameraKeyframe
import net.punchtree.loquainteractable.joining.splash.CameraTrack
import net.punchtree.loquainteractable.joining.splash.Cinematic
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInputEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

class PlayerJoinListener(
    private val playerInputsManager: PlayerInputsManager,
    private val splashScreenManager: SplashScreenManager
) : Listener {

    init {
        // TODO this dependency is kind of ugly - any other ideas?
        //  maybe an event (either bukkit, or our own basic observable) ?
        splashScreenManager.onExitSplashScreen =:: onPlayerExitSplashScreen
       // look ma, I discovered a new operator! ^
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(Component.empty())

        val loquaPlayer = LoquaPlayerManager.initializePlayer(event.player)
        playerInputsManager.initializeInputs(event.player.uniqueId)

        if (loquaPlayer.isInStaffMode()) {
            // TODO as we add characters, players in STAFF MODE should not have any character, just their skin
            //  we need to account for that
            return
        }

        // Not a staff member
        splashScreenManager.showSplashScreen(loquaPlayer)
    }

    private fun onPlayerExitSplashScreen(player: Player) {
        require(player.isConnected)
        val loquaPlayer = LoquaPlayerManager[player]
        require(!loquaPlayer.isInStaffMode()) {
            // Unclear what we would do with their inventory in this case
        }

        // This WILL reset their inventory if we're not saving it
        loquaPlayer.restoreInventoryToLastSave()

        /** Following logic is what I'm thinking:
         *
         *  If not seen intro cutscene -> show intro cutscene, else don't, then
         *  Show character select
         *  In character select, can choose existing character if they have one, or create new (only option if you've never made one) if you have open slots
         *  Upon character creation success -> back to character select screen
         *  Upon character select -> enter game (teleport to housing)
         *
         */

        loquaPlayer.teleport(Housings.DEFAULT_HOUSING_SPAWN)
        loquaPlayer.gameMode = GameMode.ADVENTURE
    }
}

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
        val track1 = CameraTrack(sortedSetOf(
            CameraKeyframe(track1Start, 0),
            CameraKeyframe(track1End, track1Duration)
        ))
        val track2 = CameraTrack(sortedSetOf(
            CameraKeyframe(track2Start, 0),
            CameraKeyframe(track2End, track2Duration)
        ))
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
        player.sendActionBar(text("Press Space to Play!"))
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