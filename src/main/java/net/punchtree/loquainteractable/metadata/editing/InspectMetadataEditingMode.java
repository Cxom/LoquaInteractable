package net.punchtree.loquainteractable.metadata.editing;

import static net.punchtree.loquainteractable.displayutil.PrintingObjectUtils.formatBlock;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.displayutil.HighlightingItems;
import net.punchtree.loquainteractable.displayutil.ModelBlockHighlight;
import net.punchtree.loquainteractable.metadata.MetadataApi;
import net.punchtree.loquainteractable.metadata.MetadataKeys;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;
import net.punchtree.util.color.MaterialColors;

/**
 * This is the default editing mode.
 * It does nothing but allow you to read metadata with right click
 * @author Cxom
 *
 */
public class InspectMetadataEditingMode implements MetadataEditingMode {	
	
	private int confirmCount = 0;
	private long lastConfirm = System.currentTimeMillis();
	private static final long MAX_CONFIRM_DELAY_MILLIS = 400;
	
	private ModelBlockHighlight radiusHighlighting = new ModelBlockHighlight();
	
	public InspectMetadataEditingMode() {
		radiusHighlighting.setColoredItem(HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL, MaterialColors.CONCRETE_LIME);
	}
	
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
		player.sendMessage("Inspect: Right Click Air: " + (System.currentTimeMillis() - lastConfirm));
		if ((System.currentTimeMillis() - lastConfirm) > MAX_CONFIRM_DELAY_MILLIS) {
			confirmCount = 1;
			lastConfirm = System.currentTimeMillis();
		} else {
			++confirmCount;
			lastConfirm = System.currentTimeMillis();
		}
		if (confirmCount == 3) {
			confirmCount = 0;
			highlightCurrentChunk(player);
		}
	}
	
	private void highlightCurrentChunk(Player player) {
		final int RADIUS = 8;
		Block center = player.getLocation().getBlock();
		player.sendMessage(ChatColor.of(Color.ORANGE) + "Radius of " + RADIUS + " around " + formatBlock(center));
		radiusHighlighting.cleanupDisable();
		Map<Block, Map<String, Object>> radiusMetadata = MetadataApi.getMetadataInRadius(center, RADIUS);
		for (Block block : radiusMetadata.keySet()) {
			radiusHighlighting.highlightIndefinitely(block);
		}
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
		radiusHighlighting.cleanupDisable();
	}

	@Override
	public void displayStatus(Player player, MetadataEditingSession session) {
		// Nothing to display
	}

}
