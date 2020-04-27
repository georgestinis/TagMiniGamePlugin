package com.jaymun.listeners;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.jaymun.TagMiniGamePlugin;
import com.jaymun.players.PlayerType;

public class Listeners implements Listener{
	private Plugin plugin = TagMiniGamePlugin.getPlugin(TagMiniGamePlugin.class);
	private boolean game_started = false;
	private boolean round_started = false; 
	private PlayerType pt[] = new PlayerType[2];
	private int p1_rounds = 0, p2_rounds = 0;
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		TagMiniGamePlugin.players_size = plugin.getServer().getOnlinePlayers().size();
		System.out.println("Players: " + TagMiniGamePlugin.players_size);
		World world = event.getPlayer().getWorld();
		if(TagMiniGamePlugin.players_size == 2) {
			Bukkit.getScheduler().runTaskLater(plugin, ()-> startGame(world), 200);
		}
	}
	//Check if the hunter tagged the hunted and end the game
	@EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
		if(isGame_started() && isRound_started()) {
	        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
	            Player whoHit = (Player) event.getDamager();
	            for (int i=0; i<pt.length; i++) {
	            	System.out.println("True or False : " + pt[i].getPlayer().getName().equals(whoHit.getName()) + " && " + pt[i].getType().equals("Hunter"));
	            	if (pt[i].getPlayer().getName().equals(whoHit.getName()) && pt[i].getType().equals("Hunter")) {
	            		//Won the round
	            		setRound_started(false);
	            		switch (i) {
							case 0:
			            		pt[0].setType("Hunted");
			            		pt[1].setType("Hunter");			        
								setP1_rounds(getP1_rounds() + 1);
								break;
							case 1:
								pt[1].setType("Hunted");
			            		pt[0].setType("Hunter");
								setP2_rounds(getP2_rounds() + 1);
							default:
								break;
						}	            		
	            		//if (getP1_rounds() < 7 && getP2_rounds() < 7) {
						if (getP1_rounds() < 2 && getP2_rounds() < 2) {
							plugin.getServer().broadcastMessage(pt[0].getPlayer().getName() + " - Rounds: " + getP1_rounds());
		            		plugin.getServer().broadcastMessage(pt[1].getPlayer().getName() + " - Rounds: " + getP2_rounds());
							plugin.getServer().broadcastMessage(pt[0].getPlayer().getName() + " - " + pt[0].getType());
							plugin.getServer().broadcastMessage(pt[1].getPlayer().getName() + " - " + pt[1].getType());
							System.out.println("P1 : " + getP1_rounds() + "P2:" + getP2_rounds());
	            			Bukkit.getScheduler().runTaskLater(plugin, ()-> startGame(whoHit.getWorld()), 200);
	            		}
	            		else {
	            			// TODO show the winner
							if (getP1_rounds() == 2) {
								plugin.getServer().broadcastMessage(pt[0].getPlayer().getName() + " - Is the winner");
							}
							else {
								plugin.getServer().broadcastMessage(pt[1].getPlayer().getName() + " - Is the winner");
							}
	            			Bukkit.getScheduler().runTaskLater(plugin, ()->{
								Location spawn = whoHit.getWorld().getSpawnLocation();		            			
								for (int p =0; p<pt.length; p++) {
									pt[p].getPlayer().setHealth(pt[p].getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
		            				pt[p].getPlayer().setFoodLevel(20);
		            				pt[p].getPlayer().teleport(spawn);
		            				pt[p] = null;
		            			}
		            			setGame_started(false);
		            			setP1_rounds(0);
		            			setP2_rounds(0);
							}, 200);	   
	            			
	            		}
	            	}
	            }
	        }
		}
		else {
			event.setCancelled(true);
		}
    }
	//Check who died to give the round to his opponent
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if(isGame_started() && isRound_started()) {
			if (event.getEntity() instanceof Player) {
				Player killed = event.getEntity();
				setRound_started(false);
				for (int i=0; i<pt.length; i++) {
					if (pt[i].getPlayer().getName().equals(killed.getName())) {
						switch (i) {
							case 0:
								if (pt[0].getType().equals("Hunter")) {
									pt[0].setType("Hunted");
									pt[1].setType("Hunter");
								}
								else {
									pt[0].setType("Hunter");
									pt[1].setType("Hunted");
								}
								setP2_rounds(getP2_rounds() + 1);
								break;
							case 1:
								if (pt[1].getType().equals("Hunter")) {
									pt[0].setType("Hunter");
									pt[1].setType("Hunted");
								}
								else {
									pt[0].setType("Hunted");
									pt[1].setType("Hunter");
								}
								setP1_rounds(getP1_rounds() + 1);
							default:
								break;
						}
		        		//if (getP1_rounds() < 7 && getP2_rounds() < 7) {
						if (getP1_rounds() < 2 && getP2_rounds() < 2) {
							plugin.getServer().broadcastMessage(pt[0].getPlayer().getName() + " - Rounds: " + getP1_rounds());
		            		plugin.getServer().broadcastMessage(pt[1].getPlayer().getName() + " - Rounds: " + getP2_rounds());
							plugin.getServer().broadcastMessage(pt[0].getPlayer().getName() + " - " + pt[0].getType());
							plugin.getServer().broadcastMessage(pt[1].getPlayer().getName() + " - " + pt[1].getType());
							System.out.println("P1 : " + getP1_rounds() + "P2:" + getP2_rounds());
		        			Bukkit.getScheduler().runTaskLater(plugin, ()-> startGame(killed.getWorld()), 200);
		        		}
						else {
							// TODO show the winner
							if (getP1_rounds() == 2) {
								plugin.getServer().broadcastMessage(pt[0].getPlayer().getName() + " - Is the winner");
							}
							else {
								plugin.getServer().broadcastMessage(pt[1].getPlayer().getName() + " - Is the winner");
							}
							Bukkit.getScheduler().runTaskLater(plugin, ()->{
								Location spawn = killed.getWorld().getSpawnLocation();		            			
		            			for (int p =0; p<pt.length; p++) {
		            				pt[p].getPlayer().setHealth(pt[p].getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
		            				pt[p].getPlayer().setFoodLevel(20);
		            				pt[p].getPlayer().teleport(spawn);
		            				pt[p] = null;
		            			}
		            			setGame_started(false);
		            			setP1_rounds(0);
		            			setP2_rounds(0);
							}, 200);	            			
	            		}
					}
				}
			}
		}
	}
	
	public void startGame(World world){
		Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
		int x = getRandom(-10000, 10000);
		int z = getRandom(-10000, 10000);
		int x1 = x+70;
		int z1 = z+70;
		for (Player player : players) {
			int y = player.getWorld().getHighestBlockAt(x, z).getY();
			System.out.println("Y = " + y);
			Location l = new Location(world, x, y, z);
			world.getChunkAt(l).load();
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
			player.setFoodLevel(20);
			if (world.getChunkAt(l).isLoaded()) {
				Bukkit.getScheduler().runTaskLater(plugin, ()->{
					player.teleport(l);	
					player.getInventory().clear();
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_AXE));
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_SHOVEL));
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
					player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 20));
					setRound_started(true);
				}, 60);	 
				x+=50;
				z+=50;
			}
		}

		for (int i = z1; i>(z1-100); i--) {
			for (int j = 0; j<150; j++) {
				if (world.getBlockAt(new Location(world, x1, j, i)).getType().isAir() || 
					world.getBlockAt(new Location(world, x1, j, i)).getType()==Material.WATER || 
					world.getBlockAt(new Location(world, x1, j, i)).getType()==Material.LAVA) {
					world.getBlockAt(new Location(world, x1, j, i)).setType(Material.GLASS);
				}
				if (world.getBlockAt(new Location(world, (x1-100), j, i)).getType().isAir() || 
					world.getBlockAt(new Location(world, (x1-100), j, i)).getType()==Material.WATER || 
					world.getBlockAt(new Location(world, (x1-100), j, i)).getType()==Material.LAVA) {
					world.getBlockAt(new Location(world, (x1-100), j, i)).setType(Material.GLASS);
				}					
			}
		}
		
		for (int i = x1; i>(x1-100); i--) {
			for (int j = 0; j<150; j++) {
				if (world.getBlockAt(new Location(world, i, j, z1)).getType().isAir() || 
					world.getBlockAt(new Location(world, i, j, z1)).getType()==Material.WATER || 
					world.getBlockAt(new Location(world, i, j, z1)).getType()==Material.LAVA) {
					world.getBlockAt(new Location(world, i, j, z1)).setType(Material.GLASS);
				}
				if (world.getBlockAt(new Location(world, i, j, (z1-100))).getType().isAir() || 
					world.getBlockAt(new Location(world, i, j, (z1-100))).getType()==Material.WATER || 
					world.getBlockAt(new Location(world, i, j, (z1-100))).getType()==Material.LAVA) {
					world.getBlockAt(new Location(world, i, j, (z1-100))).setType(Material.GLASS);
				}
			}
		}
		if (!isGame_started()) {
			gameOn(players);
		}
	}
	
	public int getRandom(int lowest, int highest) {
        Random random = new Random();
        return random.nextInt((highest - lowest) + 1) + lowest;  //99
	}
	
	public void gameOn(Collection<? extends Player> players) {
		setGame_started(true);
		int i = 0;
		loop:
		for (Player p : players) {
			switch (i) {
				case 0:
					pt[i] = new PlayerType(p, "Hunter", true);
					System.out.println(pt[i].getPlayer().getName() + " einai " + pt[i].getType());
					plugin.getServer().broadcastMessage(pt[i].getPlayer().getName() + " - " + pt[i].getType());
					break;
				case 1:
					pt[i] = new PlayerType(p, "Hunted", true);
					System.out.println(pt[i].getPlayer().getName() + " einai " + pt[i].getType());
					plugin.getServer().broadcastMessage(pt[i].getPlayer().getName() + " - " + pt[i].getType());
					break;
				default:
					System.out.println("Οι παίκτες συμπληρώθηκαν");
					break loop;
			}			
			i++;
		}
	}
	
	public boolean isGame_started() {
		return game_started;
	}

	public void setGame_started(boolean game_started) {
		this.game_started = game_started;
	}

	public int getP1_rounds() {
		return p1_rounds;
	}

	public void setP1_rounds(int p1_rounds) {
		this.p1_rounds = p1_rounds;
	}

	public int getP2_rounds() {
		return p2_rounds;
	}

	public void setP2_rounds(int p2_rounds) {
		this.p2_rounds = p2_rounds;
	}
	public boolean isRound_started() {
		return round_started;
	}
	public void setRound_started(boolean round_started) {
		this.round_started = round_started;
	}

}
