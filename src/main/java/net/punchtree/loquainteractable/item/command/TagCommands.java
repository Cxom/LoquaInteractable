package net.punchtree.loquainteractable.item.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.item.ItemTags;

public class TagCommands implements CommandExecutor, TabCompleter {

	private static final String ERROR = ChatColor.RED + "";
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if ( ! ( sender instanceof Player player )) return false;
		if (!"listtags".equalsIgnoreCase(label) && args.length < 1) return false;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || item.getItemMeta() == null) {
			player.sendMessage(ERROR + "You must be holding an item!");
			return true;
		}
		
		switch(label.toLowerCase()) {
		case "addtag":
			ItemTags.addTag(item, args[0]);
			player.sendMessage(ChatColor.GREEN + "Added tag '" + args[0] + "'.");
			return true;
		case "deletetag":
			ItemTags.removeTag(item, args[0]);
			player.sendMessage(ChatColor.GREEN + "Deleted tag '" + args[0] + "'.");
			return true;
		case "listtags":
			Set<String> tags = ItemTags.getTags(item);
			String tagsString = tags.stream().collect(Collectors.joining(", "));
			if (tagsString.isEmpty()) tagsString = ChatColor.RED + "This item has no tags.";
			player.sendMessage(tagsString);
			return true;
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if ( ! ( sender instanceof Player player )) return Collections.emptyList();
		ItemStack item = player.getInventory().getItemInMainHand();
		if ("deletetag".equalsIgnoreCase(label) && args.length <= 1 && item != null) {
			List<String> completions = new ArrayList<>();
			StringUtil.copyPartialMatches(args[0], ItemTags.getTags(item), completions);
			return completions;
		}
		return Collections.emptyList();
	}
	
}
