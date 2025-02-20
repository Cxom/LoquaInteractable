package net.punchtree.loquainteractable;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.punchtree.loquainteractable.city.garbagecans.GarbageCansService;
import net.punchtree.loquainteractable.clothing.SkinGrabberTestCommand;
import net.punchtree.loquainteractable.commands.item.NbtUtilCommands;
import net.punchtree.loquainteractable.commands.item.SetLeatherColorCommand;
import net.punchtree.loquainteractable.commands.testing.CircleGameTesting;
import net.punchtree.loquainteractable.commands.testing.PlayerInputsTesting;
import net.punchtree.loquainteractable.commands.testing.ToastTesting;
import net.punchtree.loquainteractable.displayutil.ArmorStandChunkLoadingReglow;
import net.punchtree.loquainteractable.displayutil.ArmorStandUtilsTesting;
import net.punchtree.loquainteractable.gui.inventory.InventoryMenuListener;
import net.punchtree.loquainteractable.gui.inventory.InventoryMenuTesting;
import net.punchtree.loquainteractable.guns.qualityarmory.QualityArmoryTestCommand;
import net.punchtree.loquainteractable.heist.HeistTestCommand;
import net.punchtree.loquainteractable.input.ChangeHeldItemInputPacketAdapter;
import net.punchtree.loquainteractable.input.PlayerInputsManager;
import net.punchtree.loquainteractable.input.SteerVehicleInputPacketAdapter;
import net.punchtree.loquainteractable.instruments.InstrumentListener;
import net.punchtree.loquainteractable.instruments.InstrumentManager;
import net.punchtree.loquainteractable.instruments.InstrumentTestCommand;
import net.punchtree.loquainteractable.item.CustomItemRegistry;
import net.punchtree.loquainteractable.item.DrinkItemListener;
import net.punchtree.loquainteractable.item.command.*;
import net.punchtree.loquainteractable.listeners.PlayerJoinListener;
import net.punchtree.loquainteractable.listeners.PlayerQuitListener;
import net.punchtree.loquainteractable.metadata.commands.MetadataWandCommand;
import net.punchtree.loquainteractable.metadata.editing.MetadataWand;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager;
import net.punchtree.loquainteractable.npc.citizens.CitizensNPCManager;
import net.punchtree.loquainteractable.npc.citizens.CitizensTestCommand;
import net.punchtree.loquainteractable.npc.citizens.heist.GuardTesting;
import net.punchtree.loquainteractable.player.InteractablePlayer;
import net.punchtree.loquainteractable.player.PlayerMapping;
import net.punchtree.loquainteractable.transit.streetcar.StreetcarTesting;
import net.punchtree.loquainteractable.transit.streetcar.StreetcarTestingCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LoquaInteractablePlugin extends JavaPlugin {

	public static LoquaInteractablePlugin getInstance() {
		return getPlugin(LoquaInteractablePlugin.class);
	}
	
	private boolean initialized = false;
	
	private ProtocolManager protocolManager;
	private PlayerInputsManager playerInputsManager;
	private SteerVehicleInputPacketAdapter vehicleInputPacketAdapter;
	private ChangeHeldItemInputPacketAdapter heldItemInputPacketAdapter;
	private CustomItemRegistry customItemRegistry;
	
	private GarbageCansService garbageCansService;
	
	private PlayerMapping<InteractablePlayer> playerMapping;

	public void setPlayerMapping(PlayerMapping<InteractablePlayer> playerMapping) {
		if (!initialized) {
			this.playerMapping = playerMapping;
			onInitialize();
			this.initialized = true;
		} else {
			throw new IllegalStateException("Can't initialize LoquaInteractable twice!");
		}
	}
	
	@Override
	public void onEnable() {
		saveDefaultConfig();

		this.protocolManager = ProtocolLibrary.getProtocolManager();
		this.playerInputsManager = new PlayerInputsManager();


		this.customItemRegistry = CustomItemRegistry.load();

		CitizensNPCManager.INSTANCE.initialize();

		garbageCansService = new GarbageCansService(this);
		garbageCansService.onEnable();

		registerEvents();
		setCommandExecutors();

		// The way player input tracking is initialized is unrefined
		// and so this happens after event registration so that player inputs object is initialized
		// before packets are received and processed
		initializeInputTracking();
	}

	private void initializeInputTracking() {
		this.vehicleInputPacketAdapter = new SteerVehicleInputPacketAdapter(this, playerInputsManager);
		this.heldItemInputPacketAdapter = new ChangeHeldItemInputPacketAdapter(this, playerInputsManager);

		protocolManager.addPacketListener(vehicleInputPacketAdapter);
		protocolManager.addPacketListener(heldItemInputPacketAdapter);
	}

	private void onInitialize() {
		
	}
	
	public CustomItemRegistry getCustomItemRegistry() {
		return this.customItemRegistry;
	}
	
	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new MessageOfTheDay(), this);
		PluginManager pluginManager = Bukkit.getPluginManager();

		pluginManager.registerEvents(new MessageOfTheDay(), this);

		pluginManager.registerEvents(new ArmorStandChunkLoadingReglow(), this);
		
		pluginManager.registerEvents(InventoryMenuListener.getInstance(), this);
		
		// TODO per player instances for data accumulation?
		pluginManager.registerEvents(new MetadataWand(), this);
		pluginManager.registerEvents(MetadataEditingSessionManager.getInstance(), this);
		
		// Just for testing
		pluginManager.registerEvents(new ArmorStandUtilsTesting(), this);
		
		// For now, only input processing, may have more concerns later
		pluginManager.registerEvents(new PlayerJoinListener(playerInputsManager), this);
		pluginManager.registerEvents(new PlayerQuitListener(playerInputsManager), this);
		
		// Interactables/Consumables
		pluginManager.registerEvents(new DrinkItemListener(this, protocolManager, playerMapping), this);

		pluginManager.registerEvents(new InstrumentListener(playerInputsManager), this);
	}
	
	private void setCommandExecutors() {
		getCommand("metadatawand").setExecutor(new MetadataWandCommand());
		getCommand("invtest").setExecutor(new InventoryMenuTesting());
		getCommand("circlegame").setExecutor(new CircleGameTesting());
		getCommand("toast").setExecutor(new ToastTesting());
		getCommand("getnbt").setExecutor(new NbtUtilCommands());
		getCommand("verifyplayerinputsmap").setExecutor(new PlayerInputsTesting(playerInputsManager));
		
		var giveCustomItemCommand = new GiveCustomItemCommand(customItemRegistry);
		getCommand("givecustom").setExecutor(giveCustomItemCommand);
		getCommand("givecustom").setTabCompleter(giveCustomItemCommand);
		
		var addItemCommand = new AddItemCommand(customItemRegistry);
		getCommand("additem").setExecutor(addItemCommand);
		getCommand("additem").setTabCompleter(addItemCommand);
		getCommand("overrideitem").setExecutor(addItemCommand);
		getCommand("overrideitem").setTabCompleter(addItemCommand);
		
		var renameItemIdCommand = new RenameItemCommand(customItemRegistry);
		getCommand("renameitemid").setExecutor(renameItemIdCommand);
		getCommand("renameitemid").setTabCompleter(renameItemIdCommand);
		
		var deleteItemCommand = new DeleteItemCommand(customItemRegistry);
		getCommand("deleteitem").setExecutor(deleteItemCommand);
		getCommand("deleteitem").setTabCompleter(deleteItemCommand);
		
		var itemsCommand = new ItemsCommand(customItemRegistry);
		getCommand("items").setExecutor(itemsCommand);
		
		getCommand("rename").setExecutor(new RenameCommand());
		
		var tagCommands = new TagCommands();
		getCommand("addtag").setExecutor(tagCommands);
		getCommand("deletetag").setExecutor(tagCommands);
		getCommand("deletetag").setExecutor(tagCommands);
		getCommand("listtags").setExecutor(tagCommands);

		var setLeatherColorCommand = new SetLeatherColorCommand();
		getCommand("setleathercolor").setExecutor(setLeatherColorCommand);

		getCommand("streetcar").setExecutor(StreetcarTestingCommand.INSTANCE);

		getCommand("changeskin").setExecutor(SkinGrabberTestCommand.INSTANCE);
		getCommand("changecape").setExecutor(SkinGrabberTestCommand.INSTANCE);

		// citizens test commands
		getCommand("create-npc").setExecutor(CitizensTestCommand.INSTANCE);
		getCommand("make-npc-move").setExecutor(CitizensTestCommand.INSTANCE);

		// gun test commands
		getCommand("test-gun").setExecutor(QualityArmoryTestCommand.INSTANCE);
		getCommand("list-guns").setExecutor(QualityArmoryTestCommand.INSTANCE);

		// heist test commands
		getCommand("heist").setExecutor(HeistTestCommand.INSTANCE);

		// instrument test command
		getCommand("instrument").setExecutor(new InstrumentTestCommand(playerInputsManager));
	}
	
	@Override
	public void onDisable() {
		customItemRegistry.save();
		garbageCansService.onDisable();

		InstrumentManager.INSTANCE.onDisable();

		StreetcarTesting.INSTANCE.onDisable();
		CitizensNPCManager.INSTANCE.onDisable();

		GuardTesting.INSTANCE.onDisable();
		
		MetadataEditingSessionManager.cleanupSessions();

		protocolManager.removePacketListeners(this);
	}
	
	
}
