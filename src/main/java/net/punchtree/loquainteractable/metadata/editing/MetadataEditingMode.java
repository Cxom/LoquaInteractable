package net.punchtree.loquainteractable.metadata.editing;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;

public interface MetadataEditingMode {

	ItemStack getMenuItem();

	String getName();
	
	List<String> getDescription();

	MetadataEditingMode getNewInstance();
	
	// Things you can do all the time??
	// Open a menu
	// Select blocks
	// Select entities
	
	default ItemStack generateDefaultNameAndDescriptionMenuItem(Material material) {
		return generateDefaultNameAndDescriptionMenuItem(new ItemStack(material));
	}
	
	default ItemStack generateDefaultNameAndDescriptionMenuItem(ItemStack baseItem) {
		ItemMeta im = baseItem.getItemMeta();
		im.setDisplayName(ChatColor.RESET + getName());
		im.setLore(getDescription());
		baseItem.setItemMeta(im);
		return baseItem;
	}
	
	static ItemStack generateMenuItem(String name, List<String> description, Material material) {
		ItemStack baseItem = new ItemStack(material);
		ItemMeta im = baseItem.getItemMeta();
		im.setDisplayName(ChatColor.RESET + name);
		im.setLore(description);
		baseItem.setItemMeta(im);
		return baseItem;
	}

	void onRightClickAir(PlayerInteractEvent event, Player player, MetadataEditingSession session);
	void onRightClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session);
	void onLeftClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session);

	void onEnterEditingMode(Player player, MetadataEditingSession session);
	void onLeaveEditingMode(Player player, MetadataEditingSession session);
	
}
