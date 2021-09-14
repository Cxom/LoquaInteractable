package net.punchtree.loquainteractable.item;

import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

public class CustomItem extends ItemStack {

	private final Component customName;
	
	public CustomItem(String customName, ItemStack itemStack) {
		this(Component.text(customName), itemStack);
	}
	
	public CustomItem(Component customName, ItemStack itemStack) {
		super(itemStack);
		this.customName = customName;
	}

	public final Component getCustomName() {
		return customName;
	}
	
}
