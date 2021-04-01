package net.punchtree.loquainteractable.displayutil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ArmorStandUtils {

	private static Team pinkTeam;
	
	private static void initializePinkTeam() {
		if (pinkTeam == null) {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard scoreboard = manager.getMainScoreboard();
			pinkTeam = scoreboard.getTeam("testPinkTeam");
			if (pinkTeam == null) {				
				pinkTeam = scoreboard.registerNewTeam("testPinkTeam");
			}
			pinkTeam.setColor(ChatColor.LIGHT_PURPLE);
		}
	}
	
	private static final Color PINK = Color.fromRGB(255, 107, 250);
	
	private static final ItemStack blockHighlight = new ItemStack(Material.LEATHER_HELMET);
	static {
		LeatherArmorMeta lm = (LeatherArmorMeta) blockHighlight.getItemMeta();
		lm.setColor(Color.fromRGB(255, 85, 255));
		lm.setCustomModelData(300);
		blockHighlight.setItemMeta(lm);
	}
	
	private static final Vector LEFT_HAND_OFFSET = new Vector(+.125, -.25, +.375);
	private static final Vector RIGHT_HAND_OFFSET = new Vector(+.875, -.25, +.375);
	
	// Consumer<ArmorStand> doBeforeSpawn
	public static ArmorStand spawnArmorStand(Location location, boolean lit) {
		ArmorStand newstand = location.getWorld().spawn(
				location.clone().add(LEFT_HAND_OFFSET), 
				ArmorStand.class,
				stand -> {
					stand.setGravity(false);
					stand.setArms(true);
					stand.setBodyPose(new EulerAngle(0, 0, 0));
					stand.setLeftArmPose(new EulerAngle(0, 0, 0));
					stand.setRightArmPose(new EulerAngle(0, 0, 0));
					stand.setLeftLegPose(new EulerAngle(0, 0, 0));
					stand.setRightLegPose(new EulerAngle(0, 0, 0));
					stand.setItem(EquipmentSlot.OFF_HAND, blockHighlight);
					stand.addScoreboardTag("loqinttemp");
					// Make sure to do this and not setInvisible(true) in order to set the NBT tag
					stand.setVisible(false);
					stand.setInvulnerable(true);
					// These two lines prevent the model from turning black when it's shoved inside a block
					stand.setFireTicks(100);
					stand.setMarker(true);
					
					if (lit) {
						stand.setGlowing(true);
						initializePinkTeam();
						pinkTeam.addEntry(stand.getUniqueId().toString());
						
					}
				});
		
		return newstand;
	}
	
	public static void inspectArmorStand(Player inspector, ArmorStand stand) {
		inspector.sendMessage(ChatColor.DARK_GRAY + "---------------------------------");
		inspector.sendMessage(ChatColor.RED + "BodyPose: " + formatEulerAngle(stand.getBodyPose()));
		inspector.sendMessage(ChatColor.RED + "HeadPose: " + formatEulerAngle(stand.getHeadPose()));
		inspector.sendMessage(ChatColor.YELLOW + "LeftArmPose: " + formatEulerAngle(stand.getLeftArmPose()));
		inspector.sendMessage(ChatColor.YELLOW + "RightArmPose: " + formatEulerAngle(stand.getRightArmPose()));
		inspector.sendMessage(ChatColor.AQUA + "Yaw: " + stand.getLocation().getYaw());
		inspector.sendMessage(ChatColor.AQUA + "Pitch: " + stand.getLocation().getPitch());
		inspector.sendMessage(ChatColor.GREEN + "LeftLegPose: " + formatEulerAngle(stand.getLeftLegPose()));
		inspector.sendMessage(ChatColor.GREEN + "LeftLegPose: " + formatEulerAngle(stand.getRightLegPose()));
		inspector.sendMessage(ChatColor.GREEN + "Pose: " + stand.getPose());
		inspector.sendMessage(ChatColor.RED + "Location: " + formatLocation(stand.getLocation()));
	}
	
	public static String formatEulerAngle(EulerAngle euler) {
		return String.format("[%.5f, %.5f, %.5f]", euler.getX(), euler.getY(), euler.getZ());
	}
	
	public static String formatLocation(Location loc) {
		return String.format("%s[%.5f %.5f %.5f]", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
	}
	
}
