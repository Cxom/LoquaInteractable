package net.punchtree.loquainteractable.lighting;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.punchtree.loquainteractable.metadata.editing.MetadataEditingMode;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;

public class ToggleMetadataEditingMode implements MetadataEditingMode {

	private static final String METADATA_KEY = "Toggle";
	
	private static final String MENU_NAME = "Toggle Blocks";
	private static final List<String> MENU_DESCRIPTION = Arrays.asList("Set up blocks that toggle between states");
	
	@Override
	public ItemStack getMenuItem() {
		return generateDefaultNameAndDescriptionMenuItem(Material.DAYLIGHT_DETECTOR);
	}

	@Override
	public String getName() {
		return MENU_NAME;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		player.sendMessage("Toggle: Right Click Air");
	}

	@Override
	public void onLeftClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		player.sendMessage("Toggle: Right Click Air");
	}

	@Override
	public void onEnterEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("Toggle: Right Click Air");
	}

	@Override
	public void onLeaveEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("Toggle: Right Click Air");
	}

	@Override
	public void displayStatus(Player player, MetadataEditingSession session) {
		player.sendMessage("Toggle: Right Click Air");
	}

}
