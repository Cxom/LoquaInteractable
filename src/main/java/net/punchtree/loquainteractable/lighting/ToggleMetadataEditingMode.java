package net.punchtree.loquainteractable.lighting;

import static net.punchtree.loquainteractable.displayutil.PrintingObjectUtils.formatBlock;
import static net.punchtree.loquainteractable.displayutil.PrintingObjectUtils.formatMaterial;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.displayutil.BlockSelection;
import net.punchtree.loquainteractable.displayutil.HighlightingItems;
import net.punchtree.loquainteractable.displayutil.ModelBlockHighlight;
import net.punchtree.loquainteractable.displayutil.SelectionColor;
import net.punchtree.loquainteractable.metadata.MetadataApi;
import net.punchtree.loquainteractable.metadata.editing.MetadataEditingMode;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;

public class ToggleMetadataEditingMode implements MetadataEditingMode {
	
	private static final String EDITING_MODE_NAME = "Toggle Blocks";
	private static final List<String> MENU_DESCRIPTION = Arrays.asList("Set up blocks that toggle between states");
	
	private final ItemStack MENU_ITEM = generateDefaultNameAndDescriptionMenuItem(Material.DAYLIGHT_DETECTOR);
	
	private BlockSelection togglingBlocksSelection = new BlockSelection(SelectionColor.CONCRETE_LIGHT_BLUE);
	
	private static final SelectionColor PRIMARY_COLOR = SelectionColor.DARK_PURPLE;
	private static final SelectionColor SECONDARY_COLOR = SelectionColor.LIGHT_PURPLE;
	private static final SelectionColor TO_CONFIRM_COLOR = SelectionColor.CONCRETE_ORANGE;

	private class ToggleSelectionIndicator {
		private ModelBlockHighlight modelBlockHighlighting = new ModelBlockHighlight();
		private Block primary;
		private Block secondary;
		private Block toConfirm;

		private void setPrimary(Block newPrimary, Player editor) {
			if (primary != null) {
				modelBlockHighlighting.removeHighlight(primary);
			}
			if (newPrimary != null && newPrimary.equals(secondary)) {
				setSecondary(null, editor);
			}
			primary = newPrimary;
			if (primary != null) {
				modelBlockHighlighting.setColoredItem(HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL, PRIMARY_COLOR)
									  .highlightIndefinitely(primary);
				editor.sendMessage(PRIMARY_COLOR  + "Primary material set to " + formatMaterial(selection.primary.getType()));
			} else {
				editor.sendMessage(PRIMARY_COLOR  + "Primary material cleared");
			}
			sendBothMaterialsClearedMessageIfApplicable(editor);
		}
		
		private void setSecondary(Block newSecondary, Player editor) {
			if (secondary != null) {
				modelBlockHighlighting.removeHighlight(secondary);
			}
			if (newSecondary != null && newSecondary.equals(primary)) {
				setPrimary(null, editor);
			}
			secondary = newSecondary;
			if (secondary != null) {
				modelBlockHighlighting.setColoredItem(HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL, SECONDARY_COLOR)
									  .highlightIndefinitely(secondary);
				editor.sendMessage(SECONDARY_COLOR + "Secondary material set to " + formatMaterial(selection.secondary.getType()));
			} else {
				editor.sendMessage(SECONDARY_COLOR  + "Secondary material cleared");
			}
			sendBothMaterialsClearedMessageIfApplicable(editor);
		}
		
		private void sendBothMaterialsClearedMessageIfApplicable(Player editor) {
			if (primary == null && secondary == null) {
				editor.sendMessage(ChatColor.GRAY + "Both materials are cleared - applying now will remove metadata");				
			}
		}
		
		private void setToConfirm(Block newToConfirm) {
			if (toConfirm != null) {
				modelBlockHighlighting.removeHighlight(toConfirm);
			}
			toConfirm = newToConfirm;
			if (toConfirm != null) {
				modelBlockHighlighting.setColoredItem(HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL, TO_CONFIRM_COLOR)
									  .highlightIndefinitely(toConfirm);
			} else {
				// TODO?????????
			}
		}
		
		private void cleanupDisable() {
			modelBlockHighlighting.cleanupDisable();
		}
	}
	
	private ToggleSelectionIndicator selection = new ToggleSelectionIndicator();
	
	@Override
	public ItemStack getMenuItem() {
		return MENU_ITEM;
	}

	@Override
	public String getName() {
		return EDITING_MODE_NAME;
	}

	@Override
	public List<String> getDescription() {
		return MENU_DESCRIPTION;
	}

	@Override
	public MetadataEditingMode getNewInstance() {
		return new ToggleMetadataEditingMode();
	}

