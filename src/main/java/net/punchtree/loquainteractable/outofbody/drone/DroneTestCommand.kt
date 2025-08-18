package net.punchtree.loquainteractable.outofbody.drone

import net.punchtree.loquainteractable.text.LoquaTextColors.failure
import net.punchtree.loquainteractable.text.LoquaTextColors.success
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object DroneTestCommand : CommandExecutor, TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf("start", "stop")
            else -> mutableListOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false

        when (args[0].lowercase()) {
            "start" -> {
                DroneManager.startFlyingDrone(sender)
                sender.sendMessage(success("Flying drone started."))
            }
            "stop" -> {
                DroneManager.stopFlyingDrone(sender)
                sender.sendMessage(failure("Flying drone stopped."))
            }
        }

        return true
    }

}