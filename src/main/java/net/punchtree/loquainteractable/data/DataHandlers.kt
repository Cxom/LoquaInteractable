package net.punchtree.loquainteractable.data

import com.jeff_media.morepersistentdatatypes.DataType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.DataHandlerRegistry.createDataHandler
import net.punchtree.loquainteractable.displayutil.toSimpleString
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.CompletableFuture

interface DataHandler<C> {
    fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, C>, sender: Player, args: Array<out String>): CompletableFuture<C>
    fun display(value: C): Component = text(value.toString())
}

// idk which is more readable so I left both... I don't have the energy to make a decision here
private val StringDataHandler = createDataHandler(edit = { pdc, loquaDataKey, sender, args ->
    val value = args.joinToString(" ")
    pdc.set(loquaDataKey, value)
    CompletableFuture.completedFuture(value)
})

private val BooleanDataHandler = createDataHandler(edit = { pdc, loquaDataKey, sender, args ->
    val value = args[0].lowercase().toBooleanStrictOrNull()
        ?: throw IllegalArgumentException("Error: '${args[0]}' is not a boolean")
    pdc.set(loquaDataKey, value)
    CompletableFuture.completedFuture(value)
})

private class ByteDataHandler : DataHandler<Byte> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, Byte>, sender: Player, args: Array<out String>): CompletableFuture<Byte> {
        val value = args[0].toByteOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a byte")
        pdc.set(loquaDataKey, value)
        return CompletableFuture.completedFuture(value)
    }
}

private class IntegerDataHandler : DataHandler<Int> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, Int>, sender: Player, args: Array<out String>): CompletableFuture<Int> {
        val value = args[0].toIntOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not an integer")
        pdc.set(loquaDataKey, value)
        return CompletableFuture.completedFuture(value)
    }
}

private class LongDataHandler : DataHandler<Long> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, Long>, sender: Player, args: Array<out String>): CompletableFuture<Long> {
        val value = args[0].toLongOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a long")
        pdc.set(loquaDataKey, value)
        return CompletableFuture.completedFuture(value)
    }
}

private class FloatDataHandler : DataHandler<Float> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, Float>, sender: Player, args: Array<out String>): CompletableFuture<Float> {
        val value = args[0].toFloatOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a float")
        pdc.set(loquaDataKey, value)
        return CompletableFuture.completedFuture(value)
    }
}

private class DoubleDataHandler : DataHandler<Double> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, Double>, sender: Player, args: Array<out String>): CompletableFuture<Double> {
        val value = args[0].toDoubleOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a double")
        pdc.set(loquaDataKey, value)
        return CompletableFuture.completedFuture(value)
    }
}

private class LocationDataHandler : DataHandler<Location> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, Location>, sender: Player, args: Array<out String>): CompletableFuture<Location> {
        if (!sender.location.world.equals(LoquaInteractablePlugin.world)) {
            throw IllegalArgumentException("Error: '${sender.location.world.name}' is not the Loqua world")
        }
        val locationToSet = when {
            args[0] == "eye" -> sender.eyeLocation
            args[0] == "feet" -> sender.location
            else -> throw IllegalArgumentException("Error: '${args[0]}' is not 'eye' or 'feet'")
        }
        pdc.set(loquaDataKey, locationToSet)
        return CompletableFuture.completedFuture(locationToSet)
    }
    
    override fun display(value: Location): Component = value.toSimpleString()
}

internal object DataHandlerRegistry {
    private val handlers = mutableMapOf<PersistentDataType<*, *>, DataHandler<*>>()
    
    init {
        handlers[DataType.STRING] = StringDataHandler
        handlers[DataType.BOOLEAN] = BooleanDataHandler
        handlers[DataType.BYTE] = ByteDataHandler()
        handlers[DataType.INTEGER] = IntegerDataHandler()
        handlers[DataType.LONG] = LongDataHandler()
        handlers[DataType.FLOAT] = FloatDataHandler()
        handlers[DataType.DOUBLE] = DoubleDataHandler()
        handlers[DataType.LOCATION] = LocationDataHandler()
    }

    operator fun <C> get(dataType: PersistentDataType<*, C>): DataHandler<C>? {
        @Suppress("UNCHECKED_CAST")
        return handlers[dataType] as? DataHandler<C>
    }

    internal fun <C : Any> createDataHandler(
        edit: (pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, C>, sender: Player, args: Array<out String>) -> CompletableFuture<C>,
        display: (value: C) -> Component = { text(it.toString()) }
    ) : DataHandler<C> {
        return object : DataHandler<C> {
            override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, C>, sender: Player, args: Array<out String>): CompletableFuture<C> {
                return edit(pdc, loquaDataKey, sender, args)
            }
            override fun display(value: C): Component {
                return display(value)
            }
        }
    }
}
