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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.punchtree.util.color.ColoredScoreboardTeams;

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
	
	private static final ItemStack ROAD_TEST_BLOCK = new ItemStack(Material.NETHERITE_SCRAP);
	static {
		ItemMeta im = ROAD_TEST_BLOCK.getItemMeta();
		im.setCustomModelData(200);
		ROAD_TEST_BLOCK.setItemMeta(im);
	}
	
	@EventHandler
	public void onPlayerStaticPlace(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK 
				&& (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WARPED_FUNGUS
						|| event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_SCRAP)) {
			event.setCancelled(true);
			if (event.getHand() != EquipmentSlot.HAND) {
				return;
			}
			Material type = event.getPlayer().getInventory().getItemInMainHand().getType();
			ArmorStandUtils.spawnArmorStand(event.getClickedBlock().getLocation(),
					event.getPlayer().isSneaking(),
					ColoredScoreboardTeams.WHITE_TEAM,
					type == Material.WARPED_FUNGUS ? HighlightingItems.BLOCK_HIGHLIGHT_BORDER_MODEL
							: ROAD_TEST_BLOCK);
		}
	}
	
}
