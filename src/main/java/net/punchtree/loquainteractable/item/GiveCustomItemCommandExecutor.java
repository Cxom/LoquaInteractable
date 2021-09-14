package net.punchtree.loquainteractable.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class GiveCustomItemCommandExecutor implements CommandExecutor, TabCompleter {

	public final Iterable<String> customItemKeys = Arrays.asList("coffee");
	
	// This will be abstracted out into taking in a custom item registry
//	public GiveCustomItemCommandExecutor(Iterable<String> customItemKeys) {
//		this.customItemKeys = customItemKeys;
//	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if ( ! (sender instanceof Player player)) return true;
		
		player.getInventory().addItem(DrinkItems.COFFEE.clone());
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		StringUtil.copyPartialMatches(args[0], customItemKeys, completions);
		return completions;
	}
	
	
}

	
