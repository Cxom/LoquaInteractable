package net.punchtree.loquainteractable._unstable.experimental.testing;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.punchtree.loquainteractable.LoquaInteractablePlugin;
import net.punchtree.loquainteractable.gui.hud.advancements.CustomAdvancement;

public class ToastTesting implements CommandExecutor {

	private static final CustomAdvancement TEST_TOAST = new CustomAdvancement(
		new NamespacedKey(LoquaInteractablePlugin.getInstance(), "litestadvancement"),
		"TestTitleY",
		"TestDescYY",
		"diamond_hoe",
		"{CustomModelData:500}"
	);
	
	public ToastTesting() {
		TEST_TOAST.add();
	}
	
	public static void sendTestToast(Player player, double duration) {
//		AdvancementProgress test = player.getAdvancementProgress()
		TEST_TOAST.showTo(player);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if ( ! (sender instanceof Player)) return true;
		Player player = (Player) sender;
		
//		if (args.length > 1) {
//			TEST_TOAST.setTitle(args[0]);
//		}
//				
//		ItemStack itemInHand = player.getInventory().getItemInMainHand();
//		if (itemInHand != null) {
//			TEST_TOAST.setIconItemKey(itemInHand.getType().getKey().asString());
//			TEST_TOAST.setIconItemNbt(getItemNbtString(itemInHand));
//		}
		
		sendTestToast(player, 1);
		
		return true;
	}
	

	
}
