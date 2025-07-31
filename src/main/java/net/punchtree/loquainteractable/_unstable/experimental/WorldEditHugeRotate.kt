package net.punchtree.loquainteractable._unstable.experimental

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.math.transform.AffineTransform
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.util.Direction
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor.*
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.util.color.PunchTreeColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

// TODO delete this when it is no longer necessary!
object WorldEditHugeRotate {

    object WorldEditHugeRotateCommand : CommandExecutor, TabCompleter {
        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
            if (sender !is Player) return false

            if (args.isEmpty()) return false

            when (val subcommand = args[0].lowercase()) {
                "tryhugerotate" -> {
                    val startIndex = if (args.size > 1) args[1].toIntOrNull() ?: run {
                        sender.sendMessage("Invalid number: ${args[1]}")
                        return false
                    } else 0
                    tryHugeRotate(startIndex)
                }
                "cancelrotate" -> {
                    ongoingRotate?.cancel().also {
                        Bukkit.broadcast(text("Cancelled ongoing rotate task").color(RED))
                    }
                    ongoingRotate = null
                }

                "tryhugeflip" -> {
                    val startIndex = if (args.size > 1) args[1].toIntOrNull() ?: run {
                        sender.sendMessage("Invalid number: ${args[1]}")
                        return false
                    } else 0
                    val isCopyingEntities = if (args.size > 2) args[2].toBooleanStrictOrNull() ?: run {
                        sender.sendMessage("Invalid boolean: ${args[2]}")
                        return false
                    } else true
                    tryHugeFlip(startIndex, isCopyingEntities)
                }
                "cancelflip" -> {
                    ongoingFlip?.cancel().also {
                        Bukkit.broadcast(text("Cancelled ongoing flip task").color(RED))
                    }
                    ongoingFlip = null
                }
                else -> {
                    sender.sendMessage("Unknown subcommand: $subcommand")
                    return false
                }
            }

            return true
        }

