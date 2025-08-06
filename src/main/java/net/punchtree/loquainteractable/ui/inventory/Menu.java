package net.punchtree.loquainteractable.ui.inventory;

import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.listeners.InventoryMenuListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

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
	private final Map<Integer, Consumer<Player>> slotClickCallbacks = new HashMap<>();
	private final List<Consumer<InventoryClickEvent>> globalClickCallbacks = new ArrayList<>();
	private boolean disableOnClose = false;
	
	public Menu(String title, int rows) {
		this.title = title;
		this.menuSize = rows * 9;
		this.inventory = Bukkit.createInventory(null, menuSize, title);
		InventoryMenuListener.getInstance().addMenu(this, inventory);
	}
	
	public void setItem(int slot, ItemStack itemstack) {
		inventory.setItem(slot, itemstack);
	}
	
	public void setItems(int startSlot, Iterator<ItemStack> items) {
		for (int slot = startSlot; slot < menuSize; ++slot) {
			if (!items.hasNext()) break;
			setItem(slot, items.next());
		}
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
		slotClickCallbacks.put(slot, onClick);
		inventory.setItem(slot, menuItem);
	}
	
	@Override
	public void onMenuClick(InventoryClickEvent event) {
		if(!event.getView().getTitle().equals(title)){
			Bukkit.getLogger().severe("Inventory click event dispatched to wrong menu?!");
			return;
		}
		for (Consumer<InventoryClickEvent> globalClickCallback : globalClickCallbacks) {
			globalClickCallback.accept(event);
			if (event.isCancelled()) {
				break;
			}
		}
		Player player = (Player) event.getWhoClicked();
		event.setCancelled(true);
		int clickedSlot = event.getSlot();
		Consumer<Player> callback = slotClickCallbacks.get(clickedSlot);
		if (callback != null) {
			callback.accept(player);
		}
	}
	
	@Override
	public void onMenuDrag(InventoryDragEvent event) {
		if(!event.getView().getTitle().equals(title)){
			Bukkit.broadcastMessage("Inventory drag event dispatched to wrong menu?!");
			return;
		}
		event.setCancelled(true);
	}
	
	@Override
	public void onMenuClose(InventoryCloseEvent event) {
		if (this.disableOnClose ) {
			disable();
		}
	}
	
	public void onMenuClick(Consumer<InventoryClickEvent> clickEventHandler) {
		globalClickCallbacks.add(clickEventHandler);
	}
	
	public void disableOnClose() {
		this.disableOnClose = true;
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
