package net.punchtree.loquainteractable.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.punchtree.loquainteractable.LoquaInteractablePlugin;
import net.punchtree.loquainteractable.player.PlayerMapping;
import net.punchtree.loquainteractable.status.Status;
import net.punchtree.loquainteractable.status.StatusReceiver;
import net.punchtree.loquainteractable.status.StatusType;

public class DrinkItemListener extends PacketAdapter implements Listener {

	private static final int DRINKING_SOUND_PERIOD_TICKS = 5;
	private static final int DRINKING_SOUND_REPEAT_AMOUNT = 7;
	
	// Could use one set of players and a persistent singular task to play sounds,
	// But at that point, we'd be better off with a per-player abstraction for sound pre-scheduling 
	// and cancelling if need be (say on logout). Drinking sounds aren't common, but sounds will be
	
	private final PlayerMapping<? extends StatusReceiver> statusReceiverMapping;
	
	private Map<Player, BukkitTask> drinkingSoundsForPlayers = new HashMap<>();
	
	public DrinkItemListener(Plugin plugin, ProtocolManager protocolManager, PlayerMapping<? extends StatusReceiver> statusReceiverMapping) {
		super(plugin, ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.BLOCK_DIG });
		protocolManager.addPacketListener(this);
		this.statusReceiverMapping = statusReceiverMapping;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (!isRightClick(event.getAction())) return;
		if (!isDrink(item)) return;
		if (!canEat(player, item)) return;
		
