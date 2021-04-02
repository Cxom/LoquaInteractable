package net.punchtree.loquainteractable.displayutil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class ColoredScoreboardTeams {

	private static ScoreboardManager manager;
	private static Scoreboard scoreboard;
	
	public static Team AQUA_TEAM;
	public static Team BLACK_TEAM;
	public static Team BLUE_TEAM;
	public static Team DARK_AQUA_TEAM;
	public static Team DARK_BLUE_TEAM;
	public static Team DARK_GRAY_TEAM;
	public static Team DARK_GREEN_TEAM;
	public static Team DARK_PURPLE_TEAM;
	public static Team DARK_RED_TEAM;
	public static Team GOLD_TEAM;
	public static Team GRAY_TEAM;
	public static Team GREEN_TEAM;
	public static Team LIGHT_PURPLE_TEAM;
	public static Team RED_TEAM;
	public static Team WHITE_TEAM;
	public static Team YELLOW_TEAM;

	public static void initializeTeams() {
		manager = Bukkit.getScoreboardManager();
		scoreboard = manager.getMainScoreboard();
		if (AQUA_TEAM == null) {
			AQUA_TEAM = initializeTeam("aquaTeam", ChatColor.AQUA);
			BLACK_TEAM = initializeTeam("blackTeam", ChatColor.BLACK);
			BLUE_TEAM = initializeTeam("blueTeam", ChatColor.BLUE);
			DARK_AQUA_TEAM = initializeTeam("darkAquaTeam", ChatColor.DARK_AQUA);
			DARK_BLUE_TEAM = initializeTeam("darkBlueTeam", ChatColor.DARK_BLUE);
			DARK_GRAY_TEAM = initializeTeam("darkGrayTeam", ChatColor.DARK_GRAY);
			DARK_GREEN_TEAM = initializeTeam("darkGreenTeam", ChatColor.DARK_GREEN);
			DARK_PURPLE_TEAM = initializeTeam("darkPurpleTeam", ChatColor.DARK_PURPLE);
			DARK_RED_TEAM = initializeTeam("darkRedTeam", ChatColor.DARK_RED);
			GOLD_TEAM = initializeTeam("goldTeam", ChatColor.GOLD);
			GRAY_TEAM = initializeTeam("grayTeam", ChatColor.GRAY);
			GREEN_TEAM = initializeTeam("greenTeam", ChatColor.GREEN);
			LIGHT_PURPLE_TEAM = initializeTeam("lightPurpleTeam", ChatColor.LIGHT_PURPLE);
			RED_TEAM = initializeTeam("redTeam", ChatColor.RED);
			WHITE_TEAM = initializeTeam("whiteTeam", ChatColor.WHITE);
			YELLOW_TEAM = initializeTeam("yellowTeam", ChatColor.YELLOW);
		}
	}
	
	private static Team initializeTeam(String teamName, ChatColor chatColor) {
		Team team = scoreboard.getTeam(teamName);
		if (team == null) {				
			team = scoreboard.registerNewTeam(teamName);
		}
		team.setColor(chatColor);
		return team;
	}
	
}