package net.punchtree.loquainteractable.player

import net.punchtree.loquainteractable.staff.commands.LuckPermsNode

data object LoquaPermissions {

    enum class StaffRole {

        OWNER,
        ADMINISTRATOR,
        // WHILE INDEV ON BUILD SERVER - REPLACE WITH REAL STAFF ROLES IN PRODUCTION
        ARCHITECT,
        BUILDER,
        EXPLORER;

        companion object {
            fun getRoleFor(loquaPlayer: LoquaPlayer): StaffRole? =
                when {
                    loquaPlayer.hasPermission("loqua.staff.explorer") -> EXPLORER
                    loquaPlayer.hasPermission("loqua.staff.builder") -> BUILDER
                    loquaPlayer.hasPermission("loqua.staff.architect") -> ARCHITECT
                    loquaPlayer.hasPermission("loqua.staff.administrator") -> ADMINISTRATOR
                    loquaPlayer.hasPermission("loqua.staff.owner") -> OWNER
                    else -> null
                }
        }
        fun getPermissionGroup(): LuckPermsNode {
            return LuckPermsNode.builder("group.${name.lowercase()}").build()
        }
    }
}