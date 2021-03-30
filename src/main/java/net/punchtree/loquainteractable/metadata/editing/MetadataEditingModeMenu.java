package net.punchtree.loquainteractable.metadata.editing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager;

public class MetadataEditingModeMenu implements Listener {

	private final String MENU_NAME = "Select Metadata Editing Mode";
	
	private final Inventory menu;
	
	private List<MetadataEditingMode> editingModes;
	
	MetadataEditingModeMenu(Collection<MetadataEditingMode> editingModes) {
		this.menu = Bukkit.createInventory(null, (editingModes.size() / 9 + 1) * 9, MENU_NAME);
		this.editingModes = new ArrayList<MetadataEditingMode>(editingModes);
		
		constructMenu();

		// Will this leak memory? - Regardless, menu objects are meant to be SAVED AND REUSED
		Bukkit.getServer().getPluginManager().registerEvents(this, LoquaInteractablePlugin.getInstance());
	}
	
	private void constructMenu() {

		int slotIndex = 0;
		for (MetadataEditingMode editingMode : editingModes) {

			ItemStack editingModeMarker = editingMode.getMenuItem();
			ItemMeta meta = editingModeMarker.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + editingMode.getName());
			meta.setLore(editingMode.getDescription());
			editingModeMarker.setItemMeta(meta);

			menu.setItem(slotIndex, editingModeMarker);

			slotIndex++;
		}
	}
	
	public void refresh() {
		// TODO - change inventory size if a new lobby is added - when doing this, make sure this lobby never overflows (iteration limit)
		menu.clear();
		constructMenu();
	}
	
	public void showTo(Player player) {
		refresh();
		player.openInventory(menu);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if (clickedInMenu(e)){
			//Prevent picking up an item
			e.setCancelled(true);
			
//			if (clickedAValidItem(e.getCurrentItem())){ 
			Player player = (Player) e.getWhoClicked();
			
			if (clickedOnEditingMode(e)) {
				MetadataEditingMode editingMode = getClickedEditingMode(e).getNewInstance();
				MetadataEditingSession editingSession = MetadataEditingSessionManager.getSessionFor(player);
				if (!editingMode.getClass().equals(editingSession.getEditingMode().getClass())) {
					editingSession.changeEditingMode(editingMode);
				}
			}
			
			player.closeInventory();
			refresh();
//			}
		}
	}
	
	private boolean clickedInMenu(InventoryClickEvent e) {
		return e.getClickedInventory() != null
			&& e.getView().getTitle().equals(MENU_NAME);
	}
	
	private boolean clickedOnEditingMode(InventoryClickEvent e) {
		return e.getSlot() < editingModes.size();
	}
	
	private MetadataEditingMode getClickedEditingMode(InventoryClickEvent event) {
		return editingModes.get(event.getSlot());
	}
	
//	private boolean clickedAValidItem(ItemStack currentItem) {
//		return currentItem != null
//			&& currentItem.hasItemMeta()
//			&& currentItem.getItemMeta().hasLore()
//		//TODO i18n : replace toString ^^
//	}
	
	//Prevent modifying menu
	@EventHandler
	private void onMenuDrag(InventoryDragEvent e){
		if(e.getView().getTitle().equals(MENU_NAME)){
			e.setCancelled(true);
		}
	}

}
