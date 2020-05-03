package com.jaymun.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.jaymun.TagMiniGamePlugin;
import com.jaymun.Commands.JoinGameCommand;
import com.jaymun.players.PlayerType;

public class Listeners implements Listener{
	private Plugin plugin = TagMiniGamePlugin.getPlugin(TagMiniGamePlugin.class);
	private boolean round_started = false; 
	private PlayerType pt[] = new PlayerType[2];
	private int p1_rounds = 0, p2_rounds = 0, time;
	protected BukkitTask task, tracker_task;
	private Player[] players = new Player[2];
	private World world;
	private static final int ROUND_TIME = 120;
	
	// Set the players and get the world, then set the player's type and start the game
	public Listeners(Player[] player) {
		this.players = player;
		world = players[0].getPlayer().getWorld();
		gameOn();
		playerJoin();
	}
	
	// Calling start game function in 7 seconds 
	public void playerJoin() {
		for (Player player : players) {
			player.sendMessage(ChatColor.RED + "Game starts in 10 seconds");
		}
		Bukkit.getScheduler().runTaskLater(plugin, ()-> startGame(), 140);
	}
	
	// If you are the hunter and you hold a compass it shows you the coords of the hunted
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event){  
		// Check if the round is started otherwise do nothing
		if (isRound_started()) {
			// Create a new task that runs every 5 seconds
	        tracker_task = new BukkitRunnable() {
				@Override
				public void run() {
					// Get the player and the item that he/she holds in main hand
					Player p = event.getPlayer();
			        ItemStack i = p.getInventory().getItemInMainHand();
			        // If it's a compass find the hunted player and get his/her coordinates and show them in hunter's chat
					if(i.getType() == Material.COMPASS){
						double closest = Double.MAX_VALUE;
						Player closestp = null;					
			        	for (PlayerType player_type : pt) {
			        		if (player_type.getType().equals("Hunted")) {
			        			double dist = player_type.getPlayer().getLocation().distance(p.getLocation());
								if (closest == Double.MAX_VALUE || dist < closest) {
									closest = dist;
									closestp = player_type.getPlayer();
								}
			        		}
			        	}
						if (closestp != null) {
							p.setCompassTarget(closestp.getLocation());	
						}				        	

			        }
					// If it's not a compass cancel the task and the event
			        else {
			        	cancel();
			        	event.setCancelled(true);
			        }
				}			
			}.runTaskTimer(plugin, 0L, 20L);
		}
    }
	
	// Check if the hunter tagged the hunted and end the game
	@EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
		// Check if the round is started otherwise disable hitting event
		if(isRound_started()) {
			// Check if the damager and the damaged are players
	        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
	            Player whoHit = (Player) event.getDamager();
	            for (int i=0; i<pt.length; i++) {
	            	// Check if the damager is the Hunter and so he wins the round
	            	if (pt[i].getPlayer().getName().equals(whoHit.getName()) && pt[i].getType().equals("Hunter")) {
	            		// Won the round, stop the timer if there is time left and change roles
	            		setRound_started(false);
	            		stopTimer();
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
	            		// Call the next round
	            		nextRound();
						break;
	            	}
	            }
	        }
		}
		else {
			event.setCancelled(true);
		}
    }
	// Check who died to give the round to his opponent
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		// Check if the round is started otherwise do nothing
		if(isRound_started()) {
			// Check if the dead entity is a player
			if (event.getEntity() instanceof Player) {
				// If he/she is a player then end the round give the win to the opponent and stop the timer
				Player killed = event.getEntity();
				setRound_started(false);
				stopTimer();
				for (int i=0; i<pt.length; i++) {
					// Check if the killed player is the Hunter and then change roles
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
	            		// Call the next round
		        		nextRound();
						break;
					}
				}
			}
		}
	}
	
	// Starts the rounds of the game
	public void startGame(){
		// Find some random coordinates to create the arena
		int x = getRandom(-10000, 10000);
		int z = getRandom(-10000, 10000);
		// This is to get the player more at the center of the arena
		int x1 = x+70;
		int z1 = z+70;
		for (Player player : players) {
			// For every player we get the ground Coordinate
			int y = world.getHighestBlockAt(x, z).getY();
			// Create a new location
			Location l = new Location(world, x, y, z);
			// Load the chunks at this location and reset player's health and food
			world.getChunkAt(l).load();
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
			player.setFoodLevel(20);
			// Check if the chunks are loaded and then wait a few seconds to be sure and then teleport the player
			if (world.getChunkAt(l).isLoaded()) {
				Bukkit.getScheduler().runTaskLater(plugin, ()->{					
					player.teleport(l);	
					// Clear their inventories and give them tools
					player.getInventory().clear();
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_AXE));
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_SHOVEL));
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
					player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 20));
					// The hunter gets a compass as well
					for (PlayerType p_type : pt) {
						if (p_type.getType().equals("Hunter") && player.getName().equals(p_type.getPlayer().getName())) {
							p_type.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS, 1));
							break;
						}
					}
					// Start the round
					setRound_started(true);
				}, 60);	 
				x+=50;
				z+=50;
			}
		}		
		
		for (Player player : players) {
			player.sendMessage(ChatColor.RED + "Game starts in 3 seconds");
		}
		// Set how much time has the round and start the countdown
		setTimer(ROUND_TIME);
		Bukkit.getScheduler().runTaskLater(plugin, ()->	startTimer(), 60);	 
		Location l1;
		// Build the arena
		for (int i = z1; i>(z1-100); i--) {
			for (int j = 0; j<150; j++) {
				l1 = new Location(world, x1, j, i);
				if (world.getBlockAt(l1).getType().isAir() || 
					world.getBlockAt(l1).getType()==Material.WATER || 
					world.getBlockAt(l1).getType()==Material.LAVA) {
					world.getBlockAt(l1).setType(Material.GLASS);
				}
				l1 = new Location(world, (x1-100), j, i);
				if (world.getBlockAt(l1).getType().isAir() || 
					world.getBlockAt(l1).getType()==Material.WATER || 
					world.getBlockAt(l1).getType()==Material.LAVA) {
					world.getBlockAt(l1).setType(Material.GLASS);
				}					
			}
		}
		
		for (int i = x1; i>(x1-100); i--) {
			for (int j = 0; j<150; j++) {
				l1 = new Location(world, i, j, z1);
				if (world.getBlockAt(l1).getType().isAir() || 
					world.getBlockAt(l1).getType()==Material.WATER || 
					world.getBlockAt(l1).getType()==Material.LAVA) {
					world.getBlockAt(l1).setType(Material.GLASS);
				}
				l1 = new Location(world, i, j, (z1-100));
				if (world.getBlockAt(l1).getType().isAir() || 
					world.getBlockAt(l1).getType()==Material.WATER || 
					world.getBlockAt(l1).getType()==Material.LAVA) {
					world.getBlockAt(l1).setType(Material.GLASS);
				}
			}
		}
	}
	
	// Timer for the round
	public void startTimer() {
		// Run the task every second
		task = new BukkitRunnable() {
			@Override
			public void run() {
				// If time is zero round ends and the hunted wins the round
				if (time == 0) {
					for (Player player : players) {
						player.sendMessage(ChatColor.RED + "Time is up!");
					}
					// Cancel the task
					cancel();
					setRound_started(false);
					// Change roles
					for (int p=0; p<pt.length; p++) {
						if (pt[p].getType().equals("Hunted")) {
							switch (p) {
								case 0:
				            		pt[0].setType("Hunter");
				            		pt[1].setType("Hunted");			        
									setP1_rounds(getP1_rounds() + 1);
									break;
								case 1:
									pt[1].setType("Hunter");
				            		pt[0].setType("Hunted");
									setP2_rounds(getP2_rounds() + 1);
								default:
									break;
							}	
							// Check for next round
							nextRound();
							return;
						}
					}
				}
				// Else show how much time left
				else if (time == 30 || time == 15 || time == 60) {
					for (Player player : players) {
						player.sendMessage(ChatColor.RED + "Time remaining " + time + " seconds");
					}
				}
				else if ( time <= 5 && time > 0) {
					for (Player player : players) {
						player.sendMessage(ChatColor.RED + "Round ends in " + time + " seconds");
					}
				}
				time--;
			}			
		}.runTaskTimer(plugin, 0L, 20L);
	}
	
	public void stopTimer() {
		task.cancel();		
    }
	
	// Check for next round
	public void nextRound() {
		//if (getP1_rounds() < 7 && getP2_rounds() < 7) {
		// If no one has 2 wins then show their role for the next round and show their rounds
		if (getP1_rounds() < 2 && getP2_rounds() < 2) {
			for (Player player : players) {
				player.sendMessage(ChatColor.BOLD + pt[0].getPlayer().getName() + " - " + ChatColor.GOLD + "Rounds: " + getP1_rounds());
				player.sendMessage(ChatColor.BOLD + pt[1].getPlayer().getName() + " - " + ChatColor.GOLD + "Rounds: " + getP2_rounds());
				player.sendMessage(ChatColor.GOLD + pt[0].getPlayer().getName() + ChatColor.WHITE + "" + ChatColor.BOLD + " - " + pt[0].getType());
				player.sendMessage(ChatColor.GOLD + pt[1].getPlayer().getName() + ChatColor.WHITE + "" + ChatColor.BOLD + " - " + pt[1].getType());
				player.sendMessage(ChatColor.RED + "Next round in 10 seconds");
			}			
			// Start the game
			Bukkit.getScheduler().runTaskLater(plugin, ()-> startGame(), 140);
		}
		else {
			// Show the winner
			if (getP1_rounds() == 2) {
				for (Player player : players) {
					player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + pt[0].getPlayer().getName() + " - Is the winner");					
				}	
			}
			else {
				for (Player player : players) {
					player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + pt[1].getPlayer().getName() + " - Is the winner");					
				}
			}
			for (Player player : players) {
				player.sendMessage(ChatColor.RED + "Teleporting back to spawn in 5 seconds");					
			}
			// Teleport the players back to spawn
			Bukkit.getScheduler().runTaskLater(plugin, ()->{
				Location spawn = world.getSpawnLocation();		            			
				for (int p =0; p<pt.length; p++) {
					pt[p].getPlayer().setHealth(pt[p].getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
    				pt[p].getPlayer().setFoodLevel(20);
    				pt[p].getPlayer().teleport(spawn);
    				pt[p] = null;
    			}
				// Reset rounds, players for the game and unregister this listener
    			setP1_rounds(0);
    			setP2_rounds(0);
    			JoinGameCommand.P_COUNT = 0;
    			HandlerList.unregisterAll(TagMiniGamePlugin.LISTENER);
			}, 100);	   			
		}
	}
	
	public int getRandom(int lowest, int highest) {
        Random random = new Random();
        return random.nextInt((highest - lowest) + 1) + lowest;  //99
	}
	
	// Initialize players for the first round, set the Hunter and the Hunted
	public void gameOn() {
		int i = 0;
		loop:
		for (Player p : players) {
			switch (i) {
				case 0:
					pt[i] = new PlayerType(p, "Hunter", true);
					break;
				case 1:
					pt[i] = new PlayerType(p, "Hunted", true);
					break;
				default:
					break loop;
			}
			i++;			
		}	
		// Show each player's type
		for (Player p : players) {
			p.sendMessage(ChatColor.GOLD + pt[0].getPlayer().getName() + 
					ChatColor.WHITE + "" + ChatColor.BOLD + " - " + pt[0].getType());
			p.sendMessage(ChatColor.GOLD + pt[1].getPlayer().getName() + 
					ChatColor.WHITE + "" + ChatColor.BOLD + " - " + pt[1].getType());
		}
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

	public void setTimer(int amount) {
        time = amount;
    }
}
