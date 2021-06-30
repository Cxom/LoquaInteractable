package net.punchtree.loquainteractable.input;

import java.util.UUID;

public class PlayerInputs {

	private final UUID uuid;

	public PlayerInputs(UUID uuid) {
		this.uuid = uuid;
	}

	// XXX TODO We need to figure out how reseting inputs happens. If a packet does
	// not line up with the loop,
	// then for which controls do we maintain what was last input and which do we
	// reset to their default values
	private float vehicleForwardAxis;
	private float vehicleSidewaysAxis;
	// The following 4 are just aliases for the axes above, which seem to only hold
	// 2 values
	// It might be an equalizer since sending non-discrete values seems to be rare,
	// if even possible
	// It would be good to find out if there's anything in the vanilla client that
	// can send continuous
	// values for the forward and sideways axes in the Steer Vehicle packet
	private boolean vehicleForward;
	private boolean vehicleBackward;
	private boolean vehicleLeft;
	private boolean vehicleRight;

	private boolean vehicleJump;
	private boolean vehicleUnmount;

	public UUID getUuid() {
		return uuid;
	}

	public float getVehicleForwardAxis() {
		return vehicleForwardAxis;
	}

	public float getVehicleSidewaysAxis() {
		return vehicleSidewaysAxis;
	}

	public boolean getVehicleForward() {
		return vehicleForward;
	}

	public boolean getVehicleBackward() {
		return vehicleBackward;
	}

	public boolean getVehicleLeft() {
		return vehicleLeft;
	}

	public boolean getVehicleRight() {
		return vehicleRight;
	}

	public boolean getVehicleJump() {
		return vehicleJump;
	}

	public boolean getVehicleUnmount() {
		return vehicleUnmount;
	}

	public void setVehicleForwardAxis(float vehicleForwardAxis) {
		this.vehicleForwardAxis = vehicleForwardAxis;
		this.vehicleForward = vehicleForwardAxis > 0;
		this.vehicleBackward = vehicleForwardAxis < 0;
	}

	public void setVehicleSidewaysAxis(float vehicleSidewaysAxis) {
		this.vehicleSidewaysAxis = vehicleSidewaysAxis;
		this.vehicleLeft = vehicleSidewaysAxis > 0;
		this.vehicleRight = vehicleSidewaysAxis < 0;
	}

	public void setVehicleJump(boolean vehicleJump) {
		this.vehicleJump = vehicleJump;
	}

	public void setVehicleUnmount(boolean vehicleUnmount) {
		this.vehicleUnmount = vehicleUnmount;
	}

}
