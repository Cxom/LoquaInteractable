package net.punchtree.loquainteractable.font

import net.kyori.adventure.text.Component.text

data object SpecialCharacters {

    val DARK = text("\uE000").font(Fonts.SPECIAL)
    val PRESS_F5 = text("\uE002").font(Fonts.SPECIAL)

}

