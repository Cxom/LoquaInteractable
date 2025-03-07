package net.punchtree.loquainteractable.joining.splash

import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.phys.Vec3
import net.punchtree.loquainteractable.font.SpecialCharacters
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import java.util.*

object CameraUtils {

    /* TODO properly compare the following approaches
     *  1. making the player ride the camera
     *    pros
     *     - playsound continues to work as expected
     *    cons
     *     - can accidentally try to spectate yourself, have to prevent that
     *     - (think) your camera is raised to your sitting eye height, not perfectly centered
     *  2. using an audio-listener display
     *     pros
     *      - camera is perfectly aligned
     *      - can reuse audio listener as a second display
     *     cons
     *      - second entity
     *      - player isn't actually there
     *             - can cause problems with some methods (playSound)
     *             - need to hide the player wherever they actually are
     */

    internal fun spawnFakeCamera(
        cameraEntityId: Int,
        // TODO an extension on the player to send packets that fetches the craft player and then the handle on it
        //  will make it so that we can stop using CraftPlayer explicitly here and in Cinematic
        player: CraftPlayer,
        location: Location,
        durationTicks: Int,
        passengerEntityId: Int
    ) {
        // TODO this might cause problems if called async?
        // This is necessary as we don't want to teleport, we just want the server
        // to think the player is nearby in order to send them the chunks they need
        player.handle.setPos(location.x, location.y, location.z)

        // This packet is necessary when teleporting across chunks so that the client knows that the audio listener is currently
        // in the right chunk (and can be mounted on the other entity
        val passengerTeleportPacket = ClientboundEntityPositionSyncPacket(
            passengerEntityId,
            PositionMoveRotation(
                Vec3(location.x, location.y, location.z),
                Vec3(0.0, 0.0, 0.0),
                0f,
                0f
            ),
            false
        )

        val addPacket = ClientboundAddEntityPacket(
            cameraEntityId,
            UUID.randomUUID(),
            location.x,
            location.y,
            location.z,
            location.pitch,
            location.yaw,
            EntityType.TEXT_DISPLAY,
            0,
            Vec3.ZERO,
            0.0
        )
        val metadataPacket = ClientboundSetEntityDataPacket(
            cameraEntityId,
            listOf(
                SynchedEntityData.DataValue(
                    Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID.id,
                    EntityDataSerializers.INT,
                    durationTicks
                ),
                // TODO the PRESS_F5 screen does get the point across, but fails to actually block the camera's line of sight
                //  an item display with a double sided flat plane would block both other f5 modes
                //  and, if used with a composite model, could also offer a non-toggleable overlay
                //  (although, for the opening cinematic, I don't mind the hud being toggleable)
                SynchedEntityData.DataValue(
                    TEXT_DISPLAY_TEXT_ID,
                    EntityDataSerializers.COMPONENT,
                    PaperAdventure.asVanilla(SpecialCharacters.PRESS_F5)
                )
            )
        )
        val gameModeChangePacket = ClientboundGameEventPacket(
            ClientboundGameEventPacket.CHANGE_GAME_MODE,
            GameMode.SPECTATOR.value.toFloat()
        )
        // constructor takes an entity but we only have an entityId, so we need reflection - ignore the passing of the player to the constructor
        val spectateTheCameraPacket = ClientboundSetCameraPacket(player.handle)
        val cameraIdField = ClientboundSetCameraPacket::class.java.getDeclaredField("cameraId")
        cameraIdField.isAccessible = true
        cameraIdField.setInt(spectateTheCameraPacket, cameraEntityId)

        val passengersPacket = ClientboundSetPassengersPacket(player.handle)
        val vehicleField = ClientboundSetPassengersPacket::class.java.getDeclaredField("vehicle")
        vehicleField.isAccessible = true
        vehicleField.setInt(passengersPacket, cameraEntityId)
        val passengersField = ClientboundSetPassengersPacket::class.java.getDeclaredField("passengers")
        passengersField.isAccessible = true
        passengersField.set(passengersPacket, intArrayOf(passengerEntityId))

        player.handle.connection.send(passengersPacket)

        val packetBundle =
            ClientboundBundlePacket(listOf(passengerTeleportPacket, addPacket, metadataPacket, gameModeChangePacket, spectateTheCameraPacket, passengersPacket))

        // TODO extensions for packets
        player.handle.connection.send(packetBundle)
    }

    internal fun teleportFakeCamera(
        cameraEntityId: Int,
        // TODO an extension on the player to send packets that fetches the craft player and then the handle on it
        //  will make it so that we can stop using CraftPlayer explicitly here and in Cinematic
        passengerEntityId: Int,
        player: CraftPlayer,
        location: Location,
        durationTicks: Int
    ) {
        if (durationTicks == 0 && DebugVars.getBoolean("send-music-packet-to-player", true)) {
            // There is no way to avoid packets potentially being processed on the client out of order, so create a new camera on instant teleport
            // I'm pretty sure the reusing of the entity id prevents this from actually leaving behind old entities on the client, but not sure
            spawnFakeCamera(cameraEntityId, player, location, 0, passengerEntityId)
            return
        }

        val metadataPacket = ClientboundSetEntityDataPacket(
            cameraEntityId,
            listOf(SynchedEntityData.DataValue(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID.id, EntityDataSerializers.INT, durationTicks))
        )
        val teleportPacket = ClientboundTeleportEntityPacket(
            cameraEntityId,
            PositionMoveRotation(
                Vec3(location.x, location.y, location.z),
                Vec3.ZERO,
                location.yaw,
                location.pitch,
            ),
            emptySet(),
            false
        )

        val packetBundle = ClientboundBundlePacket(listOf(metadataPacket, teleportPacket))
        player.handle.connection.send(packetBundle)
    }

    internal fun removeCamera(cameraEntityId: Int, player: CraftPlayer) {
        player.handle.connection.send(ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, player.gameMode.value.toFloat()))
        player.handle.connection.send(ClientboundSetCameraPacket(player.handle))
        player.handle.connection.send(ClientboundRemoveEntitiesPacket(cameraEntityId))
    }

}