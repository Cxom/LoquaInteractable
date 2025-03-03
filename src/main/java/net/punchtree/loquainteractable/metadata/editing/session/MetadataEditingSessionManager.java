package net.punchtree.loquainteractable.metadata.editing.session;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.WeakHashMap;

@Deprecated
public class MetadataEditingSessionManager implements Listener {

	private MetadataEditingSessionManager() {
	}
	
	private static final MetadataEditingSessionManager instance = new MetadataEditingSessionManager();
	public static MetadataEditingSessionManager getInstance() {
		return instance;
	}
	
	private static Map<Player, MetadataEditingSession> sessions = new WeakHashMap<>();
	
	public static MetadataEditingSession getSessionFor(Player player) {
		if (!sessions.containsKey(player)) {
			sessions.put(player, new MetadataEditingSession(player));
		}
		return sessions.get(player);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		MetadataEditingSession session = sessions.remove(event.getPlayer());
		if (session != null) {			
			session.cleanupDisable();
		}
	}

	public static void cleanupSessions() {
		for(MetadataEditingSession session : sessions.values()) {
			session.cleanupDisable();
		}
		sessions.clear();
	}
	
}
