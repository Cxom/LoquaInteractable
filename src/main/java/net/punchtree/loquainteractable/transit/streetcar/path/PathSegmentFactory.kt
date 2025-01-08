package net.punchtree.loquainteractable.transit.streetcar.path

import net.punchtree.loquainteractable.transit.streetcar.path.segment.CircleArcPathSegment
import net.punchtree.loquainteractable.transit.streetcar.path.segment.StraightPathSegment
import org.bukkit.Location
import org.bukkit.util.Vector

object PathSegmentFactory {

    fun straight(start: Location, end: Location) = StraightPathSegment(start, end)

    fun circleArc(center: Location, radius: Double, startDegrees: Double, endDegrees: Double) = CircleArcPathSegment(center, radius, startDegrees, endDegrees)

    fun circleArc(p1: Location, tangent1: Vector, p2: Location) : CircleArcPathSegment {
        // We want to find the center of the circle that goes through p1 and p2 and has tangent tangent1 at p1
        // The center is the intersection of the perpendicular bisectors of the line segments p1p2 and p1 + tangent1
        val p1p2 = p2.toVector().subtract(p1.toVector())
        val p1tangent1 = tangent1
        val p1p2Midpoint = p1.toVector().add(p1p2.clone().multiply(0.5))
//        val p1tangent1Midpoint = p1.toVector().add(p1tangent1.clone().multiply(0.5))
//        val p1p2Perpendicular = Vector(p1p2.z, 0.0, -p1p2.x)
//        val p1tangent1Perpendicular = Vector(p1tangent1.z, 0.0, -p1tangent1.x)
//        val center = p1p2Midpoint.toLocation(p1.world).add(p1p2Perpendicular).toVector().crossProduct(p1tangent1Midpoint.toLocation(p1.world).add(p1tangent1Perpendicular).toVector())
//        val radius = center.distance(p1.toVector())
//        val startDegrees = Vector(1.0, 0.0, 0.0).angle(p1p2)
//        val endDegrees = Vector(1.0, 0.0, 0.0).angle(p2.toVector().subtract(center))
//        return CircleArcPathSegment(center.toLocation(p1.world), radius, startDegrees, endDegrees)
        TODO("Not implemented yet")
    }

}