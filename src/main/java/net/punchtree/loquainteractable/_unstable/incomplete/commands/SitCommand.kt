package net.punchtree.loquainteractable._unstable.incomplete.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.net.http.WebSocket.Listener

class SitCommand : CommandExecutor, Listener {

    // TODO this doesn't need to be a command at all
    //  devise an input detection scheme that just sits in the current location
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return false



        return true
    }

    // the difficulty of input detection where we want to match sequences is in resolving the
    // difficult memory constraints of many possible states over a period of time with many possible grace periods
    // what we really need, is a set of FSA or DFAs that are compiled down into one that dispatches actions

    // imagine, you want to sit. To sit, you have to double tap sprint while sneaking
    // using input events, we have the following states

    //

}