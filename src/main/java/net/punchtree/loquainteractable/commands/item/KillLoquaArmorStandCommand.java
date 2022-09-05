package net.punchtree.loquainteractable.commands.item;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillLoquaArmorStandCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if ( ! (sender instanceof Player player)) return true;
		
		Bukkit.getServer().dispatchCommand(player, "minecraft:kill @e[type=minecraft:armor_stand,distance=..3,limit=1,tag=loqinttemp]");
		return true;
	}
	
}
