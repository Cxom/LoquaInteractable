package net.punchtree.loquainteractable.item.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.item.CustomItemRegistry;

public class DeleteItemCommand implements CommandExecutor, TabCompleter {

	private static final String ERROR = ChatColor.RED + "";

	private final CustomItemRegistry itemRegistry;
	
	public DeleteItemCommand(CustomItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if ( ! ( sender instanceof Player player )) return false;
		if (args.length < 1) return false;
		
		String name = args[0];
		if (!itemRegistry.contains(name)) {
			player.sendMessage(Component.text(ERROR + "An item with that name does not exist."));
			return true;
		}
		
		itemRegistry.delete(name);
		player.sendMessage(ChatColor.GREEN + "Deleted item '" + name + "'.");
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		switch(args.length) {
		case 1:
			return itemRegistry.getPartiallyMatchingNames(args[0]);
		case 0:
			return itemRegistry.getPartiallyMatchingNames("");
		default:
			return Collections.emptyList();
		}
	}	
	
}
