package net.punchtree.loquainteractable.metadata.commands

import net.punchtree.loquainteractable.metadata.editing.MetadataWand.giveMetadataWandItem
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MetadataWandCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        giveMetadataWandItem(sender)

        return true
    }
}
