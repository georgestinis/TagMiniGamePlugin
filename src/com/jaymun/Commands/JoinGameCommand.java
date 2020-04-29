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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (P_COUNT < 2) {
				player[P_COUNT] = (Player) sender;				
			}
			P_COUNT++;
			if (P_COUNT < 2) {
				player[P_COUNT-1].sendMessage("Waiting for another player - " + P_COUNT + "/2");
			}
			else if (P_COUNT == 2) {
				player[0].sendMessage("Game starting soon - " + P_COUNT + "/2");
				player[1].sendMessage("Game starting soon - " + P_COUNT + "/2");
				TagMiniGamePlugin.listener = new Listeners(player);
				plugin.getServer().getPluginManager().registerEvents(TagMiniGamePlugin.listener, plugin);
			}
			else if (P_COUNT > 2) {
				sender.sendMessage("Hey it's already filled up");
			}
		}
		return true;
	}

}
