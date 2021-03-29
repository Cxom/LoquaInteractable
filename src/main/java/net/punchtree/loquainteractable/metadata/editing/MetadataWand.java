package net.punchtree.loquainteractable.metadata.editing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import net.punchtree.loquainteractable.metadata.MetadataKeys;

// TODO is this more a session manager class?
public class MetadataWand implements Listener {
	
	private static final ItemStack METADATA_WAND_ITEM = new ItemStack(Material.BLAZE_ROD);
	static {
		ItemMeta im = METADATA_WAND_ITEM.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Metadata Wand");
		im.setLore(Arrays.asList("Right click to inspect",
								 "Left click to edit"));
		METADATA_WAND_ITEM.setItemMeta(im);
	}
	
	private static boolean isMetadataWandItem(ItemStack itemStack) {
		return METADATA_WAND_ITEM.equals(itemStack);
	}
	
	public static void giveMetadataWandItem(Player player) {
		player.getInventory().addItem(METADATA_WAND_ITEM.clone());
	}
	
	enum Mode {
		INSPECT,
		EDIT
	}
	
	private static Set<MetadataEditingMode> editingModes = new HashSet<>();
	
	public static void addEditingMode(MetadataEditingMode editingMode) {
		editingModes.add(editingMode);
	}
	
	
	
	@EventHandler
	public void onInteractWithWand(PlayerInteractEvent event) {
		// TODO tie to sessions
		if (!isMetadataWandItem(event.getItem())) return;
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			onInspect(event);
		} else if (event.getAction() == Action.LEFT_CLICK_AIR) {
			onSelectWritingMode();
		} else if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			onEdit(event);
		}
	}

	private void onInspect(PlayerInteractEvent event) {
		event.setCancelled(true);
		Block block = event.getClickedBlock();
		boolean hasAnyMetadata = false;
		for (Map.Entry<String, Function<MetadataValue, Object>> keyEntry: MetadataKeys.keys()) {
			String metadataKey = keyEntry.getKey();
			Function<MetadataValue, Object> deserializeFunction = keyEntry.getValue();
			if (block.hasMetadata(metadataKey)) {
				hasAnyMetadata = true;
				event.getPlayer().sendMessage(
						ChatColor.GREEN + metadataKey + ": " 
						+ ChatColor.GRAY + block.getMetadata(metadataKey).stream()
																		 .map(deserializeFunction)
																		 .map(Object::toString)
																		 .collect(Collectors.joining("\n| | | |"))
				);
			}
		}
		if (!hasAnyMetadata) {
			event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "No metadata.");
		}
	}
	
	private void onSelectWritingMode() {
		
	}
	
	private void onEdit(PlayerInteractEvent event) {
		
	}
	
}
