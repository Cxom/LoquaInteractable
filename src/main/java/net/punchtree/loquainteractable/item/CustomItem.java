package net.punchtree.loquainteractable.item;


import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;

//public class CustomItem extends ItemStack {
//
//	private final TextComponent displayName;
//	
//	public CustomItem(String itemName, ItemStack itemStack, int customModelData, CustomItemType customItemType) {
//		this(Component.text(itemName).decoration(TextDecoration.ITALIC, false), itemStack, customModelData, customItemType);
//	}
//	
//	public CustomItem(TextComponent displayName, ItemStack itemStack, int customModelData, CustomItemType customItemType) {
//		super(initializeItemMeta(displayName, itemStack, customModelData, customItemType));
//		this.displayName = displayName;
//		
//	}
//	
//	private static ItemStack initializeItemMeta(TextComponent displayName, ItemStack itemStack, int customModelData, CustomItemType customItemType) {
//		itemStack.editMeta(im -> {
//			im.setCustomModelData(customModelData);
//			im.displayName(displayName);
//			im.getPersistentDataContainer().set(CustomItemType.ITEM_TYPE_KEY, PersistentDataType.STRING, customItemType.name());
//		});
//		return itemStack;
//	}
//
//	public String getItemName() {
//		return displayName.content();
//	}
//	
//	public TextComponent getDisplayName() {
//		return displayName;
//	}
//	
//}
