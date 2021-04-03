package net.punchtree.loquainteractable.displayutil;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

@SuppressWarnings("serial")
public class SelectionColor extends Color {

	// Bukkit Colors:

		public static final SelectionColor WHITE = new SelectionColor(255, 255, 255, ChatColor.WHITE);
		public static final SelectionColor YELLOW = new SelectionColor(255, 255, 85, ChatColor.YELLOW);
		public static final SelectionColor LIGHT_PURPLE = new SelectionColor(255, 85, 255, ChatColor.LIGHT_PURPLE);
		public static final SelectionColor RED = new SelectionColor(255, 85, 85, ChatColor.RED);
		public static final SelectionColor AQUA = new SelectionColor(85, 255, 255, ChatColor.AQUA);
		public static final SelectionColor GREEN = new SelectionColor(85, 255, 85, ChatColor.GREEN);
		public static final SelectionColor BLUE = new SelectionColor(85, 85, 255, ChatColor.BLUE);
		public static final SelectionColor DARK_GRAY = new SelectionColor(85, 85, 85, ChatColor.DARK_GRAY);
		public static final SelectionColor GRAY = new SelectionColor(170, 170, 170, ChatColor.GRAY);
		public static final SelectionColor GOLD = new SelectionColor(255, 170, 0, ChatColor.GOLD);
		public static final SelectionColor DARK_PURPLE = new SelectionColor(170, 0, 170, ChatColor.DARK_PURPLE);
		public static final SelectionColor DARK_RED = new SelectionColor(170, 0, 0, ChatColor.DARK_RED);
		public static final SelectionColor DARK_AQUA = new SelectionColor(0, 170, 170, ChatColor.DARK_AQUA);
		public static final SelectionColor DARK_GREEN = new SelectionColor(0, 170, 0, ChatColor.DARK_GREEN);
		public static final SelectionColor DARK_BLUE = new SelectionColor(0, 0, 170, ChatColor.DARK_BLUE);
		public static final SelectionColor BLACK = new SelectionColor(0, 0, 0, ChatColor.BLACK);

		private static final Map<String, SelectionColor> defaults;
		static{
			defaults = new HashMap<String, SelectionColor>();
			defaults.put("WHITE", SelectionColor.WHITE);
			defaults.put("YELLOW", SelectionColor.YELLOW);
			defaults.put("LIGHT_PURPLE", SelectionColor.LIGHT_PURPLE);
			defaults.put("RED", SelectionColor.RED);
			defaults.put("AQUA", SelectionColor.AQUA);
			defaults.put("GREEN", SelectionColor.GREEN);
			defaults.put("BLUE", SelectionColor.BLUE);
			defaults.put("DARK_GRAY", SelectionColor.DARK_GRAY);
			defaults.put("GRAY", SelectionColor.GRAY);
			defaults.put("GOLD", SelectionColor.GOLD);
			defaults.put("DARK_PURPLE", SelectionColor.DARK_PURPLE);
			defaults.put("DARK_RED", SelectionColor.DARK_RED);
			defaults.put("DARK_AQUA", SelectionColor.DARK_AQUA);
			defaults.put("DARK_GREEN", SelectionColor.DARK_GREEN);
			defaults.put("DARK_BLUE", SelectionColor.DARK_BLUE);
			defaults.put("BLACK", SelectionColor.BLACK);
		}
		
