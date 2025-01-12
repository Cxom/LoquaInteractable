package net.punchtree.loquainteractable.npc.citizens.heist

import me.zombie_striker.qg.QAMain
import me.zombie_striker.qg.api.QualityArmory
import me.zombie_striker.qg.guns.utils.GunUtil
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import net.citizensnpcs.util.PlayerAnimation
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.npc.citizens.CitizensNPCManager
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.random.Random

class Guard(spawnLocation: Location) {

    private val npc: NPC
    private val equipment: Equipment
    private val skin: SkinTrait

    private var accuracy: Double = 0.05

    init {
        val gun = QualityArmory.getGunByName("ak47")
        this.npc = CitizensNPCManager.heistTestingRegisty.createNPC(EntityType.PLAYER, "Guard")

        equipment = Equipment()
        npc.addTrait(equipment)
        equipment.set(Equipment.EquipmentSlot.HAND, gun.itemStack)

        skin = SkinTrait()
        npc.addTrait(skin)

        skin.setFetchDefaultSkin(false)
        skin.setShouldUpdateSkins(false)

        // the first time an npc is spawned, there is a delay in setting the skin
        // this doesn't happen for subsequent npcs spawned with the same skin
        // it can also be lowered down to a few ticks in the config
        // finally, TODO there seems to maybe be a way to permanently cache the skin, worth looking into
        skin.setSkinPersistent("Guard", "QGQY9+UABac85LB1zKyJ5yamLry4Z91xWjP5jUr++B9L7i/5VA6JE7cZBryU7F90GCfyOr1vw9vJrOiQAMR96vOO01+0UO8wKFMtEaTDAQSAr5unBJ5awVJzM3byDzn+0go6oF5KAJxinI/9YVuAx5cw3c7wy/gJCat4M9s1cT8wNGXVUcX1qrRbjCTxImeN+bWtDB+n5JMHzuldlKta1tKPqYcgqgEr7MQ3Asp2BrZCsontHEOF3HpPAyRmGVcAlO1p76PeG/tONBQnAclHdulj4G5IEEgTVccKtQMXc6L/GMdtfHHtkwrn00J2WVcFIIsO9Lk10IInPQoGpWiuYl+wHGWsbK4eevCnrj5cdi6wEzWu41oqcsqdrd0W9Iv5PtFUFeicYSyRKaz9Hi4fWHqvmU4OR37h/7uIHnCymmEh1FN+f/eL5XqzhJul0AihXxGseGItuTXvGo3OnUpyqrmOSfRj2jELtAq5hk39qHMIa8JKJ+d+j/x66wqxcrfwTTwgNxpus1NgZfOOycczEB50l33zAfnHk1wF3aIyiyGh3xe5ZTVYN+L3yWqgMAecGdMQMmr07o43dsxbLlqvjnjcYmSdyWLnX5LJpEh1Nhxe3s66zLYtg5oarEXZOlHuOVNCayp0OjWKzdbAdrb0TtMaccux1U0cLdSRwMFx44Y=", "ewogICJ0aW1lc3RhbXAiIDogMTczNjQxNTcyMzM2MCwKICAicHJvZmlsZUlkIiA6ICI5MGQ1NDY0OGEzNWE0YmExYTI2Yjg1YTg4NTU4OGJlOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFdW4wbWlhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzljNWJlY2UxZjFhMTI0N2FlYmQ0MDM2YzYyNWYxNDE4ODAwMWIwNGZkMjg3ZjU3ZWQ4OGYxZjNjMDZjZTdmNmIiCiAgICB9CiAgfQp9")

        npc.spawn(spawnLocation)

//        skin.setTexture("http://textures.minecraft.net/texture/edc326c9cfecdebb50182af79fd030db85a3d941a4e858acdbe6326b224d22af", "")
//        skin.setSkinName("Guard", true)
    }

    fun destroy() {
        npc.destroy()
    }

    fun fireGun() {
        val item = equipment.get(Equipment.EquipmentSlot.HAND)
        val gun = QualityArmory.getGun(item)
        // TODO the gun can just be cached instead of refetched from the hand - good idea?
        QAMain.DEBUG("Getting gun! gun = " + gun)
        if (gun == null) {
            Bukkit.broadcastMessage("Failed to get gun for npc to shoot!")
            return
        }

        // TODO check if the gun needs ammo - keep track of ammo that the guard has
        // for now, assume the guard has unlimited ammo
        // casting npc.entity WHICH IS DEFINITELY NOT A PLAYER to a player seems like a bad idea, but it's what
        // the QA-sentinel integration effectively does?????
        val npcPlayer = npc.entity as Player
        GunUtil.basicReload(gun, npcPlayer, true)

        val livingEntity = npc.entity as LivingEntity

        var facingVector = npc.entity.location.direction.normalize()
        facingVector = alterForAccuracy(facingVector)
        faceLocation(livingEntity, livingEntity.eyeLocation.clone().add(facingVector.multiply(10)))

        // TODO - cooldown between shots
        // TODO sway
        // TODO deal with custom projectiles

        PlayerAnimation.START_USE_MAINHAND_ITEM.play(npc.entity as Player)
        GunUtil.shootInstantVector(gun, npcPlayer, gun.sway * accuracy, gun.damage, gun.bulletsPerShot, gun.maxDistance)
        GunUtil.playShoot(gun, npcPlayer)
        QAMain.DEBUG("Heist Guard shooting!")
        object : BukkitRunnable() {
            override fun run() {
                if (npc.isSpawned) {
                    PlayerAnimation.STOP_USE_ITEM.play(npc.entity as Player)
                }
            }
        }.runTaskLater(LoquaInteractablePlugin.getInstance(), DebugVars.getInteger("heist:guard:lower_gun_delay", 4).toLong())
//        PlayerAnimation.ARM_SWING.play(npcPlayer)

        // TODO reloading if tracking ammo

        // TODO if implementing chasing, check distance for re-engaging in chasing/navigating after the player
    }

    private fun faceLocation(livingEntity: LivingEntity, location: Location) {
        val faceTowards = location.clone().subtract(0.0, livingEntity.eyeHeight, 0.0)
        // TODO why are we subtracting the eye height? I don't understand, draw a debug ray of both vectors

        npc.faceLocation(faceTowards)
        // potentially, could make them track the look location if they're actively navigating
    }

    private fun alterForAccuracy(input: Vector): Vector {
        if (input.x.isInfinite() || input.z.isInfinite()) {
            return Vector(0, 0, 0)
        }
        return Vector(input.x + randomAccuracy(), input.y + randomAccuracy(), input.z + randomAccuracy())
    }

    private fun randomAccuracy(): Double {
        // TODO any point in seeding these randoms for enemy ai?
        return Random.nextDouble() * accuracy * 2 - accuracy
    }
}