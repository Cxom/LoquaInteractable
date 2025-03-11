package net.punchtree.loquainteractable.staff.commands

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.luckperms.api.LuckPermsProvider
import net.punchtree.loquainteractable.data.LoquaDataKeys
import net.punchtree.loquainteractable.data.get
import net.punchtree.loquainteractable.data.remove
import net.punchtree.loquainteractable.data.set
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.player.LoquaPermissions
import net.punchtree.loquainteractable.player.LoquaPlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias LuckPermsNode = net.luckperms.api.node.Node

object StaffModeCommand : CommandExecutor {

    /**
     * Staff mode requires storing the following information to the player:
     *  - Last location in player mode - save for session (until server restart)
     *  - Toggle for resetting spawn on join - save persistently to player data
     *  - Player mode inventory contents - save persistently to player data
     */

    // TODO store and offer an option for going back to the last STAFF MODE location

    // TODO foolproof staff role fetching, saving, and checking against changes to the underlying permissions
    //  if a user lacks a staff role permission, or has a different one to the group they are in, their group
    //  should be removed or updated to match. trust the loqua.staff.<STAFF_ROLE> permission as authoritative

    data object Messages {
        // staff mode messages
        val ALREADY_IN_STAFF_MODE = text("You are already in staff mode.").color(RED)
        // TODO clickable component on '/staff-mode confirm'
        val IN_COMBAT_WARNING = text("You are currently in combat! Run '/staff-mode confirm' to confirm you really want to enter staff mode (it will be logged).").color(RED)
        val NOW_IN_STAFF_MODE = text("You are now in staff mode.").color(GOLD)

        // unstaff mode messages
        val ALREADY_NOT_IN_STAFF_MODE = text("You are not in staff mode!").color(RED)
        val NOW_EXITED_STAFF_MODE = text("You have exited staff mode.").color(GOLD)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if ( sender !is Player ) return false
        val loquaPlayer = LoquaPlayerManager[sender]

        if (!loquaPlayer.isStaffMember()) return true

        if (label.equals("staff-mode", true)) {
            enterStaffMode(loquaPlayer, args)
        } else if (label.equals("unstaff-mode", true)) {
            exitStaffMode(loquaPlayer)
        }

        return true
    }

    private fun enterStaffMode(loquaPlayer: LoquaPlayer, args: Array<out String>) {
        if (loquaPlayer.isInStaffMode()) {
            loquaPlayer.sendMessage(Messages.ALREADY_IN_STAFF_MODE)
            return
        }

        if (isInCombat(loquaPlayer)) {
            if (isWithoutConfirmation(args)) {
                // TODO text styling utilities
                loquaPlayer.sendMessage(Messages.IN_COMBAT_WARNING)
                return
            }

            trackStaffMemberEnteredStaffModeFromCombat(loquaPlayer)
        }

        saveAndRemovePlayModeState(loquaPlayer)

        // make fly
        makeFly(loquaPlayer)
        loquaPlayer.isInvulnerable = true
        // TODO make spectator in production
        loquaPlayer.gameMode = GameMode.CREATIVE

        loquaPlayer.persistentDataContainer.set(LoquaDataKeys.Player.IS_IN_STAFF_MODE, true)

        // gives them their staff permission group as parent
        giveStaffPermissions(loquaPlayer)

        // restore their staff mode inventory
        restoreStaffModeInventory(loquaPlayer)

        // message them
        loquaPlayer.sendMessage(Messages.NOW_IN_STAFF_MODE)
    }

    private fun makeFly(loquaPlayer: LoquaPlayer) {
        loquaPlayer.allowFlight = true
        loquaPlayer.isFlying = true
    }

