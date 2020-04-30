package com.jaymun.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.jaymun.TagMiniGamePlugin;
import com.jaymun.listeners.Listeners;

public class JoinGameCommand implements CommandExecutor{
	public static int P_COUNT = 0;
	private Player player[] = new Player[2];
	private Plugin plugin = TagMiniGamePlugin.getPlugin(TagMiniGamePlugin.class);
	
	/* When you press /join_game this functions checks if the counted players are less than 2 and it adds the player, if they are 2
	so you can start the game and if they are more than 2 then send a message to the last one*/
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Check if the sender is a player
		if (sender instanceof Player) {
			// If sender is a player then check if the player count is <2 and add the player
			if (P_COUNT < 2) {
				player[P_COUNT] = (Player) sender;				
			}			
			P_COUNT++;
			// Show a message to the player who is waiting
			if (P_COUNT < 2) {
				player[P_COUNT-1].sendMessage("Waiting for another player - " + P_COUNT + "/2");
			}
			// If the 2 players are filled then the game is starting
			else if (P_COUNT == 2) {
				player[0].sendMessage("Game starting soon - " + P_COUNT + "/2");
				player[1].sendMessage("Game starting soon - " + P_COUNT + "/2");
				// Initializing the listener for the game
				TagMiniGamePlugin.LISTENER = new Listeners(player);
				plugin.getServer().getPluginManager().registerEvents(TagMiniGamePlugin.LISTENER, plugin);
			}
			// If another player tries to join sends a message that the game is full
			else if (P_COUNT > 2) {
				sender.sendMessage("Hey it's already filled up");
			}
		}
		return true;
	}

}
