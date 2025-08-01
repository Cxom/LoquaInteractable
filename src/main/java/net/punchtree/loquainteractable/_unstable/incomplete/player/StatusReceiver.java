package net.punchtree.loquainteractable._unstable.incomplete.player;

import net.punchtree.loquainteractable.status.Status;

public interface StatusReceiver {
	
	void applyStatus(Status status);
	
	void removeStatus(Status status);
	
}
