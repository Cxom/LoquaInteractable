package net.punchtree.loquainteractable._unstable.experimental.testing;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;

public class CircleGame {

	// This hardcoding just reduces the number of images we actually need in the texturepack
	// It could be moved to a config if that's usefully less brittle (but it's unlikely to change 
	// without needing an implementation update to go along with)
//	private static final int[] seg2_startpos8 = {0, 6, 4, 5, 3, 7, 8, 9, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg2_startpos10 = {15, 29, 58, 60, 61, 62, 8, 9, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg2_startpos12 = {16, 63, 64, 65, 1, 21, 22, 9, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg2_startpos14 = {17, 23, 24, 25, 26, 27, 75, 76, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg2_startpos16 = {19, 77, 78, 79, 166, 167, 168, 169, 170, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg2_startpos18 = {47, 66, 67, 70, 71, 72, 73, 74, 100, 127, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg2_startpos20 = {48, 101, 102, 103, 104, 105, 106, 107, 108, 109, 141, 59, 2, 126, 68, 86};
//	private static final int[] seg2_startpos22 = {18, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 88, 2, 126, 68, 86};
//	
//	private static final int[] seg4_startpos8 = {49, 89, 90, 91, 92, 62, 8, 9, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg4_startpos10 = {50, 93, 94, 95, 96, 97, 22, 9, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg4_startpos12 = {51, 98, 80, 81, 82, 83, 84, 76, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg4_startpos14 = {52, 171, 172, 173, 174, 175, 151, 152, 170, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg4_startpos16 = {53, 30, 31, 32, 33, 34, 35, 36, 177, 127, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg4_startpos18 = {54, 110, 40, 41, 42, 43, 44, 45, 46, 69, 141, 59, 2, 126, 68, 86};
//	private static final int[] seg4_startpos20 = {138, 128, 129, 130, 131, 132, 133, 134, 135, 136, 179, 88, 2, 126, 68, 86};
//	
//	private static final int[] seg6_startpos8 = {55, 140, 142, 143, 144, 97, 22, 9, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg6_startpos10 = {56, 85, 37, 38, 39, 57, 84, 76, 10, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg6_startpos12 = {13, 153, 154, 155, 11, 12, 99, 152, 170, 20, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg6_startpos14 = {14, 119, 120, 121, 122, 123, 124, 125, 177, 127, 28, 59, 2, 126, 68, 86};
//	private static final int[] seg6_startpos16 = {87, 111, 112, 113, 114, 115, 116, 117, 118, 69, 141, 59, 2, 126, 68, 86};
//	private static final int[] seg6_startpos18 = {139, 137, 145, 146, 147, 148, 149, 150, 176, 178, 179, 88, 2, 126, 68, 86};
	
