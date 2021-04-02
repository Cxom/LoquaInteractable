package net.punchtree.loquainteractable.displayutil;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class ArmorStandChunkLoadingReglow implements Listener {

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		// TODO move this into latching functions onto one loop off the main plugin instance?
		for (Entity entity : event.getChunk().getEntities()) {
			if (entity.getType() == EntityType.ARMOR_STAND) {
				ArmorStand stand = (ArmorStand) entity;
				if (stand.getScoreboardTags().contains("loqinttempglowing")) {
					stand.setCanTick(true);
					stand.setFireTicks(1000);
					new BukkitRunnable() {
						public void run() {							
							stand.setCanTick(false);
						}
					}.runTaskLater(LoquaInteractablePlugin.getInstance(), 20);
				}
			}
		}
	}
	
}
