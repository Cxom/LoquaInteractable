package net.punchtree.loquainteractable.player

import com.jeff_media.morepersistentdatatypes.DataType
import net.punchtree.loquainteractable.LOQUA_NAMESPACE
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType

//data class LoquaDataKey<P, C>(val key: String, val persistentDataType: PersistentDataType<P, C>, val namespace: String = LOQUA_NAMESPACE) {
//    val namespacedKey = NamespacedKey(namespace, key)
//}

// TODO - we are MOST DEFINITELY going to be renaming keys during the development process and in the future
//  infrastructure in place to read and convert old keys to new key names on join (some sort of migration script system)
//  will be incredibly useful and encouraging us not to be lazy about key names

internal data object LoquaDataKeys {
    /** ITEM_STACK_ARRAY of the player's inventory when it was last saved (usually logout)
     *  It is also saved for staff when they enter staff mode
     * */
    val INVENTORY = NamespacedKey(LOQUA_NAMESPACE, "inventory")

    // =========== STAFF MODE ===========
    /** BOOLEAN whether the player is in staff mode */
    val IS_IN_STAFF_MODE = NamespacedKey(LOQUA_NAMESPACE, "staff.is_in_staff_mode")
    /** LOCATION the staff member was last in play mode at */
    val STAFF_LAST_PLAY_MODE_LOCATION = NamespacedKey(LOQUA_NAMESPACE, "staff.last_play_mode_location")
    /** ITEM_STACK_ARRAY of the staff member's inventory when they last exited staff mode */
    val STAFF_MODE_INVENTORY = NamespacedKey(LOQUA_NAMESPACE, "staff.staff_mode_inventory")
}