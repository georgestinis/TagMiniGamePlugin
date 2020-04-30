package com.jaymun;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaymun.Commands.JoinGameCommand;
import com.jaymun.listeners.Listeners;

public class TagMiniGamePlugin extends JavaPlugin{
	private static TagMiniGamePlugin instance;
	public static Listeners LISTENER;
	public static int PLAYERS_SIZE;
	
	public TagMiniGamePlugin getInstance() {
		return TagMiniGamePlugin.instance;
	}
	
	public void setPlayers(int i) {
		TagMiniGamePlugin.PLAYERS_SIZE = i;
	}
	
	public int getPlayers() {
		return TagMiniGamePlugin.PLAYERS_SIZE;
	}
	
	// On enable a command which starts the game is added to the commands
	@Override
	public void onEnable() {
		instance = this;
		instance.getCommand("join_minigame").setExecutor((CommandExecutor)new JoinGameCommand());
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}

	public static Listeners getListener() {
		return LISTENER;
	}

	public static void setListener(Listeners listener) {
		TagMiniGamePlugin.LISTENER = listener;
	}
}
