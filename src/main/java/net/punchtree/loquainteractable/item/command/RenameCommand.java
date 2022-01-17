package net.punchtree.loquainteractable.item.command;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class RenameCommand implements CommandExecutor {

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if ( ! ( sender instanceof Player player )) return false;
		if (args.length < 1) return false;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null) {
			player.sendMessage(ChatColor.RED + "You must be holding an item!");
			return true;
		}
		
		String name = Arrays.stream(args).collect(Collectors.joining(" "));
		final char LEGACY_CHAR = LegacyComponentSerializer.AMPERSAND_CHAR;
		name.replaceAll(LEGACY_CHAR + "_", " ");
		item.editMeta(im -> im.displayName(LegacyComponentSerializer.legacy(LEGACY_CHAR).deserialize(name).decoration(TextDecoration.ITALIC, name.startsWith(LEGACY_CHAR + "o"))));
		player.sendMessage(ChatColor.GREEN + "Renamed item.");
		
		return true;
	}
}
