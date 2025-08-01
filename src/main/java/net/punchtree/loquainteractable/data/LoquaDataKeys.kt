package net.punchtree.loquainteractable.data

import com.jeff_media.morepersistentdatatypes.DataType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.punchtree.loquainteractable.LOQUA_NAMESPACE
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.displayutil.toSimpleString
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.CompletableFuture

data class LoquaDataKey<P, C>(val key: String, val persistentDataType: PersistentDataType<P, C>, val namespace: String = LOQUA_NAMESPACE) {
    val namespacedKey = NamespacedKey(namespace, key)
}

// Right now we only have text editing, so this interface makes sense
// as we expand the contexts in which we may wish to modify data (client mod, web editor, etc.)
// we can split each individual method into its own interface, and then have the create method take each one as optional
// and then return an anonymous object implementing the respective interfaces
// one example use case is that of modifying an item stack array, particularly an inventory
// there's not a good way to do this with just a parse - what we would ideally have happen, is that modifying an item stack array gives a view, that allows moving around items, and then
// when that inventory is closed, it "commits" the changes to the data structure
// this can just be a plain set call, depending on how it's triggered

interface InspectablePersistentDataType<P, C> : PersistentDataType<P, C> {
    fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<P, C>, sender: Player, args: Array<out String>): CompletableFuture<C>
    fun display(value: C): Component
}

internal fun <P : Any, C : Any> createInspectableDataType(
    persistentDataType: PersistentDataType<P, C>,
    edit: (PersistentDataContainer, LoquaDataKey<P, C>, Player, Array<out String>) -> CompletableFuture<C> = { _, _, _, _ -> throw NotImplementedError("Editing not implemented for this data type") },
    display: (C) -> Component = { value -> text(value.toString()) }
): InspectablePersistentDataType<P, C> {
    return object : InspectablePersistentDataType<P, C> {
        override fun edit(pdc: PersistentDataContainer, loquaDataKey: LoquaDataKey<P, C>, sender: Player, args: Array<out String>): CompletableFuture<C> {
            return edit(pdc, loquaDataKey, sender, args)
        }
        override fun display(value: C): Component {
            return display(value)
        }
        override fun getPrimitiveType(): Class<P?> {
            return persistentDataType.getPrimitiveType()
        }
        override fun getComplexType(): Class<C?> {
            return persistentDataType.getComplexType()
        }
        override fun toPrimitive(complex: C, context: PersistentDataAdapterContext): P {
            return persistentDataType.toPrimitive(complex, context)
        }
        override fun fromPrimitive(primitive: P, context: PersistentDataAdapterContext): C {
            return persistentDataType.fromPrimitive(primitive, context)
        }
    }
}

