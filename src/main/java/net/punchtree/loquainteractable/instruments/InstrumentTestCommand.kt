package net.punchtree.loquainteractable.instruments

import io.papermc.paper.entity.TeleportFlag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.punchtree.loquainteractable.LoquaInteractablePlugin
import net.punchtree.loquainteractable.input.PlayerInputs
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.util.debugvar.DebugVars
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable

class InstrumentTestCommand(
    private val playerInputsManager: PlayerInputsManager
) : CommandExecutor, TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if (args.size == 1) {
            return mutableListOf(
                "give",
                "basic-input-monitoring-test",
                "play-acoustic-guitar",
                "play-trumpet",
                "stop-playing"
            )
        }
        if (args.size == 2 && args[0].lowercase() == "give") {
            return mutableListOf("guitar", "trumpet")
        }
        return null
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if ( sender !is Player ) return false
        if (args.isEmpty()) return false

        when (args[0].lowercase()) {
            "give" -> giveInstrument(sender, args)
            "basic-input-monitoring-test" -> doBasicInputMonitoringTest(sender)
            "play-acoustic-guitar" -> InstrumentManager.startPlayerPlaying(sender, Instruments.AcousticGuitar)
            "play-trumpet" -> InstrumentManager.startPlayerPlaying(sender, Instruments.LegatoTrumpet)
            "stop-playing" -> InstrumentManager.stopPlayerPlaying(sender)
        }

        return true
    }

    private fun doBasicInputMonitoringTest(player: Player) {
        val originalGameMode = player.gameMode
        player.gameMode = GameMode.SPECTATOR
        val somethingToRide = player.world.spawnEntity(player.location, EntityType.ITEM_DISPLAY, CreatureSpawnEvent.SpawnReason.CUSTOM) {
            it as ItemDisplay
            it.setItemStack(ItemStack(Material.SCAFFOLDING))
            it.interpolationDuration = DebugVars.getInteger("loquainteractable.bimt.interpolation-duration", 0)
            it.teleportDuration = DebugVars.getInteger("loquainteractable.bimt.teleport-duration", 0)
            it.setMetadata("instrument", FixedMetadataValue(LoquaInteractablePlugin.instance, "acoustic_guitar"))
        }
        player.spectatorTarget = somethingToRide

        val runLength = 3 * 20L
        object : BukkitRunnable() {
            var counter = 0
            override fun run() {
                if (counter >= runLength) {
                    cancel()
                    return
                }

                val playerInputs = playerInputsManager[player]
                val sidewaysAxisComponent = buildSidewaysAxisComponent(playerInputs)
                val forwardBackwardAxisComponent = buildForwardBackwardAxisComponent(playerInputs)
                val sneakComponent = text("Sh ").color(if (playerInputs.shift) GREEN else GRAY)
                val jumpComponent = text(",_, ").color(if (playerInputs.jump) GREEN else GRAY)
                val sprintComponent = text("Sp ").color(if (playerInputs.sprint) GREEN else GRAY)
                val slotComponent = text(playerInputs.heldItemSlot.toString()).color(
                    if (System.currentTimeMillis() - playerInputs.heldItemSlotLastModified <= 50) GREEN else GRAY
                )
                val swapHandsComponent = text(" F").color(
                    if (System.currentTimeMillis() - playerInputs.lastSwappedHands <= 50) GREEN else GRAY
                )
                val assembledComponent = text()
                    .append(sidewaysAxisComponent)
                    .append(text(" "))
                    .append(forwardBackwardAxisComponent)
                    .append(text(" "))
                    .append(sneakComponent)
                    .append(jumpComponent)
                    .append(sprintComponent)
                    .append(slotComponent)
                    .append(swapHandsComponent)
                player.sendActionBar(assembledComponent)

//                , (counter * 2).toFloat()
//                somethingToRide.setRotation(counter.toFloat(), 0f)
                somethingToRide.teleport(somethingToRide.location.also {
                    it.yaw = (counter * 2).toFloat()
                    it.pitch = counter.toFloat()
                }
                    , TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.Relative.VELOCITY_X, TeleportFlag.Relative.VELOCITY_Y, TeleportFlag.Relative.VELOCITY_Z
                )

                counter++
            }

            private fun buildForwardBackwardAxisComponent(playerInputs: PlayerInputs): Component {
                val forward = text("↑").color(if (playerInputs.forward) GREEN else GRAY)
                val backward = text("↓").color(if (playerInputs.backward) GREEN else GRAY)
                return text().append(forward).append(backward).build()
            }

            // "■"

            private fun buildSidewaysAxisComponent(playerInputs: PlayerInputs): Component {
                val left = text("←").color(if (playerInputs.left) GREEN else GRAY)
                val right = text("→").color(if (playerInputs.right) GREEN else GRAY)
                return text().append(left).append(right).build()
            }
        }.runTaskTimer(LoquaInteractablePlugin.instance, 0, 1)

        object : BukkitRunnable() {
            override fun run() {
                somethingToRide.remove()
                player.gameMode = originalGameMode
            }
        }.runTaskLater(LoquaInteractablePlugin.instance, runLength + 1)
    }

    private fun giveInstrument(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage("Usage: /instrument give <instrument>")
            player.sendMessage("Available instruments: guitar, trumpet")
        }
        when (args[1].lowercase()) {
            "guitar" -> player.inventory.addItem(Instruments.AcousticGuitar.itemStack())
            "trumpet" -> player.inventory.addItem(Instruments.LegatoTrumpet.itemStack())
        }
    }

}