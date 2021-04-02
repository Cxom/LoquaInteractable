package net.punchtree.loquainteractable.displayutil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class ColoredScoreboardTeams {

	private static ScoreboardManager manager;
	private static Scoreboard scoreboard;
	
	public static Team PINK_TEAM;
	public static Team RED_TEAM;

	public static void initializeTeams() {
		manager = Bukkit.getScoreboardManager();
		scoreboard = manager.getMainScoreboard();
		if (PINK_TEAM == null) {
			PINK_TEAM = initializeTeam("pinkTeam", ChatColor.LIGHT_PURPLE);
		}
		if (RED_TEAM == null) {
			RED_TEAM = initializeTeam("redTeam", ChatColor.RED);
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
