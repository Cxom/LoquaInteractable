package net.punchtree.loquainteractable.commands.item;


import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class SetLeatherColorCommand implements CommandExecutor {

	private static final String ERROR = ChatColor.RED + "";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if ( ! (sender instanceof Player player)) return true;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null) {
			player.sendMessage(Component.text(ERROR + "You must be holding an item!"));
			return true;
		}
		if ( ! (item.getItemMeta() instanceof LeatherArmorMeta leatherMeta)) {
			player.sendMessage(Component.text(ERROR + "This item does not have leather armor meta!"));
			return true;
		}
		int r = 0, g = 0, b = 0;
		try {
			r = Math.max(0, Math.min(255, Integer.parseInt(args[0])));
			g = Math.max(0, Math.min(255, Integer.parseInt(args[1])));
			b = Math.max(0, Math.min(255, Integer.parseInt(args[2])));
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			return false;
		}
		leatherMeta.setColor(Color.fromRGB(r, g, b));
		item.setItemMeta(leatherMeta);

		return true;
	}
	
}
