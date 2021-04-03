package net.punchtree.loquainteractable.displayutil;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.EulerAngle;

public class PrintingObjectUtils {

	public static String formatEulerAngle(EulerAngle euler) {
		return String.format("[%.5f, %.5f, %.5f]", euler.getX(), euler.getY(), euler.getZ());
	}
	
	public static String formatLocation(Location loc) {
		return String.format("%s[%.5f %.5f %.5f]", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	public static String formatBlock(Block block) {
		return String.format("%s:%d,%d,%d", block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
	}
	
}
