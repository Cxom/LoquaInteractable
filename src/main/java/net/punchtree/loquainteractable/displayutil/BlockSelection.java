package net.punchtree.loquainteractable.displayutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class BlockSelection {

	private static final SelectionColor DESELECT_COLOR = new SelectionColor(Color.RED);
	
	private ModelBlockHighlight modelBlockHighlighting = new ModelBlockHighlight();	
	private Set<Block> selectedBlocks = new HashSet<>();
	
	private SelectionColor selectionColor = new SelectionColor(255, 85, 255, ChatColor.LIGHT_PURPLE);
			
	public BlockSelection() {
		
	}
	
	public BlockSelection(SelectionColor selectionColor) {
		this.selectionColor = selectionColor;
	}
	
	public Set<Block> getSelection() {
		return selectedBlocks;
	}
	
	public String getBlocksList(int columns) {
		List<Block> lightsList = new ArrayList<>(selectedBlocks);  
		String blocksList = IntStream.range(0, (lightsList.size() + columns - 1) / columns)
				 .mapToObj(i -> lightsList.subList(i * columns, Math.min(columns * (i + 1), lightsList.size())))
				 .map(sublist -> sublist.stream()
						 				.map(b -> String.format("%s[%4d %3d %4d]", b.getWorld().getName(), b.getX(), b.getY(), b.getZ()))
						 				.collect(Collectors.joining("          ")))
				 .collect(Collectors.joining("\n"));
		return blocksList;
	}
	
	public void toggleSelectBlock(Block block) {
		if (selectedBlocks.contains(block)) {
			deselectBlock(block);
		} else {
			selectBlock(block);
		}
	}
	
	public void selectBlock(Block block) {
		if (selectedBlocks.add(block)) {			
			modelBlockHighlighting.setColoredItem(HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL, selectionColor);
			modelBlockHighlighting.highlightIndefinitely(block);
		}
	}
	
	public void deselectBlock(Block block) {
		if (selectedBlocks.remove(block)) {			
			showDeselectAnimation(block);
		}
	}
	
	public void deselectAll() {
		for(Block block : selectedBlocks) {
			showDeselectAnimation(block);
		}
		selectedBlocks.clear();
	}
	
	public void cleanupDisable() {
		selectedBlocks.clear();
		modelBlockHighlighting.cleanupDisable();
	}
	
	private void showDeselectAnimation(Block block) {
		modelBlockHighlighting.removeHighlight(block);
		modelBlockHighlighting.setColoredItem(HighlightingItems.BLOCK_HIGHLIGHT_CROSS_MODEL, DESELECT_COLOR);
		ArmorStand highlight = modelBlockHighlighting.highlightIndefinitely(block);
		ItemStack highlightItem = highlight.getItem(EquipmentSlot.HAND); 
		new BukkitRunnable() {
			public void run() {
				highlight.setItem(EquipmentSlot.HAND, null); 
			}
		}.runTaskLater(LoquaInteractablePlugin.getInstance(), 5);
		new BukkitRunnable() {
			public void run() {
				highlight.setItem(EquipmentSlot.HAND, highlightItem); 
			}
		}.runTaskLater(LoquaInteractablePlugin.getInstance(), 10);
		new BukkitRunnable() {
			public void run() {
				highlight.setItem(EquipmentSlot.HAND, null); 
			}
		}.runTaskLater(LoquaInteractablePlugin.getInstance(), 15);
	}
	
}
