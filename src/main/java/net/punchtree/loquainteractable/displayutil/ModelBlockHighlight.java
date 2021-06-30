package net.punchtree.loquainteractable.displayutil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;
import net.punchtree.util.color.ColoredScoreboardTeams;
import net.punchtree.util.color.PunchTreeColor;

public class ModelBlockHighlight {

	private static class HighlightInstance {
		public final Block block;
		public final ArmorStand entity;
		public long expiration;
		
		public HighlightInstance(Block block, ArmorStand highlight, long expiration) {
			this.block = block;
			this.entity = highlight;
			this.expiration = expiration;
		}
	}
	
	// LinkedHashMap preserves iteration order so pruning is maximally efficient (chronological)
	private LinkedHashMap<Block, HighlightInstance> highlightUntil = new LinkedHashMap<>();
	private Map<Block, Entity> highlightIndefinitely = new HashMap<>();

	private BukkitTask pruningTask;
	
	private ItemStack highlightItem = HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL;
	private Team coloredTeam = ColoredScoreboardTeams.LIGHT_PURPLE_TEAM;
	
	public ModelBlockHighlight() {
		
	}
	
	private class PruningRunnable extends BukkitRunnable {
		public void run() {
			pruneHighlights();
			pruningTask = null;
			if (!highlightUntil.isEmpty()) {

				long nextExpiration = highlightUntil.values().iterator().next().expiration;
				long timerDelay = (long) Math.ceil((nextExpiration - System.currentTimeMillis()) / 20d);
				pruningTask = new PruningRunnable().runTaskLater(LoquaInteractablePlugin.getInstance(), timerDelay);
			}
		}
	}
	
	public ModelBlockHighlight setColoredItem(ItemStack highlightItem, PunchTreeColor scolor) {
		this.highlightItem = highlightItem.clone();
		LeatherArmorMeta lm = (LeatherArmorMeta) this.highlightItem.getItemMeta();
		lm.setColor(scolor.getBukkitColor());
		this.highlightItem.setItemMeta(lm);
		this.coloredTeam = scolor.getGlowingTeam();
		return this;
	}
	
	public ModelBlockHighlight setHighlightItem(ItemStack highlightItem) {
		this.highlightItem = highlightItem;
		return this;
	}
	
	public ModelBlockHighlight setColoredTeam(Team coloredTeam) {
		this.coloredTeam = coloredTeam;
		return this;
	}
	
	private void pruneHighlights() {
		Iterator<HighlightInstance> highlightIterator = highlightUntil.values().iterator();
		while(highlightIterator.hasNext()) {
			HighlightInstance highlight = highlightIterator.next();
			if (highlight.expiration > System.currentTimeMillis()) {
				return;
			}
			highlight.entity.remove();
			highlightIterator.remove();
		}
	}
	
	public ArmorStand highlightUntil(Block block, Long expiration) {
		HighlightInstance highlight = null;
		if (highlightUntil.containsKey(block)) {
			// Removing the element if the time changes preserves chronological iteration
			// order in the linkedhashmap
			highlight = highlightUntil.remove(block);
			highlight.expiration = expiration;
		} else {
			highlight = new HighlightInstance(block,
											  ArmorStandUtils.spawnArmorStand(block.getLocation(), 
													  						  true,
													  						  coloredTeam,
													  						  highlightItem),
											  expiration);
		}
		highlightUntil.put(block, highlight);
		if (pruningTask == null) {
			long timerDelay = (long) Math.ceil((expiration - System.currentTimeMillis()) / 20d);
			pruningTask = new PruningRunnable().runTaskLater(LoquaInteractablePlugin.getInstance(), timerDelay);
		}
		return highlight.entity;
	}
	
	public ArmorStand highlightFor(Block block, double seconds) {
		return highlightUntil(block, System.currentTimeMillis() + (long) (seconds * 1000));
	}
	
	public ArmorStand highlightIndefinitely(Block block) {
		ArmorStand highlight = ArmorStandUtils.spawnArmorStand(block.getLocation(),
															   true,
															   coloredTeam,
															   highlightItem);
		if (highlightIndefinitely.containsKey(block)) {
			// Remove it from the map 
			highlightIndefinitely.remove(block).remove();
		}
		highlightIndefinitely.put(block, highlight);
		return highlight;
	}
	
	public void removeHighlight(Block block) {
		Entity highlight = highlightIndefinitely.remove(block);
		if (highlight != null) {
			highlight.remove();
		}
		HighlightInstance highlightInstance = highlightUntil.remove(block);
		if (highlightInstance != null) {
			highlightInstance.entity.remove();
		}
	}
	
	public void cleanupDisable() {
		if (pruningTask != null) {			
			pruningTask.cancel();
		}
		pruningTask = null;
		for(HighlightInstance highlight : highlightUntil.values()) {
			highlight.entity.remove();
		}
		highlightUntil.clear();
		for(Entity highlight : highlightIndefinitely.values()) {
			highlight.remove();
		}
		highlightIndefinitely.clear();
	}
	
}
