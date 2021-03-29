package net.punchtree.loquainteractable.metadata.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.punchtree.loquainteractable.metadata.editing.MetadataWand;

public class MetadataWandCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if ( ! (sender instanceof Player)) return true;
		Player player = (Player) sender;
		
		MetadataWand.giveMetadataWandItem(player);
		
		return true;
	}

	
}
