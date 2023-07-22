package net.punchtree.loquainteractable.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class ItemTags implements PersistentDataType<String, Set<String>>{

	public static final NamespacedKey ITEM_TAGS_KEY = new NamespacedKey(LoquaInteractablePlugin.getInstance(), "ITEM_TAGS");
	public static final ItemTags ITEM_TAGS_TYPE = new ItemTags();
	
	@Override
	public @NotNull Class<String> getPrimitiveType() {
		return String.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public @NotNull Class<Set<String>> getComplexType() {
		return (Class) Set.class;
	}

	@Override
	public String toPrimitive(Set<String> complex, PersistentDataAdapterContext context) {
		return complex.stream().collect(Collectors.joining(";"));
	}

	@Override
	public Set<String> fromPrimitive(String primitive, PersistentDataAdapterContext context) {
		Set<String> tags = new HashSet<>(Arrays.asList(primitive.split(";")));
		return tags;
	}

	public static Set<String> getTags(ItemStack itemStack) {
		if (itemStack == null || !itemStack.hasItemMeta()) {
			return Collections.emptySet();
		}
		
		PersistentDataContainer data = itemStack.getItemMeta().getPersistentDataContainer();
		
		if (!data.has(ITEM_TAGS_KEY, ITEM_TAGS_TYPE)) return Collections.emptySet();
		
		Set<String> tags = data.get(ITEM_TAGS_KEY, ITEM_TAGS_TYPE);
		return tags;
	}
	
	public static boolean hasTag(ItemStack itemStack, String string) {
		return itemStack != null && getTags(itemStack).contains(string.toUpperCase());
//		return itemStack.getType() == Material.CHORUS_FRUIT && itemStack.getItemMeta().getCustomModelData() == 1;
	}

	public static void addTag(@NonNull ItemStack itemStack, String tag) {
		tag = tag.toUpperCase();
		Set<String> tags = new HashSet<>();
		tags.addAll(getTags(itemStack));
		tags.add(tag);

		itemStack.editMeta(im -> {
			PersistentDataContainer data = im.getPersistentDataContainer();
			data.set(ITEM_TAGS_KEY, ITEM_TAGS_TYPE, tags);
		});
	}
	
	public static void removeTag(@NonNull ItemStack itemStack, String tag) {
		tag = tag.toUpperCase();
		Set<String> tags = getTags(itemStack);
		tags.remove(tag);

		itemStack.editMeta(im -> {
			PersistentDataContainer data = im.getPersistentDataContainer();
			data.set(ITEM_TAGS_KEY, ITEM_TAGS_TYPE, tags);
		});
	}

}
