package net.punchtree.loquainteractable.displayutil;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class BlockHighlight {

	private static Set<Entity> spawnedArmorStands = new HashSet<>();
	
	public BlockHighlight modelHighlightBorder(Block block) {
//		ArmorStandUtils.spawnArmorStand(location, lit)
		ArmorStand highlight = ArmorStandUtils.spawnArmorStand(block.getLocation(), true);
		new BukkitRunnable() {
			public void run() {
				highlight.remove();
			}
		}.runTaskLater(LoquaInteractablePlugin.getInstance(), 20);
		return this;
	}
	
	private enum ParticleType {
		REDSTONE,
		NORMAL
	}
	
	private ParticleType particleType = ParticleType.REDSTONE;
	private Particle particle = Particle.FIREWORKS_SPARK;
	private Color color = Color.RED;
	private float particleSize = 1;
	public boolean border = true;
	public boolean diagonalForward = false;
	public boolean diagonalCross = false;
	public int steps = 5;
	
	public BlockHighlight setRedstoneParticleColor(Color color) {
		this.color = color;
		this.particleType = ParticleType.REDSTONE;
		return this;
	}
	
	public BlockHighlight setParticle(Particle particle) {
		this.particle = particle;
		this.particleType = ParticleType.NORMAL;
		return this;
	}
	
	public BlockHighlight setParticleSize(float particleSize) {
		this.particleSize = particleSize;
		return this;
	}
	
	public BlockHighlight particleHighlight(Block block) {
		double displayOffsetFromBlock = 0.06;
		double de = 1 + 2 * displayOffsetFromBlock;
		Location[] corners = new Location[8];
		corners[0] = block.getLocation().subtract(displayOffsetFromBlock, displayOffsetFromBlock, displayOffsetFromBlock);
		corners[1] = corners[0].clone().add(de, 0, 0);
		corners[2] = corners[0].clone().add(0, de, 0);
		corners[3] = corners[0].clone().add(0, 0, de);
		corners[4] = corners[0].clone().add(de, de, 0);
		corners[5] = corners[0].clone().add(de, 0, de);
		corners[6] = corners[0].clone().add(0, de, de);
		corners[7] = corners[0].clone().add(de, de, de);
		// Edges
		if (border) {
			particleLine(corners[0], corners[1], steps);
			particleLine(corners[0], corners[2], steps);
			particleLine(corners[0], corners[3], steps);
			particleLine(corners[1], corners[4], steps);
			particleLine(corners[1], corners[5], steps);
			particleLine(corners[2], corners[4], steps);
			particleLine(corners[2], corners[6], steps);
			particleLine(corners[3], corners[5], steps);
			particleLine(corners[3], corners[6], steps);
			particleLine(corners[4], corners[7], steps);
			particleLine(corners[5], corners[7], steps);
			particleLine(corners[6], corners[7], steps);
		}
		// Diagonals (1-way)
		if (diagonalForward) {			
			particleLine(corners[0], corners[4], steps);
			particleLine(corners[0], corners[5], steps);
			particleLine(corners[0], corners[6], steps);
			particleLine(corners[1], corners[7], steps);
			particleLine(corners[2], corners[7], steps);
			particleLine(corners[3], corners[7], steps);
		}
		// Diagonals (2-way)
		if (diagonalCross) {			
			particleLine(corners[1], corners[2], steps);
			particleLine(corners[1], corners[3], steps);
			particleLine(corners[2], corners[3], steps);
			particleLine(corners[4], corners[5], steps);
			particleLine(corners[4], corners[6], steps);
			particleLine(corners[5], corners[6], steps);
		}
		return this;
	}
	
	public BlockHighlight particleLine(Location a, Location b, int steps) {
		particleLine(a, b, a.distance(b) / steps);
		return this;
	}
	
	public BlockHighlight particleLine(Location a, Location b, double stepSize) {
		Vector direction = b.toVector().subtract(a.toVector()).normalize();
		Vector step = direction.multiply(stepSize);
		Location marcher = a.clone();
		double distance = a.distance(b);
		int i = 0;
		for(double d = 0; d <= distance; d += stepSize) {
			if (i > 10) {
				Bukkit.broadcastMessage("ERROR Early termination of particle spawn loop");
				return this;
			}
			++i;
			spawnParticle(marcher);
			marcher.add(step);
		}
		return this;
	}
	
	public BlockHighlight spawnParticle(Location particleSpawnLocation) {
		if (particleType == ParticleType.REDSTONE) {
			spawnRedstoneParticle(particleSpawnLocation, particleSize, color);
		} else {
			spawnParticle(particleSpawnLocation, particleSize, particle);
		}
		return this;
	}
	
	public static void spawnRedstoneParticle(Location particleSpawnLocation, float particleSize, Color color) {
		DustOptions dustOptions = new DustOptions(color, particleSize);
		World world = particleSpawnLocation.getWorld();
		world.spawnParticle(
				Particle.REDSTONE, 
				particleSpawnLocation, 
				1, 
				dustOptions);
	}
	
	public static void spawnParticle(Location particleSpawnLocation, float particleSize, Particle particle) {
		World world = particleSpawnLocation.getWorld();
		world.spawnParticle(
				particle, 
				particleSpawnLocation, 
				1
				);
	}
	
}
