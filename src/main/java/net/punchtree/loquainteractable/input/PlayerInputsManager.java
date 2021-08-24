package net.punchtree.loquainteractable.input;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manages PlayerInputs objects
 * @author Cxom
 *
 */
public class PlayerInputsManager {

	// Don't make this a weakhashmap without also making the uuid member of PlayerInputs a weakreference
	private Map<UUID, PlayerInputs> playerInputsMap = new HashMap<>();
	
	public PlayerInputs getInputsForPlayer(Player player) {
		return getInputsForPlayer(player.getUniqueId());
	}
	
	public PlayerInputs getInputsForPlayer(UUID uuid) {
		return playerInputsMap.get(uuid);
	}
	
	public void initializeInputsForPlayer(UUID uuid) {
		playerInputsMap.put(uuid, new PlayerInputs(uuid));
	}
	
	public void destroyInputsForPlayer(UUID uuid) {
		playerInputsMap.remove(uuid);
	}
	
	// TODO actually run this test (some sort of test command probs)
	public boolean verifyAllPlayersAreOnline() {
		for ( UUID uuid : playerInputsMap.keySet() ) {
			Player p = Bukkit.getPlayer(uuid);
			if ( p == null || !p.isOnline()) {
				return false;
			}
		}
		return true;
	}
	
}
