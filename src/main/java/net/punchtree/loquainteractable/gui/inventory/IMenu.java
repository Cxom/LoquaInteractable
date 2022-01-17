package net.punchtree.loquainteractable.gui.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface IMenu {

	public void onMenuClick(InventoryClickEvent event);
	
	public void onMenuDrag(InventoryDragEvent event);
	
	public void onMenuClose(InventoryCloseEvent event);
	
}
