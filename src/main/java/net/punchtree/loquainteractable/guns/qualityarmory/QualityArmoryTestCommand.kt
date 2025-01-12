package net.punchtree.loquainteractable.guns.qualityarmory

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import me.zombie_striker.qg.api.QualityArmory

object QualityArmoryTestCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return false

        if (label.equals("test-gun", true)) {
            if (!QualityArmory.isGun(sender.inventory.itemInMainHand)) {
                sender.sendMessage("Not a gun!")
                return true
            }
            val gun = QualityArmory.getGun(sender.inventory.itemInMainHand)
            sender.sendMessage("Gun: ${gun.name}")
        }

        if (label.equals("list-guns", true)) {
            sender.sendMessage("Guns:")
            QualityArmory.getGuns().forEach {
                sender.sendMessage(" - " + it.name)
            }
            return true
        }

        return true
    }

}