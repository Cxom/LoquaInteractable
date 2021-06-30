package net.punchtree.loquainteractable.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.punchtree.loquainteractable.input.PlayerInputsManager;

public class PlayerJoinListener implements Listener {

	private final PlayerInputsManager playerInputsManager;

	public PlayerJoinListener(PlayerInputsManager playerInputsManager) {
		this.playerInputsManager = playerInputsManager;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		playerInputsManager.initializeInputsForPlayer(event.getPlayer().getUniqueId());
	}
	
}