		//old wool colors
//		public static final MinigameColor WOOL_WHITE = new MinigameColor(228,228,228);
//		public static final MinigameColor WOOL_ORANGE = new MinigameColor(234,126,53);
//		public static final MinigameColor WOOL_MAGENTA = new MinigameColor();
//		public static final MinigameColor WOOL_LIGHT_BLUE = new MinigameColor();
//		public static final MinigameColor WOOL_YELLOW = new MinigameColor(194,181,28);
//		public static final MinigameColor WOOL_LIME = new MinigameColor(57,186,46);
//		public static final MinigameColor WOOL_PINK = new MinigameColor();
//		public static final MinigameColor WOOL_DARK_GRAY = new MinigameColor(65,65,65);
//		public static final MinigameColor WOOL_LIGHT_GRAY = new MinigameColor(160,167,167);
//		public static final MinigameColor WOOL_CYAN = new MinigameColor();
//		public static final MinigameColor WOOL_PURPLE = new MinigameColor();
//		public static final MinigameColor WOOL_BLUE = new MinigameColor();
//		public static final MinigameColor WOOL_BROWN = new MinigameColor();
//		public static final MinigameColor WOOL_GREEN = new MinigameColor();
//		public static final MinigameColor WOOL_RED = new MinigameColor(158,43,39);
//		public static final MinigameColor WOOL_BLACK = new MinigameColor(24,20,20);
		
		// still old wool colors - better than wolves
//		public static final MinigameColor WOOL_WHITE = new MinigameColor(228,228,228);
//		public static final MinigameColor WOOL_ORANGE = new MinigameColor(234,126,53);
//		public static final MinigameColor WOOL_MAGENTA = new MinigameColor();
//		public static final MinigameColor WOOL_LIGHT_BLUE = new MinigameColor();
//		public static final MinigameColor WOOL_YELLOW = new MinigameColor(194,181,28);
//		public static final MinigameColor WOOL_LIME = new MinigameColor(57,186,46);
//		public static final MinigameColor WOOL_PINK = new MinigameColor();
//		public static final MinigameColor WOOL_DARK_GRAY = new MinigameColor(65,65,65);
//		public static final MinigameColor WOOL_LIGHT_GRAY = new MinigameColor(160,167,167);
//		public static final MinigameColor WOOL_CYAN = new MinigameColor();
//		public static final MinigameColor WOOL_PURPLE = new MinigameColor();
//		public static final MinigameColor WOOL_BLUE = new MinigameColor();
//		public static final MinigameColor WOOL_BROWN = new MinigameColor();
//		public static final MinigameColor WOOL_GREEN = new MinigameColor();
//		public static final MinigameColor WOOL_RED = new MinigameColor(158,43,39);
//		public static final MinigameColor WOOL_BLACK = new MinigameColor(24,20,20);
		
		public static final SelectionColor CONCRETE_WHITE = new SelectionColor(207,213,214);
		public static final SelectionColor CONCRETE_ORANGE = new SelectionColor(224,97,1);
		public static final SelectionColor CONCRETE_MAGENTA = new SelectionColor(169,48,159);
		public static final SelectionColor CONCRETE_LIGHT_BLUE = new SelectionColor(36,136,199);
		public static final SelectionColor CONCRETE_YELLOW = new SelectionColor(241,175,21);
		public static final SelectionColor CONCRETE_LIME = new SelectionColor(94,169,25);
		public static final SelectionColor CONCRETE_PINK = new SelectionColor(214,101,143);
		public static final SelectionColor CONCRETE_DARK_GRAY = new SelectionColor(55,58,62);
		public static final SelectionColor CONCRETE_LIGHT_GRAY = new SelectionColor(125,125,115);
		public static final SelectionColor CONCRETE_CYAN = new SelectionColor(21,119,136);
		public static final SelectionColor CONCRETE_PURPLE = new SelectionColor(100,32,156);
		public static final SelectionColor CONCRETE_BLUE = new SelectionColor(45,47,143);
		public static final SelectionColor CONCRETE_BROWN = new SelectionColor(96,60,32);
		public static final SelectionColor CONCRETE_GREEN = new SelectionColor(73,91,36);
		public static final SelectionColor CONCRETE_RED = new SelectionColor(142,33,33);
		public static final SelectionColor CONCRETE_BLACK = new SelectionColor(8,10,15);
		
