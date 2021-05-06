package net.punchtree.loquainteractable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.punchtree.loquainteractable.commands.CustomModelDataCommands;
import net.punchtree.loquainteractable.commands.testing.CircleGameTesting;
import net.punchtree.loquainteractable.displayutil.ArmorStandChunkLoadingReglow;
import net.punchtree.loquainteractable.displayutil.ArmorStandUtilsTesting;
import net.punchtree.loquainteractable.displayutil.ColoredScoreboardTeams;
import net.punchtree.loquainteractable.gui.inventory.InventoryMenuListener;
import net.punchtree.loquainteractable.gui.inventory.InventoryMenuTesting;
import net.punchtree.loquainteractable.metadata.commands.MetadataWandCommand;
import net.punchtree.loquainteractable.metadata.editing.MetadataWand;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager;

public class LoquaInteractablePlugin extends JavaPlugin {

	private static LoquaInteractablePlugin instance;
	public static LoquaInteractablePlugin getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		ColoredScoreboardTeams.initializeTeams();
		registerEvents();
		setCommandExecutors();
	}
	
	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new SendResourcePackOnJoin(), this);
		Bukkit.getPluginManager().registerEvents(new ArmorStandChunkLoadingReglow(), this);
		
		Bukkit.getPluginManager().registerEvents(InventoryMenuListener.getInstance(), this);
		
		// TODO per player instances for data accumulation?
		Bukkit.getPluginManager().registerEvents(new MetadataWand(), this);
		Bukkit.getPluginManager().registerEvents(MetadataEditingSessionManager.getInstance(), this);
		
		// Just for testing
		Bukkit.getPluginManager().registerEvents(new ArmorStandUtilsTesting(), this);
	}
	
	private void setCommandExecutors() {
		getCommand("metadatawand").setExecutor(new MetadataWandCommand());
		getCommand("invtest").setExecutor(new InventoryMenuTesting());
		getCommand("cmd").setExecutor(new CustomModelDataCommands());
		getCommand("circlegame").setExecutor(new CircleGameTesting());
	}
	
	@Override
	public void onDisable() {
		MetadataEditingSessionManager.cleanupSessions();
	}
	
	
}
