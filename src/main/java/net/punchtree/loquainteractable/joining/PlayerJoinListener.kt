package net.punchtree.loquainteractable.joining

import net.kyori.adventure.text.Component.text
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.joining.splash.CameraKeyframe
import net.punchtree.loquainteractable.joining.splash.CameraTrack
import net.punchtree.loquainteractable.joining.splash.Cinematic
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInputEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

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
    }
}

class SplashScreenManager : Listener {

    // TODO block any commands and use of chat while in splash screen

    internal var onExitSplashScreen : ((Player) -> Unit)? = null

    private val splashCameraTracks by lazy {
        // TODO replace this with data stored on the world PDC not in debugvars
        val trackStart1 = DebugVars.getLocation("cinematic-track-start-1", Housings.DEFAULT_HOUSING_SPAWN)
        val trackEnd1 = DebugVars.getLocation("cinematic-track-end-1", Housings.DEFAULT_HOUSING_SPAWN)
        val trackStart2 = DebugVars.getLocation("cinematic-track-start-2", Housings.DEFAULT_HOUSING_SPAWN)
        val trackEnd2 = DebugVars.getLocation("cinematic-track-end-2", Housings.DEFAULT_HOUSING_SPAWN)
        val keyframe1 = CameraKeyframe(trackStart1, 0)
        val keyframe2 = CameraKeyframe(trackEnd1, 5000)
        val keyframe3 = CameraKeyframe(trackStart2, 0)
        val keyframe4 = CameraKeyframe(trackEnd2, 5000)
        listOf(
            CameraTrack(sortedSetOf(keyframe1, keyframe2)),
            CameraTrack(sortedSetOf(keyframe3, keyframe4))
        )
    }

    private val splashScreenPlayers = mutableSetOf<LoquaPlayer>()

    internal fun showSplashScreen(player: LoquaPlayer) {
        splashScreenPlayers.add(player)
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
        if (event.input.isJump && splashScreenPlayers.contains(event.player)) {
            val loquaPlayer = event.player as LoquaPlayer // We know it's a LoquaPlayer because it's in the set
            cleanupSplashScreen(loquaPlayer)
            loquaPlayer.isInSplashScreen = false
            onExitSplashScreen?.invoke(loquaPlayer)
        }
    }

    private fun cleanupSplashScreen(player: Player) {
        Cinematic.stopCinematic(player)
        splashScreenPlayers.remove(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        cleanupSplashScreen(event.player)
        // Don't reset loquaPlayer.isInSplashScreen,
        // because we don't want other quit listeners to think they were actually playing
    }
}