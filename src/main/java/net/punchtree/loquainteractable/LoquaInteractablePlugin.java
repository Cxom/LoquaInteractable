package net.punchtree.loquainteractable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.punchtree.loquainteractable.metadata.commands.MetadataWandCommand;
import net.punchtree.loquainteractable.metadata.editing.MetadataWand;

public class LoquaInteractablePlugin extends JavaPlugin {

	private static LoquaInteractablePlugin instance;
	public static LoquaInteractablePlugin getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		Bukkit.getPluginManager().registerEvents(new SendResourcePackOnJoin(), this);
		// TODO per player instances for data accumulation?
		Bukkit.getPluginManager().registerEvents(new MetadataWand(), this);
		
		getCommand("metadatawand").setExecutor(new MetadataWandCommand());
	}
	
	@Override
	public void onDisable() {
		
	}
	
	
}
