package net.punchtree.loquainteractable.city.garbagecans;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;
import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class GarbageCanCollection implements ConfigurationSerializable {

	private static final long ACCEPT_RETRY_DELAY = (long) (5 * 20);
	private static final int DEFAULT_CAPACITY = 9;
	
	private int capacity = DEFAULT_CAPACITY;
	private Deque<GarbageItem> contents;
	private Inventory inventory;
	private Location location; // Redundant, used only for saving
	
	public GarbageCanCollection(Location location) {
		this.contents = new ArrayDeque<>();
		int rows = (int) Math.ceil(capacity / 9.0);
		this.inventory = Bukkit.createInventory(null, rows * 9, Component.empty());
		this.location = location;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public GarbageCanCollection(Map<String, Object> serialized) {
		this.capacity = (int) serialized.get("capacity");
		int rows = (int) Math.ceil(capacity / 9.0);
		this.inventory = Bukkit.createInventory(null, rows * 9, Component.empty());
		inventory.setContents(((List<ItemStack>) serialized.get("inventory")).toArray(new ItemStack[rows * 9]));
		this.contents = new ArrayDeque<GarbageItem>((List<GarbageItem>) serialized.get("contents"));
		this.location = (Location) serialized.get("location");
	}
	
	public void addItem(Item item) {
		HashMap<Integer, ItemStack> results = inventory.addItem(item.getItemStack());
		if (results.isEmpty()) {
			contents.add(new GarbageItem(item.getItemStack(), System.currentTimeMillis()));
			item.remove();
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!item.isDead()) {
						addItem(item);
					}
				}
			}.runTaskLater(LoquaInteractablePlugin.getInstance(), ACCEPT_RETRY_DELAY);
		}
	}
	
	public int prune(long time) {
		int removed = 0;
		while (!contents.isEmpty() && contents.peek().throwAwayTime < time) {
			ItemStack item = contents.poll().item;
			inventory.removeItem(item);
			++removed;
		}
		return removed;
	}
	
	public void empty() {
		contents.clear();
		inventory.clear();
	}
	
	public boolean isEmpty() {
		return contents.isEmpty();
	}

	public void showTo(Player player) {
		player.openInventory(inventory);
	}
	
	public Location getLocation() {
		return location;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<>();
		serialized.put("capacity", capacity);
		serialized.put("inventory", inventory.getContents());
		serialized.put("contents", Arrays.asList(contents.toArray()));
		serialized.put("location", location);
		return serialized;
	}
	
	public static class GarbageItem implements ConfigurationSerializable {
		private final ItemStack item;
		private final long throwAwayTime;
		private GarbageItem(ItemStack item, long throwAwayTime) {
			this.item = item;
			this.throwAwayTime = throwAwayTime;
		}
		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> serialized = new HashMap<>();
			serialized.put("item", item);
			serialized.put("throwAwayTime", throwAwayTime);
			return serialized;
		}
		public static GarbageItem deserialize(Map<String, Object> serialized) {
			ItemStack item = (ItemStack) serialized.get("item");
			long throwAwayTime = (long) serialized.get("throwAwayTime");
			return new GarbageItem(item, throwAwayTime);
		}
	}
	
}
