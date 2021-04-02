package net.punchtree.loquainteractable.displayutil;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

import static net.punchtree.loquainteractable.displayutil.PrintingObjectUtils.formatEulerAngle;
import static net.punchtree.loquainteractable.displayutil.PrintingObjectUtils.formatLocation;

public class ArmorStandUtils {
	
	private static final Color PINK = Color.fromRGB(255, 107, 250);
	
	private static final Vector LEFT_HAND_OFFSET = new Vector(+.125, -.25, +.375);
	private static final Vector RIGHT_HAND_OFFSET = new Vector(+.875, -.25, +.375);
	
	// Consumer<ArmorStand> doBeforeSpawn
	public static ArmorStand spawnArmorStand(Location location, boolean lit, Team coloredTeam, ItemStack highlightItem) {
		ArmorStand newstand = location.getWorld().spawn(
				location.clone().add(RIGHT_HAND_OFFSET), 
				ArmorStand.class,
				stand -> {
					stand.setGravity(false);
					stand.setArms(true);
					resetPose(stand);
					stand.setItem(EquipmentSlot.HAND, highlightItem);
					stand.addScoreboardTag("loqinttemp");
					// TODO use entity metadata for glowing?
					stand.addScoreboardTag("loqinttempglowing");
					// Make sure to do this and not setInvisible(true) in order to set the NBT tag
					stand.setVisible(false);
					stand.setInvulnerable(true);
					// These four lines prevent the model from turning black when it's shoved inside a block
					stand.setCanTick(true);
					stand.setFireTicks(2);
					stand.setMarker(true);
					new BukkitRunnable() {
						public void run() {							
							stand.setCanTick(false);
						}
					}.runTaskLater(LoquaInteractablePlugin.getInstance(), 2);
					
					
					if (lit) {
						stand.setGlowing(true);
						coloredTeam.addEntry(stand.getUniqueId().toString());
					}
				});
		
		return newstand;
	}
	
	private static void resetPose(ArmorStand stand) {
		stand.setBodyPose(new EulerAngle(0, 0, 0));
		stand.setLeftArmPose(new EulerAngle(0, 0, 0));
		stand.setRightArmPose(new EulerAngle(0, 0, 0));
		stand.setLeftLegPose(new EulerAngle(0, 0, 0));
		stand.setRightLegPose(new EulerAngle(0, 0, 0));
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
		inspector.sendMessage(ChatColor.DARK_AQUA + "ScoreboardTags: " + stand.getScoreboardTags());
		inspector.sendMessage(ChatColor.RED + "Location: " + formatLocation(stand.getLocation()));
	}
	
}
