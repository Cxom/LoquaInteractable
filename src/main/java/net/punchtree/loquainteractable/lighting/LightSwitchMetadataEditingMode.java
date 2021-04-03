package net.punchtree.loquainteractable.lighting;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.displayutil.BlockSelection;
import net.punchtree.loquainteractable.metadata.editing.MetadataEditingMode;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;

public class LightSwitchMetadataEditingMode implements MetadataEditingMode {

	private static final String EDITING_MODE_NAME = "Lightswitch";
	private static final List<String> EDITING_MODE_DESCRIPTION = Arrays.asList("Right click lights to add them to the list",
																			   "Shift-left click a lightswitch to apply",
																			   "the list to the switch");
	private final ItemStack MENU_ITEM = generateDefaultNameAndDescriptionMenuItem(Material.SEA_LANTERN);
	
//	private List<Block> lights = new ArrayList<>();
	private BlockSelection lightsSelection = new BlockSelection();
	
	@Override
	public void displayStatus(Player player, MetadataEditingSession session) {
		if (lightsSelection.getSelection().isEmpty()) {
			player.sendMessage(ChatColor.DARK_GRAY + "No lights selected.");
			return;
		}
		player.sendMessage(lightsSelection.getBlocksList(2));
	}

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
		return EDITING_MODE_DESCRIPTION;
	}

	@Override
	public void onRightClickAir(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		if (player.isSneaking()) {
			displayStatus(player, session);
		}
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		lightsSelection.toggleSelectBlock(event.getClickedBlock());
	}

	@Override
	public void onLeftClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
//		player.sendMessage("LightSwitch: Left Click Block");
	}

	@Override
	public MetadataEditingMode getNewInstance() {
		return new LightSwitchMetadataEditingMode();
	}

	@Override
	public void onEnterEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("LightSwitch: Enter Editing Mode");
	}

	@Override
	public void onLeaveEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("LightSwitch: Leave Editing Mode");
		lightsSelection.cleanupDisable();
	}
	
}
