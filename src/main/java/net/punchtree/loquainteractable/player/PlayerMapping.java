package net.punchtree.loquainteractable.player;

import org.bukkit.entity.Player;

public interface PlayerMapping<V> {

	V get(Player player);
	
}
