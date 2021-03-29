package net.punchtree.loquainteractable;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class InteractorListener implements Listener {

	@EventHandler
	public void onBlockInteract(PlayerInteractEvent event) {
		if (!interactsWithBlock(event.getAction())) return;
		
		Block block = event.getClickedBlock();
		BlockState blockState = block.getState();
	}
	
	private boolean interactsWithBlock(Action action) {
		return action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK;
	}
	
}
