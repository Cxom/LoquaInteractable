package net.punchtree.loquainteractable.resourcepack;

import org.bukkit.entity.Player;

public class ResourcePackSender {

	// TODO these things should be loaded from the server.properties
	private static final String RESOURCE_PACK_VERSION = "0.2";
	private static final String RESOURCE_PACK_SHA1 = "862A45916C12037EAEE8AB9F26173FDE9632DCDA";
	
	public void sendResourcePack(Player player) {
		player.setResourcePack("http://punchtree.net/resources/gtacraft-dev-rpack_v%s.zip#%s".formatted(RESOURCE_PACK_VERSION, RESOURCE_PACK_SHA1), RESOURCE_PACK_SHA1);
	}
	
}
