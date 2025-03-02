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
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent

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

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
//        Bukkit.broadcastMessage("Player ${event.player.name} login event!")
    }

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

        // TODO preprocessing on the player
        //  teleporting them somewhere inconspicuous, but more likely simply hiding them from everyone
        //  on join, put them in spectator mode, etc)
        //  currently the player remains visible at the start of the first camera track of the splash


        player.gameMode = GameMode.SPECTATOR
        player.isInvisible = true

        // Not a staff member
        splashScreenManager.startSplashScreen(loquaPlayer)
    }

    @EventHandler
    fun onPlayerClientLoadedWorldOrTimedOut(event: PlayerClientLoadedWorldEvent) {
        val player = LoquaPlayerManager[event.player]
        if (player.isInStaffMode()) {
            // see note in onPlayerJoin
            return
        }
//        Bukkit.broadcastMessage("Player ${player.name} client loaded world event - Timeout: ${event.isTimeout}!")

        splashScreenManager.fadeInOnClientLoadedWorld(player)
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.user.profile.uuid?.let { Bukkit.getPlayer(it) }
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
            // TODO Unclear what we would do with their inventory in this case
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
        loquaPlayer.isInvisible = false
    }
}

