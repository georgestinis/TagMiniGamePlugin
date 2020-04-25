package com.jaymun.players;

import org.bukkit.entity.Player;

public class PlayerType{
	private Player player;
	private String type;
	private Boolean inGame;
	
	public PlayerType(Player player, String type, Boolean inGame) {
		this.player = player;
		this.type = type;
		this.setInGame(inGame);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	public Boolean getInGame() {
		return inGame;
	}

	public void setInGame(Boolean inGame) {
		this.inGame = inGame;
	}
}
