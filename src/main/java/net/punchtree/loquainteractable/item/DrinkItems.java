package net.punchtree.loquainteractable.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class DrinkItems {

	public static final CustomItem COFFEE = new CustomItem("Coffee", new ItemStack(Material.BREAD));
	static {
		COFFEE.editMeta(im -> {
			im.setCustomModelData(1);
			im.displayName(Component.text("Coffee").decoration(TextDecoration.ITALIC, false));
			im.getPersistentDataContainer().set(CustomItemType.ITEM_TYPE_KEY, PersistentDataType.STRING, CustomItemType.DRINK.name());
		});
	}
	
}
