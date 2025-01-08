package net.punchtree.loquainteractable.transit.streetcar

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object StreetcarTestingCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false

        when(val subcommand = args[0]) {
            "straight-and-curve" -> StreetcarTesting.doStraightAndCurve(sender)
            "cleanup" -> StreetcarTesting.onDisable()
            "catrom" -> StreetcarTesting.doCatmullRom(sender)
            else -> sender.sendMessage("Unknown subcommand: $subcommand")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return mutableListOf("straight-and-curve", "cleanup", "catrom")
    }

}
