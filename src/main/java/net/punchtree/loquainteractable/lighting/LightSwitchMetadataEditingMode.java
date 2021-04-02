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
	BlockSelection lightsSelection = new BlockSelection();
	
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
//		player.sendMessage("LightSwitch: Right Click Air");
		if (player.isSneaking()) {
			displayStatus(player, session);
		}
//		else {
//			highlightSelectedLights();
//		}
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		Block block = event.getClickedBlock();
		lightsSelection.toggleSelectBlock(block);
//		highlightSelectedLights();
		
//		String blocksList = lights.stream()
//	 			  .map(b -> String.format("%s:%d,%d,%d", b.getWorld().getName(), b.getX(), b.getY(), b.getZ()))
//	 			  .collect(Collectors.joining("\n"));
		
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
	
//	private void highlightRemovedBlock(Block block) {
//		BlockHighlight bh = new BlockHighlight();
//		bh.diagonalForward = true;
//		bh.diagonalCross = true;
//		bh.steps = 10;
//		bh.setRedstoneParticleColor(Color.RED)
//		  .setParticleSize(0.5f);
//		highlightBlock(block, bh);
		
//		new BukkitRunnable() {
//			public void run() {
//				highlight.setInvisible(true);
//			}
//		}.runTaskLater(LoquaInteractablePlugin.getInstance(), 30);
//	}
	
//	private void highlightSelectedLights() {
//		modelBlockHighlighting.setHighlightItem(HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL);
//		
//		//		for (Block light : lights) {
////			highlightBlock(light, new BlockHighlight().setRedstoneParticleColor(Color.fromRGB(255, 107, 250))
////													  .setParticleSize(0.75f));
////		}
//	}
//	
//	private void highlightBlock(Block block, BlockHighlight bh) {
////		bh.modelHighlightBorder(block);
//		new BukkitRunnable() {
//			int i = 0;
//			public void run() {
//				if (i >= 4) {
//					this.cancel();
//				}
//				++i;
//				bh.particleHighlight(block);
//			}
//		}.runTaskTimerAsynchronously(LoquaInteractablePlugin.getInstance(), 0, 2);
////		new BlockHighlight().setRedstoneParticleColor(color)
////							.setParticleSize(0.75f)
////						    .particleHighlight(block);
//	}
	
}
