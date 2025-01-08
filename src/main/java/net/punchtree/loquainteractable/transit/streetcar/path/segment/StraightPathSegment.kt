package net.punchtree.loquainteractable.transit.streetcar.path.segment

import org.bukkit.Location
import org.bukkit.util.Vector
import org.checkerframework.checker.units.qual.t

class StraightPathSegment(private val start: Location, private val end: Location) : PathSegment {

    init {
        require(start.world == end.world) { "Locations must be in the same world" }
    }

    private val length = start.distance(end)
    private val direction = end.toVector().subtract(start.toVector()).normalize()

    override fun length(): Double {
        return length
    }

    override fun pointAt(d: Double): Location {
        val offset = direction.clone().multiply(d)
        return start.clone().add(offset)
    }

}
