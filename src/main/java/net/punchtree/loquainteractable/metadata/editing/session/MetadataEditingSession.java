package net.punchtree.loquainteractable.metadata.editing.session;

import org.bukkit.entity.Player;

import net.punchtree.loquainteractable.metadata.editing.InspectMetadataEditingMode;
import net.punchtree.loquainteractable.metadata.editing.MetadataEditingMode;

public class MetadataEditingSession {

	private final Player player;
	
	private MetadataEditingMode editingMode;
	
	public MetadataEditingSession(Player player) {
		this.player = player;
		editingMode = new InspectMetadataEditingMode();
		editingMode.onEnterEditingMode(player, this);
	}
	
	public MetadataEditingMode getEditingMode() {
		return editingMode;
	}
	
	public void changeEditingMode(MetadataEditingMode editingMode) {
		this.editingMode.onLeaveEditingMode(player, this);
		this.editingMode = editingMode;
		editingMode.onEnterEditingMode(player, this);
	}

}
