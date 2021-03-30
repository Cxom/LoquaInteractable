package net.punchtree.loquainteractable.lighting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.punchtree.loquainteractable.metadata.editing.MetadataEditingMode;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;

public class LightSwitchMetadataEditingMode implements MetadataEditingMode {

	private static final String EDITING_MODE_NAME = "Lightswitch";
	private static final List<String> EDITING_MODE_DESCRIPTION = Arrays.asList("Use this mode to set up light",
																			   "switches with lights they toggle");
	private final ItemStack MENU_ITEM = generateDefaultNameAndDescriptionMenuItem(Material.SEA_LANTERN);
	
	private Set<Block> lights = new HashSet<>();
	
	public void onDisplayStatus() {
		String blocksList = lights.stream()
					 			  .map(b -> String.format("%s:%d,%d,%d", b.getWorld().getName(), b.getX(), b.getY(), b.getZ()))
					 			  .collect(Collectors.joining("\n"));
		
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
		player.sendMessage("LightSwitch: Right Click Air");
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		Block block = event.getClickedBlock();
		if (lights.contains(block)) {
			lights.remove(block);
		} else {
			lights.add(block);
		}
	}

	@Override
	public void onLeftClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		player.sendMessage("LightSwitch: Left Click Block");
	}

	@Override
	public MetadataEditingMode getNewInstance() {
		return new LightSwitchMetadataEditingMode();
	}

	@Override
	public void onEnterEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("LightSwitch: Enter Inspect Editing Mode");
	}

	@Override
	public void onLeaveEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("LightSwitch: Leave Inspect Editing Mode");
	}
	
}
