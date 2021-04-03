package net.punchtree.loquainteractable.lighting;

import org.bukkit.block.Block;

import net.punchtree.loquainteractable.metadata.MetadataApi;

public class ToggleBlockApi {

	public static final String TOGGLE_BLOCK_METADATA_KEY = "Toggle";
	
	private static void toggleBlock(Block toggleBlock) {
		Object potentialMetadata = MetadataApi.getMetadata(toggleBlock, TOGGLE_BLOCK_METADATA_KEY);
		if (potentialMetadata == null) return;
		String metadataValue = (String) potentialMetadata;
//		metadataValue.
//		Material primary = 
	}
	
}