	private static final int[] seg2_startpos8 = {0, 6, 4, 5, 3, 7, 8, 9, 10, 22, 35, 36, 2, 149, 184, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg2_startpos10 = {13, 24, 25, 26, 39, 40, 41, 42, 43, 201, 202, 36, 2, 149, 184, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg2_startpos12 = {14, 85, 86, 87, 88, 89, 90, 91, 38, 82, 83, 84, 143, 149, 184, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg2_startpos14 = {15, 236, 238, 239, 23, 136, 137, 140, 235, 240, 241, 237, 138, 139, 220, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg2_startpos16 = {16, 104, 105, 106, 107, 71, 72, 73, 74, 75, 76, 77, 78, 79, 251, 127, 293, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg2_startpos18 = {28, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 161, 162, 163, 337, 219, 222, 223, 80, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg2_startpos20 = {196, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 175, 176, 177, 178, 179, 180, 295, 208, 203, 164, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg2_startpos22 = {81, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 92, 93, 94, 95, 96, 97, 98, 99, 100, 294, 101, 145, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg4_startpos8 = {31, 229, 230, 231, 232, 233, 234, 199, 200, 201, 202, 36, 2, 149, 184, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg4_startpos10 = {32, 275, 243, 244, 245, 247, 248, 249, 250, 60, 274, 84, 143, 149, 184, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg4_startpos12 = {33, 128, 129, 130, 131, 132, 133, 134, 225, 226, 227, 296, 321, 139, 220, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg4_startpos14 = {34, 173, 174, 146, 147, 148, 108, 109, 110, 111, 112, 167, 170, 169, 281, 127, 293, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg4_startpos16 = {12, 44, 45, 46, 47, 48, 214, 215, 20, 21, 224, 11, 114, 216, 221, 102, 27, 223, 80, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg4_startpos18 = {197, 204, 205, 206, 63, 64, 65, 66, 67, 68, 69, 70, 212, 297, 298, 207, 210, 211, 209, 203, 164, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg4_startpos20 = {264, 291, 292, 254, 255, 256, 257, 258, 259, 260, 261, 285, 286, 287, 288, 289, 290, 283, 262, 17, 284, 101, 145, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg6_startpos8 = {29, 266, 267, 268, 269, 270, 271, 272, 273, 60, 274, 84, 143, 149, 184, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg6_startpos10 = {30, 115, 116, 117, 118, 119, 120, 121, 228, 122, 135, 296, 321, 139, 220, 242, 252, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg6_startpos12 = {37, 18, 19, 103, 165, 182, 126, 125, 181, 171, 168, 172, 166, 169, 281, 127, 293, 300, 324, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg6_startpos14 = {183, 188, 189, 190, 191, 192, 193, 194, 195, 323, 325, 217, 218, 186, 187, 102, 27, 223, 80, 299, 336, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg6_startpos16 = {198, 61, 62, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 49, 213, 263, 322, 211, 209, 203, 164, 1, 123, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	private static final int[] seg6_startpos18 = {265, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 276, 277, 278, 279, 280, 282, 338, 339, 17, 284, 101, 145, 150, 185, 246, 253, 113, 124, 141, 142, 144};
	
	private static final int[][] seg2s = {seg2_startpos8,
										  seg2_startpos10,
										  seg2_startpos12,
										  seg2_startpos14,
										  seg2_startpos16,
										  seg2_startpos18,
										  seg2_startpos20,
										  seg2_startpos22}; 
	
	private static final int[][] seg4s = {seg4_startpos8,
										  seg4_startpos10,
										  seg4_startpos12,
										  seg4_startpos14,
										  seg4_startpos16,
										  seg4_startpos18,
										  seg4_startpos20}; 
	
	private static final int[][] seg6s = {seg6_startpos8,
										  seg6_startpos10,
										  seg6_startpos12,
										  seg6_startpos14,
										  seg6_startpos16,
										  seg6_startpos18};
	
	private static final int[][][] segs = {seg2s, seg4s, seg6s};
	
	public static void runForPlayer(Player player, int speed) {
		int segLength = ThreadLocalRandom.current().nextInt(0, 3);
		int segStartPosRand = ThreadLocalRandom.current().nextInt(0, 8 - segLength);
		// While segStartPos is in [0, 8], use it to index a random startpos array
		int[] animationKeyframes = segs[segLength][segStartPosRand];
		// Now modify segStartPos to represent the actual startPos;
		int segStartPos = segStartPosRand + 4;
		new BukkitRunnable() {
			int keyframeIdx = 0;
			public void run() {
				if (keyframeIdx >= animationKeyframes.length) {
					this.cancel();
					return;
				}
				if (keyframeIdx >= segStartPos && keyframeIdx <= segStartPos + segLength) {
					player.sendMessage("Acceptable now!");
				}
				int keyframe = animationKeyframes[keyframeIdx];
				player.getInventory().setItemInOffHand(getAnimationKeyframeItem(keyframe));
				++keyframeIdx;
			}
			public void cancel() {
				super.cancel();
				player.getInventory().setItemInOffHand(null);
			}
		}.runTaskTimer(LoquaInteractablePlugin.getInstance(), 20, speed);
	}
	
	
	public static void testRunAnimation(Player player, int speed) {
		int segLength = ThreadLocalRandom.current().nextInt(0, 3);
		int segStartPos = ThreadLocalRandom.current().nextInt(0, 8 - segLength);
		// While segStartPos is in [0, 8], use it to index a random startpos array
		int[] animationKeyframes = segs[segLength][segStartPos];
		// Now modify segStartPos to represent the actual startPos;
		segStartPos += 4;
		new BukkitRunnable() {
			int keyframeIdx = 0;
			public void run() {
				if (keyframeIdx >= animationKeyframes.length) {
					this.cancel();
					return;
				}
				int keyframe = animationKeyframes[keyframeIdx];
				player.getInventory().setItemInOffHand(getAnimationKeyframeItem(keyframe));
				++keyframeIdx;
			}
			public void cancel() {
				super.cancel();
				player.getInventory().setItemInOffHand(null);
			}
		}.runTaskTimer(LoquaInteractablePlugin.getInstance(), 20, speed);
	}
	
	private static ItemStack getAnimationKeyframeItem(int keyframe) {
		ItemStack animationKeyframeItem = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta im = animationKeyframeItem.getItemMeta();
		im.setCustomModelData(5000 + keyframe);
		animationKeyframeItem.setItemMeta(im);
		return animationKeyframeItem;
	}
}