// TODO this type implies reuse, but in-game based inspection is really only triggered via commands. This should really be moved to
//  PdcCommand in the form of mapped data handlers, not requiring that the LoquaDataKey is declared with one of these types
internal data object InspectableDataType {
    // TODO really use more advanced brigadier commands to parse a bit better? for text location parsing? idek maybe it's not worth it
    val STRING = createInspectableDataType(DataType.STRING,
        edit = { pdc, loquaDataKey, sender, args ->
            val value = args.joinToString(" ")
            pdc.set(loquaDataKey, value)
            return@createInspectableDataType CompletableFuture.completedFuture(value)
        }
    )
    val BOOLEAN = createInspectableDataType(DataType.BOOLEAN,
        edit = { pdc, loquaDataKey, sender, args ->
            val value = args[0].lowercase().toBooleanStrictOrNull()
                ?: throw IllegalArgumentException("Error: '${args[0]}' is not a boolean")
            pdc.set(loquaDataKey, value)
            return@createInspectableDataType CompletableFuture.completedFuture(value)
        }
    )
    val BYTE = createInspectableDataType(DataType.BYTE,
        edit = { pdc, loquaDataKey, sender, args ->
            val value = args[0].toByteOrNull()
                ?: throw IllegalArgumentException("Error: '${args[0]}' is not a byte")
            pdc.set(loquaDataKey, value)
            return@createInspectableDataType CompletableFuture.completedFuture(value)
        }
    )
    val INTEGER = createInspectableDataType(DataType.INTEGER,
        edit = { pdc, loquaDataKey, sender, args ->
            val value = args[0].toIntOrNull()
                ?: throw IllegalArgumentException("Error: '${args[0]}' is not an integer")
            pdc.set(loquaDataKey, value)
            return@createInspectableDataType CompletableFuture.completedFuture(value)
        }
    )
    val LONG = createInspectableDataType(DataType.LONG,
        edit = { pdc, loquaDataKey, sender, args ->
            val value = args[0].toLongOrNull()
                ?: throw IllegalArgumentException("Error: '${args[0]}' is not a long")
            pdc.set(loquaDataKey, value)
            return@createInspectableDataType CompletableFuture.completedFuture(value)
        }
    )
    val FLOAT = createInspectableDataType(DataType.FLOAT,
        edit = { pdc, loquaDataKey, sender, args ->
            val value = args[0].toFloatOrNull()
                ?: throw IllegalArgumentException("Error: '${args[0]}' is not a float")
            pdc.set(loquaDataKey, value)
            return@createInspectableDataType CompletableFuture.completedFuture(value)
        }
    )
    val DOUBLE = createInspectableDataType(DataType.DOUBLE,
        edit = { pdc, loquaDataKey, sender, args ->
            val value = args[0].toDoubleOrNull()
                ?: throw IllegalArgumentException("Error: '${args[0]}' is not a double")
            pdc.set(loquaDataKey, value)
            return@createInspectableDataType CompletableFuture.completedFuture(value)
        }
    )
    val LOCATION = createInspectableDataType(DataType.LOCATION,
        edit = { pdc, loquaDataKey, sender, args ->
            if (!sender.location.world.equals(LoquaInteractablePlugin.world)) {
                throw IllegalArgumentException("Error: '${sender.location.world.name}' is not the Loqua world")
            }
            val locationToSet = when {
                args[0] == "eye" -> sender.eyeLocation
                args[0] == "feet" -> sender.location
                else -> throw IllegalArgumentException("Error: '${args[0]}' is not 'eye' or 'feet'")
            }
            pdc.set(loquaDataKey, locationToSet)
            return@createInspectableDataType CompletableFuture.completedFuture(locationToSet)
        },
        display = Location::toSimpleString
    )
}

// TODO - we are MOST DEFINITELY going to be renaming keys during the development process and in the future
//  infrastructure in place to read and convert old keys to new key names on join (some sort of migration script system)
//  will be incredibly useful and encouraging us not to be lazy about key names

internal data object LoquaDataKeys {

    fun registryForPdcTypeOf(pdcHolder: PersistentDataHolder): Map<NamespacedKey, LoquaDataKey<out Any, out Any>> {
        return when (pdcHolder) {
            is org.bukkit.World -> World.registry
            is org.bukkit.entity.Player -> Player.registry
            else -> emptyMap()
        }
    }

    internal data object Player {
        /** ITEM_STACK_ARRAY of the player's inventory when it was last saved (usually logout)
         *  It is also saved for staff when they enter staff mode */
        val INVENTORY = LoquaDataKey("inventory", DataType.ITEM_STACK_ARRAY)

        // =========== STAFF MODE ===========
        /** BOOLEAN whether the player is in staff mode */
        val IS_IN_STAFF_MODE = LoquaDataKey("staff.is_in_staff_mode", InspectableDataType.BOOLEAN)
        /** LOCATION the staff member was last in play mode at */
        val STAFF_LAST_PLAY_MODE_LOCATION = LoquaDataKey("staff.last_play_mode_location", InspectableDataType.LOCATION)
        /** ITEM_STACK_ARRAY of the staff member's inventory when they last exited staff mode */
        val STAFF_MODE_INVENTORY = LoquaDataKey("staff.staff_mode_inventory", DataType.ITEM_STACK_ARRAY)

        val registry = listOf(
            INVENTORY,
            IS_IN_STAFF_MODE,
            STAFF_LAST_PLAY_MODE_LOCATION,
            STAFF_MODE_INVENTORY
        ).associateBy { it.namespacedKey }
    }

    internal data object World {
        // TODO these keys should really be morphed into some sort of dynamic array/object system
        //  that needs to be designed - and in the process of designing that the question of if this shouldn't all be written
        //  to a configurable file should probably also be answered
        //  Proper editing tools in a separate plugin that depends on this one may be the way to go

