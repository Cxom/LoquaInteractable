package net.punchtree.loquainteractable.transit.streetcar.path.segment

import org.bukkit.Location
import org.bukkit.util.Vector

interface PathSegment {

    fun length(): Double

    /** Not a lerp between [0, 1]! d is expected to be in the range [0, length] */
    fun pointAt(d: Double): Location

}
