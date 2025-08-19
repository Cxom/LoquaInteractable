package net.punchtree.loquainteractable

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import net.punchtree.loquainteractable._unstable.debug.TempPacketListener
import net.punchtree.loquainteractable._unstable.experimental.PermissionTestingCommand
import net.punchtree.loquainteractable._unstable.experimental.SideProfileRenderCommand
import net.punchtree.loquainteractable._unstable.experimental.UiTestingCommand
import net.punchtree.loquainteractable._unstable.experimental.WorldEditHugeRotate
import net.punchtree.loquainteractable._unstable.experimental.testing.CircleGameTesting
import net.punchtree.loquainteractable._unstable.experimental.testing.PlayerInputsTesting
import net.punchtree.loquainteractable._unstable.experimental.testing.ToastTesting
import net.punchtree.loquainteractable.city.garbagecans.GarbageCansService
import net.punchtree.loquainteractable.clothing.SkinGrabberTestCommand
import net.punchtree.loquainteractable.commands.item.NbtUtilCommands
import net.punchtree.loquainteractable.commands.item.SetLeatherColorCommand
import net.punchtree.loquainteractable.data.PdcCommand
import net.punchtree.loquainteractable.guns.qualityarmory.QualityArmoryTestCommand
import net.punchtree.loquainteractable.heist.HeistTestCommand
import net.punchtree.loquainteractable.input.ChangeHeldItemInputPacketAdapter
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.input.SteerVehicleInputPacketAdapter
import net.punchtree.loquainteractable.item.CustomItemRegistry
import net.punchtree.loquainteractable.item.DrinkItemListener
import net.punchtree.loquainteractable.item.command.*
import net.punchtree.loquainteractable.listeners.*
import net.punchtree.loquainteractable.listeners.input.CapturableInputsWithDefaultActionListener
import net.punchtree.loquainteractable.listeners.input.PlayerInputListener
import net.punchtree.loquainteractable.metadata.commands.MetadataWandCommand
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager
import net.punchtree.loquainteractable.npc.citizens.CitizensNPCManager
import net.punchtree.loquainteractable.npc.citizens.CitizensTestCommand
import net.punchtree.loquainteractable.npc.citizens.heist.GuardTesting
import net.punchtree.loquainteractable.outofbody.drone.DroneTestCommand
import net.punchtree.loquainteractable.outofbody.instruments.InstrumentTestCommand
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.loquainteractable.player.character.select.CharacterSelectManager
import net.punchtree.loquainteractable.splash.SplashScreenManager
import net.punchtree.loquainteractable.staff.commands.StaffModeCommand
import net.punchtree.loquainteractable.transit.streetcar.StreetcarTesting
import net.punchtree.loquainteractable.transit.streetcar.StreetcarTestingCommand
import net.punchtree.loquainteractable.ui.inventory.InventoryMenuTesting
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

internal const val LOQUA_NAMESPACE = "loqua"

class LoquaInteractablePlugin : JavaPlugin() {

    private lateinit var protocolManager: ProtocolManager
    internal lateinit var playerInputsManager: PlayerInputsManager
    internal lateinit var splashScreenManager: SplashScreenManager
        private set
    internal lateinit var characterSelectManager: CharacterSelectManager
        private set
    private lateinit var vehicleInputPacketAdapter: SteerVehicleInputPacketAdapter
    private lateinit var heldItemInputPacketAdapter: ChangeHeldItemInputPacketAdapter
    lateinit var customItemRegistry: CustomItemRegistry
        private set

    private lateinit var garbageCansService: GarbageCansService

    override fun onEnable() {
        saveDefaultConfig()

        this.protocolManager = ProtocolLibrary.getProtocolManager()
        this.playerInputsManager = PlayerInputsManager()
        this.splashScreenManager = SplashScreenManager()
        this.characterSelectManager = CharacterSelectManager()


        this.customItemRegistry = CustomItemRegistry.load()

        garbageCansService = GarbageCansService(this)
        garbageCansService.onEnable()

        registerEvents()
        setCommandExecutors()

        // The way player input tracking is initialized is unrefined
        // and so this happens after event registration so that player inputs object is initialized
        // before packets are received and processed
        initializeInputTracking()
    }

    private fun initializeInputTracking() {
        this.vehicleInputPacketAdapter = SteerVehicleInputPacketAdapter(this, playerInputsManager)
        this.heldItemInputPacketAdapter = ChangeHeldItemInputPacketAdapter(this, playerInputsManager)

        protocolManager.addPacketListener(vehicleInputPacketAdapter)
        protocolManager.addPacketListener(heldItemInputPacketAdapter)
    }

    private fun registerEvents() {
        val pluginManager = Bukkit.getPluginManager()

        // proper listeners in listeners package
        pluginManager.registerEvents(ChunkLoadListener(), this)
        pluginManager.registerEvents(FoodLevelChangeListener(), this)
        pluginManager.registerEvents(InventoryMenuListener.getInstance(), this)
        pluginManager.registerEvents(PlayerDeathAndRespawnListener(), this)
        pluginManager.registerEvents(PlayerInputListener(splashScreenManager), this)
        pluginManager.registerEvents(PlayerInteractListener(), this)
        pluginManager.registerEvents(PlayerInteractEntityListener(), this)
        pluginManager.registerEvents(PlayerJoinListener(playerInputsManager, splashScreenManager, characterSelectManager), this)
        pluginManager.registerEvents(PlayerQuitListener(playerInputsManager), this)
        pluginManager.registerEvents(ServerPingListener(), this)

        // TODO this needs to be incorporated with high intentionality into the input processing solution
        pluginManager.registerEvents(CapturableInputsWithDefaultActionListener(playerInputsManager), this)

        // Interactables/Consumables
        // TODO this is complicated and best refactored as we figure out both the food system and the item systems
        //  moreover, it's probably not necessary now that we can simply mark items as consumable with components
        //  I think anything can be made drinkable (although maybe not with the coffee particles? idek)
        pluginManager.registerEvents(DrinkItemListener(this, protocolManager), this)

        // TODO sort out all packet registration in one place with one api (PacketEvents)
        // packet registration
        PacketEvents.getAPI().eventManager.registerListener(TempPacketListener(), PacketListenerPriority.MONITOR)
    }

