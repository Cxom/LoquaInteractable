package net.punchtree.loquainteractable.data

import com.jeff_media.morepersistentdatatypes.DataType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.punchtree.loquainteractable.LoquaConstants
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.CompletableFuture
import kotlin.math.absoluteValue
import kotlin.math.sign

/** Command for operating on the data stored to players' PersistentDataContainers (PDCs) */
object PdcCommand : CommandExecutor, TabCompleter {

    // TODO go through all messaging and make sure it's got consistent styling
    // TODO make sure keys are case-insensitive by forcing all input to be lowercase

    // TODO implement the ability to set locations with actual data

    // TODO - exporting system
    // TODO - backup system

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

        try {

            val pdcHolderName = args[0]
            val pdcHolder = getPdcHolderFromName(pdcHolderName) ?: run {
                sender.sendMessage(Messages.PLAYER_NOT_FOUND(pdcHolderName))
                return true
            }

            when (val subcommand = args[1].lowercase()) {
                "keys" -> showKeys(sender, pdcHolder, pdcHolderName, args)
                "remove" -> removeData(sender, pdcHolder, pdcHolderName, args)
                "view-items" -> viewItems(sender, pdcHolder, pdcHolderName, args)
                "get" -> get(sender, pdcHolder, pdcHolderName, args)
                "set" -> setSubcommand(sender, pdcHolder, pdcHolderName, args)
                "get-unsafe" -> getUnsafe(sender, pdcHolder, pdcHolderName, args)
                "set-unsafe" -> setUnsafe(sender, pdcHolder, pdcHolderName, args)
                else -> sender.sendMessage("Unknown subcommand: $subcommand")
            }

        } catch (e: IllegalArgumentException) {
            sender.sendMessage(checkNotNull(e.message))
            return true
        }

