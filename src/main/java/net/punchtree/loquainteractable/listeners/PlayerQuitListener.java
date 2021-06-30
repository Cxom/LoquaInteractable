package net.punchtree.loquainteractable.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.punchtree.loquainteractable.input.PlayerInputsManager;

public class PlayerQuitListener implements Listener {

	private final PlayerInputsManager playerInputsManager;

	public PlayerQuitListener(PlayerInputsManager playerInputsManager) {
		this.playerInputsManager = playerInputsManager;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		playerInputsManager.destroyInputsForPlayer(event.getPlayer().getUniqueId());
	}
	
}
