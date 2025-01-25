package net.punchtree.loquainteractable.instruments

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

data object Instruments {

    interface Instrument {
        fun notes(): List<String>
        fun letRing(): Boolean
        fun itemStack(): ItemStack
    }

    fun isInstrument(itemStack: ItemStack): Boolean {
        // TODO proper metadata
        return AcousticGuitar.itemStack() == itemStack ||
                LegatoTrumpet.itemStack() == itemStack
    }

    data object AcousticGuitar : Instrument {
        override fun notes(): List<String> = notes

        override fun letRing(): Boolean = true

        /** This is not cloned! Do not modify!!! */
        override fun itemStack(): ItemStack = GUITAR

        private val GUITAR = ItemStack(Material.PAPER).also {
            it.editMeta { itemMeta ->
                itemMeta.itemModel = NamespacedKey("punchtree", "acoustic_guitar")
                itemMeta.itemName(Component.text("Acoustic Guitar"))
            }
        }

        private val e_2 = "punchtree:instrument.guitar.e_2"
        private val f_2 = "punchtree:instrument.guitar.f_2"
        private val f_sharp_2 = "punchtree:instrument.guitar.f_sharp_2"
        private val g_2 = "punchtree:instrument.guitar.g_2"
        private val g_sharp_2 = "punchtree:instrument.guitar.g_sharp_2"
        private val a_2 = "punchtree:instrument.guitar.a_2"
        private val a_sharp_2 = "punchtree:instrument.guitar.a_sharp_2"
        private val b_2 = "punchtree:instrument.guitar.b_2"
        private val c_3 = "punchtree:instrument.guitar.c_3"
        private val c_sharp_3 = "punchtree:instrument.guitar.c_sharp_3"
        private val d_3 = "punchtree:instrument.guitar.d_3"
        private val d_sharp_3 = "punchtree:instrument.guitar.d_sharp_3"
        private val e_3 = "punchtree:instrument.guitar.e_3"
        private val f_3 = "punchtree:instrument.guitar.f_3"
        private val f_sharp_3 = "punchtree:instrument.guitar.f_sharp_3"
        private val g_3 = "punchtree:instrument.guitar.g_3"
        private val g_sharp_3 = "punchtree:instrument.guitar.g_sharp_3"
        private val a_3 = "punchtree:instrument.guitar.a_3"
        private val a_sharp_3 = "punchtree:instrument.guitar.a_sharp_3"
        private val b_3 = "punchtree:instrument.guitar.b_3"
        private val c_4 = "punchtree:instrument.guitar.c_4"
        private val c_sharp_4 = "punchtree:instrument.guitar.c_sharp_4"
        private val d_4 = "punchtree:instrument.guitar.d_4"
        private val d_sharp_4 = "punchtree:instrument.guitar.d_sharp_4"
        private val e_4 = "punchtree:instrument.guitar.e_4"
        private val f_4 = "punchtree:instrument.guitar.f_4"
        private val f_sharp_4 = "punchtree:instrument.guitar.f_sharp_4"
        private val g_4 = "punchtree:instrument.guitar.g_4"
        private val g_sharp_4 = "punchtree:instrument.guitar.g_sharp_4"
        private val a_4 = "punchtree:instrument.guitar.a_4"
        private val a_sharp_4 = "punchtree:instrument.guitar.a_sharp_4"
        private val b_4 = "punchtree:instrument.guitar.b_4"

        private val notes = listOf(
            e_2, f_2, f_sharp_2, g_2, g_sharp_2, a_2, a_sharp_2, b_2,
            c_3, c_sharp_3, d_3, d_sharp_3, e_3, f_3, f_sharp_3, g_3, g_sharp_3, a_3, a_sharp_3, b_3,
            c_4, c_sharp_4, d_4, d_sharp_4, e_4, f_4, f_sharp_4, g_4, g_sharp_4, a_4, a_sharp_4, b_4
        )
    }

    data object LegatoTrumpet : Instrument {
        override fun notes(): List<String> = notes

        override fun letRing(): Boolean = false

        /** This is not cloned! Do not modify!!! */
        override fun itemStack(): ItemStack = TRUMPET

        private val TRUMPET = ItemStack(Material.PAPER).also {
            it.editMeta { itemMeta ->
//                itemMeta.itemModel = NamespacedKey("punchtree", "trumpet")
                itemMeta.itemModel = NamespacedKey("punchtree", "trumpet")
                itemMeta.itemName(Component.text("Trumpet"))
            }
        }

        private val c_sharp_3 = "punchtree:instrument.trumpet.c_sharp_3"
        private val d_3 = "punchtree:instrument.trumpet.d_3"
        private val d_sharp_3 = "punchtree:instrument.trumpet.d_sharp_3"
        private val e_3 = "punchtree:instrument.trumpet.e_3"
        private val f_3 = "punchtree:instrument.trumpet.f_3"
        private val f_sharp_3 = "punchtree:instrument.trumpet.f_sharp_3"
        private val g_3 = "punchtree:instrument.trumpet.g_3"
        private val g_sharp_3 = "punchtree:instrument.trumpet.g_sharp_3"
        private val a_3 = "punchtree:instrument.trumpet.a_3"
        private val a_sharp_3 = "punchtree:instrument.trumpet.a_sharp_3"
        private val b_3 = "punchtree:instrument.trumpet.b_3"
        private val c_4 = "punchtree:instrument.trumpet.c_4"
        private val c_sharp_4 = "punchtree:instrument.trumpet.c_sharp_4"
        private val d_4 = "punchtree:instrument.trumpet.d_4"
        private val d_sharp_4 = "punchtree:instrument.trumpet.d_sharp_4"
        private val e_4 = "punchtree:instrument.trumpet.e_4"
        private val f_4 = "punchtree:instrument.trumpet.f_4"
        private val f_sharp_4 = "punchtree:instrument.trumpet.f_sharp_4"
        private val g_4 = "punchtree:instrument.trumpet.g_4"
        private val g_sharp_4 = "punchtree:instrument.trumpet.g_sharp_4"
        private val a_4 = "punchtree:instrument.trumpet.a_4"
        private val a_sharp_4 = "punchtree:instrument.trumpet.a_sharp_4"
        private val b_4 = "punchtree:instrument.trumpet.b_4"
        private val c_5 = "punchtree:instrument.trumpet.c_5"
        private val c_sharp_5 = "punchtree:instrument.trumpet.c_sharp_5"
        private val d_5 = "punchtree:instrument.trumpet.d_5"
        private val d_sharp_5 = "punchtree:instrument.trumpet.d_sharp_5"

        private val notes = listOf(
            c_sharp_3, d_3, d_sharp_3, e_3, f_3, f_sharp_3, g_3, g_sharp_3, a_3, a_sharp_3, b_3,
            c_4, c_sharp_4, d_4, d_sharp_4, e_4, f_4, f_sharp_4, g_4, g_sharp_4, a_4, a_sharp_4, b_4,
            c_5, c_sharp_5, d_5, d_sharp_5
        )

    }

}