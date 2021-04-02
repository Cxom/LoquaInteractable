package net.punchtree.loquainteractable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SendResourcePackOnJoin implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().setResourcePack("http://punchtree.net/resources/gtacraft-dev-rpack.zip", "59A6357BFF92284DCA452DE08C1BC6EDD9D92A1F");
	}
	
}
