package net.punchtree.loquainteractable.ui.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class InventoryMenuListener implements Listener {
	
	// Singleton just to prevent accidentally creating multiple instances
	private static final InventoryMenuListener instance = new InventoryMenuListener();
	public static InventoryMenuListener getInstance() { return instance; }
	private InventoryMenuListener() {}
	
	Map<Integer, IMenu> menus = new HashMap<>();
	
	public void addMenu(IMenu menu, Inventory inventory) {
		menus.put(inventory.hashCode(), menu);
	}
	
	public boolean removeMenu(Inventory inventory) {
		return menus.remove(inventory.hashCode()) != null;
	}
	
	@EventHandler
	public void onMenuClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null) return;
		int inventoryHash = event.getClickedInventory().hashCode();
		IMenu menu = menus.get(inventoryHash);
		if (menu != null) {
			menu.onMenuClick(event);
		}
	}
	
	@EventHandler
	public void onMenuDrag(InventoryDragEvent event) {
		int inventoryHash = event.getInventory().hashCode();
		IMenu menu = menus.get(inventoryHash);
		if (menu != null) {
			menu.onMenuDrag(event);
		}
	}
	
	@EventHandler
	public void onMenuClose(InventoryCloseEvent event) {
		int inventoryHash = event.getInventory().hashCode();
		IMenu menu = menus.get(inventoryHash);
		if (menu != null) {
			menu.onMenuClose(event);
		}
	}
	
}
