package net.punchtree.loquainteractable.joining

import net.kyori.adventure.text.Component
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.loquainteractable.player.character.select.CharacterSelectManager
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

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

