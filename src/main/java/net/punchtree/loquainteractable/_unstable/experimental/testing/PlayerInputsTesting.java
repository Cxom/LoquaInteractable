package net.punchtree.loquainteractable._unstable.experimental.testing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.punchtree.loquainteractable.input.PlayerInputsManager;

public class PlayerInputsTesting implements CommandExecutor {

	private final PlayerInputsManager playerInputsManager;
	
	
	public PlayerInputsTesting(PlayerInputsManager playerInputsManager) {
		this.playerInputsManager = playerInputsManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				
//		if ( ! ( sender instanceof Player )) return true;
//		Player player = (Player) sender;

		// TODO adapt this to loqua player manager?
		if ( "verifyplayerinputsmap".equalsIgnoreCase(label) ) {
			boolean verified = playerInputsManager.verifyAllPlayersAreOnline();
			sender.sendMessage("All players are online: " + verified);
			return true;
		}
		
		return false;
	}

	

}