    // TODO probably want to save the staff mode inventory too
    private fun exitStaffMode(staffMember: LoquaPlayer) {
        if (!staffMember.isInStaffMode()) {
            staffMember.sendMessage(Messages.ALREADY_NOT_IN_STAFF_MODE)
            return
        }

        // save their staff mode inventory
        saveStaffModeInventory(staffMember)

        // remove their staff permissions
        removeStaffPermissions(staffMember)

        // if valid last-play-location, teleport there
        // else teleport to their housing spawn
        // this behavior gives staff a slight ability over default players since they don't have to spawn in at their housing every time
        // very minor, just a good reminder to make sure we trust our staff
        // if we want total guarding against staff abuse (I believe it's a good practice, generally) we could clear this value periodically
        // TODO it's a cool idea to create a timestamped generic datatype that wraps others and includes a timestamp - useful generally
        // TODO move this to ::restorePlayModeState
        staffMember.persistentDataContainer.get(LoquaDataKeys.Player.STAFF_LAST_PLAY_MODE_LOCATION)?.let {
            staffMember.teleport(it)
            staffMember.persistentDataContainer.remove(LoquaDataKeys.Player.STAFF_LAST_PLAY_MODE_LOCATION)
        } ?: run {
            // TODO generalize this to a proper spawn-in on join system
            staffMember.teleport(Housings.DEFAULT_HOUSING_SPAWN)
        }

        // give them their play mode inventory and state
        restorePlayModeState(staffMember)

        // remove the staff mode state
        staffMember.persistentDataContainer.remove(LoquaDataKeys.Player.IS_IN_STAFF_MODE)

        // message them
        staffMember.sendMessage(Messages.NOW_EXITED_STAFF_MODE)
    }

    private fun saveStaffModeInventory(staffMember: LoquaPlayer) {
        staffMember.persistentDataContainer.set(
            LoquaDataKeys.Player.STAFF_MODE_INVENTORY,
            staffMember.inventory.contents
        )
    }

    private fun restoreStaffModeInventory(staffMember: LoquaPlayer) {
        staffMember.persistentDataContainer.get(LoquaDataKeys.Player.STAFF_MODE_INVENTORY)?.let {
            staffMember.inventory.contents = it
        }
    }

    private fun restorePlayModeState(staffMember: LoquaPlayer) {
        staffMember.restoreInventoryToLastSave()
        // make them not fly
        staffMember.isFlying = false
        staffMember.allowFlight = false

        staffMember.isInvulnerable = false

        staffMember.gameMode = GameMode.ADVENTURE
    }

    private fun saveAndRemovePlayModeState(loquaPlayer: LoquaPlayer) {
        saveStaffLastPlayModeLocation(loquaPlayer)
        saveAndRemoveGameplayInventory(loquaPlayer)
        // TODO health, hunger, saturation, other status effects
    }

    private fun saveAndRemoveGameplayInventory(loquaPlayer: LoquaPlayer) {
        loquaPlayer.saveInventory()
        loquaPlayer.inventory.clear()
    }

    private fun saveStaffLastPlayModeLocation(staffMember: LoquaPlayer) {
        staffMember.persistentDataContainer.set(LoquaDataKeys.Player.STAFF_LAST_PLAY_MODE_LOCATION, staffMember.location)
    }

    private fun isWithoutConfirmation(args: Array<out String>): Boolean {
        return args.isEmpty() || args[0].lowercase() != "confirm"
    }

    private fun giveStaffPermissions(loquaPlayer: LoquaPlayer) {
        // The idea for this is that each staff member has a permission custom assigned to this that follows the pattern:
        // loqua.staff.<STAFF_ROLE>
        // When we give them staff permissions, we check for any such permission
        // and then give them the corresponding *group* permission group.<STAFF_ROLE>
        // inverse to remove
        val staffRole = checkNotNull(LoquaPermissions.StaffRole.getRoleFor(loquaPlayer)) {
            "Staff member ${loquaPlayer.name} has no staff role!"
        }

        val permsApi = LuckPermsProvider.get()
        val user = permsApi.getPlayerAdapter(Player::class.java).getUser(loquaPlayer)

        user.data().add(staffRole.getPermissionGroup())
    }

    private fun removeStaffPermissions(loquaPlayer: LoquaPlayer) {
        val permsApi = LuckPermsProvider.get()
        val user = permsApi.getPlayerAdapter(Player::class.java).getUser(loquaPlayer)

        val staffRole = checkNotNull(LoquaPermissions.StaffRole.getRoleFor(loquaPlayer)) {
            "Staff member ${loquaPlayer.name} has no staff role!"
        }
        user.data().remove(staffRole.getPermissionGroup())
    }

    private fun trackStaffMemberEnteredStaffModeFromCombat(loquaPlayer: LoquaPlayer) {
        TODO("Not yet implemented")
    }

    private fun isInCombat(staffMember: LoquaPlayer): Boolean {
        return false
        TODO("Not yet implemented")
    }
}