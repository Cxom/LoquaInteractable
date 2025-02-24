package net.punchtree.loquainteractable._unstable.experimental

import net.punchtree.loquainteractable.ui.Fade.fadeIn
import net.punchtree.loquainteractable.ui.Fade.fadeOut
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object UiTestingCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.isEmpty()) return false

        when(val subcommand = args[0]) {
            "fade-out" -> fadeOut(sender, args.getOrNull(1)?.toIntOrNull() ?: 5)
            "fade-in" -> fadeIn(sender, args.getOrNull(1)?.toIntOrNull() ?: 5)
            else -> sender.sendRichMessage("<red>Unknown subcommand: $subcommand</red>")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return listOf("fade-out", "fade-in")
    }

}