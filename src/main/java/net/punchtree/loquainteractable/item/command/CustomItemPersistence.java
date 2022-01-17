package net.punchtree.loquainteractable.item.command;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class CustomItemPersistence {

	private static final File customItemsFile = new File(LoquaInteractablePlugin.getInstance().getDataFolder(), "custom-items.yml");
	private FileConfiguration customItemsConfig;
	
	private CustomItemPersistence() {}
	
	private static CustomItemPersistence instance = new CustomItemPersistence();
	public static CustomItemPersistence getInstance() {
		return instance;
	}
	
	public SortedMap<String, ItemStack> loadCustomItems() {
		if (!prepareFiles()) {
			Bukkit.getLogger().severe("Could not create custom items file!");
			return new TreeMap<>();
		}
		SortedMap<String, ItemStack> customItems = new TreeMap<>();
		
		for (String nameId : customItemsConfig.getKeys(false)) {
			customItems.put(nameId, customItemsConfig.getItemStack(nameId));
		}
		Bukkit.getLogger().info("Loaded " + customItems.size() + " custom items");

		return customItems;
	}
	
	public void saveCustomItems(Map<String, ItemStack> customItems) {
		customItems.forEach(customItemsConfig::set);
		Bukkit.getLogger().info("Saved " + customItems.size() + " custom items");
		try {
			customItemsConfig.save(customItemsFile);
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.getLogger().severe("Could not save custom items file!");
		}
	}
	
	private boolean prepareFiles() {
		if (!customItemsFile.exists()) {
			customItemsFile.getParentFile().mkdirs();
			try {
				customItemsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		customItemsConfig = new YamlConfiguration();
		try {
			customItemsConfig.load(customItemsFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
