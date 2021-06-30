package net.punchtree.loquainteractable.commands.item;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.NBTTagCompound;

public class NbtUtilCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if ( ! (sender instanceof Player)) return true;
		Player player = (Player) sender;
		
		if ("getnbt".equalsIgnoreCase(label)) {
			player.sendMessage(getItemNbtString(player.getInventory().getItemInMainHand()));
			return true;
		}

		return true;
	}
	
	
	public String getItemNbtString(ItemStack item) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		if (!nmsItem.hasTag()) return "{}";
		NBTTagCompound tag = nmsItem.getTag();
		IChatBaseComponent component = tag.getNbtPrettyComponent();
		return component.getString();
	}
	
//	public String getStringForCompound(NBTTagCompound tag) {
//	for (String key : tag.getKeys()) {
//		NBTBase nbtbase = tag.get(key);
//		if (nbtbase.get)
//	}
//}
	
}
