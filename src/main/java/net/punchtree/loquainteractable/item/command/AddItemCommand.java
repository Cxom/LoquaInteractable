package net.punchtree.loquainteractable.item.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.item.CustomItemRegistry;

public class AddItemCommand implements CommandExecutor, TabCompleter {

	private static final String ERROR = ChatColor.RED + "";
	
	private final CustomItemRegistry itemRegistry;
	
	public AddItemCommand(CustomItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if ( ! ( sender instanceof Player player )) return false;
		if (args.length < 1) return false;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null) {
			player.sendMessage(Component.text(ERROR + "You must be holding an item!"));
			return true;
		}
		
		String name = args[0];
		if (!label.equalsIgnoreCase("overrideitem") && itemRegistry.contains(name)) {
			player.sendMessage(Component.text(ERROR + "An item with that name already exists. Use /overrideitem if you so desire to."));
			return true;
		}
		
		String[] categories = Arrays.copyOfRange(args, 1, args.length);
		for ( String category : categories ) {
			// TODO idk verify
//			verifyCategory();
		}
		
		itemRegistry.add(item, name, categories);
		if (label.equalsIgnoreCase("additem")) {
			player.sendMessage(ChatColor.GREEN + "Added item '" + name + "'.");			
		} else {
			player.sendMessage(ChatColor.GREEN + "Overrode item '" + name + "'.");
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if ("overrideitem".equalsIgnoreCase(label) && args.length == 1) {			
			return itemRegistry.getPartiallyMatchingNames(args[0]);
		} else {
			return new ArrayList<>();
		}
	}

}
