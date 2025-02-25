package net.punchtree.loquainteractable.player

import com.jeff_media.morepersistentdatatypes.DataType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.punchtree.loquainteractable.LoquaConstants
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.bukkit.persistence.PersistentDataType
import java.io.Serializable
import kotlin.math.absoluteValue
import kotlin.math.sign

/** Command for operating on the data stored to players' PersistentDataContainers (PDCs) */
object PlayerDataCommand : CommandExecutor, TabCompleter {

    // TODO go through all messaging and make sure it's got consistent styling

    /** Certain third-party namespaces are data we're not interested in */
    private val defaultFilters = setOf("axiom")

    @Suppress("FunctionName")
    private data object Messages {
        fun PLAYER_NOT_FOUND(playerName: String) = text("Player $playerName not found").color(NamedTextColor.RED)
        // TODO
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if ( sender !is Player) return false

        if (args.size < 2) return false

        val targetPlayerName = args[0]
        val targetPlayer = Bukkit.getPlayer(targetPlayerName) ?: run {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND(targetPlayerName))
            return true
        }

        when (val subcommand = args[1].lowercase()) {
            "keys" -> showKeys(sender, targetPlayer, args)
            "remove" -> removeData(sender, targetPlayer, args)
            "view-items" -> viewItems(sender, targetPlayer, args)
            "get" -> get(sender, targetPlayer, args)
            "set" -> set(sender, targetPlayer, args)
            "get-unsafe" -> getUnsafe(sender, targetPlayer, args)
            "set-unsafe" -> setUnsafe(sender, targetPlayer, args)
            else -> sender.sendMessage("Unknown subcommand: $subcommand")
        }

