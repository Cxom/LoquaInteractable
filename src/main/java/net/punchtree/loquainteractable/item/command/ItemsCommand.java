package net.punchtree.loquainteractable.item.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.punchtree.loquainteractable.gui.inventory.Menu;
import net.punchtree.loquainteractable.item.CustomItemRegistry;

public class ItemsCommand implements CommandExecutor, TabCompleter {

	private final static int ITEMS_PER_PAGE = 9 * 5;
	
	
	private final CustomItemRegistry itemRegistry;
	
	
	public ItemsCommand(CustomItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	
		if ( ! ( sender instanceof Player player )) return false;
		
		showItemsMenu(player);
		
		return true;
	}
	
	private void showItemsMenu(Player player) {
		constructMenuForPage(1).openFor(player);
	}
	
	private Menu constructMenuForPage(int page) {
		Menu itemsMenu = new Menu("Loqua Custom Items", 6);
		Collection<ItemStack> pageItems = itemRegistry.getSublist(ITEMS_PER_PAGE * (page-1), Math.min(ITEMS_PER_PAGE * page, itemRegistry.getSize()));
		itemsMenu.setItems(0, pageItems.iterator());
		itemsMenu.onMenuClick(giveThemWhatTheyClickedOn);
		itemsMenu.registerCallback(49, new ItemStack(Material.BARRIER), ChatColor.RED + "Close", Collections.emptyList(), HumanEntity::closeInventory);
		addPagingButtons(itemsMenu, page);
		itemsMenu.disableOnClose();
		return itemsMenu;
	}
	
	private final static Consumer<InventoryClickEvent> giveThemWhatTheyClickedOn = clickEvent -> {
		if (clickEvent.getSlot() < ITEMS_PER_PAGE && clickEvent.getCurrentItem() != null) {
			clickEvent.getWhoClicked().getInventory().addItem(clickEvent.getCurrentItem().clone());
		}
	};
	
	private void addPagingButtons(Menu itemsMenu, int page) {
		if (page > 1) {
			itemsMenu.registerCallback(48, 
				new ItemStack(Material.ARROW), 
				"Previous Page", 
				Arrays.asList(String.valueOf(page - 1)), 
				player -> constructMenuForPage(page - 1).openFor(player));
		}
		if (thereAreMorePages(page)) {
			itemsMenu.registerCallback(50, 
				new ItemStack(Material.ARROW), 
				"Next Page", 
				Arrays.asList(String.valueOf(page + 1)), 
				player -> constructMenuForPage(page + 1).openFor(player));	
		}
	}
	
	private boolean thereAreMorePages(int currentPage) {
		return itemRegistry.getSize() > currentPage * ITEMS_PER_PAGE;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO category tab completion
		return Collections.emptyList();
	}
	
}