		onStartDrinking(player, item);
	}
	
	public static boolean isRightClick(Action action) {
		return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
	}
	
	public static boolean canEat(Player player, ItemStack item) {
		return player.getFoodLevel() < 20 || item.getType() == Material.CHORUS_FRUIT;
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if ( ! isDrink(item) ) return;
		// Cancel any effects of whatever item we're overriding (does not cancel food level changing - see FoodLevelChangeEvent (listener below))
		event.setCancelled(true);
		
		if ( player.getGameMode() != GameMode.CREATIVE ) {
			// Not entirely sure why this works because the ItemStack object we get back from the inventory has a new reference each time it's fetched (so not the same object)
			// maybe it replaces the instance tied to the inventory internally whenever you fetch it?
			ItemStack raisedItem = player.getInventory().getItem(player.getHandRaised());
			raisedItem.setAmount(raisedItem.getAmount()-1);
			// For some reason, chorus fruit food level is not cancellable (doesn't even trigger FoodLevelChangeEvent)
			// so just hackily reverse it quickly if we ate it
			reverseChorusFruitFoodLevel(player, item);
		}
		
		onFinishDrinking(player, item);
	}

	private void reverseChorusFruitFoodLevel(Player player, ItemStack item) {
		if (item.getType() == Material.CHORUS_FRUIT) {
			// Magic numbers are chorus fruit stats
			int foodToRemove = Math.min(4, 20 - player.getFoodLevel());
			float saturationToRemove = Math.min(2.4f, player.getSaturation());
			new BukkitRunnable() {
				public void run() {
					player.setFoodLevel(player.getFoodLevel() - foodToRemove);
					player.setSaturation(player.getSaturation() - saturationToRemove);
				}
			}.runTaskLater(LoquaInteractablePlugin.getInstance(), 2);
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		// Leaving dead code here because this should work if Chorus Fruit triggers FoodLevelChangeEvent. It doesn't, but that seems like a bug
//		if ( ! (event.getEntity() instanceof Player player)) {
//			Bukkit.broadcastMessage("Not a player food level change");
//			return;
//		}
//		ItemStack item = event.getItem();
//		if ( isDrink(item) ) {
//			Bukkit.getServer().sendMessage(Component.text(player.getName() + ": Cancelling food level change - ").append(item.displayName()));
//			event.setCancelled(true);  
//		} else {
//			Bukkit.getServer().sendMessage(Component.text(player.getName() + ": Not cancelling food level change - "));
//		}
//		
//		if ( player.getGameMode() != GameMode.CREATIVE ) {
//			player.getInventory().remove(item);
//			Bukkit.getServer().sendMessage(Component.text(player.getName() + ": removing item"));
//		} else {
//			Bukkit.getServer().sendMessage(Component.text(player.getName() + ": not removing - creative"));
//		}
		
	}

	public boolean isDrink(ItemStack itemStack) {
		return ItemTags.hasTag(itemStack, "DRINKABLE");
	}
	
//	public void spawnItemParticles(Player player, ItemStack item, int amount) {	
//		Random random = new Random();
//		
//		float xRot = player.getLocation().getPitch();
//		float yRot = player.getLocation().getYaw();
//		double x = player.getLocation().getX();
//		double eyeY = player.getEyeLocation().getY();
//		double z = player.getLocation().getZ();
//		for(int i = 0; i < amount; ++i) {
//	         Vec3 vec3 = new Vec3(((double) random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
//	         vec3 = vec3.xRot(-xRot * ((float)Math.PI / 180F));
//	         vec3 = vec3.yRot(-yRot * ((float)Math.PI / 180F));
//	         double d0 = (double)(-random.nextFloat()) * 0.6D - 0.3D;
//	         Vec3 vec31 = new Vec3(((double)random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
//	         vec31 = vec31.xRot(-xRot * ((float)Math.PI / 180F));
//	         vec31 = vec31.yRot(-yRot * ((float)Math.PI / 180F));
//	         vec31 = vec31.add(x, eyeY, z);
//	         CraftWorld craftWorld = (CraftWorld) player.getLocation().getWorld();
//	         ServerLevel level = craftWorld.getHandle();
//	         player.getWorld().spawnParticle(Particle.ITEM_CRACK, vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05D, vec3.z, DebugVars.getDecimal("drinking_particles_speed", 0.0), item);
//	         level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, CraftItemStack.asNMSCopy(item)), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
//	    }
//	}
	
	public void onStartDrinking(Player player, ItemStack item) {
		int initDelayTicks = DRINKING_SOUND_PERIOD_TICKS;
		
		BukkitTask drinkingSoundsTask = new BukkitRunnable() {
			int i = DRINKING_SOUND_REPEAT_AMOUNT;
			Random random = new Random();
			public void run() {
				if (i <= 0 || player.getHandRaisedTime() == 0 || !player.isOnline()) {
					this.cancel();
					drinkingSoundsForPlayers.remove(player);
					return;
				}
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.5F + 0.5F * (float) random.nextInt(2), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
				--i;
				
			}
		}.runTaskTimer(LoquaInteractablePlugin.getInstance(), initDelayTicks, DRINKING_SOUND_PERIOD_TICKS);
		drinkingSoundsForPlayers.put(player, drinkingSoundsTask);
	}
	
	public void onFinishDrinking(Player player, ItemStack item) {
		assert(isDrink(item));
		
		// Placeholder for future behavior
		player.sendMessage(Component.text("Drunk a ").append(item.getItemMeta().displayName()));
		applyCaffeinatedStatusEffect(player);
	}
	
	
	private static final int MINUTES = 60;
	private static final int COFFEE_CAFFEINATION_DURATION_SECONDS = 3 * MINUTES + 20;
	private void applyCaffeinatedStatusEffect(Player player) {
//		StatusReceiver statusReceiver = statusReceiverMapping.get(player);
//		Status coffeeCaffeination = new Status(StatusType.CAFFEINATED, COFFEE_CAFFEINATION_DURATION_SECONDS); 
//		statusReceiver.applyStatus(coffeeCaffeination);
		// This doesn't handle expiry - or do anything for that matter
		// It will be implemented when it is needed
	}
	
	public void onAbortDrinking(Player player, ItemStack item) {
		assert(isDrink(item));
		
		if (drinkingSoundsForPlayers.get(player) != null) {
			BukkitTask drinkingSoundsTask = drinkingSoundsForPlayers.remove(player);
			drinkingSoundsTask.cancel();
		}
	}
	
	@Override
	public void onPacketReceiving(PacketEvent packetEvent) {
		PacketContainer packetContainer = packetEvent.getPacket();
		assert(packetContainer.getHandle() instanceof ServerboundPlayerActionPacket);

		Player player = packetEvent.getPlayer();
		ItemStack itemInUse = player.getItemInUse();
		
		if (isDrink(itemInUse)) {
			onAbortDrinking(player, itemInUse);
		}
	}
	
}
