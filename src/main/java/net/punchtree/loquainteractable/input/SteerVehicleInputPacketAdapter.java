package net.punchtree.loquainteractable.input;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class SteerVehicleInputPacketAdapter extends PacketAdapter {

	private final ProtocolManager protocolManager;
	private final PlayerInputsManager playerInputsManager;
	
	public SteerVehicleInputPacketAdapter(Plugin plugin, ProtocolManager protocolManager, PlayerInputsManager playerInputsManager) {
		super(plugin, ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.STEER_VEHICLE });
		this.protocolManager = protocolManager;
		this.playerInputsManager = playerInputsManager;
	}
	
	public void handleInput() {
		protocolManager.addPacketListener(this);
	}
		
	public void stopHandlingInput() {
		protocolManager.removePacketListener(this);
	}
	
	@Override
	public void onPacketReceiving(PacketEvent packetEvent) {
		
		PacketContainer packetContainer = packetEvent.getPacket();
		
		float sidewaysAxis = packetContainer.getFloat().readSafely(0).floatValue();
		float forwardAxis = packetContainer.getFloat().readSafely(1).floatValue();
		boolean jump = packetContainer.getBooleans().readSafely(0);
		boolean unmount = packetContainer.getBooleans().readSafely(1);

		Player player = packetEvent.getPlayer();
		
		PlayerInputs playerInputs = playerInputsManager.getInputsForPlayer(player);
		assert(playerInputs != null);
		playerInputs.setVehicleForwardAxis(forwardAxis);
		playerInputs.setVehicleSidewaysAxis(sidewaysAxis);
		playerInputs.setVehicleJump(jump);
		playerInputs.setVehicleUnmount(unmount);
	}
		
	
	
}
