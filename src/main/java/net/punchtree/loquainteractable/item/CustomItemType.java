package net.punchtree.loquainteractable.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public enum CustomItemType {

	// XXX BE VERY CAREFUL changing names - it will BREAK items without explicit backwards compatibility
	
	DRINK;

	public static final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey(LoquaInteractablePlugin.getInstance(), "item-type");
	
	public static CustomItemType fromItemStack(ItemStack itemStack) {
		if (!itemStack.hasItemMeta()) return null;
		return fromPersistentData(itemStack.getItemMeta());
	}
	
	public static CustomItemType fromPersistentData(@NonNull PersistentDataHolder persistentDataHolder) {
		if (!persistentDataHolder.getPersistentDataContainer().has(ITEM_TYPE_KEY, PersistentDataType.STRING)) return null;
		return fromPersistentDataValue(persistentDataHolder.getPersistentDataContainer().get(ITEM_TYPE_KEY, PersistentDataType.STRING));
	}
	
	public static CustomItemType fromPersistentDataValue(String persistentDataValue) {
		// XXX add legacy mappings for any refactorings here
		try {
			return valueOf(persistentDataValue);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
}
