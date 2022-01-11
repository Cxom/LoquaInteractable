package net.punchtree.loquainteractable.gui.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Menu implements IMenu {

	public static final ItemStack INVISIBLE_MENU_ITEM = new ItemStack(Material.PAPER);
	static {
		ItemMeta im = INVISIBLE_MENU_ITEM.getItemMeta();
		im.setCustomModelData(1);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		INVISIBLE_MENU_ITEM.setItemMeta(im);
	}
	
	private final String title;
	private final int menuSize;
	private final Inventory inventory;
	private final Map<Integer, Consumer<Player>> menuClickCallbacks = new HashMap<>();
	
	public Menu(String title, int rows) {
		this.title = title;
		this.menuSize = rows * 9;
		this.inventory = Bukkit.createInventory(null, menuSize, title);
		InventoryMenuListener.getInstance().addMenu(this, inventory);
	}
	
	public void setItem(int slot, ItemStack itemstack) {
		inventory.setItem(slot, itemstack);
	}
	
	public void registerCallback(int slot, String label, List<String> description, Consumer<Player> onClick) {
		registerCallback(slot, INVISIBLE_MENU_ITEM, label, description, onClick);
	}
	
	public void registerCallback(int slot, ItemStack item, String label, List<String> description, Consumer<Player> onClick) {
		if (slot < 0 || slot >= menuSize) {
			throw new IllegalArgumentException("An invalid slot id of '" + slot + "' was supplied when creating a menu");
		}
		
		ItemStack menuItem = item.clone();
		ItemMeta im = INVISIBLE_MENU_ITEM.getItemMeta();
		im.setDisplayName(label);
		im.setLore(description);
		menuItem.setItemMeta(im);
		menuClickCallbacks.put(slot, onClick);
		inventory.setItem(slot, menuItem);
	}
	
	@EventHandler
	public void onMenuClick(InventoryClickEvent event) {
		if(!event.getView().getTitle().equals(title)){
			Bukkit.broadcastMessage("Inventory click event dispatched to wrong menu?!");
			return;
		}
		if (event.getWhoClicked().getType() != EntityType.PLAYER) {
			// TODO is this even possible? Why is inventoryclickevent getWhoClicked abstracted to HumanEntity?
			return;
		}
		Player player = (Player) event.getWhoClicked();
		event.setCancelled(true);
		int clickedSlot = event.getSlot();
		Consumer<Player> callback = menuClickCallbacks.get(clickedSlot);
		if (callback != null) {
			callback.accept(player);
		}
	}
	
	@EventHandler
	public void onMenuDrag(InventoryDragEvent event) {
		if(!event.getView().getTitle().equals(title)){
			Bukkit.broadcastMessage("Inventory drag event dispatched to wrong menu?!");
			return;
		}
		event.setCancelled(true);
	}
	
	public void disable() {
		boolean success = InventoryMenuListener.getInstance().removeMenu(inventory);
		if (!success) {
			Bukkit.broadcastMessage(ChatColor.RED + "Failed to remove unregister inventory events for inventory '" + title + "'!");
		}
	}

	@Deprecated
	public void showToPlayer(Player player) {
		player.openInventory(inventory);
	}
	
	public void openFor(HumanEntity humanEntity) {
		humanEntity.openInventory(inventory);
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
}
