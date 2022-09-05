package net.punchtree.loquainteractable.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import net.punchtree.loquainteractable.item.command.CustomItemPersistence;

public class CustomItemRegistry {
	
	// Enforcing sorted facilitates easier use for autocompletion
	// and is low-cost as we expect very few insertions
	private final SortedMap<String, ItemStack> customItemRegistry;
//	private final Map<String, Category> categories;
	
	private CustomItemRegistry(SortedMap<String, ItemStack> customItemRegistry) {
		this.customItemRegistry = customItemRegistry;
	}
	
	public static CustomItemRegistry load() {
		return new CustomItemRegistry(CustomItemPersistence.getInstance().loadCustomItems());
	}
	
	public void save() {
		CustomItemPersistence.getInstance().saveCustomItems(customItemRegistry);
	}
	
	public boolean contains(String name) {
		return customItemRegistry.containsKey(name);
	}

	public void delete(String name) {
		customItemRegistry.remove(name);
	}
	
	public ItemStack get(String name) {
		return customItemRegistry.get(name).clone();
	}
	
	public void add(ItemStack item, String name, String... categories) {
		customItemRegistry.put(name, item.clone());
	}
	
	public List<String> getPartiallyMatchingNames(String arg) {
		List<String> completions = new ArrayList<>();
		StringUtil.copyPartialMatches(arg, customItemRegistry.keySet(), completions);
		return completions;
	}

	public String[] getCategories(String name) {
		return new String[] {};
	}
	
	public List<ItemStack> getSublist(int from, int to) {
		return new ArrayList<>(customItemRegistry.values()).subList(from, to);
	}
	
	public int getSize() {
		return customItemRegistry.size();
	}
	
	

}
