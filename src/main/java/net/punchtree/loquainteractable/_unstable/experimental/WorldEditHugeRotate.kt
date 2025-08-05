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
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.*
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.BoundingBox
import java.util.*
import kotlin.math.max
import kotlin.math.min

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

                "rotateitemframes" -> {
                    if (ongoingItemFrameRotate != null) {
                        Bukkit.broadcast(text("An ongoing item frame rotate task is already running. Please cancel it first!").color(RED))
                        return false
                    }
                    Bukkit.broadcast(text("Rotating item frames!"))
                    rotateItemFrames()
                }
                "cancelitemframerotate" -> {
                    ongoingItemFrameRotate?.cancel().also {
                        Bukkit.broadcast(text("Cancelled ongoing item frame rotate task").color(RED))
                    }
                    ongoingItemFrameRotate = null
                }
                else -> {
                    sender.sendMessage("Unknown subcommand: $subcommand")
                    return false
                }
            }

            return true
        }

        override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
            if (args.size <= 1) return mutableListOf("tryHugeRotate", "cancelRotate", "tryHugeFlip", "cancelFlip", "rotateItemFrames", "cancelItemFrameRotate")
            return mutableListOf()
        }
    }

    val xMin = -992
    val xMax = 3551
    val yMin = 0
    val yMax = 319
    val zMin = -240
    val zMax = 2175

