package net.punchtree.loquainteractable.transit.streetcar.path.segment.spline

import net.punchtree.loquainteractable.transit.streetcar.minus
import net.punchtree.loquainteractable.transit.streetcar.path.segment.PathSegment
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import org.checkerframework.checker.units.qual.t
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import java.util.*

class CatmullRomSplineSegment(controlPoints: List<Location>) : PathSegment {

    private val world: Reference<World>
    private val controlPoints: MutableList<Vector>
    private val lookupTable = TreeMap<Double, Double>()

    init {
        require(controlPoints.size >= 4) { "Catmull-Rom splines require at least 4 control points" }

        // todo initialize additional controlpoints on either end of the list based on mirroring the second and second-to-last points along the line between the first and last points
        this.controlPoints = controlPoints.map { it.toVector() }.toMutableList()
//        val toFirstDir = controlPoints[0] - controlPoints[1]
//        this.controlPoints.add(0, controlPoints[0].toVector().add(toFirstDir))
//        val toLastDir = controlPoints[controlPoints.size - 1] - controlPoints[controlPoints.size - 2]
//        this.controlPoints.add(controlPoints[controlPoints.size - 1].toVector().add(toLastDir))

        this.world = WeakReference(controlPoints[0].world)

        buildLookupTable()
    }

    private fun buildLookupTable() {
        val numSamples = 100
        val step = 1.0 / numSamples
        lookupTable[0.0] = 0.0
        for (i in 1 until numSamples) {
            val distanceFromLastPoint = invoke(i * step).distance(invoke((i - 1) * step))
            val cumulativeLength = distanceFromLastPoint + lookupTable.lastKey()
            lookupTable[cumulativeLength] = i * step
        }
    }

    /**
     * Unlike pointAt, this method expects a parameter t in the range [0, 1] and IS NOT UNIFORMLY DISTRIBUTED
     */
    operator fun invoke(t: Double) : Location {
        require(t in 0.0..1.0) { "t value $t must be in the range [0, 1]" }
        val t2 = t * t
        val t3 = t2 * t
        val constant = controlPoints[1]
        val t1Coeff = (controlPoints[2] - controlPoints[0]).multiply(0.5)
        val t2Coeff = controlPoints[0].clone().add(controlPoints[1] * -2.5).add(controlPoints[2] * 2.0).add(controlPoints[3] * -0.5)
        val t3Coeff = controlPoints[0].clone().multiply(-0.5).add(controlPoints[1] * 1.5).add(controlPoints[2] * -1.5).add(controlPoints[3] * 0.5)
        return Location(
            world.get(),
            constant.x + t1Coeff.x * t + t2Coeff.x * t2 + t3Coeff.x * t3,
            constant.y + t1Coeff.y * t + t2Coeff.y * t2 + t3Coeff.y * t3,
            constant.z + t1Coeff.z * t + t2Coeff.z * t2 + t3Coeff.z * t3
        )
    }

    override fun length(): Double {
        return lookupTable.lastKey()
    }

    override fun pointAt(d: Double): Location {
        if (d < 0 || d > lookupTable.lastKey()) {
            // Possible to extrapolate, but because for now our only use is rail tracks, we'll just throw an exception
            throw IllegalArgumentException("d value $d is out of bounds for path with total length ${lookupTable.lastKey()}")
            // extrapolate outside the curve
            // return invoke(d / length())
        }

        if (d == 0.0) {
            return invoke(0.0)
        }

        // Find the lookup table entries that are above and below d
        val lowerEntry = lookupTable.floorEntry(d)
        val upperEntry = lookupTable.ceilingEntry(d)
        // Interpolate between the two entries
        val lowerT = lowerEntry.value
        val upperT = upperEntry.value
        val lowerLength = lowerEntry.key
        val upperLength = upperEntry.key
        val t = lowerT + (((d - lowerLength) / (upperLength - lowerLength)) * (upperT - lowerT))
//        Bukkit.getLogger().info("Interpolated t: $t (d: $d, lowerT: $lowerT, upperT: $upperT, lowerLength: $lowerLength, upperLength: $upperLength)")
        val point = invoke(t)
//        Bukkit.getLogger().info("Found point at $d (t: $t): $point")
        return point
    }

}

private operator fun Vector.minus(vector: Vector): Vector {
    return Vector(this.x - vector.x, this.y - vector.y, this.z - vector.z)
}

private operator fun Vector.times(d: Double): Vector {
    return Vector(this.x * d, this.y * d, this.z * d)
}