        override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
            if (args.size <= 1) return mutableListOf("tryHugeRotate", "cancelRotate", "tryHugeFlip", "cancelFlip")
            return mutableListOf()
        }
    }

    val xMin = -992
    val xMax = 3551
    val yMin = 0
    val yMax = 319
    val zMin = -240
    val zMax = 2175

    val min = BlockVector3.at(-992, 0, -240)
    val max = BlockVector3.at(3551, 319, 2175)
    val bukkitWorld = Bukkit.getWorld("GTA_City")
    val world = BukkitAdapter.adapt(bukkitWorld);

    val xMinHundred = -1000
    val xMaxHundred = 3600
    val zMinHundred = -300
    val zMaxHundred = 2200

    val minHundredPoint = BlockVector3.at(xMinHundred, yMin, zMinHundred)

    var ongoingRotate: BukkitTask? = null
    var ongoingFlip: BukkitTask? = null

    fun tryHugeRotate(startIndex: Int = 0) {

        if (ongoingRotate != null) {
            Bukkit.broadcast(text("An ongoing rotate task is already running. Please cancel it first!").color(RED))
            return
        }

        val cubes = mutableListOf<CuboidRegion>()
        for (x in xMinHundred until xMaxHundred step 100) {
            for (z in zMinHundred until zMaxHundred step 100) {
                val cubeMin = BlockVector3.at(x, yMin, z)
                val cubeMax = cubeMin.add(100, yMax, 100)
                cubes.add(CuboidRegion(world, cubeMin, cubeMax))
            }
        }

        if (startIndex < 0 || startIndex >= cubes.size) {
            Bukkit.broadcast(text("Invalid start index: $startIndex. Must be between 0 and ${cubes.size - 1}.").color(RED))
            return
        }

        ongoingRotate = object : BukkitRunnable() {
            var operationIndex = startIndex
            override fun run() {
                if (operationIndex >= cubes.size) {
                    ongoingRotate = null
                    Bukkit.broadcast(text("Huge rotate operation completed!").color(GREEN))
                    cancel()
                    return
                }

                val cube = cubes[operationIndex]

                try {
                    WorldEdit.getInstance().newEditSessionBuilder()
                        .world(world)
                        .build()
                        .use { copyEditSession ->
                        val clipboard = BlockArrayClipboard(cube)
                        clipboard.origin = minHundredPoint
                        val copy = ForwardExtentCopy(
                            copyEditSession, cube, minHundredPoint, clipboard, minHundredPoint
                        )
                        copy.isCopyingEntities = true
                        copy.isCopyingBiomes = true
                        copy.isRemovingEntities = false
                        val clipboardHolder = ClipboardHolder(clipboard)
                        clipboardHolder.transform = AffineTransform().rotateY(180.0)
                        val rotateAndPaste = clipboardHolder
                            .createPaste(copyEditSession)
                            .to(minHundredPoint)
                            .copyEntities(true)
                            .copyBiomes(true)
                            .build()
                        Operations.complete(copy)
                        Operations.complete(rotateAndPaste)
                        operationIndex++
                        Bukkit.broadcast(text("Copying loqua chunk $operationIndex/${cubes.size}").color(WHITE).append(
                            text(" TELEPORT")
                                .color(PunchTreeColor(255, 200, 200))
                                .clickEvent(ClickEvent.callback { audience ->
                                    if (audience is Player) {
                                        val x = xMinHundred + (xMinHundred - cube.center.x())
                                        val z = zMinHundred + (zMinHundred - cube.center.z())
                                        audience.teleport(Location(bukkitWorld, x, 150.0, z, 0f, 90f))
                                    } else {
                                        audience.sendMessage(text("Need to be a player in game to teleport!").color(RED))
                                    }
                                } )))
                        }
                } catch (e: Exception) {
                    Bukkit.broadcast(text("Error occurred while processing chunk $operationIndex: ${e.message}").color(RED))
                    e.printStackTrace()
                    cancel()
                }

            }
        }.runTaskTimerAsynchronously(LoquaInteractablePlugin.instance, 0, 4 * 20)
    }

    fun tryHugeFlip(startIndex: Int = 0, shouldCopyEntities: Boolean) {

        if (ongoingFlip != null) {
            Bukkit.broadcast(text("An ongoing flip task is already running. Please cancel it first!").color(RED))
            return
        }

        val cubes = mutableListOf<CuboidRegion>()
        for (x in xMinHundred until xMaxHundred step 100) {
            for (z in zMinHundred until zMaxHundred step 100) {
                val cubeMin = BlockVector3.at(x, yMin, z)
                val cubeMax = cubeMin.add(100, yMax, 100)
                cubes.add(CuboidRegion(world, cubeMin, cubeMax))
            }
        }

        if (startIndex < 0 || startIndex >= cubes.size) {
            Bukkit.broadcast(text("Invalid start index: $startIndex. Must be between 0 and ${cubes.size - 1}.").color(RED))
            return
        }

        ongoingFlip = object : BukkitRunnable() {
            var operationIndex = startIndex
            override fun run() {
                if (operationIndex >= cubes.size) {
                    ongoingRotate = null
                    Bukkit.broadcast(text("Huge flip operation completed!").color(GREEN))
                    cancel()
                    return
                }

                val cube = cubes[operationIndex]

                try {
                    WorldEdit.getInstance().newEditSessionBuilder()
                        .world(world)
                        .build()
                        .use { copyEditSession ->
                            val clipboard = BlockArrayClipboard(cube)
                            clipboard.origin = minHundredPoint
                            val copy = ForwardExtentCopy(
                                copyEditSession, cube, minHundredPoint, clipboard, minHundredPoint
                            )
                            copy.isCopyingEntities = shouldCopyEntities
                            copy.isCopyingBiomes = true
                            copy.isRemovingEntities = false
                            val clipboardHolder = ClipboardHolder(clipboard)
                            val direction = Direction.WEST.toBlockVector()
                            clipboardHolder.transform = AffineTransform().scale(direction.abs().multiply(-2).add(1, 1, 1).toVector3()).combine(clipboardHolder.transform)
                            val flipAndPaste = clipboardHolder
                                .createPaste(copyEditSession)
                                .to(minHundredPoint)
                                .copyEntities(shouldCopyEntities)
                                .copyBiomes(true)
                                .build()
                            Operations.complete(copy)
                            Operations.complete(flipAndPaste)
                            operationIndex++
                            Bukkit.broadcast(text("Flipping loqua chunk $operationIndex/${cubes.size}").color(WHITE).append(
                                text(" TELEPORT")
                                    .color(PunchTreeColor(255, 200, 200))
                                    .clickEvent(ClickEvent.callback { audience ->
                                        if (audience is Player) {
                                            val x = xMinHundred + (xMinHundred - cube.center.x())
                                            val z = cube.center.z()
                                            audience.teleport(Location(bukkitWorld, x, 150.0, z, 0f, 90f))
                                        } else {
                                            audience.sendMessage(text("Need to be a player in game to teleport!").color(RED))
                                        }
                                    } )))
                        }
                } catch (e: Exception) {
                    Bukkit.broadcast(text("Error occurred while processing chunk $operationIndex: ${e.message}").color(RED))
                    e.printStackTrace()
                    cancel()
                }

            }
        }.runTaskTimerAsynchronously(LoquaInteractablePlugin.instance, 0, 4 * 20)
    }

//    fun rotateItemFrames() {
//        val rotloquaXMin = -5551
//        val rotloquaYMin = 0
//        val rotloquaZMin = -2775
//        val rotloquaXMax = -1008
//        val rotloquaYMax = 319
//        val rotloquaZMax = -360
//    }

    fun onDisable() {
        ongoingRotate?.cancel()
        ongoingRotate = null

        ongoingFlip?.cancel()
        ongoingFlip = null
    }

}