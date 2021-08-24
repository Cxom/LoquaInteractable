package net.punchtree.loquainteractable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
import net.punchtree.loquainteractable.listeners.PlayerJoinListener;
import net.punchtree.loquainteractable.listeners.PlayerQuitListener;
import net.punchtree.loquainteractable.metadata.commands.MetadataWandCommand;
import net.punchtree.loquainteractable.metadata.editing.MetadataWand;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager;

public class LoquaInteractablePlugin extends JavaPlugin {

	public static LoquaInteractablePlugin getInstance() {
		return getPlugin(LoquaInteractablePlugin.class);
	}
	
	private PlayerInputsManager playerInputsManager;
	
	@Override
	public void onEnable() {
		playerInputsManager = new PlayerInputsManager();
		
		registerEvents();
		setCommandExecutors();
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
		
	}
	
	private void setCommandExecutors() {
		getCommand("metadatawand").setExecutor(new MetadataWandCommand());
		getCommand("invtest").setExecutor(new InventoryMenuTesting());
		getCommand("cmd").setExecutor(new CustomModelDataCommands());
		getCommand("circlegame").setExecutor(new CircleGameTesting());
		getCommand("toast").setExecutor(new ToastTesting());
		getCommand("getnbt").setExecutor(new NbtUtilCommands());
		getCommand("verifyplayerinputsmap").setExecutor(new PlayerInputsTesting(playerInputsManager));
	}
	
	@Override
	public void onDisable() {
		MetadataEditingSessionManager.cleanupSessions();
	}
	
	
}
