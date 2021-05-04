package net.punchtree.loquainteractable.gui.inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryMenuTesting implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if ( ! (sender instanceof Player)) return true;
		Player player = (Player) sender;
		
		if (args.length == 0) {
			player.sendMessage("Subcommands are: rows");
			return true;
		}
		
		switch(args[0]) {
		case "rows": 
			int rows = Integer.parseInt(args[1]);
			Inventory menu = Bukkit.createInventory(null, rows * 9, "Test menu");
			player.openInventory(menu);
			return true;
		}
		
		return true;
	}

}
