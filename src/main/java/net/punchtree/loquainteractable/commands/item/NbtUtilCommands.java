package net.punchtree.loquainteractable.commands.item;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		CompoundTag tag = nmsItem.getTag();
		return tag.getAsString();
	}
	
//	public String getStringForCompound(NBTTagCompound tag) {
//	for (String key : tag.getKeys()) {
//		NBTBase nbtbase = tag.get(key);
//		if (nbtbase.get)
//	}
//}
	
}
