package net.punchtree.loquainteractable

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import net.punchtree.loquainteractable.city.garbagecans.GarbageCansService
import net.punchtree.loquainteractable.clothing.SkinGrabberTestCommand
import net.punchtree.loquainteractable.commands.item.NbtUtilCommands
import net.punchtree.loquainteractable.commands.item.SetLeatherColorCommand
import net.punchtree.loquainteractable.commands.testing.CircleGameTesting
import net.punchtree.loquainteractable.commands.testing.PlayerInputsTesting
import net.punchtree.loquainteractable.commands.testing.ToastTesting
import net.punchtree.loquainteractable.displayutil.ArmorStandChunkLoadingReglow
import net.punchtree.loquainteractable.displayutil.ArmorStandUtilsTesting
import net.punchtree.loquainteractable.gui.inventory.InventoryMenuListener
import net.punchtree.loquainteractable.gui.inventory.InventoryMenuTesting
import net.punchtree.loquainteractable.guns.qualityarmory.QualityArmoryTestCommand
import net.punchtree.loquainteractable.heist.HeistTestCommand
import net.punchtree.loquainteractable.input.ChangeHeldItemInputPacketAdapter
import net.punchtree.loquainteractable.input.PlayerInputsManager
import net.punchtree.loquainteractable.input.SteerVehicleInputPacketAdapter
import net.punchtree.loquainteractable.instruments.InstrumentListener
import net.punchtree.loquainteractable.instruments.InstrumentManager
import net.punchtree.loquainteractable.instruments.InstrumentTestCommand
import net.punchtree.loquainteractable.item.CustomItemRegistry
import net.punchtree.loquainteractable.item.DrinkItemListener
import net.punchtree.loquainteractable.item.command.*
import net.punchtree.loquainteractable.listeners.PlayerJoinListener
import net.punchtree.loquainteractable.listeners.PlayerQuitListener
import net.punchtree.loquainteractable.metadata.commands.MetadataWandCommand
import net.punchtree.loquainteractable.metadata.editing.MetadataWand
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager
import net.punchtree.loquainteractable.npc.citizens.CitizensNPCManager
import net.punchtree.loquainteractable.npc.citizens.CitizensNPCManager.initialize
import net.punchtree.loquainteractable.npc.citizens.CitizensTestCommand
import net.punchtree.loquainteractable.npc.citizens.heist.GuardTesting
import net.punchtree.loquainteractable.player.InteractablePlayer
import net.punchtree.loquainteractable.player.LoquaPlayerManager
import net.punchtree.loquainteractable.player.PlayerMapping
import net.punchtree.loquainteractable.transit.streetcar.StreetcarTesting
import net.punchtree.loquainteractable.transit.streetcar.StreetcarTestingCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class LoquaInteractablePlugin : JavaPlugin() {
    private var initialized = false

    private lateinit var protocolManager: ProtocolManager
    private lateinit var playerInputsManager: PlayerInputsManager
    private lateinit var vehicleInputPacketAdapter: SteerVehicleInputPacketAdapter
    private lateinit var heldItemInputPacketAdapter: ChangeHeldItemInputPacketAdapter
    lateinit var customItemRegistry: CustomItemRegistry
        private set

    private lateinit var garbageCansService: GarbageCansService

    private lateinit var playerMapping: PlayerMapping<InteractablePlayer>

    fun setPlayerMapping(playerMapping: PlayerMapping<InteractablePlayer>) {
        if (!initialized) {
            this.playerMapping = playerMapping
            onInitialize()
            this.initialized = true
        } else {
            throw IllegalStateException("Can't initialize LoquaInteractable twice!")
        }
    }

    override fun onEnable() {
        saveDefaultConfig()

        this.protocolManager = ProtocolLibrary.getProtocolManager()
        this.playerInputsManager = PlayerInputsManager()


        this.customItemRegistry = CustomItemRegistry.load()

        initialize()

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

    private fun onInitialize() {
    }

    private fun registerEvents() {
        val pluginManager = Bukkit.getPluginManager()

        pluginManager.registerEvents(MessageOfTheDay(), this)

        pluginManager.registerEvents(LoquaPlayerManager, this)

        pluginManager.registerEvents(ArmorStandChunkLoadingReglow(), this)

        pluginManager.registerEvents(InventoryMenuListener.getInstance(), this)


        // TODO per player instances for data accumulation?
        pluginManager.registerEvents(MetadataWand(), this)
        pluginManager.registerEvents(MetadataEditingSessionManager.getInstance(), this)


        // Just for testing
        pluginManager.registerEvents(ArmorStandUtilsTesting(), this)


        // For now, only input processing, may have more concerns later
        pluginManager.registerEvents(PlayerJoinListener(playerInputsManager), this)
        pluginManager.registerEvents(PlayerQuitListener(playerInputsManager), this)


        // Interactables/Consumables
        pluginManager.registerEvents(DrinkItemListener(this, protocolManager, playerMapping), this)

        pluginManager.registerEvents(InstrumentListener(playerInputsManager), this)
    }

    private fun setCommandExecutors() {
        getCommand("metadatawand")!!.setExecutor(MetadataWandCommand())
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
    }

    override fun onDisable() {
        customItemRegistry.save()
        garbageCansService.onDisable()

        InstrumentManager.onDisable()

        StreetcarTesting.onDisable()
        CitizensNPCManager.onDisable()

        GuardTesting.onDisable()

        MetadataEditingSessionManager.cleanupSessions()

        protocolManager.removePacketListeners(this)
    }


    companion object {
        @JvmStatic
		val instance: LoquaInteractablePlugin
            get() = getPlugin(LoquaInteractablePlugin::class.java)
    }
}
