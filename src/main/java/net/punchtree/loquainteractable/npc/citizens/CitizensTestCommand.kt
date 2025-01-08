package net.punchtree.loquainteractable.npc.citizens

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CitizensTestCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return false

        if (label.equals("create-npc", true)) {
            CitizensNPCManager.createNPC(sender)
        } else if (label.equals("make-npc-move", true)) {
            CitizensNPCManager.makeMove(sender)
        }

        return true
    }

}