        return true;
    }

    private val DATA_VALUE_COLOR = LoquaConstants.Colors.LoquaFlagYellow
    private val NAMESPACED_KEY_COLOR = LoquaConstants.Colors.LoquaFlagBlueLight
    private val PDC_SOURCE_COLOR = LoquaConstants.Colors.LoquaFlagBlue
    private val SUCCESSFUL_MODIFICATION_COLOR = LoquaConstants.Colors.LoquaFlagYellowLight

    private fun showKeys(sender: Player, targetPlayer: Player, args: Array<out String>) {
        // TODO paginate
        val headerMessage = text("«======== ${targetPlayer.name}'s Data ========»").color(PDC_SOURCE_COLOR)
        sender.sendMessage(headerMessage)
        val pdc = targetPlayer.persistentDataContainer
        val filteredKeys = pdc.keys.filter {
            return@filter when {
                args.size >= 3 && it.namespace != args[2] -> false
                defaultFilters.contains(it.namespace) -> false
                else -> true
            }
        }
        filteredKeys.forEach { namespacedKey ->
            val dataType = LoquaDataKeys.registry[namespacedKey]?.persistentDataType ?: run {
                sender.sendMessage(text("Warning: NamespacedKey '$namespacedKey' is not in the Loqua registry - not trying to display").color(LoquaConstants.Colors.LoquaFlagWhite))
                return@forEach
            }
            // TODO Create a read-unsafe subcommand and handle attempting to parse non-registry data with a passed in datatype there
            val value = pdc.get(namespacedKey, dataType)
            val valueComponent = getPdcValueString(value, dataType, namespacedKey, targetPlayer)
            val namespacedKeyComponent = namespacedKeyComponent(namespacedKey)
            sender.sendMessage(namespacedKeyComponent
                .append(text(" "))
                .append(valueComponent))
        }
    }

    private fun get(sender: Player, targetPlayer: Player, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage("Usage: /playerdata <player> get <namespacedKey>")
            return
        }
        val pdc = targetPlayer.persistentDataContainer
        val namespacedKey = NamespacedKey.fromString(args[2]) ?: run {
            sender.sendMessage("Invalid namespacedKey '${args[2]}'")
            return
        }
        val dataType = LoquaDataKeys.registry[namespacedKey]?.persistentDataType ?: run {
            if (!pdc.has(namespacedKey)) {
                sender.sendMessage("Error: NamespacedKey '$namespacedKey' not found in ${targetPlayer.name}'s data")
            } else {
                sender.sendMessage(text("Error:").color(NamedTextColor.RED).append(text("NamespacedKey '$namespacedKey' is not in the Loqua registry - use 'get-unsafe' to access it anyway").color(LoquaConstants.Colors.LoquaFlagRedLight)))
            }
            return
        }
        val value = pdc.get(namespacedKey, dataType) ?: run {
            sender.sendMessage("Error: NamespacedKey '$namespacedKey' not found in ${targetPlayer.name}'s data")
            return
        }
        val pdcSourceComponent = pdcSourceComponent(targetPlayer)
        val namespacedKeyComponent = namespacedKeyComponent(namespacedKey)
        val valueComponent = getPdcValueString(value, dataType, namespacedKey, targetPlayer)
        sender.sendMessage(pdcSourceComponent.append(text(" ")).append(namespacedKeyComponent).append(text(" ")).append(valueComponent))
    }

    private fun set(sender: Player, targetPlayer: Player, args: Array<out String>) {
        if (args.size < 4) {
            sender.sendMessage("Usage: /playerdata <player> set <namespacedKey> <value>")
            return
        }
        val pdc = targetPlayer.persistentDataContainer
        val namespacedKey = NamespacedKey.fromString(args[2]) ?: run {
            sender.sendMessage("Invalid namespacedKey '${args[2]}'")
            return
        }
        val loquaDataKey = LoquaDataKeys.registry[namespacedKey] ?: run {
            sender.sendMessage("Warning: NamespacedKey '$namespacedKey' is not in the Loqua registry - use 'set-unsafe' to access it anyway")
            return
        }
        val setValue: Any = when (val dataType = loquaDataKey.persistentDataType) {
            // We can't use the convenient extension functions here because of type erasure since thes sets are dynamic
            // TODO really use more advanced brigadier commands to parse a bit better
            DataType.STRING -> pdc.set(namespacedKey, DataType.STRING, args.sliceArray(3 until args.size).joinToString(" "))
            DataType.BOOLEAN -> args[3].lowercase().toBooleanStrictOrNull()?.let {
                pdc.set(namespacedKey, DataType.BOOLEAN, it)
                it
            } ?: run {
                sender.sendMessage("Error: '${args[3]}' is not a boolean")
                return
            }

            DataType.INTEGER -> args[3].toIntOrNull()?.let {
                pdc.set(namespacedKey, DataType.INTEGER, it)
                it
            } ?: run {
                sender.sendMessage("Error: '${args[3]}' is not an integer")
                return
            }

            DataType.LONG -> args[3].toLongOrNull()?.let {
                pdc.set(namespacedKey, DataType.LONG, it)
                it
            } ?: run {
                sender.sendMessage("Error: '${args[3]}' is not a long")
                return
            }

            DataType.DOUBLE -> args[3].toDoubleOrNull()?.let {
                pdc.set(namespacedKey, DataType.DOUBLE, it)
                it
            } ?: run {
                sender.sendMessage("Error: '${args[3]}' is not a double")
                return
            }

            DataType.FLOAT -> args[3].toFloatOrNull()?.let {
                pdc.set(namespacedKey, DataType.FLOAT, it)
                it
            } ?: run {
                sender.sendMessage("Error: '${args[3]}' is not a float")
                return
            }

            DataType.BYTE -> args[3].toByteOrNull()?.let {
                pdc.set(namespacedKey, DataType.BYTE, it)
                it
            } ?: run {
                sender.sendMessage("Error: '${args[3]}' is not a byte")
                return
            }

            DataType.SHORT -> args[3].toShortOrNull()?.let {
                pdc.set(namespacedKey, DataType.SHORT, it)
                it
            } ?: run {
                sender.sendMessage("Error: '${args[3]}' is not a short")
                return
            }

            DataType.LOCATION -> {
                if (!targetPlayer.location.world.equals(LoquaInteractablePlugin.world)) {
                    sender.sendMessage("Error: '${targetPlayer.location.world.name}' is not the Loqua world")
                    return
                }
                pdc.set(namespacedKey, DataType.LOCATION, targetPlayer.location)
                targetPlayer.location
            }
            else -> {
                sender.sendMessage("Error: NamespacedKey '$namespacedKey' is not a supported type for setting (${dataType.complexType.simpleName})")
                return
            }
        }

        val setDataMessage =
            text("Set ").color(SUCCESSFUL_MODIFICATION_COLOR)
            .append(pdcSourceComponent(targetPlayer))
            .append(text(" "))
            .append(namespacedKeyComponent(namespacedKey))
            .append(text(" to ").color(SUCCESSFUL_MODIFICATION_COLOR))
            .append(getPdcValueString(setValue, loquaDataKey.persistentDataType, namespacedKey, targetPlayer))
        sender.sendMessage(setDataMessage)
    }

    private fun pdcSourceComponent(targetPlayer: Player) =
        text("[${targetPlayer.name}]").color(PDC_SOURCE_COLOR)

    private fun namespacedKeyComponent(namespacedKey: NamespacedKey) =
        text(namespacedKey.toString()).color(NAMESPACED_KEY_COLOR).insertion(namespacedKey.toString())

    private fun getUnsafe(sender: Player, targetPlayer: Player, args: Array<out String>) {
        sender.sendMessage("Sorry, get-unsafe is not implemented yet!")
        // TODO implement get-unsafe
    }

    private fun setUnsafe(sender: Player, targetPlayer: Player, args: Array<out String>) {
        sender.sendMessage("Sorry, set-unsafe is not implemented yet!")
        // TODO implement set-unsafe
    }

    @Suppress("UnstableApiUsage")
    private fun viewItems(sender: Player, targetPlayer: Player, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage("Usage: /playerdata <player> view-items <namespacedKey>")
            return
        }
        val pdc = targetPlayer.persistentDataContainer
        val namespacedKey = NamespacedKey.fromString(args[2]) ?: run {
            sender.sendMessage("Invalid namespacedKey '${args[2]}'")
            return
        }
        when {
            !pdc.has(namespacedKey) -> {
                sender.sendMessage("Error: NamespacedKey '$namespacedKey' not found in PDC")
                return
            }
            LoquaDataKeys.registry[namespacedKey] == null -> {
                sender.sendMessage("Warning: NamespacedKey '$namespacedKey' is not in the Loqua registry")
            }
            LoquaDataKeys.registry[namespacedKey]!!.persistentDataType != DataType.ITEM_STACK_ARRAY -> {
                sender.sendMessage("Error: NamespacedKey '$namespacedKey' is not an ItemStack array! This won't work. If you are confident that the underlying data is not the type defined in the registry of Loqua Data Keys, rename or remove the data!")
                return
            }
        }
        val dataType = DataType.ITEM_STACK_ARRAY
        val value = pdc.get(namespacedKey, dataType)!!
        val items = value as Array<ItemStack>
        // TODO prevent moving items in the view (it won't update the data anyway, so make the (lack of) affordance clear)
        // TODO create a MODIFY-ITEMS subcommand for doing exactly that editing (and saving on close)
        val menuType = when {
            items.size <= 9 -> MenuType.GENERIC_9X1
            items.size <= 18 -> MenuType.GENERIC_9X2
            items.size <= 27 -> MenuType.GENERIC_9X3
            items.size <= 36 -> MenuType.GENERIC_9X4
            items.size <= 45 -> MenuType.GENERIC_9X5
            else -> MenuType.GENERIC_9X6
        }
        val inventoryView = menuType.builder()
            .title(text("V:${targetPlayer.name}'s $namespacedKey"))
            .build(sender)
        if (items.size <= 54) {
            for (i in items.indices) {
                inventoryView.setItem(i, items[i])
            }
        } else {
            val page = if (args.size >= 4) args[3].toIntOrNull() ?: 1 else 1
            val ITEMS_PER_PAGE = 5 * 9
            page.coerceIn(1, items.size.ceilDiv(ITEMS_PER_PAGE))
            val start = (page - 1) * ITEMS_PER_PAGE
            val end = minOf(start + ITEMS_PER_PAGE, items.size)
            for (i in start until end) {
                inventoryView.setItem(i - start, items[i])
            }
            // TODO page buttons
        }
        sender.openInventory(inventoryView)
    }

    // TODO move to a number utils place
    internal fun Int.ceilDiv(divisor: Int) =
