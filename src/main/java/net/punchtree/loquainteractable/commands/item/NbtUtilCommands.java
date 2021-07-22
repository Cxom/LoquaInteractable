package net.punchtree.loquainteractable.commands.item;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;

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
		net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		if (!nmsItem.hasTag()) return "{}";
		NBTTagCompound tag = nmsItem.getTag();
		return tag.asString();
	}
	
//	public String getStringForCompound(NBTTagCompound tag) {
//	for (String key : tag.getKeys()) {
//		NBTBase nbtbase = tag.get(key);
//		if (nbtbase.get)
//	}
//}
	
}
