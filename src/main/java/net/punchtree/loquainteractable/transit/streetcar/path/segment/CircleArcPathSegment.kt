package net.punchtree.loquainteractable.transit.streetcar.path.segment

import org.bukkit.Location
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class CircleArcPathSegment(val center: Location, val radius: Double, val startDegrees: Double, val endDegrees: Double) : PathSegment {

    private val length = run {
        val circumference = 2 * Math.PI * radius
        val fraction = abs(endDegrees - startDegrees) / 360
        circumference * fraction
    }

    override fun length(): Double {
        return length
    }

    override fun pointAt(d: Double): Location {
        val fraction = d / length
        val degrees = startDegrees + fraction * (endDegrees - startDegrees)
        val radians = Math.toRadians(degrees)
        val x = center.x + radius * cos(radians)
        val z = center.z + radius * sin(radians)
        return Location(center.world, x, center.y, z)
    }
}