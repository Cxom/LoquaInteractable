package net.punchtree.loquainteractable.player

import net.punchtree.loquainteractable.staff.commands.LuckPermsNode

data object LoquaPermissions {
    internal const val STAFF = "loqua.staff"

    enum class StaffRole {

        ADMINISTRATOR,
        // WHILE INDEV ON BUILD SERVER - REPLACE WITH REAL STAFF ROLES IN PRODUCTION
        ARCHITECT,
        BUILDER,
        EXPLORER;

        companion object {
            fun getRoleFor(loquaPlayer: LoquaPlayer): StaffRole =
                when {
                    loquaPlayer.hasPermission("$STAFF.explorer") -> EXPLORER
                    loquaPlayer.hasPermission("$STAFF.builder") -> BUILDER
                    loquaPlayer.hasPermission("$STAFF.architect") -> ARCHITECT
                    loquaPlayer.hasPermission("$STAFF.administrator") -> ADMINISTRATOR
                    else -> throw IllegalStateException("Staff member ${loquaPlayer.name} has no staff role!")
                }
        }
        fun getPermissionGroup(): LuckPermsNode {
            return LuckPermsNode.builder("loqua.staff.${name.lowercase()}").build()
        }
    }
}