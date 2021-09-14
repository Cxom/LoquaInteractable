package net.punchtree.loquainteractable.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class DrinkItemListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!isRightClick(event.getAction())) return;
		if (!isDrink(event.getItem())) return;
		
		onStartDrinking(event.getPlayer(), event.getItem());
	}
	
	public boolean isRightClick(Action action) {
		return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if ( ! isDrink(item) ) return;
		// Cancel any effects of whatever item we're overriding
		event.setCancelled(true);
		// Also default to the item going away (no replacement, e.g. remove the empty bucket you're returned for milk)
		event.setReplacement(null);
		
		onFinishDrinking(player, item);
	}

	public boolean isDrink(ItemStack itemStack) {
		return CustomItemType.fromItemStack(itemStack) == CustomItemType.DRINK;
	}
	
	public void onStartDrinking(Player player, ItemStack item) {
//		player.sendMessage(Component.text("Started drinking a ").append(item.getItemMeta().displayName()));
	}
	
	public void onFinishDrinking(Player player, ItemStack item) {
//		player.sendMessage(Component.text("Drunk a ").append(item.getItemMeta().displayName()));
	}

}
