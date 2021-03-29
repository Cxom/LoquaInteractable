package net.punchtree.loquainteractable.metadata;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;

import net.punchtree.persistentmetadata.PersistentMetadata;

public class MetadataApi {

	private enum BlockMetadataImplementation {
		BLOCK_METADATA,
		EXTERNAL_DATABASE
	}
	
	private enum EntityMetadataImplementation {
		PERSISTENT_DATA_CONTAINERS,
		EXTERNAL_DATABASE
	}
	
//	private PersistentMetadataImpl impl;
//	
//	MetadataApi(PersistentMetadataImpl impl) {
//		
//	}
	
	public static void setMetadata(Block block, String key, Object value) {
		PersistentMetadata.setMetadata(block, key, value);
	}
	
//	public static <T> T getMetadata(Block block) {	
//		
//	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getMetadata(Block block, String key) {
		return (T) block.getMetadata(key);
	}

	public static void removeMetadata(Block block, String key) {
		PersistentMetadata.removeMetadata(block, key);
	}
	
	
}
