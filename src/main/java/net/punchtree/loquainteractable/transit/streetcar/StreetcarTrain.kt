package net.punchtree.loquainteractable.transit.streetcar

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import io.papermc.paper.math.Position
import net.punchtree.loquainteractable.transit.streetcar.path.StreetcarRoute
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Matrix4f
import org.joml.Vector3f


class StreetcarTrain private constructor(
    internal val cars: List<StreetcarCar>,
    private val route: StreetcarRoute
) {

    private var positionOnRoute = cars[0].d1 - wheelsOffsetFromEndOfTrain
        get() = cars[0].d1 - wheelsOffsetFromEndOfTrain
        set(value) {
            field = value
            moveCarsToPointOnRoute(value)
        }

    internal fun advance(d: Double) {
        positionOnRoute += d
    }

    private fun moveCarsToPointOnRoute(spotOnRoute: Double) {
        var d1: Double
        var d2: Double
        var p1: Location
        var p2: Location
        var pCarMiddle: Location
        for ((index, car) in cars.withIndex()) {
            // Just adding the distanceBetweenCars should be a good enough approximation since
            // The cars are roughly tangent to their position (so will better follow the curve around linkages
            // than the rest of the cars) and the linkages will have some flexibility
            // I think properly solving placement is an ode or something not sure
            //  you'd have to
            //  take point a (end of last car beyond wheelbase)
            //       point b (start of new car beyond wheelbase)
            //       point p1 (new car's first wheels)
            //       point p2 (new car's second wheels)
            //       the goal is to a as clos to b as possible. b, p1, and p2 must remain linear at all times. p1 and p2 have to be on the track
            if (index == 0) {
                d1 = spotOnRoute + wheelsOffsetFromEndOfTrain
            } else {
                d1 = cars[index - 1].d2 + wheelsOffsetFromEndOfTrain + distanceBetweenCars + wheelsOffsetFromEndOfTrain
            }
            d2 = getPointOnRouteWithStraightLineDistance(route, d1, wheelbase)
            p1 = route.path.pointAt(d1)
            p2 = route.path.pointAt(d2)
            pCarMiddle = Location(
                p1.world,
                (p1.x + p2.x) / 2,
                (p1.y + p2.y) / 2,
                (p1.z + p2.z) / 2
            )
            val pCarMiddleWithRotation = pCarMiddle.clone()
            pCarMiddleWithRotation.setDirection(p2 - p1)

            teleportAndForceYawUpdate(car, pCarMiddle, pCarMiddleWithRotation.yaw, pCarMiddleWithRotation.pitch)

            car.d1 = d1
            car.d2 = d2
        }
    }

    private fun teleportAndForceYawUpdate(
        car: StreetcarCar,
        pCarMiddle: Location,
        yaw: Float,
        pitch: Float
    ) {
        car.itemDisplay.teleport(pCarMiddle)

        // TODO optimize - JOML contains lots of generator methods for the rotation we probably need
        val R_yaw = Matrix4f().rotationY(Math.toRadians(-yaw.toDouble()).toFloat())
        val R_pitch = Matrix4f().rotationX(Math.toRadians(pitch.toDouble()).toFloat())
        val R_final = R_pitch.mul(R_yaw).scale(4f)

        car.itemDisplay.setTransformationMatrix(R_final)

//        val yawPacket = WrapperPlayServerEntityLook(PacketContainer(PacketType.Play.Server.ENTITY_LOOK))
//        yawPacket.entityID = car.itemDisplay.entityId
//        yawPacket.yaw = pCarMiddle.yaw
//        yawPacket.pitch = pCarMiddle.pitch
//        Bukkit.getPlayer("Cxom")?.let {
//            yawPacket.sendPacket(it)
//            Bukkit.getLogger().info("PACKET Car 1 angle: ${pCarMiddle.yaw}")
//        }
    }

    companion object {
        fun spawn(route: StreetcarRoute) : StreetcarTrain {
            val cars = spawnCarsOnRoute(route)
            val train = StreetcarTrain(cars, route)
            return train
        }

        private fun spawnCarsOnRoute(route: StreetcarRoute, spotOnRoute: Double = 0.0): List<StreetcarCar> {
            // We're placing from zero in increasing order - but we also advance in positive direction, so place the cars backwards
            val car1 = placeCar(StreetcarCar.Type.END, route, spotOnRoute)
            val car2 = placeCar(StreetcarCar.Type.MIDDLE, route, car1.d2 + distanceBetweenCars)
            val car3 = placeCar(StreetcarCar.Type.START, route, car2.d2 + distanceBetweenCars)
            return listOf(car1, car2, car3)
        }

        /**
         * returns the spot on the route where the end of the train car is placed
         */
        private fun placeCar(carType: StreetcarCar.Type, route: StreetcarRoute, spotOnRoute: Double): StreetcarCar {
            val d1 = spotOnRoute + wheelsOffsetFromEndOfTrain
            val d2 = getPointOnRouteWithStraightLineDistance(route, d1, wheelbase)
            val p1 = route.path.pointAt(d1)
            val p2 = route.path.pointAt(d2)

            /*
             * We have to deal with rotation.
             *  - The train is centered
             *  - The train is 10 .25 blocks, or 2.5 blocks.
             *  - The wheelbase is 8 .25 blocks, or 2 blocks, or 1 block away from center
             *  - positive z is zero yaw
             *           0
             *          +Z
             *   -90 +X    -X 90
             *          -Z
             *          180
             */

            val pCarMiddle = Location(
                p1.world,
                (p1.x + p2.x) / 2,
                (p1.y + p2.y) / 2,
                (p1.z + p2.z) / 2
            )
            pCarMiddle.setDirection(p2 - p1)

            val carDisplay = pCarMiddle.world.spawnEntity(pCarMiddle, EntityType.ITEM_DISPLAY, CreatureSpawnEvent.SpawnReason.CUSTOM) {
                it as ItemDisplay
                it.setItemStack(ItemStack(Material.PAPER).also {
                    it.editMeta {
                        it.setCustomModelData(carType.customModelData)
                    }
                })
                it.displayWidth = 20.0f
                it.transformation = Transformation(
                    it.transformation.translation,
                    it.transformation.leftRotation,
                    Vector3f(4.0f, 4.0f, 4.0f),
                    it.transformation.rightRotation
                )
                it.teleportDuration = 1
                it.interpolationDuration = 1
                it.scoreboardTags.add("streetcar")
            } as ItemDisplay

            val car = StreetcarCar(carDisplay)
            car.d1 = d1
            car.d2 = d2

            return car
        }

        private fun getPointOnRouteWithStraightLineDistance(route: StreetcarRoute, start: Double, lineLength: Double) : Double {
            // We have to iterate to find the correct spot on the route where the car ends because the train car is
            // straight but the track might not be

            val ERROR_THRESHOLD = DebugVars.getDecimalAsDouble("streetcar:straight-line-error-threshold", 0.01)
            val MAX_ITERATIONS = DebugVars.getInteger("streetcar:straight-line-max-iterations", 20)

            val a = route.path.pointAt(start)
            var tb = start + lineLength
            var b: Location

            var error = wheelbase // some arbitrary high value for first iteration
            for (i in 0 until MAX_ITERATIONS) {
                b = route.path.pointAt(tb)

                val newError = lineLength - a.distance(b)
                check(newError <= error) { "Error is increasing! Is the track impossible for the train car?!?! (lineLength: $lineLength, distance: ${a.distance(b)}, error: $error, newError: $newError)" }
                error = newError
                check(error >= -0.00001) { "Distance can never be larger than the wheelbase! What happened!?!?! (lineLength: $lineLength, distance: ${a.distance(b)}, error: $error, condition: ${error >= -0.00001})" }

                if (error < ERROR_THRESHOLD) {
                    Bukkit.getLogger().fine("d2 iterations: $i")
                    break
                }

                tb += error
                if (i == MAX_ITERATIONS - 1) {
                    Bukkit.getLogger().warning("Max iterations reached for d2!!! Error is still $error!!!")
                }
            }

            return tb
        }
    }

    internal fun remove() {
        cars.forEach(StreetcarCar::remove)
    }

}

internal operator fun Position.minus(p1: Position): Vector {
    return Vector(x() - p1.x(), y() - p1.y(), z() - p1.z())
}

internal operator fun Location.plus(vector: Vector): Location {
    return clone().add(vector)
}

private const val wheelsOffsetFromEndOfTrain = 1.0
internal const val distanceBetweenCars = 1.0
internal const val carLength = 10.0
private const val carWidth = 4.0
private const val carHeight = 7.0
private const val wheelbase = carLength - 2 * wheelsOffsetFromEndOfTrain