package net.punchtree.loquainteractable.city.garbagecans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;


public class GarbageCansService implements Listener {

	private static final long ITEM_DROP_CHECK_DELAY = 2 * 20; // seconds * ticks
	public static final long EXPIRY_STATUTE_SECONDS = 15 * 60; // minutes * seconds
	
	public JavaPlugin plugin;
	
	private Map<Location, GarbageCanCollection> garbageCans = new LinkedHashMap<>();
	
	private BukkitTask expiryTask;
	
	private BukkitRunnable expiryRunnable = new BukkitRunnable() {
		@Override
		public void run() {
			int items = 0;
			int cans = 0;
			List<Location> toRemove = new ArrayList<>();
			for (Map.Entry<Location, GarbageCanCollection> gc : garbageCans.entrySet()) {
				items += gc.getValue().prune(System.currentTimeMillis() - (EXPIRY_STATUTE_SECONDS * 1000));
				if (gc.getValue().isEmpty()) {
					toRemove.add(gc.getKey());
					++cans;
				}
			}
			toRemove.forEach(key -> garbageCans.remove(key));
			Bukkit.getLogger().finer(ChatColor.GRAY + "Purged " + ChatColor.RED + items + ChatColor.GRAY + " item(s) " + "and " + ChatColor.RED + cans + ChatColor.GRAY + " can(s)" + ".");
		}
	};
	
	public GarbageCansService(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void onEnable() {
		ConfigurationSerialization.registerClass(GarbageCanCollection.class);
		ConfigurationSerialization.registerClass(GarbageCanCollection.GarbageItem.class);

		
		PersistenceManager.getInstance().createFiles();
		garbageCans = PersistenceManager.getInstance().loadGarbageCans();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		plugin.getCommand("purge-garbage-cans").setExecutor(new PurgeGarbageCansCommand());
		
		expiryTask = expiryRunnable.runTaskTimer(plugin, EXPIRY_STATUTE_SECONDS * 20, EXPIRY_STATUTE_SECONDS * 20);
	}
	
	public void onDisable() {
		expiryTask.cancel();
		PersistenceManager.getInstance().saveGarbageCans(garbageCans);
		garbageCans.clear();
	}
	
	private class PurgeGarbageCansCommand implements CommandExecutor {
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			
			if ("purge-garbage-cans".equalsIgnoreCase(label) && sender.hasPermission("garbagecans.purge")) {
				if (args.length == 0) { return false; }
				long time;
				try {
					time = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					return false;
				}
				time = System.currentTimeMillis() - (time * 1000);
				int items = 0;
				int cans = 0;
				List<Location> toRemove = new ArrayList<>();
				for (Map.Entry<Location, GarbageCanCollection> gc : garbageCans.entrySet()) {
					items += gc.getValue().prune(time);
					if (gc.getValue().isEmpty()) {
						toRemove.add(gc.getKey());
						++cans;
					}
				}
				toRemove.forEach(key -> garbageCans.remove(key));
				sender.sendMessage(ChatColor.GRAY + "Purged " + ChatColor.RED + items + ChatColor.GRAY + " item(s) " + "and " + ChatColor.RED + cans + ChatColor.GRAY + " can(s)" + ".");
			}
			
			return true;
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Item item = e.getItemDrop();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!item.isDead() && item.getItemStack().getType() != Material.AIR) {
					attemptThrowIntoCan(item, e.getPlayer());
				}
			}
		}.runTaskLater(plugin, ITEM_DROP_CHECK_DELAY);
	}
	
	private void attemptThrowIntoCan(Item item, Player thrower) {
		if (isGarbageCan(item.getLocation().getBlock())) {
			Location loc = item.getLocation().getBlock().getLocation();
			if (garbageCans.containsKey(loc)) {
				garbageCans.get(loc).addItem(item);
			} else {
				GarbageCanCollection gcc = new GarbageCanCollection(loc);
				garbageCans.put(loc, gcc);
				gcc.addItem(item);
			}
			onThrowAwayItem(thrower, item);
		}
	}
	
	private void onThrowAwayItem(Player player, Item item) {
		// Give karma?
		spawnThrowAwayParticles(item);
		playThrowAwaySound(item);
	}

	private static final Color[] GARBAGE_COLORS = {
			Color.fromRGB(40, 43, 22),
			Color.fromRGB(24, 36, 15),
			Color.fromRGB(30, 22, 19),
			Color.fromRGB(20, 12, 8)
	};
	private void spawnThrowAwayParticles(Item item) {
		item.getWorld().spawnParticle(Particle.SNEEZE, item.getLocation(), 1 + ThreadLocalRandom.current().nextInt(2), Math.random() * 0.3, Math.random() * 0.3, Math.random() * 0.3, 0.1);
		item.getWorld().spawnParticle(Particle.REDSTONE, item.getLocation(), 3 + ThreadLocalRandom.current().nextInt(3), Math.random() * 0.65, -.1 + Math.random() * 0.3, Math.random() * 0.65, 1, 
				new DustOptions(GARBAGE_COLORS[ThreadLocalRandom.current().nextInt(GARBAGE_COLORS.length)], (float) (1 + Math.random() * 0.3)));
	}
	
	private void playThrowAwaySound(Item item) {
		item.getWorld().playSound(item.getLocation(), Sound.ENTITY_MAGMA_CUBE_DEATH, 0.5f, 1.1f);
	}
	
	@EventHandler
	public void playerClickGarbage(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && isGarbageCan(event.getClickedBlock())) {
			Location loc = event.getClickedBlock().getLocation();
			GarbageCanCollection gcc = garbageCans.get(loc);
			if (gcc != null) {
				gcc.showTo(event.getPlayer());
			}
		}
	}
	
	// Currently requires an item to be *IN* the can/block. 
	// If we want e.g. dumpsters will have to modify this predicate to take different parameters
	public boolean isGarbageCan(Block block) {
		return block.getType() == Material.CAULDRON;
	}
	
//	@SuppressWarnings("unchecked")
//	private void loadGarbageCans() {
//		File garbagecansf = new File(GarbageCans.plugin.getDataFolder() + File.separator + GARBAGE_CANS_FILENAME);
//		if(garbagecansf.exists() && !garbagecansf.isDirectory()) { 
//			try {
//			     FileInputStream streamIn = new FileInputStream(garbagecansf);
//			     ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
//			     garbageCans = (Map<Location, GarbageCanCollection>) objectinputstream.readObject();
//			     streamIn.close();
//			     objectinputstream.close();
//			 } catch (ClassNotFoundException | IOException e) {
//			     e.printStackTrace();
//			 }
//		}
//	}
//	
//	private void persistGarbageCans() {
//		File garbagecansf = new File(GarbageCans.plugin.getDataFolder() + File.separator + GARBAGE_CANS_FILENAME);
//		if(!garbagecansf.exists()) {
//			garbagecansf.getParentFile().mkdirs();
//		}
//		try {
//			FileOutputStream fout = new FileOutputStream(garbagecansf);
//			ObjectOutputStream oos = new ObjectOutputStream(fout);
//			oos.writeObject(garbageCans);
//			fout.close();
//			oos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
