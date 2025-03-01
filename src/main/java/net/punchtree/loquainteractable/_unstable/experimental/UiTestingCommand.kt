package net.punchtree.loquainteractable._unstable.experimental

import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.sound.Sound
import net.punchtree.loquainteractable.housing.Housings
import net.punchtree.loquainteractable.joining.SplashScreenManager
import net.punchtree.loquainteractable.joining.splash.CameraKeyframe
import net.punchtree.loquainteractable.joining.splash.CameraTrack
import net.punchtree.loquainteractable.joining.splash.Cinematic
import net.punchtree.loquainteractable.player.craftPlayer
import net.punchtree.loquainteractable.ui.Fade.fadeIn
import net.punchtree.loquainteractable.ui.Fade.fadeOut
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
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
//                Cinematic.startCinematic(sender, cameraTracks)
                Cinematic.startCinematic(sender, SplashScreenManager.splashCameraTracks)
            }
            "stop-cinematic" -> {
                Cinematic.stopCinematic(sender)
                sender.gameMode = GameMode.CREATIVE
            }
            "play-music" -> {
                val music = Sound.sound(org.bukkit.Sound.MUSIC_DISC_RELIC, Sound.Source.MASTER, 1f, 1f)
//                val music = Sound.sound(org.bukkit.Sound.BLOCK_NETHERRACK_BREAK, Sound.Source.MASTER, 1f, 1f)
                if (args.size < 2 || args[1] == "enum") {
                    sender.playSound(music)
                    sender.sendMessage("Playing wait from enum key")
                } else if (args[1] == "string"){
                    sender.playSound(Sound.sound(NamespacedKey("minecraft", "music_disc.wait"), Sound.Source.MASTER, 1f, 1f))
                    sender.sendMessage("Playing wait from string key")
                } else if (args[1] == "local") {
                    sender.playSound(music, sender.location.x, sender.location.y, sender.location.z)
                    sender.sendMessage("Playing wait with fixed range")
                } else if (args[1] == "emitter") {
                    sender.playSound(music, sender)
                    sender.sendMessage("Playing wait with emitter")
                } else if (args[1] == "packet-local") {
                    val soundPacket = PaperAdventure.asSoundPacket(
                        music, sender.location.x, sender.location.y, sender.location.z, 0L, null
                    )
                    sender.craftPlayer().handle.connection.send(soundPacket)
                } else if (args[1] == "packet-emitter") {
                    val soundPacket = PaperAdventure.asSoundPacket(
                        music, sender.craftPlayer().handle, 0L, null
                    )
                    sender.craftPlayer().handle.connection.send(soundPacket)
                } else {
                    sender.sendMessage("Unknown music key type: ${args[1]}")
                }
            }
            else -> sender.sendRichMessage("<red>Unknown subcommand: $subcommand</red>")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return listOf("fade-out", "fade-in", "start-cinematic", "stop-cinematic")
    }

}