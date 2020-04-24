package com.jaymun;

import org.bukkit.plugin.java.JavaPlugin;

import com.jaymun.listeners.Listeners;

public class TagMiniGamePlugin extends JavaPlugin{
	private static TagMiniGamePlugin instance;
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
		getServer().getPluginManager().registerEvents(new Listeners(), this);
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}
}
