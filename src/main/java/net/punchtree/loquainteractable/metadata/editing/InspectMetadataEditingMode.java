package net.punchtree.loquainteractable.metadata.editing;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.punchtree.loquainteractable.metadata.MetadataApi;
import net.punchtree.loquainteractable.metadata.MetadataKeys;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;

/**
 * This is the default editing mode.
 * It does nothing but allow you to read metadata with right click
 * @author Cxom
 *
 */
public class InspectMetadataEditingMode implements MetadataEditingMode {	
	
	@Override
	public ItemStack getMenuItem() {
		return generateDefaultNameAndDescriptionMenuItem(Material.BARRIER);
	}

	@Override
	public String getName() {
		return "Inspect";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("Right click to inspect metadata");
	}

	@Override
	public void onRightClickAir(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		player.sendMessage("Inspect: Right Click Air");
		// TODO Chat serialization to delete metadata entries
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		player.sendMessage("Inspect: Right Click Block");
		Block block = event.getClickedBlock();
		boolean hasAnyMetadata = false;
		for (Map.Entry<String, Function<Object, Object>> keyEntry: MetadataKeys.keys()) {
			String metadataKey = keyEntry.getKey();
			Function<Object, Object> deserializeFunction = keyEntry.getValue();
			Object metadataValue = MetadataApi.getMetadata(block, metadataKey);
			if (metadataValue != null) {
				hasAnyMetadata = true;
				event.getPlayer().sendMessage(
						ChatColor.GREEN + metadataKey + ": " 
					  + ChatColor.GRAY + deserializeFunction.apply(metadataValue).toString()
				);
			}
		}
		if (!hasAnyMetadata) {
			event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "No metadata.");
		}
	}

	@Override
	public void onLeftClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		player.sendMessage("Inspect: Left Click Block");
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

	@Override
	public MetadataEditingMode getNewInstance() {
		return new InspectMetadataEditingMode();
	}

	@Override
	public void onEnterEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("Inspect: Enter Inspect Editing Mode");
	}

	@Override
	public void onLeaveEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("Inspect: Leave Inspect Editing Mode");
	}

	@Override
	public void displayStatus(Player player, MetadataEditingSession session) {
		// Nothing to display
	}

}