	@Override
	public void onRightClickAir(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		selection.setToConfirm(null);
		if (player.isSneaking()) {
			selection.setSecondary(null, player);
		} else {			
			selection.setPrimary(null, player);
		}
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
//		 togglingBlocksSelection.toggleSelectBlock(event.getClickedBlock());
		selection.setToConfirm(null);
		if (player.isSneaking()) {
			selection.setSecondary(event.getClickedBlock(), player);
		} else {			
			selection.setPrimary(event.getClickedBlock(), player);
		}
	}

	// TODO Refactor into lots of well-named helper methods
	@Override
	public void onLeftClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		Block block = event.getClickedBlock();
		// If sneaking, just inspect
		if (player.isSneaking()) {
			player.sendMessage(ChatColor.GREEN + ToggleBlockApi.TOGGLE_BLOCK_METADATA_KEY + ": " + MetadataApi.getMetadata(block, ToggleBlockApi.TOGGLE_BLOCK_METADATA_KEY));
			return;
		}
		// If not sneaking, apply
		if (selection.primary != null && selection.secondary == null
				|| selection.primary == null & selection.secondary != null) {
			player.sendMessage(ChatColor.RED + "Exactly one material is cleared. Either clear both to remove metadata or select both to set toggles.");
			return;
		}
		if (selection.toConfirm == null || !selection.toConfirm.equals(block)) {
			if (block.equals(selection.primary) || block.equals(selection.secondary)) {
				player.sendMessage(ChatColor.RED + "For safety, you can't apply metadata to your selected primary or secondary blocks.");
				return;
			}
			if (selection.primary == null && selection.secondary == null) {
				Object potentialMetadata = MetadataApi.getMetadata(block, ToggleBlockApi.TOGGLE_BLOCK_METADATA_KEY);
				if (potentialMetadata == null) {
					player.sendMessage(ChatColor.DARK_GRAY + "This block has no " + ChatColor.ITALIC + ToggleBlockApi.TOGGLE_BLOCK_METADATA_KEY + ChatColor.RESET + ChatColor.DARK_GRAY + " metadata.");
					return;
				}
			}
			selection.setToConfirm(block);
			player.sendMessage(ChatColor.GRAY + "Selected block at " + ChatColor.YELLOW + formatBlock(selection.toConfirm) + ChatColor.GRAY + ". Click again to " + ChatColor.GOLD + getApplicationString());
			return;
		}
		if (selection.primary == null && selection.secondary == null) {
			// That there is existing metadata is checked when selecting the block for the first time (before confirming)
			String existingMetadata = (String) MetadataApi.getMetadata(block, ToggleBlockApi.TOGGLE_BLOCK_METADATA_KEY);
			MetadataApi.removeMetadata(block, ToggleBlockApi.TOGGLE_BLOCK_METADATA_KEY);
			player.sendMessage(ChatColor.RED + "Removed metadata (" + ChatColor.DARK_GRAY + existingMetadata + ChatColor.RED + ").");
		
		} else if (selection.primary != null && selection.secondary != null) {
			String metadataValue = generateMetadataValue();
			MetadataApi.setMetadata(block, ToggleBlockApi.TOGGLE_BLOCK_METADATA_KEY, metadataValue);
			player.sendMessage(ChatColor.GREEN + "Set metadata (" + ChatColor.GRAY + String.format(PRIMARY_COLOR  + "%s" + ChatColor.GRAY + ":" + SECONDARY_COLOR + "%s", selection.primary.getType().name(), selection.secondary.getType().name()) + ChatColor.GREEN + ").");
		} else {
			player.sendMessage(ChatColor.RED + "An error occured (bad programmer)");
		}
		selection.setToConfirm(null);
	}
	
	private String generateMetadataValue() {
		if (selection.primary == null || selection.secondary == null) {
			throw new IllegalStateException("Neither primary nor secondary can be null when generating metadata value!");
		}
		// TODO support blockdata;
		return String.format("%s:%s", selection.primary.getType().name(), selection.secondary.getType().name());
	}
	
	private String getApplicationString() {
		if (selection.primary == null && selection.secondary == null) {
			return "remove Toggle metadata";
		} else if (selection.primary != null && selection.secondary != null) {
			return "apply " + PRIMARY_COLOR  + "Primary: " + ChatColor.GRAY + formatMaterial(selection.primary.getType())
							+ " | "
							+ SECONDARY_COLOR  + "Secondary: " + ChatColor.GRAY + formatMaterial(selection.secondary.getType());
		} else {
			return "do nothing??";
		}
	}
	
	@Override
	public void onEnterEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("Toggle: Enter Editing Mode");
	}

	@Override
	public void onLeaveEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("Toggle: Leave Editing Mode");
//		togglingBlocksSelection.cleanupDisable();
		selection.cleanupDisable();
	}

	@Override
	public void displayStatus(Player player, MetadataEditingSession session) {
		if (togglingBlocksSelection.getSelection().isEmpty()) {
			player.sendMessage(ChatColor.DARK_GRAY + "No lights selected.");
			return;
		}
		player.sendMessage(togglingBlocksSelection.getBlocksList(2));
	}

}
