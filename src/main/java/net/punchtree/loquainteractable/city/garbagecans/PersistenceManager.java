package net.punchtree.loquainteractable.city.garbagecans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class PersistenceManager {

	private static final String GARBAGE_CANS_FILENAME = "garbage-cans.yml";
	
	private static final File CITY_FOLDER = new File(LoquaInteractablePlugin.getInstance().getDataFolder(), "city");
	private static final File garbageCansF = new File(CITY_FOLDER, GARBAGE_CANS_FILENAME);;
	private FileConfiguration garbageCansConfig;
	
	private PersistenceManager() {}
	
	private static PersistenceManager instance = new PersistenceManager();
	public static PersistenceManager getInstance() {
			return instance;
	}
	
	public void createFiles(){
		if(!garbageCansF.exists()) {
			garbageCansF.getParentFile().mkdirs();
			try {
				garbageCansF.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		garbageCansConfig = new YamlConfiguration();
		try {
			garbageCansConfig.load(garbageCansF);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Map<Location, GarbageCanCollection> loadGarbageCans(){
		Map<Location, GarbageCanCollection> garbageCans = new HashMap<>();
		
		if (garbageCansConfig.isList("garbage-cans")) {
			List<GarbageCanCollection> cansList = (List<GarbageCanCollection>) garbageCansConfig.get("garbage-cans");
			cansList.forEach(can -> garbageCans.put(can.getLocation(), can));
		}
		Bukkit.getLogger().info("Loaded " + garbageCans.size() + " garbage cans");
		
		return garbageCans;
	}
	
	public void saveGarbageCans(Map<Location, GarbageCanCollection> garbageCans) {
		List<GarbageCanCollection> cansList = new ArrayList<>(garbageCans.values());
		Bukkit.getLogger().info("Saving " + cansList.size() + " garbage cans");
		garbageCansConfig.set("garbage-cans", cansList);
		try {
			garbageCansConfig.save(garbageCansF);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
