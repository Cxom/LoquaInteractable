package net.punchtree.loquainteractable.listeners

import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.get
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.loquainteractable.text.LoquaTextColors
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent

//TODO register
class PlayerDeathAndRespawnListener : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = LoquaPlayerManager[event.entity]

        if (player.isInStaffMode()) {
            player.sendMessage(LoquaTextColors.warning("Why are you dying in staff mode??"))
        }

        event.setShouldDropExperience(false)
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = LoquaPlayerManager[event.player]

        if (player.isInStaffMode()) {
            player.sendMessage(LoquaTextColors.warning("Why are you respawning in staff mode??"))
            return
        }

        val clinicToRespawnAt = calculateNearestClinic(player)

        event.respawnLocation = clinicToRespawnAt
        // TODO what do the rest of the options and fields on this event mean/are used for?
    }

    private fun calculateNearestClinic(player: LoquaPlayer): Location {
        // TODO in order to have multiple clinic spawns be configurable, we
        //  need support for LOCATION_ARRAY in the PdcCommand
        //  We need support for ARRAY DataTypes in general
        //  BUT, it would actually be nice if there was also a mod system for setting these variables as well
        //  THEN, they can't even be modified in game unless the command system is fleshed out anyway as a fallback
        //  Building a mod is a hard but worthwhile undertaking
        //  need to strategize and prioritize here
        return checkNotNull(player.world.persistentDataContainer.get(
            LoquaDataKeys.World.TEST_CLINIC_RESPAWN
        )) {
            "No clinic respawn location set!"
        }
    }

}