        // TODO migrate the track startpoints to have 'start' in the name and use that as an opportunity to build and test migration logic
        //  also make the duration keys have millis at the end
        //  it could also be useful to build a utility that tracks accesses to these keys, as a way to investigate if we're successfully
        //  cleaning up after ourselves
        val SPLASH_CINEMATIC_TRACK_1_START = LoquaDataKey("splash_cinematic_track_1", InspectableDataType.LOCATION)
        val SPLASH_CINEMATIC_TRACK_1_END = LoquaDataKey("splash_cinematic_track_1_end", InspectableDataType.LOCATION)
        val SPLASH_CINEMATIC_TRACK_1_DURATION = LoquaDataKey("splash_cinematic_track_1_duration", InspectableDataType.INTEGER)

        val SPLASH_CINEMATIC_TRACK_2_START = LoquaDataKey("splash_cinematic_track_2", InspectableDataType.LOCATION)
        val SPLASH_CINEMATIC_TRACK_2_END = LoquaDataKey("splash_cinematic_track_2_end", InspectableDataType.LOCATION)
        val SPLASH_CINEMATIC_TRACK_2_DURATION = LoquaDataKey("splash_cinematic_track_2_duration", InspectableDataType.INTEGER)

        val CHARACTER_SELECT_CHARACTER_1_LOCATION = LoquaDataKey("character_select_character_1_location", InspectableDataType.LOCATION)
        val CHARACTER_SELECT_CHARACTER_1_CREATE_MENU_LOCATION = LoquaDataKey("character_select_character_1_create_menu_location", InspectableDataType.LOCATION)

        val TEST_CLINIC_RESPAWN = LoquaDataKey("test_clinic_respawn", InspectableDataType.LOCATION)

        val registry = listOf(
            SPLASH_CINEMATIC_TRACK_1_START,
            SPLASH_CINEMATIC_TRACK_1_END,
            SPLASH_CINEMATIC_TRACK_1_DURATION,
            SPLASH_CINEMATIC_TRACK_2_START,
            SPLASH_CINEMATIC_TRACK_2_END,
            SPLASH_CINEMATIC_TRACK_2_DURATION,
            CHARACTER_SELECT_CHARACTER_1_LOCATION,
            CHARACTER_SELECT_CHARACTER_1_CREATE_MENU_LOCATION
        ).associateBy { it.namespacedKey }
    }
}

// To whoever has to read these extension methods, let me explain what is going on here with types,
// because it was NON-OBVIOUS to me
// Kotlin types MUST be EXPLICITLY nullable or non-nullable, and nullability must be handled
// in other words, in order to return a NULLABLE type, like ::get does, we MUST indicate that nullability, with a ?
// so, that means C must be non-nullable (thus C : Any) so that C? is then nullable.
// HOWEVER - AND THIS IS THE KEY PART - LoquaDataKey just wraps a PersistentDataType<P, C>
// PersistentDataType is a JAVA class using JAVA type parameters (boxed primitives or paper/bukkit api things)
// JAVA does not KNOW or ENFORCE nullability. EVERYTHING with the generic type system here is KOTLIN TYPE CHECKING AT COMPILE TIME
// that VANISHES when we are crossing the interop barrier into java-land at runtime. the types of LoquaDataKey and in turn PersistentDataType ARE NULLABLE
// Kotlin does a type check at compile time that has NO bearing on the nullability of the JAVA types parameterized into PersistentDataType
fun <P : Any, C : Any> PersistentDataContainer.get(key: LoquaDataKey<P, C>): C? {
    return get(key.namespacedKey, key.persistentDataType)
}

fun <P : Any, C : Any> PersistentDataContainer.getOrDefault(key: LoquaDataKey<P, C>, default: C): C {
    return get(key.namespacedKey, key.persistentDataType) ?: default
}

fun <P : Any, C : Any> PersistentDataContainer.set(key: LoquaDataKey<P, C>, value: C) {
    set(key.namespacedKey, key.persistentDataType, value)
}

@Suppress("UNCHECKED_CAST")
fun PersistentDataContainer.setUnsafe(key: LoquaDataKey<*, *>, value: Any) {
    set(
        key.namespacedKey,
        key.persistentDataType as PersistentDataType<Any, Any>,
        value
    )
}

fun <P : Any, C : Any> PersistentDataContainer.remove(key: LoquaDataKey<P, C>) {
    remove(key.namespacedKey)
}

fun <P : Any, C : Any> PersistentDataContainer.has(key: LoquaDataKey<P, C>): Boolean {
    return has(key.namespacedKey, key.persistentDataType)
}