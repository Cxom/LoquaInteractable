package net.punchtree.loquainteractable.ui

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.punchtree.loquainteractable.font.SpecialCharacters
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.entity.Player
import java.time.Duration

data object Fade {

    fun blackOut(player: Player) {
        fadeOut(player, 0)
    }

    fun fadeOut(player: Player, fadeOutTime: Int) {
        player.showTitle(
            Title.title(
                SpecialCharacters.DARK,
                Component.empty(),
                Title.Times.times(Duration.ofMillis(fadeOutTime * 50L), Duration.ofDays(1), Duration.ZERO)))
    }

    fun fadeIn(player: Player, fadeInTime: Int) {
        val fadeInTime = DebugVars.getInteger("ui-fade-in-time", 5)
        player.showTitle(
            Title.title(
                SpecialCharacters.DARK,
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ZERO, Duration.ofMillis(fadeInTime * 50L))
            )
        )
    }

}