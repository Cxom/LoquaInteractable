package net.punchtree.loquainteractable.commands.item;

import net.kyori.adventure.text.Component;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
	
	
	public Component getItemNbtString(ItemStack item) {
		net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		// TODO make this useful for components!!!!!
		return item.displayName();
//		if (!nmsItem.hasTag()) return "{}";
//		CompoundTag tag = nmsItem.getTag();
//		return tag.getAsString();
	}
	
//	public String getStringForCompound(NBTTagCompound tag) {
//	for (String key : tag.getKeys()) {
//		NBTBase nbtbase = tag.get(key);
//		if (nbtbase.get)
//	}
//}
	
}
