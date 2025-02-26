package net.punchtree.loquainteractable._unstable.experimental

import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.joining.splash.CameraKeyframe
import net.punchtree.loquainteractable.joining.splash.CameraTrack
import net.punchtree.loquainteractable.joining.splash.Cinematic
import net.punchtree.loquainteractable.ui.Fade.fadeIn
import net.punchtree.loquainteractable.ui.Fade.fadeOut
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object UiTestingCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.isEmpty()) return false

        when(val subcommand = args[0]) {
            "fade-out" -> fadeOut(sender, args.getOrNull(1)?.toLongOrNull() ?: 250L)
            "fade-in" -> fadeIn(sender, args.getOrNull(1)?.toLongOrNull() ?: 250L)
            "start-cinematic" -> {
                val trackStart1 = DebugVars.getLocation("cinematic-track-start-1", Housings.DEFAULT_HOUSING_SPAWN)
                val trackEnd1 = DebugVars.getLocation("cinematic-track-end-1", Housings.DEFAULT_HOUSING_SPAWN)
                val trackStart2 = DebugVars.getLocation("cinematic-track-start-2", Housings.DEFAULT_HOUSING_SPAWN)
                val trackEnd2 = DebugVars.getLocation("cinematic-track-end-2", Housings.DEFAULT_HOUSING_SPAWN)
                val keyframe1 = CameraKeyframe(trackStart1, 0)
                val keyframe2 = CameraKeyframe(trackEnd1, 5000)
                val keyframe3 = CameraKeyframe(trackStart2, 0)
                val keyframe4 = CameraKeyframe(trackEnd2, 5000)
                val cameraTracks = listOf(
                    CameraTrack(sortedSetOf(keyframe1, keyframe2)),
                    CameraTrack(sortedSetOf(keyframe3, keyframe4))
                )
                Cinematic.startCinematic(sender, cameraTracks)
            }
            "stop-cinematic" -> {
                Cinematic.stopCinematic(sender)
                sender.gameMode = GameMode.CREATIVE
            }
            else -> sender.sendRichMessage("<red>Unknown subcommand: $subcommand</red>")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return listOf("fade-out", "fade-in", "start-cinematic", "stop-cinematic")
    }

}