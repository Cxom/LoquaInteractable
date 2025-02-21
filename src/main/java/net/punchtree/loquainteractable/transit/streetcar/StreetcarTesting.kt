package net.punchtree.loquainteractable.transit.streetcar

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.transit.streetcar.path.Path
import net.punchtree.loquainteractable.transit.streetcar.path.PathSegmentFactory
import net.punchtree.loquainteractable.transit.streetcar.path.StreetcarRoute
import net.punchtree.loquainteractable.transit.streetcar.path.StreetcarStop
import net.punchtree.loquainteractable.transit.streetcar.path.segment.PathDrawer
import net.punchtree.loquainteractable.transit.streetcar.path.segment.spline.CatmullRomSplineSegment
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

object StreetcarTesting {

    private val trainsToCleanup = mutableListOf<StreetcarTrain>()

    internal fun doStraightAndCurve(player: Player) {
        player.sendMessage("Doing straight and curve")

        val straightSegment = PathSegmentFactory.straight(player.location.clone().add(0.0, 0.0, 10.0), player.location.clone().add(20.0, 0.0, 10.0))
        val curveSegment = PathSegmentFactory.circleArc(player.location.clone().add(20.0, 0.0, -10.0), 20.0, 90.0, -90.0)
        val straightSegment2 = PathSegmentFactory.straight(player.location.clone().add(20.0, 0.0, -30.0), player.location.clone().add(0.0, 0.0, -30.0))
        val curveSegment2 = PathSegmentFactory.circleArc(player.location.clone().add(0.0, 0.0, -10.0), 20.0, -90.0, -270.0)
        val path = Path(listOf(straightSegment, curveSegment, straightSegment2, curveSegment2), isCyclic = true)
        val startStop = StreetcarStop(path.start)
        val endStop = StreetcarStop(path.end)
        val route = StreetcarRoute(listOf(startStop, endStop), path)

        val train = StreetcarTrain.spawn(route)
        trainsToCleanup.add(train)

        // Let's try to go a block a second. So one twentieth of a block every tick
        object : BukkitRunnable() {
            val twentieth = 1.0 / 20
            val trainLength = carLength * train.cars.size + distanceBetweenCars * (train.cars.size - 1)
            val totalTicks = 20 * (path.length - trainLength - 5)
//            val totalTicks = 20 * 5
            var tickCounter = 0

            override fun run() {
                train.advance(DebugVars.getDecimalAsDouble("streetcar-speed-per-tick", twentieth))
                PathDrawer.drawPath(path, 0.2, Color.RED)
                if (++tickCounter >= totalTicks) {
                    cancel()
                    train.remove()
                    trainsToCleanup.remove(train)
                }
            }
        }.runTaskTimer(LoquaInteractablePlugin.instance, 0, 1)

    }

    fun onDisable() {
        trainsToCleanup.forEach(StreetcarTrain::remove)
        trainsToCleanup.clear()
    }

    private val controlPoints: Queue<Location> = LinkedList()

    fun doCatmullRom(sender: Player) {
        controlPoints.add(sender.location.clone())
        if (controlPoints.size < 4) {
            sender.sendMessage("Added control point, need ${4 - controlPoints.size} more")
            return
        } else if (controlPoints.size > 9) {
            controlPoints.remove()
        }


        // Every set of 4 control points will be a segment
        for (i in 4 .. controlPoints.size) {
            val segment = CatmullRomSplineSegment(controlPoints.toList().subList(i - 4, i))
            val path = Path(listOf(segment), isCyclic = false)
            PathDrawer.repeatedlyDrawPath(path, 0.1, 100, 1, intToColor(i-4))
        }

        // runnable for path drawing

//        PathDrawer.drawPath(path, 0.1)

    }

    private fun intToColor(i: Int): Color {
        return when(i) {
            0 -> Color.RED
            1 -> Color.GREEN
            2 -> Color.BLUE
            3 -> Color.YELLOW
            4 -> Color.PURPLE
            5 -> Color.ORANGE
            else -> Color.WHITE
        }
    }

}