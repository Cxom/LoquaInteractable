package net.punchtree.loquainteractable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SendResourcePackOnJoin implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().setResourcePack("http://punchtree.net/resources/gtacraft-dev-rpack.zip");
	}
	
}
