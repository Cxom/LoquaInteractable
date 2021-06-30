package net.punchtree.loquainteractable.gui.hud.advancements;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonObject;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class CustomAdvancement {

	private NamespacedKey key;
	
	private String title;
	private String description;
	
//	private ItemStack iconItem;
	private String iconItemKey;
	private String iconItemNbt;
	
	private static final String FRAME_TASK = "task";
	private static final String FRAME_GOAL = "goal";
	private static final String FRAME_CHALLENGE = "challenge";
	
	private String frame = FRAME_TASK;
	private boolean showToast = true;
	private boolean announceToChat = false;
	/**
	 * Whether or not the advancement should be hidden on the advancements tab
	 */
	private boolean hidden = true;
	
	public CustomAdvancement(NamespacedKey key, String title, String description, String iconItemKey, String iconItemNbt) {
		this.key = key;
		this.title = title;
		this.description = description;
		this.iconItemKey = iconItemKey;
		this.iconItemNbt = iconItemNbt;
	}
	
	// TODO use Audiences?
	// TODO caching adding/removing?????
	
	public void showTo(Player player) {
//		add();
		grant(player);
		new BukkitRunnable() {
			public void run() {
				revoke(player);
				remove();
			}
			// TODO Let other plugins run this runnable????? Accept other plugins as DI
		}.runTaskLater(LoquaInteractablePlugin.getInstance(), 2);
	}
	
	public void grant(Player player) {
		Advancement advancement = Bukkit.getAdvancement(key);
		AdvancementProgress progress = player.getAdvancementProgress(advancement);
		if (!progress.isDone()) {
			for (String criteria : progress.getRemainingCriteria()) {
				progress.awardCriteria(criteria);
			}
		}
	}
	
	public void revoke(Player player) {
		Advancement advancement = Bukkit.getAdvancement(key);
		AdvancementProgress progress = player.getAdvancementProgress(advancement);
		if (progress.isDone()) {
			for (String criteria : progress.getAwardedCriteria()) {
				progress.revokeCriteria(criteria);
			}
		}
	}
	
	public void add() {
		try {
			Bukkit.getUnsafe().loadAdvancement(key, generateJson().toString());
		} catch (IllegalArgumentException e) {
			Bukkit.getLogger().info("Error while creating advancement '" + key + "' - it probably already exists");
		}
	}
	
	public void remove() {
		Bukkit.getUnsafe().removeAdvancement(key);
		// TODO some sort of periodic way of doing this? Does it lag stuff out
//		Bukkit.getServer().reloadData();
	}
	
	public JsonObject generateJson() {
		
		JsonObject root = new JsonObject();
		
		JsonObject icon = new JsonObject();
		icon.addProperty("item", this.iconItemKey);
		// TODO more dynamic api for nbt on this object
		// 		- hasTag, getTag -> NBTTagCompound, loop throw keys (probably has to be NMS copy of itemstack - tags will be version specific after all)
		// TODO fo nbt on objects in general?? From itemstacks???
		icon.addProperty("nbt", this.iconItemNbt);
		
		JsonObject display = new JsonObject();
		display.addProperty("title", title);
		display.addProperty("description", description);
		display.add("icon", icon);
		display.addProperty("frame", frame);
		display.addProperty("show_toast", showToast);
		display.addProperty("announce_to_chat", announceToChat);
		display.addProperty("hidden", hidden);
		
		JsonObject criteria = new JsonObject();
		JsonObject trigger = new JsonObject();
		trigger.addProperty("trigger", "minecraft:impossible");
		criteria.add("loqua-interactable-custom-advancement", trigger);
		
		root.add("display", display);
		root.add("criteria", criteria);
		
		return root;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setIconItemKey(String iconItemKey) {
		this.iconItemKey = iconItemKey;
	}
	
	public void setIconItemNbt(String iconItemNbt) {
		this.iconItemNbt = iconItemNbt;
	}
	
}
