package net.punchtree.loquainteractable._unstable.experimental

import com.jeff_media.morepersistentdatatypes.DataType
import net.punchtree.loquainteractable.player.LoquaDataKeys.INVENTORY
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

object PDCTestingCommand : CommandExecutor, TabCompleter {

    // TODO this is and will be REALLY USEFUL - flesh out in to a proper command

    private val namespacedKey = NamespacedKey("loqua", "pdctestkey")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if ( sender !is Player) return false

        if (args.isEmpty()) return false

        when (args[0].lowercase()) {
            "set" -> setPdc(sender, args)
            "get" -> getPdc(sender)
            "set-inventory" -> setInventoryPdc(sender)
            "get-inventory" -> getInventoryPdc(sender)
            "keys" -> showKeys(sender, args)
            "get-all" -> getAll(sender, args)
            else -> sender.sendMessage("Unknown subcommand: ${args[0]}")
        }

        return true;
    }

    private fun showKeys(player: Player, args: Array<out String>) {
        val pdc = player.persistentDataContainer
        val keys = if (args.size >= 2) pdc.keys.filter { it.namespace == args[1] } else pdc.keys
        player.sendMessage("PDC keys: ${pdc.keys}")
    }

    private fun getAll(player: Player, args: Array<out String>) {
        if (args.size >= 3) {
            val namespace = args[1]
            val key = args[2]
            val namespacedKey = NamespacedKey(namespace, key)
            val pdc = player.persistentDataContainer

            // TODO allow specifying type?
            val value = pdc.get(namespacedKey, PersistentDataType.STRING)
            player.sendMessage("PDC value for $namespacedKey: $value")
        }
    }

    private fun getInventoryPdc(player: Player) {
        val pdc = player.persistentDataContainer
        val inventory = pdc.get(INVENTORY, DataType.ITEM_STACK_ARRAY)
        if (inventory == null) {
            player.sendMessage("No inventory found in PDC")
            return
        }
        player.sendMessage("Restoring inventory from PDC")
        player.inventory.contents = inventory
        pdc.remove(INVENTORY)
    }

    private fun setInventoryPdc(player: Player) {
        val inventory = player.inventory.contents
        val pdc = player.persistentDataContainer
        pdc.set(INVENTORY, DataType.ITEM_STACK_ARRAY, inventory)
        player.sendMessage("Inventory saved to PDC")
    }

    private fun getPdc(player: Player) {
        val pdc = player.persistentDataContainer
        val value = pdc.get(namespacedKey, PersistentDataType.STRING)

        player.sendMessage("PDC value: $value")
    }

    private fun setPdc(sender: Player, args: Array<out String>) {
        val pdc = sender.persistentDataContainer

        val testvalue = if (args.size > 1) args[1] else "testvalue"
        pdc.set(namespacedKey, PersistentDataType.STRING, testvalue)
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when {
            args.size <= 1 -> mutableListOf("set", "get", "set-inventory", "get-inventory", "keys", "get-all")
            args.size == 2 && args[0].lowercase() == "get-all" -> mutableListOf("loqua", "minecraft")
            args.size == 3 && args[0].lowercase() == "get-all" -> mutableListOf("pdctestkey")
            else -> mutableListOf()
        }
    }

}