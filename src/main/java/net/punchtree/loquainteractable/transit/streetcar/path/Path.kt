package net.punchtree.loquainteractable.transit.streetcar.path

import net.punchtree.loquainteractable.transit.streetcar.path.segment.PathSegment
import org.bukkit.Location

data class Path(val segments: List<PathSegment>, val isCyclic: Boolean = false) {

    init {
        for (i in 0 until segments.size - 1) {
            val prevSegmentEnd = segments[i].pointAt(segments[i].length())
            val nextSegmentStart = segments[i + 1].pointAt(0.0)
            require(
                prevSegmentEnd.approximatelyEqualPosition(nextSegmentStart)
            ) {
                "Segments must be connected. Segment $i end point $prevSegmentEnd does not match segment ${i + 1} start point $nextSegmentStart"
            }
        }

        if (isCyclic) {
            require(start.approximatelyEqualPosition(end))
        }
    }

    internal var length  = segments.sumOf { it.length() }
        private set

    internal fun pointAt(d: Double): Location {
        if (isCyclic && d < 0 || d > length) {
            return pointAt(d % length)
        }
        var currentT = d
        for (segment in segments) {
            if (currentT <= segment.length()) {
                return segment.pointAt(currentT)
            }
            currentT -= segment.length()
        }
        // potentially we're here because of floating point errors, so we'll just return the end of the path if currentT is very small
        if (currentT < 0.0001) {
            return segments.last().pointAt(segments.last().length())
        }
        throw IllegalArgumentException("d value $d is out of bounds for path with total length $length (currentT $currentT, last segment length ${segments.last().length()})")
    }

    val start get() = pointAt(0.0)
    val end get() = pointAt(length)

}

// TODO relocate this somewhere better
internal fun Location.approximatelyEqualPosition(
    other: Location
): Boolean {
    val ERROR_THRESHOLD = 0.00001
    return (x - other.x <= ERROR_THRESHOLD
            && y - other.y <= ERROR_THRESHOLD
            && z - other.z <= ERROR_THRESHOLD)
}
