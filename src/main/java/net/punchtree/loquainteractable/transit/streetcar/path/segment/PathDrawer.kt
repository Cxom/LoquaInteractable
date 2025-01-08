package net.punchtree.loquainteractable.transit.streetcar.path.segment

import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.transit.streetcar.path.Path
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitRunnable

object PathDrawer {

    internal fun repeatedlyDrawPath(draw: Path, step: Double, times: Int, period: Long, color: Color = Color.RED) {
        object : BukkitRunnable() {
            var count = 0
            override fun run() {
                drawPath(draw, step, color)
                count++
                if (count >= times) {
                    cancel()
                }
            }
        }.runTaskTimer(LoquaInteractablePlugin.getInstance(), 0, period)
    }

    internal fun drawPath(path: Path, step: Double, color: Color = Color.RED) {
        val numSteps = (path.length / step).toInt()
        for (i in 0..numSteps) {
            val point = path.pointAt(i * step)
            point.world!!.spawnParticle(Particle.DUST, point, 1, 0.0, 0.0, 0.0, 0.0, Particle.DustOptions(color, 1.0f))
        }
    }

}
