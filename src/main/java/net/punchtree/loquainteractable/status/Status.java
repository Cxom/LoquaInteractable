package net.punchtree.loquainteractable.status;

public class Status {

	private StatusType type;
	private int durationSeconds;
	
	public Status(StatusType type, int durationSeconds) {
		this.type = type;
		this.durationSeconds = durationSeconds;
	}

}
