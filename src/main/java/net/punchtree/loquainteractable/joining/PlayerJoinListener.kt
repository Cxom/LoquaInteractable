package net.punchtree.loquainteractable.joining

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent
import net.kyori.adventure.text.Component
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.getOrDefault
import net.punchtree.loquainteractable.data.has
import net.punchtree.loquainteractable.data.set
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.loquainteractable.ui.Fade
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class PlayerJoinListener(
    private val playerInputsManager: PlayerInputsManager,
    private val splashScreenManager: SplashScreenManager
) : Listener, PacketListener {

    init {
        // TODO this dependency is kind of ugly - any other ideas?
        //  maybe an event (either bukkit, or our own basic observable) ?
        splashScreenManager.onExitSplashScreen =:: onPlayerExitSplashScreen
       // look ma, I discovered a new operator! ^
    }

    // TODO we may be able to improve the quality of the opening cinematic by putting a
    //  blackout overlay on their head as they're logging in (configuration) or before they log out

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
//        Bukkit.broadcastMessage("Player ${event.player.name} login event!")
    }

    @Suppress("UnstableApiUsage")
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val loquaPlayer = LoquaPlayerManager.initializePlayer(player)
        playerInputsManager.initializeInputs(player.uniqueId)

        if (!loquaPlayer.persistentDataContainer.has(LoquaDataKeys.Player.INVENTORY)) {
            loquaPlayer.persistentDataContainer.set(LoquaDataKeys.Player.IS_IN_STAFF_MODE, true)
            return
        }

        if (loquaPlayer.isInStaffMode()) {
            // TODO as we add characters, players in STAFF MODE should not have any character, just their skin
            //  we need to account for that
            return
        }

//        Bukkit.broadcastMessage("Player ${player.name} join event!")
        event.joinMessage(Component.empty())


        player.teleport(LoquaInteractablePlugin.world.persistentDataContainer.getOrDefault(LoquaDataKeys.World.SPLASH_CINEMATIC_TRACK_1_START, Housings.DEFAULT_HOUSING_SPAWN))

        // TODO preprocessing on the player (blacking out their screen, teleporting them
        //  somewhere inconspicuous, hiding them from other players, spectator mode, etc)

        // TODO test if the player remains visible in their log-out location while in the splash screen
        player.gameMode = GameMode.SPECTATOR


        if (DebugVars.getBoolean("do-join-after-client-loads-world", false)) {
            // nothin'
        } else {
//            player.sendRichMessage("Doing on join from <red>join</red>!");
            onJoin(loquaPlayer)
        }

        // TODO make a convenience method for this
        player.inventory.helmet = ItemStack(Material.BLACK_CONCRETE).also {
            it.editMeta { meta ->
                val equippable = meta.equippable
                equippable.slot = EquipmentSlot.HEAD
                equippable.cameraOverlay = NamespacedKey("punchtree", "font/special/dark")
                meta.setEquippable(equippable)
            }
        }
    }

    @EventHandler
    fun onPlayerClientLoadedWorldOrTimedOut(event: PlayerClientLoadedWorldEvent) {
        val player = LoquaPlayerManager[event.player]
        if (player.isInStaffMode()) {
            // see note in onPlayerJoin
            return
        }
//        Bukkit.broadcastMessage("Player ${player.name} client loaded world event - Timeout: ${event.isTimeout}!")

        if (DebugVars.getBoolean("do-join-after-client-loads-world", false)) {
            object : BukkitRunnable() {
                override fun run() {
                    Fade.fadeIn(player, 3000L)
                    // Don't need to reset the player's helmet because the cinematic will do that
//                event.player.inventory.helmet = null

                    onJoin(player)
                    player.sendRichMessage("Doing on join from <color:#ff5e00>client loaded world</color>!");

                }
            }.runTaskLater(LoquaInteractablePlugin.instance, DebugVars.getInteger("join-fade-in-delay", 0).toLong())
        } else {
            // The cinematic has already started - do the fade in, and then set the helmet
//            object : BukkitRunnable() {
//                override fun run() {
                    Fade.fadeIn(player, 3000L)
                    // TODO if we go with this method, we will need to make sure that we don't call fadeIn
                    //  if the cinematic is actively fading out - instead, just wait to remove the helmet
                    //  until the end of the current track
                    player.inventory.helmet = ItemStack(Material.BLACK_CONCRETE).also {
                        it.editMeta { meta ->
                            val equippable = meta.equippable
                            equippable.slot = EquipmentSlot.HEAD
                            equippable.cameraOverlay = NamespacedKey("punchtree", "font/special/loqua_splash")
                            meta.setEquippable(equippable)
                        }
                    }
//                }
//            }.runTaskLater(LoquaInteractablePlugin.instance, DebugVars.getInteger("join-fade-in-delay", 20).toLong())
        }
    }

    private fun onJoin(loquaPlayer: LoquaPlayer) {

        // Not a staff member
        splashScreenManager.showSplashScreen(loquaPlayer)
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.user.profile.uuid?.let{ Bukkit.getPlayer(it) }
        if (event.packetType == PacketType.Play.Client.PLAYER_LOADED) {
//            val playerLoadedPacket = WrapperPlayClientPlayerLoaded(event)
            if (player != null) {
//                Bukkit.broadcastMessage("Player ${player.name} loaded packet event!");
            } else {
//                Bukkit.broadcastMessage("Player ${event.user.profile.uuid} loaded packet event, but player is null!");
            }
        } else if (DebugVars.getBoolean("packet-debug", false) && player?.name == "Cxom" && event.packetType != PacketType.Play.Client.CLIENT_TICK_END) {
            Bukkit.broadcastMessage("***Received from ${player.name} packet ${event.packetType}!")
        }
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

