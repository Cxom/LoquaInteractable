package net.punchtree.loquainteractable.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.LoquaInteractablePlugin;

// TODO this was never registered or even implements listener!
public class PlayerInteractEntityListener {

	@EventHandler
	public void onLightSwitchInteract(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;
		
		ItemFrame frame = (ItemFrame) event.getRightClicked();
		PersistentDataContainer container = frame.getPersistentDataContainer();
		
		NamespacedKey key = new NamespacedKey(JavaPlugin.getPlugin(LoquaInteractablePlugin.class), "lightswitch");
		
		if (!container.has(key, PersistentDataType.INTEGER_ARRAY)) return;
		
		int[] lights = container.get(key,  PersistentDataType.INTEGER_ARRAY);
		if (lights.length % 3 != 0) {
			Bukkit.broadcastMessage(ChatColor.RED + "Wrong number of integers specified");
		}
		
		boolean on = frame.getRotation() == Rotation.NONE;
		frame.setRotation(on ? Rotation.FLIPPED : Rotation.NONE);
		
		World world = frame.getWorld();
		for(int i = 0; i < lights.length; i+=3) {
			Location loc = new Location(world, lights[i], lights[i+1], lights[i+1]);
			loc.getBlock().setType(on ? Material.SMOOTH_QUARTZ : Material.BEACON);
		}
		
	}
	
}
