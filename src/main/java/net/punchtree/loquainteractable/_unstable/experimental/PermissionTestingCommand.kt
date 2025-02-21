package net.punchtree.loquainteractable._unstable.experimental

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment

object PermissionTestingCommand : CommandExecutor, TabCompleter {

//    var blankAttachment: PermissionAttachment? = null
    var testAttachment: PermissionAttachment? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if ( sender !is Player) return false

        if (args.isEmpty()) return false

        when (args[0].lowercase()) {
//            "addblank" -> addBlank(sender, args)
            "addtest" -> addTest(sender)
            "removetestorig" -> removeOrig(sender)
            "removetestequal" -> removeEqual(sender)
            else -> sender.sendMessage("Unknown subcommand: ${args[0]}")
        }

        return true;
    }

    private fun removeEqual(player: Player) {
        val equalAttachment = PermissionAttachment(LoquaInteractablePlugin.instance, player)
        equalAttachment.setPermission("loqua.test", true)

        player.removeAttachment(equalAttachment)
        player.effectivePermissions.filter {
            it.permission == "loqua.test"
        }.forEach(
            { player.sendMessage("${it.attachment!!.plugin.name}:${it.permission} = ${it.value}") }
        )
    }

    private fun removeOrig(player: Player) {
        testAttachment?.let {
            player.removeAttachment(it)
            player.sendMessage("Removed test permission attachment")
            testAttachment = null
        } ?: player.sendMessage("No test permission attachment to remove")
    }

    private fun addTest(player: Player) {
        testAttachment = player.addAttachment(LoquaInteractablePlugin.instance, "loqua.test", true)
    }

//    private fun addBlank(player: Player, args: Array<out String>) {
//        blankAttachment = player.addAttachment(LoquaInteractablePlugin.instance)
//    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf("addblank", "addtest", "removetestorig", "removetestequal")
            else -> mutableListOf()
        }
    }
}