    private fun setCommandExecutors() {
        // --------------- NEW GTA COMMANDS -----------------

        getCommand("staff-mode")!!.setExecutor(StaffModeCommand)
        getCommand("unstaff-mode")!!.setExecutor(StaffModeCommand)

        // --------------------------------------------------

        getCommand("metadatawand")!!.setExecutor(MetadataWandCommand)
        getCommand("invtest")!!.setExecutor(InventoryMenuTesting())
        getCommand("circlegame")!!.setExecutor(CircleGameTesting())
        getCommand("toast")!!.setExecutor(ToastTesting())
        getCommand("getnbt")!!.setExecutor(NbtUtilCommands())
        getCommand("verifyplayerinputsmap")!!.setExecutor(PlayerInputsTesting(playerInputsManager))

        val giveCustomItemCommand = GiveCustomItemCommand(customItemRegistry)
        getCommand("givecustom")!!.setExecutor(giveCustomItemCommand)
        getCommand("givecustom")!!.tabCompleter = giveCustomItemCommand

        val addItemCommand = AddItemCommand(customItemRegistry)
        getCommand("additem")!!.setExecutor(addItemCommand)
        getCommand("additem")!!.tabCompleter = addItemCommand
        getCommand("overrideitem")!!.setExecutor(addItemCommand)
        getCommand("overrideitem")!!.tabCompleter = addItemCommand

        val renameItemIdCommand = RenameItemCommand(customItemRegistry)
        getCommand("renameitemid")!!.setExecutor(renameItemIdCommand)
        getCommand("renameitemid")!!.tabCompleter = renameItemIdCommand

        val deleteItemCommand = DeleteItemCommand(customItemRegistry)
        getCommand("deleteitem")!!.setExecutor(deleteItemCommand)
        getCommand("deleteitem")!!.tabCompleter = deleteItemCommand

        val itemsCommand = ItemsCommand(customItemRegistry)
        getCommand("items")!!.setExecutor(itemsCommand)

        getCommand("rename")!!.setExecutor(RenameCommand())

        val tagCommands = TagCommands()
        getCommand("addtag")!!.setExecutor(tagCommands)
        getCommand("deletetag")!!.setExecutor(tagCommands)
        getCommand("deletetag")!!.setExecutor(tagCommands)
        getCommand("listtags")!!.setExecutor(tagCommands)

        val setLeatherColorCommand = SetLeatherColorCommand()
        getCommand("setleathercolor")!!.setExecutor(setLeatherColorCommand)

        getCommand("streetcar")!!.setExecutor(StreetcarTestingCommand)
        getCommand("side-profile-render")!!.setExecutor(SideProfileRenderCommand)

        getCommand("changeskin")!!.setExecutor(SkinGrabberTestCommand)
        getCommand("changecape")!!.setExecutor(SkinGrabberTestCommand)

        // citizens test commands
        getCommand("create-npc")!!.setExecutor(CitizensTestCommand)
        getCommand("make-npc-move")!!.setExecutor(CitizensTestCommand)

        // gun test commands
        getCommand("test-gun")!!.setExecutor(QualityArmoryTestCommand)
        getCommand("list-guns")!!.setExecutor(QualityArmoryTestCommand)

        // heist test commands
        getCommand("heist")!!.setExecutor(HeistTestCommand)

        // instrument test command
        getCommand("instrument")!!.setExecutor(InstrumentTestCommand(playerInputsManager))

        getCommand("persistent-data")!!.setExecutor(PdcCommand)
        // experimental
        getCommand("permtesting")!!.setExecutor(PermissionTestingCommand)
        getCommand("uitesting")!!.setExecutor(UiTestingCommand)
        getCommand("worldedithugerotate")!!.setExecutor(WorldEditHugeRotate.WorldEditHugeRotateCommand)

        getCommand("dronetest")!!.setExecutor(DroneTestCommand)
    }

    override fun onDisable() {
        splashScreenManager.onDisable()
        characterSelectManager.onDisable()

        customItemRegistry.save()
        garbageCansService.onDisable()

        LoquaPlayerManager.cleanupAllPlayers()

        StreetcarTesting.onDisable()
        CitizensNPCManager.onDisable()

        GuardTesting.onDisable()

        MetadataEditingSessionManager.cleanupSessions()

        protocolManager.removePacketListeners(this)

        WorldEditHugeRotate.onDisable()
    }

    companion object {
        @JvmStatic
		val instance: LoquaInteractablePlugin
            get() = getPlugin(LoquaInteractablePlugin::class.java)

        @JvmStatic
        val world by lazy {
            requireNotNull(Bukkit.getWorld("GTA_City")) {
                "GTA_City world not found!!"
            }
        }
    }
}
