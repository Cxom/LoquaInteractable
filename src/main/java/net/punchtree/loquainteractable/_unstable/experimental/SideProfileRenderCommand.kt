package net.punchtree.loquainteractable._unstable.experimental

import net.kyori.adventure.text.Component.text
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.data.PdcCommand.toSimpleString
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

const val MAX_SIDE_LENGTH = 4000
const val MAX_HEIGHT = 320 + 64
const val DEFAULT_MAX_SCAN_DEPTH = 1000

object SideProfileRenderCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.isEmpty()) return false

        when(val subcommand = args[0]) {
            "scan" -> return scan(sender, args)
            else -> {
                sender.sendMessage("Unknown subcommand: $subcommand")
            }
        }

        return true
    }

    private fun scan(player: Player, args: Array<out String>): Boolean {
        if (args.size < 5) {
            return false
        }
        val left = args[1].toIntOrNull() ?: return false
        val up = args[2].toIntOrNull() ?: return false
        val right = args[3].toIntOrNull() ?: return false
        val down = args[4].toIntOrNull() ?: return false

        player.sendMessage("${player.facing.name} ${player.facing.direction} ${player.facing.direction.length()}")
        val forwardVector = player.facing.direction
        val upVector = Vector(0.0, 1.0, 0.0)
        val rightVector = forwardVector.getCrossProduct(upVector).normalize()

        val pos1 = player.location.toBlockLocation().clone()
            .add(upVector.clone().multiply(up))
            .add(rightVector.clone().multiply(-left))

        val pos2 = player.location.toBlockLocation().clone()
            .add(upVector.clone().multiply(-down))
            .add(rightVector.clone().multiply(right))

        player.sendMessage(text("pos1: ").append(pos1.toSimpleString()))
        player.sendMessage(text("pos2: ").append(pos2.toSimpleString()))

        val min = Vector(
           min(pos1.x, pos2.x),
           min(pos1.y, pos2.y),
           min(pos1.z, pos2.z)
        )
        val max = Vector(
            max(pos1.x, pos2.x),
            max(pos1.y, pos2.y),
            max(pos1.z, pos2.z)
        )

        val height = (max.y - min.y).toInt()
        val width = max(max.x - min.x, max.z - min.z).toInt()
        if (height > MAX_HEIGHT) {
            player.sendMessage("Height is too large: $height")
            return true
        }
        if (width > MAX_SIDE_LENGTH) {
            player.sendMessage("Width is too large: $width")
            return true
        }

        val isOnXAxis = max.x == min.x
        val isOnZAxis = max.z == min.z
        check(isOnXAxis xor isOnZAxis) { "Scan must be aligned to one axis! x: $isOnXAxis, z: $isOnZAxis" }

        player.sendMessage("Scanning for image of dimensions $width x $height")

        val maxScanDepth = DebugVars.getInteger("side-profile-render-max-scan-depth", DEFAULT_MAX_SCAN_DEPTH)

        object : BukkitRunnable() {
            var y = 0
            var x = 0
            val RUNTIME_BUDGET_MILLIS = 10L
            val SECONDS_BETWEEN_DIGESTS = 5L
            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            var lastDigest = System.currentTimeMillis()
            override fun run() {
                val frameStart = System.currentTimeMillis()
                while (Bukkit.getServer().tps[0] >= 17 && System.currentTimeMillis() - frameStart < RUNTIME_BUDGET_MILLIS) {
                    if (x >= width) {
                        x = 0
                        y++
                        if (y >= height) {
                            cancel()
                            return
                        }
                    }
                    val blockX = min.x + if (isOnXAxis) 0 else x
                    val blockY = min.y + y
                    val blockZ = min.z + if (isOnZAxis) 0 else x
                    var startBlock = player.world.getBlockAt(blockX.toInt(), blockY.toInt(), blockZ.toInt())
                    var searchDepth = 0
                    while (startBlock.isEmpty && searchDepth++ < maxScanDepth) {
                        startBlock = startBlock.getRelative(player.facing)
                    }
                    val mapColor = if (startBlock.isEmpty) 0 else startBlock.blockData.mapColor.asARGB()
                    image.setRGB(x, (height - 1 - y), mapColor)

                    x++
                }

                if (lastDigest + (SECONDS_BETWEEN_DIGESTS * 1000) < System.currentTimeMillis()) {
                    val scanned = y * width + x
                    val total = width * height
                    val formattedPercentage = String.format("%.1f", (scanned * 100.0 / total))
                    val pausedNotice = if (Bukkit.getServer().tps[0] < 17) " - paused because tps is below 17" else ""
                    if (player.isConnected) {
                        player.sendMessage(text("Scanning progress $scanned / $total ($formattedPercentage%)$pausedNotice"))
                    }
                    LoquaInteractablePlugin.instance.logger.info("Scanning progress $scanned / $total ($formattedPercentage%)$pausedNotice")
                    lastDigest = System.currentTimeMillis()
                }
            }

            override fun cancel() {
                val outputDir = Paths.get(LoquaInteractablePlugin.instance.dataFolder.toString(), "side-profile-renders")
                Files.createDirectories(outputDir)
                val dateTime = System.currentTimeMillis()
                val outputFile = outputDir.resolve("$dateTime.png").toFile()
                ImageIO.write(image, "png", outputFile)
                if (player.isConnected) {
                    player.sendMessage("Done scanning!")
                    player.sendMessage("Wrote image to $outputFile")
                }
                super.cancel()
            }
        }.runTaskTimer(LoquaInteractablePlugin.instance, 0, 1)

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return listOf("scan")
    }
}