package net.punchtree.loquainteractable.clothing

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SkinGrabberTestCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return false
        if (args.isEmpty()) return false

        when {
            label.equals("changeskin", true) -> SkinGrabber.changeSkin(sender, args[0])
            label.equals("changecape", true) -> SkinGrabber.changeCape(sender, args[0])
        }
        return true
    }
}
