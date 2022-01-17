package net.punchtree.loquainteractable.item.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.punchtree.loquainteractable.item.CustomItemRegistry;

public class GiveCustomItemCommand implements CommandExecutor, TabCompleter {

	private static final String GIVECUSTOM_PERMISSION = "givecustom";
	
	public final CustomItemRegistry itemRegistry;  
	
	public GiveCustomItemCommand(CustomItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if ( ! (sender instanceof Player player)) return true;
		if ( ! (sender.hasPermission(GIVECUSTOM_PERMISSION))) {
			// TODO this is a default that should be abstracted, maybe at the server level
			player.sendMessage(ChatColor.RED + "Either this command doesn't exist or you don't have permission to perform this command.");
		}
		
		if (args.length < 0) { return false; }
		
		String itemName = args[0];
		if ( !itemRegistry.contains(itemName) ) {
			player.sendMessage(ChatColor.RED + "An item with that name does not exist.");
			return true;
		}
		
		player.getInventory().addItem(itemRegistry.get(itemName));
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return itemRegistry.getPartiallyMatchingNames(args[0]);
	}
	
}

	