        return true
    }

    private fun getPdcHolderFromName(pdcHolderName: String) : PersistentDataHolder? {
        return if (pdcHolderName.lowercase() == "world") {
            LoquaInteractablePlugin.world
        } else Bukkit.getPlayerExact(pdcHolderName)
    }

    private val DATA_VALUE_COLOR = LoquaConstants.Colors.LoquaFlagYellow
    private val NAMESPACED_KEY_COLOR = LoquaConstants.Colors.LoquaFlagBlueLight
    private val PDC_SOURCE_COLOR = LoquaConstants.Colors.LoquaFlagBlue
    private val SUCCESSFUL_MODIFICATION_COLOR = LoquaConstants.Colors.LoquaFlagYellowLight

    private fun showKeys(sender: Player, pdcHolder: PersistentDataHolder, pdcHolderName: String, args: Array<out String>) {
        // TODO paginate
        val headerMessage = text("«======== ${pdcHolderName}'s Data ========»").color(PDC_SOURCE_COLOR)
        sender.sendMessage(headerMessage)
        val pdc = pdcHolder.persistentDataContainer
        val filteredKeys = pdc.keys.filter {
            return@filter when {
                args.size >= 3 && it.namespace != args[2] -> false
                defaultFilters.contains(it.namespace) -> false
                else -> true
            }
        }
        filteredKeys.forEach { namespacedKey ->
            val dataType = LoquaDataKeys.registryForPdcTypeOf(pdcHolder)[namespacedKey]?.persistentDataType ?: run {
                sender.sendMessage(text("Warning: NamespacedKey '$namespacedKey' is not in the Loqua registry - not trying to display").color(LoquaConstants.Colors.LoquaFlagWhite))
                return@forEach
            }
            // TODO Create a read-unsafe subcommand and handle attempting to parse non-registry data with a passed in datatype there
            val value = pdc.get(namespacedKey, dataType)
            val valueComponent = getPdcValueString(value, dataType, namespacedKey, pdcHolderName)
            val namespacedKeyComponent = namespacedKeyComponent(namespacedKey)
            sender.sendMessage(namespacedKeyComponent
                .append(text(" "))
                .append(valueComponent))
        }
    }

    private fun get(sender: Player, pdcHolder: PersistentDataHolder, pdcHolderName: String, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage("Usage: /pdc <player> get <namespacedKey>")
            return
        }
        val pdc = pdcHolder.persistentDataContainer
        val namespacedKey = NamespacedKey.fromString(args[2]) ?: run {
            sender.sendMessage("Invalid namespacedKey '${args[2]}'")
            return
        }
        val dataType = LoquaDataKeys.registryForPdcTypeOf(pdcHolder)[namespacedKey]?.persistentDataType ?: run {
            if (!pdc.has(namespacedKey)) {
                sender.sendMessage("Error: NamespacedKey '$namespacedKey' not found in ${pdcHolderName}'s data")
            } else {
                sender.sendMessage(text("Error:").color(NamedTextColor.RED).append(text("NamespacedKey '$namespacedKey' is not in the Loqua registry - use 'get-unsafe' to access it anyway").color(LoquaConstants.Colors.LoquaFlagRedLight)))
            }
            return
        }
        val value = pdc.get(namespacedKey, dataType) ?: run {
            sender.sendMessage("Error: NamespacedKey '$namespacedKey' not found in ${pdcHolderName}'s data")
            return
        }
        val pdcSourceComponent = pdcSourceComponent(pdcHolderName)
        val namespacedKeyComponent = namespacedKeyComponent(namespacedKey)
        val valueComponent = getPdcValueString(value, dataType, namespacedKey, pdcHolderName)
        sender.sendMessage(pdcSourceComponent.append(text(" ")).append(namespacedKeyComponent).append(text(" ")).append(valueComponent))
    }

    private fun setSubcommand(sender: Player, pdcHolder: PersistentDataHolder, pdcHolderName: String, args: Array<out String>) {
        val loquaDataKey = parseKeyFromUserInput(sender, pdcHolder, pdcHolderName, args)

        val pdc = pdcHolder.persistentDataContainer
        editValueOfType(pdc, loquaDataKey, sender, args).thenApply { value ->
            val setSuccessMessage =
                text("Set ").color(SUCCESSFUL_MODIFICATION_COLOR)
                    .append(pdcSourceComponent(pdcHolderName))
                    .append(text(" "))
                    .append(namespacedKeyComponent(loquaDataKey.namespacedKey))
                    .append(text(" to ").color(SUCCESSFUL_MODIFICATION_COLOR))
                    .append(getPdcValueString(value, loquaDataKey.persistentDataType, loquaDataKey.namespacedKey, pdcHolderName))
            sender.sendMessage(setSuccessMessage)
        }
    }

    private fun parseKeyFromUserInput(sender: Player, pdcHolder: PersistentDataHolder, pdcHolderName: String, args: Array<out String>): LoquaDataKey<out Any, out Any> {
        if (args.size < 4) {
            throw IllegalArgumentException("Usage: /pdc <player> set <namespacedKey> <value>")
        }

        val namespacedKey = NamespacedKey.fromString(args[2])
            ?: throw IllegalArgumentException("Invalid namespacedKey '${args[2]}'")

        val loquaDataKey = LoquaDataKeys.registryForPdcTypeOf(pdcHolder)[namespacedKey]
            ?: throw IllegalArgumentException("Warning: NamespacedKey '$namespacedKey' is not in the Loqua registry - use 'set-unsafe' to access it anyway")

        return loquaDataKey
    }

    private fun <P : Any, C : Any> editValueOfType(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<P, C>, sender: Player, args: Array<out String>): CompletableFuture<C> {
        // TODO replace InspectablePersistentDataType with a mapped dataHandler types specific to this command, as that's the only place they're used

        if (loquaDataKey.persistentDataType !is InspectablePersistentDataType<P, C>)
            throw IllegalArgumentException("Error: NamespacedKey '${loquaDataKey.namespacedKey}' is not a supported type for setting (${loquaDataKey.persistentDataType.complexType.simpleName})")

        return loquaDataKey.persistentDataType.edit(pdc, loquaDataKey, sender, args.sliceArray(3 until args.size))
    }

    private fun parseValueOfType(loquaDataKey: LoquaDataKey<out Any, out Any>, args: Array<out String>, sender: Player): Any = when (loquaDataKey.persistentDataType) {
        DataType.STRING -> args.slice(3 until args.size).joinToString(" ")
        DataType.BOOLEAN -> args[3].lowercase().toBooleanStrictOrNull()
            ?: throw IllegalArgumentException("Error: '${args[3]}' is not a boolean")

        DataType.BYTE -> args[3].toByteOrNull()
            ?: throw IllegalArgumentException("Error: '${args[3]}' is not a byte")

        DataType.SHORT -> args[3].toShortOrNull()
            ?: throw IllegalArgumentException("Error: '${args[3]}' is not a short")

        DataType.INTEGER -> args[3].toIntOrNull()
            ?: throw IllegalArgumentException("Error: '${args[3]}' is not an integer")

        DataType.LONG -> args[3].toLongOrNull()
            ?: throw IllegalArgumentException("Error: '${args[3]}' is not a long")

        DataType.FLOAT -> args[3].toFloatOrNull()
            ?: throw IllegalArgumentException("Error: '${args[3]}' is not a float")

        DataType.DOUBLE -> args[3].toDoubleOrNull()
            ?: throw IllegalArgumentException("Error: '${args[3]}' is not a double")

        DataType.LOCATION -> {
            if (!sender.location.world.equals(LoquaInteractablePlugin.world)) {
                throw IllegalArgumentException("Error: '${sender.location.world.name}' is not the Loqua world")
            }
            when {
                args[0] == "eye" -> sender.eyeLocation
                args[0] == "feet" -> sender.location
                else -> throw IllegalArgumentException("Error: '${args[0]}' is not 'eye' or 'feet'")
            }
        }

        else -> throw IllegalArgumentException("Error: NamespacedKey '${loquaDataKey.namespacedKey}' is not a supported type for setting (${loquaDataKey.persistentDataType.complexType.simpleName})")

        //        if (loquaDataKey.persistentDataType !is InspectablePersistentDataType<*, *>)
//            throw IllegalArgumentException("Error: NamespacedKey '$namespacedKey' is not a supported type for setting (${loquaDataKey.persistentDataType.complexType.simpleName})")
//
//        val inspectableDataType = loquaDataKey.persistentDataType as InspectablePersistentDataType<*, *>
//        val value = checkNotNull(inspectableDataType.parse(sender, args.sliceArray(3 until args.size)))
    }

    private fun pdcSourceComponent(pdcHolderName: String) =
        text("[$pdcHolderName]").color(PDC_SOURCE_COLOR)

    private fun namespacedKeyComponent(namespacedKey: NamespacedKey) =
        text(namespacedKey.toString()).color(NAMESPACED_KEY_COLOR).insertion(namespacedKey.toString())

    private fun getUnsafe(sender: Player, pdcHolder: PersistentDataHolder, pdcHolderName: String, args: Array<out String>) {
        sender.sendMessage("Sorry, get-unsafe is not implemented yet!")
        // TODO implement get-unsafe
    }

    private fun setUnsafe(sender: Player, pdcHolder: PersistentDataHolder, pdcHolderName: String, args: Array<out String>) {
        sender.sendMessage("Sorry, set-unsafe is not implemented yet!")
        // TODO implement set-unsafe
    }

    @Suppress("UnstableApiUsage")
    private fun viewItems(sender: Player, pdcHolder: PersistentDataHolder, pdcHolderName: String, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage("Usage: /pdc <player> view-items <namespacedKey>")
            return
        }
        val pdc = pdcHolder.persistentDataContainer
        val namespacedKey = NamespacedKey.fromString(args[2]) ?: run {
            sender.sendMessage("Invalid namespacedKey '${args[2]}'")
            return
        }
        when {
            !pdc.has(namespacedKey) -> {
                sender.sendMessage("Error: NamespacedKey '$namespacedKey' not found in PDC")
                return
            }
            LoquaDataKeys.registryForPdcTypeOf(pdcHolder)[namespacedKey] == null -> {
                sender.sendMessage("Warning: NamespacedKey '$namespacedKey' is not in the Loqua registry")
            }
            LoquaDataKeys.registryForPdcTypeOf(pdcHolder)[namespacedKey]!!.persistentDataType != DataType.ITEM_STACK_ARRAY -> {
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
            .title(text("V:${pdcHolderName}'s $namespacedKey"))
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

    private fun <P, C> getPdcValueString(
        value: C?,
        dataType: PersistentDataType<out P, out C>,
        namespacedKey: NamespacedKey,
        pdcHolderName: String
    ): Component {
        if (value == null) {
            return text("null (${dataType.complexType.simpleName})").color(DATA_VALUE_COLOR)
        }

        if (dataType is InspectablePersistentDataType<P, C>) {
            return dataType.display(value).color(DATA_VALUE_COLOR)
        }

        return when (dataType) {
            DataType.ITEM_STACK -> {
                value as ItemStack
                text("[${value.type}:${value.amount}]").decorate(TextDecoration.UNDERLINED).hoverEvent(value).color(
                    DATA_VALUE_COLOR
                )
            }
            DataType.ITEM_STACK_ARRAY -> {
                @Suppress("UNCHECKED_CAST")
                value as Array<ItemStack>
                text("ItemStack Array[${value.size}]").decorate(TextDecoration.UNDERLINED).clickEvent(
                    ClickEvent.suggestCommand("/pdc $pdcHolderName view-items ${namespacedKey.namespace}:${namespacedKey.key}")
                ).color(DATA_VALUE_COLOR)
            }
            // TODO add more types
            else -> text("${dataType.complexType.simpleName}: $value").color(DATA_VALUE_COLOR)
        }
    }



    private fun removeData(sender: Player, pdcHolder: PersistentDataHolder, pdcHolderName: String, args: Array<out String>) {
        // TODO this is kind of dangerous - should we do something to protect stuff? maybe an undo cache?
        if (args.size < 3) {
            sender.sendMessage("Usage: /pdc <player> remove <namespacedKey>")
            return
        }
        val pdc = pdcHolder.persistentDataContainer
        val namespacedKey = NamespacedKey.fromString(args[2]) ?: run {
            sender.sendMessage("Invalid namespacedKey '${args[2]}'")
            return
        }
        pdc.remove(namespacedKey)
        sender.sendMessage("Removed $namespacedKey from ${pdcHolderName}'s PDC")
    }

    // TODO implement a way to set a player inventory based on a stored inventory, if it proves useful

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        if (args.isEmpty()) return null
        if (args.size == 1) {
            return Bukkit.matchPlayer(args[0])
                .map { it.name }
                .toMutableList()
                .also {
                    it.add("world")
                    if (args[0].isEmpty()) it.add("<pdcHolder>")
                }
        }
        val pdcHolder = getPdcHolderFromName(args[0]) ?: return listOf("INVALID PDC HOLDER!")
        if (args.size == 2) {
            return mutableListOf("keys", "remove", "view-items", "get", "set", "get-unsafe", "set-unsafe").also {
                if (args[1].isEmpty()) it.add("<subcommand>")
            }
        }

        val loquaDataKeysRegistry = LoquaDataKeys.registryForPdcTypeOf(pdcHolder)
        if (args.size == 3) {
            val subcommand = args[1].lowercase()
            val keysOnPdcHolder = pdcHolder.persistentDataContainer.keys
            return when (subcommand) {
                "keys" -> {
                    mutableListOf("loqua", "minecraft").also {
                        if (args[2].isEmpty()) it.add("[filter (namespace:key)]")
                    }
                }

                "view-items", "get", "set", "get-unsafe", "set-unsafe", "remove"
                -> {
                    val allKeys = (keysOnPdcHolder + loquaDataKeysRegistry.keys)
                        .filter { !defaultFilters.contains(it.namespace) }
                        .map { it.toString() }
                        .filter { it.contains(args[2]) }
                        .toMutableList()
                        .also { if (args[2].isEmpty()) it.add("<namespace:key>") }
                    return allKeys
                }

                else -> {
                    emptyList()
                }
            }
        }

        if (args.size == 4 && args[1].lowercase() == "set" && loquaDataKeysRegistry[NamespacedKey.fromString(args[2])]?.persistentDataType == DataType.LOCATION) {
            return mutableListOf("eye", "feet").also {
                if (args[3].isEmpty()) it.add("<eye-or-feet>")
            }
        }

        return mutableListOf()
    }

}