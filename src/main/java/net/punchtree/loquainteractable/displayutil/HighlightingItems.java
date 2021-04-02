package net.punchtree.loquainteractable.displayutil;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class HighlightingItems {

	public static final ItemStack BLOCK_HIGHLIGHT_BORDER_MODEL = new ItemStack(Material.LEATHER_HELMET);
	static {
		LeatherArmorMeta lm = (LeatherArmorMeta) BLOCK_HIGHLIGHT_BORDER_MODEL.getItemMeta();
		lm.setColor(Color.fromRGB(255, 85, 255));
		lm.setCustomModelData(300);
		BLOCK_HIGHLIGHT_BORDER_MODEL.setItemMeta(lm);
	}
	
	public static final ItemStack BLOCK_HIGHLIGHT_CROSS_MODEL = new ItemStack(Material.LEATHER_HELMET);
	static {
		LeatherArmorMeta lm = (LeatherArmorMeta) BLOCK_HIGHLIGHT_CROSS_MODEL.getItemMeta();
		lm.setColor(Color.RED);
		lm.setCustomModelData(301);
		BLOCK_HIGHLIGHT_CROSS_MODEL.setItemMeta(lm);
	}
	
	public static final ItemStack OPEN_SIGN = new ItemStack(Material.LEATHER_HELMET);
	static {
		LeatherArmorMeta lm = (LeatherArmorMeta) OPEN_SIGN.getItemMeta();
		lm.setColor(Color.fromRGB(255, 255, 255));
		lm.setCustomModelData(400);
		OPEN_SIGN.setItemMeta(lm);
	}
	
}
