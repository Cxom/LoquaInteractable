package net.punchtree.loquainteractable.joining

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.has
import net.punchtree.loquainteractable.data.set
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.player.LoquaPermissions
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.scheduler.BukkitRunnable

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
    fun onPlayerLogin(event: PlayerLoginEvent) {
//        Bukkit.broadcastMessage("Player ${event.player.name} login event!")
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val loquaPlayer = LoquaPlayerManager.initializePlayer(player)
        playerInputsManager.initializeInputs(player.uniqueId)

        if (!loquaPlayer.persistentDataContainer.has(LoquaDataKeys.Player.INVENTORY) && loquaPlayer.isStaffMember()) {
            loquaPlayer.persistentDataContainer.set(LoquaDataKeys.Player.IS_IN_STAFF_MODE, true)
        }

        if (loquaPlayer.isInStaffMode()) {
            // TODO as we add characters, players in STAFF MODE should not have any character, just their skin
            //  we need to account for that

            loquaPlayer.sendMessage(text("You are in staff mode (${LoquaPermissions.StaffRole.getRoleFor(loquaPlayer)})!").decorate(TextDecoration.ITALIC).color(NamedTextColor.YELLOW))
            return
        }

//        Bukkit.broadcastMessage("Player ${player.name} join event!")
        // TODO eventually cancel joins for staff too, and decide on what to do for joining notices
        event.joinMessage(Component.empty())

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

        // We're already on the main thread, so I'm not sure why this works/is necessary,
        // but it prevents the "<Player> moved to quickly" warning in console
        object : BukkitRunnable() {
            override fun run() {
                loquaPlayer.teleport(Housings.DEFAULT_HOUSING_SPAWN)
            }
        }.runTask(LoquaInteractablePlugin.instance)

        loquaPlayer.gameMode = GameMode.ADVENTURE
        loquaPlayer.isInvisible = false
    }
}

