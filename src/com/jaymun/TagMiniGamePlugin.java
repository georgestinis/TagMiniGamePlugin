package com.jaymun;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaymun.Commands.JoinGameCommand;
import com.jaymun.listeners.Listeners;

public class TagMiniGamePlugin extends JavaPlugin{
	private static TagMiniGamePlugin instance;
	public static Listeners listener;
	public static int players_size;
	public TagMiniGamePlugin getInstance() {
		return TagMiniGamePlugin.instance;
	}
	
	public void setPlayers(int i) {
		TagMiniGamePlugin.players_size = i;
	}
	
	public int getPlayers() {
		return TagMiniGamePlugin.players_size;
	}
	
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
		return listener;
	}

	public static void setListener(Listeners listener) {
		TagMiniGamePlugin.listener = listener;
	}
}
