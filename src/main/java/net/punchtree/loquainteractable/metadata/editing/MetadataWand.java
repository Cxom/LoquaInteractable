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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.punchtree.loquainteractable.lighting.LightSwitchMetadataEditingMode;
import net.punchtree.loquainteractable.metadata.MetadataApi;
import net.punchtree.loquainteractable.metadata.MetadataKeys;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSessionManager;

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
	
	private static final Action INSPECT_ACTION = Action.RIGHT_CLICK_BLOCK;
	private static final Action OPEN_EDIT_MODE_MENU_ACTION = Action.LEFT_CLICK_AIR;
	private static final Action APPLY_EDIT_MODE_ACTION = Action.LEFT_CLICK_BLOCK;
	
	private static Set<MetadataEditingMode> editingModes = new HashSet<>();
	static {
		editingModes.add(new InspectMetadataEditingMode());
		editingModes.add(new LightSwitchMetadataEditingMode());
	}
	
	public static void addEditingMode(MetadataEditingMode editingMode) {
		editingModes.add(editingMode);
	}
	
	MetadataEditingModeMenu editModeMenu;
	
	@EventHandler
	public void onInteractWithWand(PlayerInteractEvent event) {
		// TODO tie to sessions
		if (event.getHand() != EquipmentSlot.HAND || !isMetadataWandItem(event.getItem())) return;
		event.setCancelled(true);
		
		MetadataEditingSession session = MetadataEditingSessionManager.getSessionFor(event.getPlayer());
		MetadataEditingMode editingMode = session.getEditingMode();
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			editingMode.onRightClickAir(event, event.getPlayer(), session);
//			onInspect(event);
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			editingMode.onRightClickBlock(event, event.getPlayer(), session);
		} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			editingMode.onLeftClickBlock(event, event.getPlayer(), session);
//			onInspectAllRaw(event);
		} else if (event.getAction() == OPEN_EDIT_MODE_MENU_ACTION) {
			onOpenEditModeMenu(event.getPlayer());
		}
		
//		else if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
//			onEdit(event);
//		}
	}

	private void onInspectAllRaw(PlayerInteractEvent event) {
		event.setCancelled(true);
		Map<String, Object> allMetadata = MetadataApi.getMetadata(event.getClickedBlock());
		if (allMetadata.isEmpty()) {
			event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "No metadata.");
			return;
		}
		event.getPlayer().sendMessage(allMetadata.entrySet().stream()
															.map(entry -> ChatColor.GREEN + entry.getKey() + ": " 
																		+ ChatColor.GRAY + entry.getValue().toString())
															.collect(Collectors.joining("\n")));
	}
	
	private void onInspect(PlayerInteractEvent event) {
		event.setCancelled(true);
		Block block = event.getClickedBlock();
		boolean hasAnyMetadata = false;
		for (Map.Entry<String, Function<Object, Object>> keyEntry: MetadataKeys.keys()) {
			String metadataKey = keyEntry.getKey();
			Function<Object, Object> deserializeFunction = keyEntry.getValue();
			if (block.hasMetadata(metadataKey)) {
				hasAnyMetadata = true;
				event.getPlayer().sendMessage(
						ChatColor.GREEN + metadataKey + ": " 
					  + ChatColor.GRAY + deserializeFunction.apply(MetadataApi.getMetadata(block, metadataKey)).toString()
						
//						block.getMetadata(metadataKey).stream()
//																		 .map(deserializeFunction)
//																		 .map(Object::toString)
//																		 .collect(Collectors.joining("\n| | | |"))
				);
			}
		}
		if (!hasAnyMetadata) {
			event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "No metadata.");
		}
	}
	
	private void onOpenEditModeMenu(Player player) {
		if (editModeMenu == null) {
			editModeMenu = new MetadataEditingModeMenu(editingModes);
		}
		editModeMenu.showTo(player);
	}
	
	private void onEdit(PlayerInteractEvent event) {
		
	}
	
}