//    val min = BlockVector3.at(-992, 0, -240)
//    val max = BlockVector3.at(3551, 319, 2175)
    val bukkitWorld = Bukkit.getWorld("GTA_City")
    val world = BukkitAdapter.adapt(bukkitWorld);

    val xMinHundred = -1000
    val xMaxHundred = 3600
    val zMinHundred = -300
    val zMaxHundred = 2200

    val minHundredPoint = BlockVector3.at(xMinHundred, yMin, zMinHundred)

    var ongoingRotate: BukkitTask? = null
    var ongoingFlip: BukkitTask? = null
    var ongoingItemFrameRotate: BukkitTask? = null

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

    fun rotateItemFrames() {
        val rotloquaXMin = -5551
        val rotloquaYMin = 0
        val rotloquaZMin = -2775
        val rotloquaXMax = -1008
        val rotloquaYMax = 319
        val rotloquaZMax = -360

        val rotloquaBB = BoundingBox(
            rotloquaXMin.toDouble(),
            rotloquaYMin.toDouble(),
            rotloquaZMin.toDouble(),
            rotloquaXMax.toDouble(),
            rotloquaYMax.toDouble(),
            rotloquaZMax.toDouble()
        )

        val rotloquaChunks = LoquaInteractablePlugin.world.getIntersectingChunks(rotloquaBB).toTypedArray()

        var numHangings = 0
        var numItemFrames = 0
        var numNonGlowingItemFrames = 0
        var numGlowingItemFrames = 0
        var numPaintings = 0
        var numAlreadyRotated = 0
        var numActuallyRotated = 0
        var numActuallyRotatedItemFrames = 0
        var numActuallyRotatedPaintings = 0

        var numTriedToScanTwice = 0

        val scannedThisTime = mutableSetOf<UUID>()

        ongoingItemFrameRotate = object : BukkitRunnable() {
            var operationIndex = 0
            var chunksToScanPerTick = 0.5
            var chunksToHaveScanned = chunksToScanPerTick
            val notificationThreshold = 8 * 1000
            var lastNotificationTime = System.currentTimeMillis()
            override fun run() {
                if (operationIndex >= rotloquaChunks.size) {
                    ongoingItemFrameRotate = null
                    Bukkit.broadcast(text("Item frame rotate operation completed!").color(GREEN))
                    cancel()
                    return
                }

                val accelerateTps = DebugVars.getDecimalAsDouble("loquainteractable.rotloqua.accelerate-tps", 19.7)
                val maintainTps = DebugVars.getDecimalAsDouble("loquainteractable.rotloqua.maintain-tps", 18.5)
                val decelerateTps = DebugVars.getDecimalAsDouble("loquainteractable.rotloqua.decelerate-tps", 17.5)
                chunksToScanPerTick = if (Bukkit.getServer().tps[0] > accelerateTps) {
                    min(222.2,chunksToScanPerTick + 0.02)
                } else if (Bukkit.getServer().tps[0] > maintainTps) {
                    chunksToScanPerTick
                } else if (Bukkit.getServer().tps[0] > decelerateTps) {
                    max(0.0, chunksToScanPerTick - 0.05)
                } else {
                    0.0
                }
                chunksToHaveScanned += chunksToScanPerTick
                while (operationIndex < min(chunksToHaveScanned, rotloquaChunks.size.toDouble())) {
                    val chunk = rotloquaChunks[operationIndex]
                    chunk.load(false)
                    chunk.entities.forEach { entity ->
                        if (entity !is Hanging) {
                            return@forEach
                        }

                        if (scannedThisTime.contains(entity.uniqueId)) {
                            numTriedToScanTwice++
                            return@forEach
                        } else {
                            scannedThisTime.add(entity.uniqueId)
                        }

                        numHangings++
                        if (entity is ItemFrame) {
                            numItemFrames++
                            if (entity !is GlowItemFrame) {
                                numNonGlowingItemFrames++
                            } else {
                                numGlowingItemFrames++
                            }
                        } else if (entity is Painting) {
                            numPaintings++
                        }

                        if (entity.hasMetadata("rotated-loqua-fix")) {
                            numAlreadyRotated++
                            return@forEach
                        }

                        if (DebugVars.getBoolean("loquainteractable.rotloqua.rotate", false)) {
                            when (entity.facing) {
                                BlockFace.NORTH -> entity.setFacingDirection(BlockFace.SOUTH, true)
                                BlockFace.SOUTH -> entity.setFacingDirection(BlockFace.NORTH, true)
                                BlockFace.EAST -> entity.setFacingDirection(BlockFace.WEST, true)
                                BlockFace.WEST -> entity.setFacingDirection(BlockFace.EAST, true)
                                BlockFace.UP, BlockFace.DOWN -> {
                                    check(entity is ItemFrame) { "Non-item frame is facing up or down! What???" }
                                    entity.rotation = entity.rotation.rotateClockwise().rotateClockwise().rotateClockwise().rotateClockwise()
                                }
                                else -> { throw IllegalStateException("Impossible hanging facing direction: ${entity.facing}") }
                            }
                            entity.setMetadata("rotated-loqua-fix", FixedMetadataValue(LoquaInteractablePlugin.instance, true))
                            numActuallyRotated++
                            if (entity is ItemFrame) {
                                numActuallyRotatedItemFrames++
                            } else if (entity is Painting) {
                                numActuallyRotatedPaintings++
                            }
                        }
                    }
                    operationIndex++
                }

                if (System.currentTimeMillis() - lastNotificationTime > notificationThreshold) {
                    lastNotificationTime = System.currentTimeMillis()
                    Bukkit.broadcast(text("Scanned ${operationIndex + 1} / ${rotloquaChunks.size} (${"%.1f".format( (operationIndex + 1) * 100.0 / rotloquaChunks.size)}%) chunks for hanging entities at ${"%.1f".format(chunksToScanPerTick)} chunks per tick.").color(WHITE))
                }
            }

            override fun cancel() {
                Bukkit.broadcastMessage("There are $numHangings hanging entities in the rotloqua bounding box.")
                Bukkit.broadcastMessage("There are $numItemFrames item frames in the rotloqua bounding box.")
                Bukkit.broadcastMessage("There are $numNonGlowingItemFrames non-glowing and $numGlowingItemFrames glowing item frames in the rotloqua bounding box.")
                Bukkit.broadcastMessage("There are $numPaintings paintings in the rotloqua bounding box.")
                Bukkit.broadcastMessage("There are $numAlreadyRotated item frames that have already been rotated!.")
                Bukkit.broadcastMessage("Actually rotated $numActuallyRotated hanging entities ($numActuallyRotatedItemFrames item frames and $numActuallyRotatedPaintings paintings).")
                Bukkit.broadcastMessage("Tried to scan $numTriedToScanTwice entities twice.")
                super.cancel()
            }
        }.runTaskTimer(LoquaInteractablePlugin.instance, 0, 1)
    }

    fun onDisable() {
        ongoingRotate?.cancel()
        ongoingRotate = null

        ongoingFlip?.cancel()
        ongoingFlip = null

        ongoingItemFrameRotate?.cancel()
        ongoingItemFrameRotate = null
    }

}