//        ((this + divisor - 1) / divisor)
        this.floorDiv(divisor) + this.rem(divisor).sign.absoluteValue

    private fun getPdcValueString(value: Any?, dataType: PersistentDataType<out Serializable, out Any>, namespacedKey: NamespacedKey, targetPlayer: Player): Component {
        if (value == null) {
            return text("null (${dataType.complexType.simpleName})").color(DATA_VALUE_COLOR)
        }
        return when (dataType) {
            DataType.STRING,
            DataType.BOOLEAN,
            DataType.INTEGER,
            DataType.LONG,
            DataType.DOUBLE,
            DataType.FLOAT,
            DataType.BYTE,
            DataType.SHORT -> text(value.toString()).color(DATA_VALUE_COLOR)
            DataType.LOCATION -> {
                value as Location
                // TODO minimessage color (USE player::sendRichMessage)
                text(value.toSimpleString()).color(DATA_VALUE_COLOR)
            }
            DataType.ITEM_STACK -> {
                value as ItemStack
                text("[${value.type}:${value.amount}]").decorate(TextDecoration.UNDERLINED).hoverEvent(value).color(DATA_VALUE_COLOR)
            }
            DataType.ITEM_STACK_ARRAY -> {
                @Suppress("UNCHECKED_CAST")
                value as Array<ItemStack>
                text("ItemStack Array[${value.size}]").decorate(TextDecoration.UNDERLINED).clickEvent(
                    ClickEvent.suggestCommand("/playerdata ${targetPlayer.name} view-items ${namespacedKey.namespace}:${namespacedKey.key}")
                ).color(DATA_VALUE_COLOR)
            }
            // TODO add more types
            else -> text("${dataType.complexType.simpleName}: $value").color(DATA_VALUE_COLOR)
        }
    }

    // TODO move/organize into single location for pretty-printing utils
    //  -- See: PrintingObjectUtils <- unify with that
    internal fun Location.toSimpleString(decimalPrecision: Int = 2): String {
        /** constrained decimal precision */
        val cdp = decimalPrecision.coerceIn(0, 10)
        return "[x:%.${cdp}f y:%.${cdp}f z:%.${cdp}f yaw:%.${cdp}f pitch:%.${cdp}f]".format(x, y, z, yaw, pitch)
    }

    private fun removeData(sender: Player, targetPlayer: Player, args: Array<out String>) {
        // TODO this is kind of dangerous - should we do something to protect stuff? maybe an undo cache?
        if (args.size < 3) {
            sender.sendMessage("Usage: /playerdata <player> remove <namespacedKey>")
            return
        }
        val pdc = targetPlayer.persistentDataContainer
        val namespacedKey = NamespacedKey.fromString(args[2]) ?: run {
            sender.sendMessage("Invalid namespacedKey '${args[2]}'")
            return
        }
        pdc.remove(namespacedKey)
        sender.sendMessage("Removed $namespacedKey from ${targetPlayer.name}'s PDC")
    }

    // TODO implement a way to set a player inventory based on a stored inventory, if it proves useful

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        return when {
            args.size == 1 -> Bukkit.matchPlayer(args[0]).map { it.name }.toMutableList().also {
                if (args[0].isEmpty()) it.add("<targetPlayer>")
            }
            args.size == 2 -> mutableListOf("keys", "remove", "view-items", "get", "set", "get-unsafe", "set-unsafe").also {
                if (args[1].isEmpty()) it.add("<subcommand>")
            }
            args.size == 3 -> when (val subcommand = args[1].lowercase()) {
                "keys" -> mutableListOf("loqua", "minecraft").also {
                    if (args[2].isEmpty()) it.add("[filter (namespace:key)]")
                }
                "view-items", "get", "set", "get-unsafe", "set-unsafe", "remove"
                    -> mutableListOf("loqua", "minecraft").also {
                    if (args[2].isEmpty()) it.add("<namespace:key>")
                }
                else -> emptyList()
            }
            // TODO namespacedKey tabcompletion based on actual data on the player
            else -> mutableListOf()
        }
    }

}