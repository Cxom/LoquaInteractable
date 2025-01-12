package net.punchtree.loquainteractable.heist

import net.punchtree.loquainteractable.npc.citizens.heist.GuardTesting
import org.bukkit.command.*
import org.bukkit.entity.Player

object HeistTestCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return false
        if (args.isEmpty()) {
            listSubcommands(sender)
        }

        val subcommand = args[0].lowercase()
        when (subcommand) {
            "create-guard" -> GuardTesting.createGuard(sender.location)
            "make-guard-fire-gun" -> GuardTesting.makeGuardFireGun(sender)
        }

        return true
    }

    private fun listSubcommands(player: Player) {
        player.sendMessage("create-guard - Creates a test guard")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return listOf("create-guard", "make-guard-fire-gun")
    }
}
