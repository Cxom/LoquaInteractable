package net.punchtree.loquainteractable.data

import com.jeff_media.morepersistentdatatypes.DataType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.displayutil.toSimpleString
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.CompletableFuture

interface DataHandler<C> {
    fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out C>, sender: Player, args: Array<out String>): CompletableFuture<C>
    fun display(value: C): Component = text(value.toString())
}

internal class StringDataHandler : DataHandler<String> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out String>, sender: Player, args: Array<out String>): CompletableFuture<String> {
        val value = args.joinToString(" ")
        @Suppress("UNCHECKED_CAST")
        pdc.set(loquaDataKey as LoquaDataKey<Any, String>, value)
        return CompletableFuture.completedFuture(value)
    }
}

internal class BooleanDataHandler : DataHandler<Boolean> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out Boolean>, sender: Player, args: Array<out String>): CompletableFuture<Boolean> {
        val value = args[0].lowercase().toBooleanStrictOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a boolean")
        @Suppress("UNCHECKED_CAST")
        pdc.set(loquaDataKey as LoquaDataKey<Any, Boolean>, value)
        return CompletableFuture.completedFuture(value)
    }
}

internal class ByteDataHandler : DataHandler<Byte> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out Byte>, sender: Player, args: Array<out String>): CompletableFuture<Byte> {
        val value = args[0].toByteOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a byte")
        @Suppress("UNCHECKED_CAST")
        pdc.set(loquaDataKey as LoquaDataKey<Any, Byte>, value)
        return CompletableFuture.completedFuture(value)
    }
}

internal class IntegerDataHandler : DataHandler<Int> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out Int>, sender: Player, args: Array<out String>): CompletableFuture<Int> {
        val value = args[0].toIntOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not an integer")
        @Suppress("UNCHECKED_CAST")
        pdc.set(loquaDataKey as LoquaDataKey<Any, Int>, value)
        return CompletableFuture.completedFuture(value)
    }
}

internal class LongDataHandler : DataHandler<Long> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out Long>, sender: Player, args: Array<out String>): CompletableFuture<Long> {
        val value = args[0].toLongOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a long")
        @Suppress("UNCHECKED_CAST")
        pdc.set(loquaDataKey as LoquaDataKey<Any, Long>, value)
        return CompletableFuture.completedFuture(value)
    }
}

internal class FloatDataHandler : DataHandler<Float> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out Float>, sender: Player, args: Array<out String>): CompletableFuture<Float> {
        val value = args[0].toFloatOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a float")
        @Suppress("UNCHECKED_CAST")
        pdc.set(loquaDataKey as LoquaDataKey<Any, Float>, value)
        return CompletableFuture.completedFuture(value)
    }
}

internal class DoubleDataHandler : DataHandler<Double> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out Double>, sender: Player, args: Array<out String>): CompletableFuture<Double> {
        val value = args[0].toDoubleOrNull()
            ?: throw IllegalArgumentException("Error: '${args[0]}' is not a double")
        @Suppress("UNCHECKED_CAST")
        pdc.set(loquaDataKey as LoquaDataKey<Any, Double>, value)
        return CompletableFuture.completedFuture(value)
    }
}

internal class LocationDataHandler : DataHandler<Location> {
    override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<out Any, out Location>, sender: Player, args: Array<out String>): CompletableFuture<Location> {
        if (!sender.location.world.equals(LoquaInteractablePlugin.world)) {
            throw IllegalArgumentException("Error: '${sender.location.world.name}' is not the Loqua world")
        }
        val locationToSet = when {
            args[0] == "eye" -> sender.eyeLocation
            args[0] == "feet" -> sender.location
            else -> throw IllegalArgumentException("Error: '${args[0]}' is not 'eye' or 'feet'")
        }
        @Suppress("UNCHECKED_CAST")
        pdc.set(loquaDataKey as LoquaDataKey<Any, Location>, locationToSet)
        return CompletableFuture.completedFuture(locationToSet)
    }
    
    override fun display(value: Location): Component = value.toSimpleString()
}

internal object DataHandlerRegistry {
    private val handlers = mutableMapOf<PersistentDataType<*, *>, DataHandler<*>>()
    
    init {
        register(DataType.STRING, StringDataHandler())
        register(DataType.BOOLEAN, BooleanDataHandler())
        register(DataType.BYTE, ByteDataHandler())
        register(DataType.INTEGER, IntegerDataHandler())
        register(DataType.LONG, LongDataHandler())
        register(DataType.FLOAT, FloatDataHandler())
        register(DataType.DOUBLE, DoubleDataHandler())
        register(DataType.LOCATION, LocationDataHandler())
    }
    
    fun <C> register(dataType: PersistentDataType<*, C>, handler: DataHandler<C>) {
        handlers[dataType] = handler
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <C> getHandler(dataType: PersistentDataType<*, C>): DataHandler<C>? {
        return handlers[dataType] as? DataHandler<C>
    }
    
    fun hasHandler(dataType: PersistentDataType<*, *>): Boolean {
        return handlers.containsKey(dataType)
    }
}
