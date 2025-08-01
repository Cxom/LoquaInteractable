package net.punchtree.loquainteractable._unstable.incomplete.player;

import org.bukkit.entity.Player;

public interface PlayerMapping<V> {

	V get(Player player);
	
}
