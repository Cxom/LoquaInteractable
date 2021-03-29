package net.punchtree.loquainteractable.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.metadata.MetadataValue;

public class MetadataKeys {

	// This is a single instance wrapper of the list of metadata keys we're using
	
	private static Map<String, Function<MetadataValue, Object>> metadataKeys = new HashMap<>();
	
	public static void registerKey(String metadataKey, Function<MetadataValue, Object> deserializeFunction) {
		if (metadataKeys.containsKey(metadataKey) ) {
			throw new IllegalArgumentException("Trying to register a metadata key that is already registered ("+metadataKey+")! Try using something else!");
		}
		metadataKeys.put(metadataKey, deserializeFunction);
	}
	
	public static void deregisterKey(String metadataKey) {
		metadataKeys.remove(metadataKey);
	}
	
	public static Iterable<Map.Entry<String, Function<MetadataValue, Object>>> keys() {
		return metadataKeys.entrySet();
	}
	
	// TODO value accessor (mapper) methods
	
}
