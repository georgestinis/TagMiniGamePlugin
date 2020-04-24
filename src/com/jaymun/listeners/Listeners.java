package com.jaymun.listeners;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.jaymun.TagMiniGamePlugin;

public class Listeners implements Listener{
	private Plugin plugin = TagMiniGamePlugin.getPlugin(TagMiniGamePlugin.class);
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		TagMiniGamePlugin.players_size = plugin.getServer().getOnlinePlayers().size();
		System.out.println("Players: " + TagMiniGamePlugin.players_size);
		Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
		if(TagMiniGamePlugin.players_size == 1) {
			System.out.println("Players: " + TagMiniGamePlugin.players_size);
			int x = getRandom(-10000, 10000);
			int y = getRandom(60, 70);
			int z = getRandom(-10000, 10000);
			World world = event.getPlayer().getWorld();
			Location l = new Location(world, x, y, z);
			for (Player player : players) {
				player.teleport(l);
				player.getInventory().clear();
				player.getInventory().addItem(new ItemStack(Material.DIAMOND_AXE));
				player.getInventory().addItem(new ItemStack(Material.DIAMOND_SHOVEL));
				player.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
				player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 20));				
				l.setX(x+100);
				l.setZ(z+100);
			}
			
			int x1 = x+140;
			int z1 = z+140;

			for (int i = z1; i>(z1-200); i--) {
				for (int j = 0; j<150; j++) {
					if (world.getBlockAt(new Location(world, x1, j, i)).getType().isAir() || 
						world.getBlockAt(new Location(world, x1, j, i)).getType()==Material.WATER || 
						world.getBlockAt(new Location(world, x1, j, i)).getType()==Material.LAVA) {
						world.getBlockAt(new Location(world, x1, j, i)).setType(Material.GLASS);
					}
					if (world.getBlockAt(new Location(world, (x1-200), j, i)).getType().isAir() || 
						world.getBlockAt(new Location(world, (x1-200), j, i)).getType()==Material.WATER || 
						world.getBlockAt(new Location(world, (x1-200), j, i)).getType()==Material.LAVA) {
						world.getBlockAt(new Location(world, (x1-200), j, i)).setType(Material.GLASS);
					}					
				}
			}
			
			for (int i = x1; i>(x1-200); i--) {
				for (int j = 0; j<150; j++) {
					if (world.getBlockAt(new Location(world, i, j, z1)).getType().isAir() || 
						world.getBlockAt(new Location(world, i, j, z1)).getType()==Material.WATER || 
						world.getBlockAt(new Location(world, i, j, z1)).getType()==Material.LAVA) {
						world.getBlockAt(new Location(world, i, j, z1)).setType(Material.GLASS);
					}
					if (world.getBlockAt(new Location(world, i, j, (z1-200))).getType().isAir() || 
						world.getBlockAt(new Location(world, i, j, (z1-200))).getType()==Material.WATER || 
						world.getBlockAt(new Location(world, i, j, (z1-200))).getType()==Material.LAVA) {
						world.getBlockAt(new Location(world, i, j, (z1-200))).setType(Material.GLASS);
					}
				}
			}
			
			
			
		}
	}
	
	public int getRandom(int lowest, int highest) {
        Random random = new Random();
        return random.nextInt((highest - lowest) + 1) + lowest;  //99
	}

}