		private static final Map<String, SelectionColor> concretes;
		static{
			concretes = new HashMap<String, SelectionColor>();
			concretes.put("CONCRETE_WHITE", SelectionColor.CONCRETE_WHITE);
			concretes.put("CONCRETE_ORANGE", SelectionColor.CONCRETE_ORANGE);
			concretes.put("CONCRETE_MAGENTA", SelectionColor.CONCRETE_MAGENTA);
			concretes.put("CONCRETE_LIGHT_BLUE", SelectionColor.CONCRETE_LIGHT_BLUE);
			concretes.put("CONCRETE_YELLOW", SelectionColor.CONCRETE_YELLOW);
			concretes.put("CONCRETE_LIME", SelectionColor.CONCRETE_LIME);
			concretes.put("CONCRETE_PINK", SelectionColor.CONCRETE_PINK);
			concretes.put("CONCRETE_DARK_GRAY", SelectionColor.CONCRETE_DARK_GRAY);
			concretes.put("CONCRETE_LIGHT_GRAY", SelectionColor.CONCRETE_LIGHT_GRAY);
			concretes.put("CONCRETE_CYAN", SelectionColor.CONCRETE_CYAN);
			concretes.put("CONCRETE_PURPLE", SelectionColor.CONCRETE_PURPLE);
			concretes.put("CONCRETE_BLUE", SelectionColor.CONCRETE_BLUE);
			concretes.put("CONCRETE_BROWN", SelectionColor.CONCRETE_BROWN);
			concretes.put("CONCRETE_GREEN", SelectionColor.CONCRETE_GREEN);
			concretes.put("CONCRETE_RED", SelectionColor.CONCRETE_RED);
			concretes.put("CONCRETE_BLACK", SelectionColor.CONCRETE_BLACK);
		}
		
		/* 
		 * Wool & Clay Colors
		 * RED(), ORANGE(), YELLOW(), GREEN(), BLUE(), PURPLE(), 
		 * LIME(), MAGENTA(), LIGHTBLUE(),
		 * PINK(), CYAN(), BROWN(),
		 * WHITE(), LIGHTGRAY(), GRAY(), BLACK();
		 */
		
		public static Collection<SelectionColor> getDefaults(){
			return defaults.values();
		}
		
		public static Collection<SelectionColor> getConcretes(){
			return concretes.values();
		}
		
		public static ChatColor getNearestChatColor(int red, int green, int blue) {
			double distance = 500;
			ChatColor closest = ChatColor.WHITE;
			for (SelectionColor c : defaults.values()) {
				double newDistance = 
						Math.sqrt(Math.pow((double) (red - c.getRed()), 2)
								+ Math.pow((double) (green - c.getGreen()), 2)
								+ Math.pow((double) (blue - c.getBlue()), 2));
				if (newDistance < distance) {
					distance = newDistance;
					closest = c.getChatColor();
				}
			}
			return closest;
		}
		
		public static SelectionColor valueOf(String colorName){
			for(String color : defaults.keySet()){
				if(colorName.equalsIgnoreCase(color)
				|| colorName.equalsIgnoreCase(color.replaceAll("_", ""))){
					return defaults.get(color);
				}
			}
			System.out.println("No color found: " + colorName);
			return SelectionColor.WHITE;
		}
	
		//------------------------------------------------------------------//

		private ChatColor chatColor;
		
		public SelectionColor(int red, int green, int blue){
			this(red, green, blue, getNearestChatColor(red, green, blue));
		}
		
		public SelectionColor(int red, int green, int blue, ChatColor chatColor){
			super(red, green, blue);
			this.chatColor = chatColor;
		}
		
		public SelectionColor(org.bukkit.Color color) {
			this(color.getRed(), color.getGreen(), color.getBlue());
		}
		
		public ChatColor getChatColor(){
			return chatColor;
		}
		
		public Team getGlowingTeam() {
			return ColoredScoreboardTeams.getGlowingTeamForChatColor(chatColor);
		}
		
		public void setChatColor(ChatColor chatColor){
			this.chatColor = chatColor;
		}
		
		public org.bukkit.Color getBukkitColor(){
			return org.bukkit.Color.fromRGB(this.getRed(), this.getGreen(), this.getBlue());
		}
		
		@Override
		public String toString() {
			return chatColor + "";
		}
	
}

