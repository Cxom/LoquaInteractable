package net.punchtree.loquainteractable.displayutil;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ArmorStandUtilsTesting implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ARMOR_STAND 
				&& event.getPlayer().isSneaking()
				&& event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WARPED_FUNGUS) {
			event.setCancelled(true);
			if (event.getHand() != EquipmentSlot.HAND) {
				return;
			}
			ArmorStand armorStand = (ArmorStand) event.getRightClicked();
			ArmorStandUtils.inspectArmorStand(event.getPlayer(), armorStand);
		}
	}
	
	@EventHandler
	public void onPlayerStaticPlace(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK 
				&& event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WARPED_FUNGUS) {
			event.setCancelled(true);
			if (event.getHand() != EquipmentSlot.HAND) {
				return;
			}
			ArmorStandUtils.spawnArmorStand(event.getClickedBlock().getLocation(),
					event.getPlayer().isSneaking(),
					ColoredScoreboardTeams.PINK_TEAM,
					HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL);
		}
	}
	
}
