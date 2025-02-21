package net.punchtree.loquainteractable._unstable.experimental.testing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.punchtree.loquainteractable.gui.hud.CircleGame;

public class CircleGameTesting implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if ( ! (sender instanceof Player)) return true;
		Player player = (Player) sender;
		
		if (args.length < 2) {
			player.sendMessage("/circlegame <test|run> <period>");
			return true;
		}
		
		boolean test = true;
		int period = 0;
		if (args[0].equalsIgnoreCase("test")) {
			test = true;
		} else if (args[0].equalsIgnoreCase("run")) {
			test = false;
		} else {
			player.sendMessage("/circlegame <test|run> <period>");
			return true;
		}
		try {
			period = Integer.parseInt(args[1]);
			if (period < 1) {
				player.sendMessage("/circlegame <test|run> <period>");
				return true;
			}
		} catch (NumberFormatException e) {
			player.sendMessage("/circlegame <test|run> <period>");
			return true;
		}
		
		if (test) {
			CircleGame.testRunAnimation(player, period);
		} else {
			CircleGame.runForPlayer(player, period);
		}
		
		return true;
	}

}
