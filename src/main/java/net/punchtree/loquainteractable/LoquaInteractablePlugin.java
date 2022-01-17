package net.punchtree.loquainteractable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import net.punchtree.loquainteractable.city.garbagecans.GarbageCansService;
import net.punchtree.loquainteractable.commands.item.CustomModelDataCommands;
import net.punchtree.loquainteractable.commands.item.NbtUtilCommands;
import net.punchtree.loquainteractable.commands.testing.CircleGameTesting;
import net.punchtree.loquainteractable.commands.testing.PlayerInputsTesting;
import net.punchtree.loquainteractable.commands.testing.ToastTesting;
import net.punchtree.loquainteractable.displayutil.ArmorStandChunkLoadingReglow;
import net.punchtree.loquainteractable.displayutil.ArmorStandUtilsTesting;
import net.punchtree.loquainteractable.gui.inventory.InventoryMenuListener;
import net.punchtree.loquainteractable.gui.inventory.InventoryMenuTesting;
import net.punchtree.loquainteractable.input.PlayerInputsManager;
import net.punchtree.loquainteractable.item.CustomItemRegistry;
import net.punchtree.loquainteractable.item.DrinkItemListener;
import net.punchtree.loquainteractable.item.command.AddItemCommand;
import net.punchtree.loquainteractable.item.command.DeleteItemCommand;
import net.punchtree.loquainteractable.item.command.GiveCustomItemCommand;
import net.punchtree.loquainteractable.item.command.ItemsCommand;
import net.punchtree.loquainteractable.item.command.RenameCommand;
import net.punchtree.loquainteractable.item.command.RenameItemCommand;
import net.punchtree.loquainteractable.item.command.TagCommands;
import net.punchtree.loquainteractable.listeners.PlayerJoinListener;
import net.punchtree.loquainteractable.listeners.PlayerQuitListener;
import net.punchtree.loquainteractable.metadata.commands.MetadataWandCommand;
import net.punchtree.loquainteractable.metadata.editing.MetadataWand;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager;
import net.punchtree.loquainteractable.player.InteractablePlayer;
import net.punchtree.loquainteractable.player.PlayerMapping;

public class LoquaInteractablePlugin extends JavaPlugin {

	public static LoquaInteractablePlugin getInstance() {
		return getPlugin(LoquaInteractablePlugin.class);
	}
	
	private boolean initialized = false;
	
	private ProtocolManager protocolManager;
	private PlayerInputsManager playerInputsManager;
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

		this.protocolManager = ProtocolLibrary.getProtocolManager();
		this.playerInputsManager = new PlayerInputsManager();
		this.customItemRegistry = CustomItemRegistry.load();
		
		garbageCansService = new GarbageCansService(this);
		garbageCansService.onEnable();

		registerEvents();
		setCommandExecutors();
	}
	
	private void onInitialize() {
		
	}
	
	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new ArmorStandChunkLoadingReglow(), this);
		
		Bukkit.getPluginManager().registerEvents(InventoryMenuListener.getInstance(), this);
		
		// TODO per player instances for data accumulation?
		Bukkit.getPluginManager().registerEvents(new MetadataWand(), this);
		Bukkit.getPluginManager().registerEvents(MetadataEditingSessionManager.getInstance(), this);
		
		// Just for testing
		Bukkit.getPluginManager().registerEvents(new ArmorStandUtilsTesting(), this);
		
		// For now, only input processing, may have more concerns later
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(playerInputsManager), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(playerInputsManager), this);
		
		// Interactables/Consumables
		Bukkit.getPluginManager().registerEvents(new DrinkItemListener(this, protocolManager, playerMapping), this);
		
	}
	
	private void setCommandExecutors() {
		getCommand("metadatawand").setExecutor(new MetadataWandCommand());
		getCommand("invtest").setExecutor(new InventoryMenuTesting());
		getCommand("cmd").setExecutor(new CustomModelDataCommands());
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
	}
	
	@Override
	public void onDisable() {
		customItemRegistry.save();
		garbageCansService.onDisable();
		
		MetadataEditingSessionManager.cleanupSessions();
		protocolManager.removePacketListeners(this);
	}
	
	
}
