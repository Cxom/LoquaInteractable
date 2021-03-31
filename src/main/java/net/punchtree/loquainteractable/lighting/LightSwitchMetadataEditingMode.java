package net.punchtree.loquainteractable.lighting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.punchtree.loquainteractable.LoquaInteractablePlugin;
import net.punchtree.loquainteractable.displayutil.BlockHighlight;
import net.punchtree.loquainteractable.metadata.editing.MetadataEditingMode;
import net.punchtree.loquainteractable.metadata.editing.session.MetadataEditingSession;

public class LightSwitchMetadataEditingMode implements MetadataEditingMode {

	private static final String EDITING_MODE_NAME = "Lightswitch";
	private static final List<String> EDITING_MODE_DESCRIPTION = Arrays.asList("Right click lights to add them to the list",
																			   "Shift-left click a lightswitch to apply",
																			   "the list to the switch");
	private final ItemStack MENU_ITEM = generateDefaultNameAndDescriptionMenuItem(Material.SEA_LANTERN);
	
	private List<Block> lights = new ArrayList<>();
	
	@Override
	public void displayStatus(Player player, MetadataEditingSession session) {
		highlightSelectedLights();
		if (lights.isEmpty()) {
			player.sendMessage(ChatColor.DARK_GRAY + "No lights selected.");
			return;
		}
		int columns = 2;
		String blocksList = IntStream.range(0, (lights.size() + columns - 1) / columns)
				 .mapToObj(i -> lights.subList(i * columns, Math.min(columns * (i + 1), lights.size())))
				 .map(sublist -> sublist.stream()
						 				.map(b -> String.format("%s[%4d %3d %4d]", b.getWorld().getName(), b.getX(), b.getY(), b.getZ()))
						 				.collect(Collectors.joining("          ")))
				 .collect(Collectors.joining("\n"));
		player.sendMessage(blocksList);
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
		} else {
			highlightSelectedLights();
		}
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent event, Player player, MetadataEditingSession session) {
		Block block = event.getClickedBlock();
		if (lights.contains(block)) {
			lights.remove(block);
			highlightRemovedBlock(block);
		} else {
			lights.add(block);
		}
		highlightSelectedLights();
		
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
		player.sendMessage("LightSwitch: Enter Inspect Editing Mode");
	}

	@Override
	public void onLeaveEditingMode(Player player, MetadataEditingSession session) {
		player.sendMessage("LightSwitch: Leave Inspect Editing Mode");
	}
	
	private void highlightRemovedBlock(Block block) {
		BlockHighlight bh = new BlockHighlight();
		bh.diagonalForward = true;
		bh.diagonalCross = true;
		bh.steps = 10;
		bh.setRedstoneParticleColor(Color.RED)
		  .setParticleSize(0.5f);
		highlightBlock(block, bh);
	}
	
	private void highlightSelectedLights() {
		for (Block light : lights) {
			highlightBlock(light, new BlockHighlight().setRedstoneParticleColor(Color.fromRGB(255, 107, 250))
													  .setParticleSize(0.75f));
		}
	}
	
	private void highlightBlock(Block block, BlockHighlight bh) {
		new BukkitRunnable() {
			int i = 0;
			public void run() {
				if (i >= 4) {
					this.cancel();
				}
				++i;
				bh.particleHighlight(block);
			}
		}.runTaskTimerAsynchronously(LoquaInteractablePlugin.getInstance(), 0, 2);
//		new BlockHighlight().setRedstoneParticleColor(color)
//							.setParticleSize(0.75f)
//						    .particleHighlight(block);
	}
	
}
