package net.punchtree.loquainteractable.metadata.editing.session;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

@Deprecated
public class MetadataEditingSessionManager {

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
	
	public void handlePlayerQuit(@NotNull Player player) {
		MetadataEditingSession session = sessions.remove(player);
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
