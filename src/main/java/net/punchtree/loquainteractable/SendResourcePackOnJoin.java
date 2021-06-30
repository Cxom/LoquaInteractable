package net.punchtree.loquainteractable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SendResourcePackOnJoin implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().setResourcePack("http://punchtree.net/resources/gtacraft-dev-rpack.zip", "862A45916C12037EAEE8AB9F26173FDE9632DCDA");
	}
	
}
