package net.punchtree.loquainteractable.displayutil;

import org.bukkit.block.Block;

public class BlockUtils {

	public static void copyBlock(Block from, Block to) {
//		BlockData fromData = from.getBlockData();
//		BlockState fromState = from.getState();
		to.setType(from.getType());
		to.setBlockData(from.getBlockData().clone());
//		to.setState(fromState);
	}
	
}
