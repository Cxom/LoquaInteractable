package net.punchtree.loquainteractable.data

import com.jeff_media.morepersistentdatatypes.DataType
import net.punchtree.loquainteractable.LOQUA_NAMESPACE
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

data class LoquaDataKey<P, C>(val key: String, val persistentDataType: PersistentDataType<P, C>, val namespace: String = LOQUA_NAMESPACE) {
    val namespacedKey = NamespacedKey(namespace, key)
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
        val IS_IN_STAFF_MODE = LoquaDataKey("staff.is_in_staff_mode", DataType.BOOLEAN)
        /** LOCATION the staff member was last in play mode at */
        val STAFF_LAST_PLAY_MODE_LOCATION = LoquaDataKey("staff.last_play_mode_location", DataType.LOCATION)
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
        val SPLASH_CINEMATIC_TRACK_1_START = LoquaDataKey("splash_cinematic_track_1", DataType.LOCATION)
        val SPLASH_CINEMATIC_TRACK_1_END = LoquaDataKey("splash_cinematic_track_1_end", DataType.LOCATION)
        val SPLASH_CINEMATIC_TRACK_1_DURATION = LoquaDataKey("splash_cinematic_track_1_duration", DataType.INTEGER)

        val SPLASH_CINEMATIC_TRACK_2_START = LoquaDataKey("splash_cinematic_track_2", DataType.LOCATION)
        val SPLASH_CINEMATIC_TRACK_2_END = LoquaDataKey("splash_cinematic_track_2_end", DataType.LOCATION)
        val SPLASH_CINEMATIC_TRACK_2_DURATION = LoquaDataKey("splash_cinematic_track_2_duration", DataType.INTEGER)

        val CHARACTER_SELECT_CHARACTER_1_LOCATION = LoquaDataKey("character_select_character_1_location", DataType.LOCATION)
        val CHARACTER_SELECT_CHARACTER_1_CREATE_MENU_LOCATION = LoquaDataKey("character_select_character_1_create_menu_location", DataType.LOCATION)

        val TEST_CLINIC_RESPAWN = LoquaDataKey("test_clinic_respawn", DataType.LOCATION)

        val registry = listOf(
            SPLASH_CINEMATIC_TRACK_1_START,
            SPLASH_CINEMATIC_TRACK_1_END,
            SPLASH_CINEMATIC_TRACK_1_DURATION,
            SPLASH_CINEMATIC_TRACK_2_START,
            SPLASH_CINEMATIC_TRACK_2_END,
            SPLASH_CINEMATIC_TRACK_2_DURATION,
            CHARACTER_SELECT_CHARACTER_1_LOCATION,
            CHARACTER_SELECT_CHARACTER_1_CREATE_MENU_LOCATION,
            TEST_CLINIC_RESPAWN
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