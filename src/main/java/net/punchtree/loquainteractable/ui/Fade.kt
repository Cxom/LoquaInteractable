package net.punchtree.loquainteractable.ui

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.punchtree.loquainteractable.font.SpecialCharacters
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.entity.Player
import java.time.Duration

data object Fade {

    // TODO using special characters in titles doesn't work with HUD disabled - look into textdisplays for that

    fun blackOut(player: Player) {
        fadeOut(player, 0)
    }

    fun fadeOut(player: Player, fadeOutMillis: Long) {
        player.showTitle(
            Title.title(
                SpecialCharacters.DARK,
                Component.empty(),
                Title.Times.times(Duration.ofMillis(fadeOutMillis), Duration.ofDays(1), Duration.ZERO)))
    }

    fun fadeIn(player: Player, fadeInMillis: Long) {
        player.showTitle(
            Title.title(
                SpecialCharacters.DARK,
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ZERO, Duration.ofMillis(fadeInMillis))
            )
        )
    }

}