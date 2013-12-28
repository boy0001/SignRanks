package com.empcraft;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
//import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
public final class SignRanks extends JavaPlugin implements Listener {
	
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;

    Timer timer = new Timer ();
	TimerTask mytask = new TimerTask () {
		String mymode;
		@Override
	    public void run () {
			double paymoney;
			Set<String> groups = getConfig().getConfigurationSection("economy.salary.groups").getKeys(false);
	    	for(Player player:getServer().getOnlinePlayers()){
	    		if (getConfig().getInt("economy.salary.notify-level")>1) {
	    			String message = getConfig().getString("economy.salary.message");
	    			if (message != "") {
	    				player.sendMessage(message);
	    			}
	    		}
				int payexp = 0;
				String amount = "";
				String amount2 = "";
				String amount3 = "";
				int paylvl = 0;
	    		paymoney = 0;
	    		for(String group : groups) {
	    			boolean has = false;
	    			if (getConfig().getString("economy.salary.check-subgroups").equalsIgnoreCase("true")) {
	  	          	  String[] mygroups = perms.getPlayerGroups(player);
		        	  if (Arrays.asList(mygroups).contains(group)) {
		        		  has = true;
		        	  }
		        	  else {
		        	  }
	    			}
	    			else if (perms.getPrimaryGroup(player)==group) {
	    				has = true;
	    			}
	    			if (has) {
	    				if (true)
	    				{
	    					String cost = getConfig().getString(("economy.salary.groups."+group+".exp.base"));
	    				  	  if (cost.contains(" exp")) {
	    				  		  cost = cost.substring(0,cost.length() - 4);
	    				  		  payexp += Integer.parseInt(cost);
	    				  	  }
	    				  	  else if (cost.contains(" lvl")) {
	    				  		  cost = cost.substring(0,cost.length() - 4);
	    				  		  paylvl += Integer.parseInt(cost);
	    				  	  }
	    					cost = getConfig().getString(("economy.salary.groups."+group+".exp.bonus"));
	    				  	  if (cost.contains(" exp")) {
	    				  		  cost = cost.substring(0,cost.length() - 4);
	    				  		  payexp += Integer.parseInt(cost)*(getServer().getOnlinePlayers().length-1);
	    				  	  }
	    				  	  else if (cost.contains(" lvl")) {
	    				  		  cost = cost.substring(0,cost.length() - 4);
	    				  		  paylvl += Integer.parseInt(cost)*(getServer().getOnlinePlayers().length-1);
	    				  	  }
	    					payexp += (getConfig().getInt(("economy.salary.groups."+group+".exp.percentage"))*(econ.getBalance(player.getName())))/100; 			
	    				}
	    				if (true)
	    				{
	    					paymoney += getConfig().getInt(("economy.salary.groups."+group+".money.base"));
	    					paymoney += getConfig().getInt(("economy.salary.groups."+group+".money.bonus"))*(getServer().getOnlinePlayers().length-1);
	    					paymoney += (getConfig().getInt(("economy.salary.groups."+group+".money.percentage"))*(econ.getBalance(player.getName())))/100;
	    				}
	    			if ((paymoney + payexp + paylvl!=0)&&(getConfig().getInt("economy.salary.notify-level")>1)) {
	    				player.sendMessage("    "+ChatColor.GRAY+getConfig().getString("economy.symbol")+paymoney+ChatColor.WHITE+", "+ChatColor.GRAY+payexp+" exp"+ChatColor.WHITE+", "+ChatColor.GRAY+paylvl+" lvl"+ChatColor.WHITE+" from group "+ChatColor.BLUE+group);
	    			}
	    			}
	    			else {
	    			}	
	    		}
				if (paymoney > 0) {
					amount = ChatColor.GRAY+"You earned: "+ChatColor.GREEN+getConfig().getString("economy.symbol")+paymoney;
					econ.depositPlayer(player.getName(), paymoney);
				}
				else if (paymoney < 0) {
					econ.withdrawPlayer(player.getName(), -paymoney);
					amount = ChatColor.GRAY+"You paid: "+ChatColor.RED+getConfig().getString("economy.symbol")+paymoney;
				}
				if (payexp > 0) {
					amount2 = ChatColor.GRAY+", gained: "+ChatColor.GREEN+payexp+" exp";
				      int myxp = player.getTotalExperience();
				      player.setTotalExperience(0);
				      player.setLevel(0);
				      player.setExp(0);
				      player.giveExp(myxp + payexp);  
				}
				else if (payexp < 0) {
					amount2 = ChatColor.GRAY+", lost: "+ChatColor.RED+payexp+" exp";
				      int myxp = player.getTotalExperience();
				      player.setTotalExperience(0);
				      player.setLevel(0);
				      player.setExp(0);
				      player.giveExp(myxp + payexp);  
				}
				if (paylvl > 0) {
					amount3 = ChatColor.GRAY+" and gained: "+ChatColor.GREEN+paylvl+" level/s";
				    player.giveExpLevels(paylvl); 
				}
				else if (paylvl < 0) {
					amount3 = ChatColor.GRAY+" and lost: "+ChatColor.RED+paylvl+" level/s";
					player.giveExpLevels(paylvl); 
				}
				if ((amount!="")||(amount2!="")||(amount3!="")) {
    				if (getConfig().getInt("economy.salary.notify-level")>0) {
    	    			player.sendMessage(amount+amount2+amount3);
    	    		}
				}
				else {
					player.sendMessage(ChatColor.GRAY+"You were paid "+ChatColor.RED+"nothing"+ChatColor.GRAY+".");
				}
	    }
		}
	};
    @Override
    public void onDisable() {
    	timer.cancel();
    	timer.purge();
    	this.reloadConfig();
    	this.saveConfig();
    }
    
	@Override
    public void onEnable(){
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
    	this.saveDefaultConfig();
    	getServer().getPluginManager().registerEvents(this, this);
    	if (getConfig().getString("economy.salary.enabled").equalsIgnoreCase("true")) {
    		if (getConfig().getInt("economy.salary.interval") > 0) {
    			timer.schedule (mytask, 0l, 1000*(getConfig().getInt("economy.salary.interval")));
    		}
    	}
	}
    
	 private boolean setupChat() {
	        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
	        chat = rsp.getProvider();
	        return chat != null;
	    }
	 @EventHandler
	 public void blockSignBreak(BlockBreakEvent event) {
		 if (this.getConfig().getString("signs.protect").equalsIgnoreCase("true")) {
		 Player player = event.getPlayer();
		 if (player.hasPermission("signranks.destroy.*")==false) {
	        if(event.getBlock().getState() instanceof Sign) {
	            Sign sign = (Sign) event.getBlock().getState();  
	            if (((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text"))))) {              
	            	player.sendMessage(ChatColor.LIGHT_PURPLE+"You broke a sign :O");
	                if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))&&(player.hasPermission("signranks.destroy.promote")==false)) {
		            	event.setCancelled(true);
		                player.sendMessage(ChatColor.GRAY+"Missing requirements: "+ChatColor.RED+"signranks.destroy.promote");
	                }
	                else if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))&&(player.hasPermission("signranks.destroy.prefix")==false)) {
		            	event.setCancelled(true);
		                player.sendMessage(ChatColor.GRAY+"Missing requirements: "+ChatColor.RED+"signranks.destroy.prefix");
	                }
	                else if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))&&(player.hasPermission("signranks.destroy.inherit")==false)) {
		            	event.setCancelled(true);
		                player.sendMessage(ChatColor.GRAY+"Missing requirements: "+ChatColor.RED+"signranks.destroy.inherit");
	                }
	                else if (((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text")))&&(player.hasPermission("signranks.destroy.xpbank")||(sign.getLine(3)==player.getName()))==false)) {
		            	if (sign.getLine(3)==player.getName()) {
		            		//TODO give them the XP that was on the sign.
		            	}
		            	else {
		            		event.setCancelled(true);
			                player.sendMessage(ChatColor.GRAY+"You need "+ChatColor.RED+"signranks.destroy.xpbanks"+ChatColor.GRAY+" to destroy "+sign.getLine(3)+"'s "+this.getConfig().getString("signs.types.xpbank.text"));
		            	}        	
	                }
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.NORTH, 1).getTypeId() == 68) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.NORTH, 1).getState();
	            if (((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text"))))) {
	                event.setCancelled(true);
	                player.sendMessage(ChatColor.RED+"Please destroy the sign directly.");
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.EAST, 1).getTypeId() == 68) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.EAST, 1).getState();
	            if (((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text"))))) {
	                event.setCancelled(true);
	                player.sendMessage(ChatColor.RED+"Please destroy the sign directly.");
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.SOUTH, 1).getTypeId() == 68) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.SOUTH, 1).getState();
	            if (((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text"))))) {
	                event.setCancelled(true);
	                player.sendMessage(ChatColor.RED+"Please destroy the sign directly.");
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.WEST, 1).getTypeId() == 68) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.WEST, 1).getState();
	            if (((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text"))))) {
	                event.setCancelled(true);
	                player.sendMessage(ChatColor.RED+"Please destroy the sign directly.");
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.UP, 1).getTypeId() == 63) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.UP, 1).getState();
	            if (((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text"))))) {
	                event.setCancelled(true);
	                player.sendMessage(ChatColor.RED+"Please destroy the sign directly.");
	            }
	        }
	 }
	 }
	 }
	@EventHandler
	public void onSignChange(SignChangeEvent event)
    {
		Block block = event.getBlock();
		Sign sign = (Sign)block.getState();
		String line1 = event.getLine(0);
		String line2 = event.getLine(1);
		String line3 = event.getLine(2);
		String line4 = event.getLine(3);
		Player player = event.getPlayer();
		getLogger().info("Sign");
		String type2 = "";
		boolean hasperm = false;
		if ((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.promote.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.promote.text")))) {
			type2 = this.getConfig().getString("signs.types.promote.text");
			if (player.hasPermission("signranks.create.promote")) {
	        	  if (Arrays.asList(perms.getGroups()).contains(line2)) {
	        		  hasperm = true;
	        	  }
	        	  else {
	        		  type2 = "";
	        		  player.sendMessage(ChatColor.GRAY+"Group not found (case sensitive): "+ChatColor.RED+line2+ChatColor.WHITE+"\nGroups: "+Arrays.asList(perms.getGroups()));
	        	  }
			}
		}
		else if ((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.inherit.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.inherit.text")))) {
			type2 = this.getConfig().getString("signs.types.inherit.text");
			if (player.hasPermission("signranks.create.inherit")) {
	        	  if (Arrays.asList(perms.getGroups()).contains(line2)) {
	        		  hasperm = true;
	        	  }
	        	  else {
	        		  type2 = "";
	        		  player.sendMessage(ChatColor.GRAY+"Group not found (case sensitive): "+ChatColor.RED+line2+ChatColor.WHITE+"\nGroups: "+Arrays.asList(perms.getGroups()));
	        	  }
			}
		}
		else if ((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.prefix.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.prefix.text")))) {
			type2 = this.getConfig().getString("signs.types.prefix.text");
			if (player.hasPermission("signranks.create.prefix")) {
				hasperm = true;
			}
		}
		else if ((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.xpbank.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.xpbank.text")))) {
			type2 = this.getConfig().getString("signs.types.xpbank.text");
			if (player.hasPermission("signranks.create.xpbank")) {
				hasperm = true;
			}
		}
		if ((hasperm)&&(type2==this.getConfig().getString("signs.types.xpbank.text"))) {
			//TODO xp place
			event.setLine(0, "§1" + type2);
			event.setLine(1, "");
			event.setLine(2, "");
			event.setLine(3, player.getName());
		}
		else if ((hasperm)&&(type2!="")) {
			try {
			int costtype;
			int use;
			String cost = line3;
		  	  if (cost.contains(" exp")) {
		  		  costtype = 1;
		  		  cost = cost.substring(0,cost.length() - 4);
		  	  }
		  	  else if (cost.contains(" lvl")) {
		  		  costtype = 2;
		  		  cost = cost.substring(0,cost.length() - 4);
		  	  }
		  	  else if (cost.contains(this.getConfig().getString("economy.symbol"))) { 
		  		  costtype = 0;
		  		  cost = cost.substring(1,cost.length());
		  	  }
		  	  else {
		  		  player.sendMessage(ChatColor.GRAY+"Unknown amount: "+ChatColor.RED+"`"+line3+"'"+ChatColor.GRAY+". Use:"+ChatColor.RED+" ["+this.getConfig().getString("economy.symbol")+", exp, lvl]");
		  		  hasperm = false;
		  	  }
		  	  if (Integer.parseInt(cost) <= 0) {
		  		  player.sendMessage(ChatColor.RED+"[WARNING] "+ChatColor.GRAY +"amount is negative: "+ChatColor.RED+line3);
		  	  }
		  	  try {
		  		use = Integer.parseInt(line4);
		  	  }
		  	  catch (Exception e) {
	        	  if (Arrays.asList(perms.getGroups()).contains(line4)) {
	        		  
	        	  }
	        	  else {
			  		  use = -1;
					  event.setLine(3, "-1");
					  sign.update(true);
	        	  }
		  	  }
		  	  if (hasperm) {
			  	  player.sendMessage(ChatColor.GRAY+"Created a new "+ChatColor.GREEN+type2+ChatColor.GRAY+" sign");
			  	  event.setLine(0, "§1" + type2);
		  	  }
		  	  else {
		  		event.setLine(0, "§4" + type2);
		  	  }
			}
		  	  catch (Exception e) {
		  		event.setLine(0, "§4" + type2);
		  	  }
    }
		else if (type2!="") {
			player.sendMessage(ChatColor.GRAY+"You do not have permission to create a "+ChatColor.RED+type2+ChatColor.GRAY+" sign");
			event.setLine(0, "§4" + type2);
		}
		else {
		}
    
    }
	
	Player player = null;
	String type2;
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
		
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    	  type2 = "right";
      }
      else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
    	  type2 = "left";
      }
      else { type2 = "false"; }
      if (type2 != "false") {
        Block block = event.getClickedBlock();
        if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN)) {
          Sign sign = (Sign)block.getState();
          Player player = event.getPlayer();
          int costtype;
          if (type2=="right") {
          if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.promote.text"))) {
        	  if (player.hasPermission("signranks.use.promote")) {
        	  String cost = sign.getLine(2);
        	  if (cost.contains(" exp")) {
        		  costtype = 1;
        		  cost = cost.substring(0,cost.length() - 4);
        	  }
        	  else if (cost.contains(" lvl")) {
        		  costtype = 2;
        		  cost = cost.substring(0,cost.length() - 4);
        	  }
        	  else { 
        		  costtype = 0;
        		  cost = cost.substring(1,cost.length());
        	  }
        	  String group = sign.getLine(1);
        	  String[] mygroups = perms.getPlayerGroups(player);
        	  if (Arrays.asList(mygroups).contains(group)) {
        		  player.sendMessage(ChatColor.GRAY+"You already have the group "+ChatColor.RED+group+ChatColor.GRAY+".");
        	  }
        	  else {
        		  int use;
        		  try {
        			  use = Integer.parseInt(sign.getLine(3));
        		  }
        		  catch (Exception e) {
        			  // if (Arrays.asList(perms.getGroups()).contains(sign.getLine(3))) {
        			  if ((Arrays.asList(mygroups).contains(sign.getLine(3)))||(Arrays.asList(perms.getGroups()).contains(sign.getLine(3))==false)) {
        				  use = -1;
        			  }
        			  else {
        				  use = 0;
        			  }
        		  }
        		  if ((use == 0)||(use<-1)) {
        			  player.sendMessage(ChatColor.GRAY+"This sign has "+ChatColor.RED+use+ChatColor.GRAY+" uses left.");
        		  }
        		  else {
        			  boolean canbuy = false;
        			  if (costtype == 0) {
        					  EconomyResponse r = econ.withdrawPlayer(player.getName(), Integer.parseInt(cost));
        					  if(r.transactionSuccess()) {
        					  canbuy = true;
        					  }
        			  }
        			  else if (costtype == 1) {
        				  if (player.getTotalExperience() >= Integer.parseInt(cost)) {
						      int myxp = player.getTotalExperience();
						      player.setTotalExperience(0);
						      player.setLevel(0);
						      player.setExp(0);
						      player.giveExp(myxp - Integer.parseInt(cost));  
        					  canbuy = true;
        				  }
        			  }
        			  else if (costtype == 2) {
        				  if (player.getLevel() >= Integer.parseInt(cost)) {
        					  player.giveExpLevels(-Integer.parseInt(cost));
        					  canbuy = true;
        				  }
        			  }
        			  if (canbuy == true) {
        				  if (use != -1) {
        					  sign.setLine(3, "" + (use-1));
        					  sign.update(true);
        				  }
        				  perms.playerAddGroup(player, sign.getLine(1));
        				  player.sendMessage(ChatColor.GRAY+"Purchase of "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+sign.getLine(2)+ChatColor.GRAY+" was successful!");
        			  }
        			  else {
        				  player.sendMessage(ChatColor.GRAY+"You do not have "+ChatColor.RED+sign.getLine(2)+ChatColor.GRAY+".");
        			  }
        		  }
        		  
        	  }
        	  }
        	  else {
        		  player.sendMessage(ChatColor.RED+"You do not have permission to use this sign. If you think this is an error, please contact your server Administrator.");
        	  }
          }
          else if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.inherit.text"))) {
        	  if (player.hasPermission("signranks.use.inherit")) {
        	  String cost = sign.getLine(2);
        	  if (cost.contains(" exp")) {
        		  costtype = 1;
        		  cost = cost.substring(0,cost.length() - 4);
        	  }
        	  else if (cost.contains(" lvl")) {
        		  costtype = 2;
        		  cost = cost.substring(0,cost.length() - 4);
        	  }
        	  else { 
        		  costtype = 0;
        		  cost = cost.substring(1,cost.length());
        	  }
        	  String group = sign.getLine(1);
        	  String[] mygroups = perms.getPlayerGroups(player);
        	  if (Arrays.asList(mygroups).contains(group)) {
        		  player.sendMessage(ChatColor.GRAY+"You already have the group "+ChatColor.RED+group+ChatColor.GRAY+".");
        	  }
        	  else {
        		  int use;
        		  try {
        			  use = Integer.parseInt(sign.getLine(3));
        		  }
        		  catch (Exception e) {
        			  // if (Arrays.asList(perms.getGroups()).contains(sign.getLine(3))) {
        			  if ((Arrays.asList(mygroups).contains(sign.getLine(3)))||(Arrays.asList(perms.getGroups()).contains(sign.getLine(3))==false)) {
        				  use = -1;
        			  }
        			  else {
        				  use = 0;
        			  }
        		  }
        		  if ((use == 0)||(use<-1)) {
        			  player.sendMessage(ChatColor.GRAY+"This sign has "+ChatColor.RED+use+ChatColor.GRAY+" uses left.");
        		  }
        		  else {
        			  boolean canbuy = false;
        			  if (costtype == 0) {
        					  EconomyResponse r = econ.withdrawPlayer(player.getName(), Integer.parseInt(cost));
        					  if(r.transactionSuccess()) {
        					  canbuy = true;
        					  }
        			  }
        			  else if (costtype == 1) {
        				  if (player.getTotalExperience() >= Integer.parseInt(cost)) {        					          					  
						      int myxp = player.getTotalExperience();
						      player.setTotalExperience(0);
						      player.setLevel(0);
						      player.setExp(0);
						      player.giveExp(myxp - Integer.parseInt(cost));        					          					  
        					  canbuy = true;
        				  }
        			  }
        			  else if (costtype == 2) {
        				  if (player.getLevel() >= Integer.parseInt(cost)) {
        					  player.giveExpLevels(-Integer.parseInt(cost));
        					  canbuy = true;
        				  }
        			  }
        			  if (canbuy == true) {
        				  if (use != -1) {
        					  sign.setLine(3, "" + (use-1));
        					  sign.update(true);
        				  }
        				  if (Bukkit.getPluginManager().isPluginEnabled("GroupManager")) {
        					  //manuaddsub <user> <group>
        					  getServer().dispatchCommand(getServer().getConsoleSender(), "manuaddsub "+ player.getName() +" "+sign.getLine(1));
	        				  
	        				  player.sendMessage(ChatColor.GRAY+"Purchase of "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+sign.getLine(2)+ChatColor.GRAY+" was successful!");
        				  }
        				  else if (Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
        					  //pex user <user> group add <group>
        					  getServer().dispatchCommand(getServer().getConsoleSender(), "pex user "+ player.getName() +" group add "+sign.getLine(1));
        					  player.sendMessage(ChatColor.GRAY+"Purchase of "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+sign.getLine(2)+ChatColor.GRAY+" was successful!");
        				  }
        				  else if (Bukkit.getPluginManager().isPluginEnabled("bPermissions")) {
        					  ///exec u:codename_B a:setgroup v:admin
        					  getServer().dispatchCommand(getServer().getConsoleSender(), "exec u:"+ player.getName() +" a:addgroup v:"+sign.getLine(1));
        					  player.sendMessage(ChatColor.GRAY+"Purchase of "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+sign.getLine(2)+ChatColor.GRAY+" was successful!");
        				  }
        				  else if (Bukkit.getPluginManager().isPluginEnabled("PermissionsBukkit")) {
        					  getServer().dispatchCommand(getServer().getConsoleSender(), "permissions "+ player.getName() +" addgroup "+sign.getLine(1));
        					  player.sendMessage(ChatColor.GRAY+"Purchase of "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+sign.getLine(2)+ChatColor.GRAY+" was successful!");
        				  }
        				  else if (Bukkit.getPluginManager().isPluginEnabled("DroxPerms")) {
        					  getServer().dispatchCommand(getServer().getConsoleSender(), "changeplayer addsub "+ player.getName() +" "+sign.getLine(1));
        					  player.sendMessage(ChatColor.GRAY+"Purchase of "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+sign.getLine(2)+ChatColor.GRAY+" was successful!");
        				  }
        				  else if (Bukkit.getPluginManager().isPluginEnabled("zPermissions")) {
        					  ///permissions player <player> addgroup <group>
        					  getServer().dispatchCommand(getServer().getConsoleSender(), "permissions player "+ player.getName() +" addgroup "+sign.getLine(1));
        					  player.sendMessage(ChatColor.GRAY+"Purchase of "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+sign.getLine(2)+ChatColor.GRAY+" was successful!");
        				  }
        				  else {
        					  player.sendMessage(ChatColor.RED+"This server lacks a permission plugin with inheritance (PEX, GroupManager, bPermissions, PermissionsBukkit, DroxPerms, zPermissions). If you think this is an error, please contact your server Administrator.");
        				  }
    				  }
        			  else {
        				  player.sendMessage(ChatColor.GRAY+"You do not have "+ChatColor.RED+sign.getLine(2)+ChatColor.GRAY+".");
        			  }
        		  }
        		  
        	  }
          }
    	  else {
    		  player.sendMessage(ChatColor.RED+"You do not have permission to use this sign. If you think this is an error, please contact your server Administrator.");
    	  }
          }
          else if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.prefix.text"))) {
        	  if (player.hasPermission("signranks.use.prefix")) {
        	  String cost = sign.getLine(2);
        	  if (cost.contains(" exp")) {
        		  costtype = 1;
        		  cost = cost.substring(0,cost.length() - 4);
        	  }
        	  else if (cost.contains(" lvl")) {
        		  costtype = 2;
        		  cost = cost.substring(0,cost.length() - 4);
        	  }
        	  else { 
        		  costtype = 0;
        		  cost = cost.substring(1,cost.length());
        	  }
        	  if (chat.getPlayerPrefix(player) == sign.getLine(1)) {
        		  player.sendMessage(ChatColor.GRAY+"You already have the prefix "+ChatColor.RED+sign.getLine(1)+ChatColor.GRAY+".");
        	  }
        	  else {
        		  int use;
        		  try {
        			  use = Integer.parseInt(sign.getLine(3));
        		  }
        		  catch (Exception e) {
        			  String[] mygroups = perms.getPlayerGroups(player);
        			  // if (Arrays.asList(perms.getGroups()).contains(sign.getLine(3))) {
        			  if ((Arrays.asList(mygroups).contains(sign.getLine(3)))||(Arrays.asList(perms.getGroups()).contains(sign.getLine(3))==false)) {
        				  use = -1;
        			  }
        			  else {
        				  use = 0;
        			  }
        		  }
        		  if ((use == 0)||(use<-1)) {
        			  if (Arrays.asList(perms.getGroups()).contains(sign.getLine(3))==false) {
        				  player.sendMessage(ChatColor.GRAY+"This sign has "+ChatColor.RED+use+ChatColor.GRAY+" uses left.");
        			  }
        			  else {
        				  player.sendMessage(ChatColor.GRAY+"You lack the group "+ChatColor.RED+sign.getLine(3)+ChatColor.GRAY+".");
        			  }
        		  }
        		  else {
        			  boolean canbuy = false;
        			  if (costtype == 0) {
        					  EconomyResponse r = econ.withdrawPlayer(player.getName(), Integer.parseInt(cost));
        					  if(r.transactionSuccess()) {
        					  canbuy = true;
        					  }
        			  }
        			  else if (costtype == 1) {
        				  if (player.getTotalExperience() >= Integer.parseInt(cost)) {
						      int myxp = player.getTotalExperience();
						      player.setTotalExperience(0);
						      player.setLevel(0);
						      player.setExp(0);
						      player.giveExp(myxp - Integer.parseInt(cost));   
        					  canbuy = true;
        				  }
        			  }
        			  else if (costtype == 2) {
        				  if (player.getLevel() >= Integer.parseInt(cost)) {
        					  player.giveExpLevels(-Integer.parseInt(cost));
        					  canbuy = true;
        				  }
        			  }
        			  if (canbuy == true) {
        				  if (use != -1) {
        					  sign.setLine(3, "" + (use-1));
        					  sign.update(true);
        				  }
        				  chat.setPlayerPrefix(player, sign.getLine(1));
        				  player.sendMessage(ChatColor.GRAY+"Purchase of "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+sign.getLine(2)+ChatColor.GRAY+" was successful!");
        			  }
        			  else {
        				  player.sendMessage(ChatColor.GRAY+"You do not have "+ChatColor.RED+sign.getLine(2)+ChatColor.GRAY+".");
        			  }
        		  }
        		  
        	  }
          }
        	  else {
        		  player.sendMessage(ChatColor.RED+"You do not have permission to use this sign. If you think this is an error, please contact your server Administrator.");
        	  }
          }
          else if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.xpbank.text"))) {
        	  //TODO right click XP bank
        	  
          }
        }
      else {
    	  if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.promote.text"))) {
    		  player.sendMessage(ChatColor.LIGHT_PURPLE+"Thanks for using SignRanksPlus+ by Empire92");
    	  }
    	  else if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.inherit.text"))) {
    		  player.sendMessage(ChatColor.LIGHT_PURPLE+"Thanks for using SignRanksPlus+ by Empire92");
    	  }
    	  else if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.prefix.text"))) {
    		  player.sendMessage(ChatColor.LIGHT_PURPLE+"Thanks for using SignRanksPlus+ by Empire92");
    	  }
    	  else if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.xpbank.text"))) {
    		  //TODO left click xp bank
    	  }
          }
        }

      }
      }
 

    
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    		

    
    
	
	
	
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("xpp")){
    		if (sender.hasPermission("signranks.xpp")) {
    		if (args.length != 2){
    			sender.sendMessage(ChatColor.GRAY+"Use "+ChatColor.GREEN+"/xpp <Player> <amount> "+ChatColor.GRAY+"to transfer XP to a player.");
    		}
    		else {
    			if (sender.getName().equalsIgnoreCase(args[0])!= true) {
    				List<Player> matches = getServer().matchPlayer(args[0]);
    				if (matches.isEmpty()) {
    					sender.sendMessage(ChatColor.GRAY+"Player not found for "+ChatColor.RED+args[0]);
    				}
    				else if (matches.size() > 1) {
    					sender.sendMessage(ChatColor.GRAY+"Too many matches found: "+ChatColor.RED + matches);
    				}
    				else {
    					Player player = matches.get(0);
    					if ((player.getWorld() == ((Player) sender).getWorld()) || (this.getConfig().getString("cross_map_trade").equalsIgnoreCase("true"))) {
          				  try {
    						if (((Player) sender).getTotalExperience() >= Integer.parseInt(args[1])) {
    							if (Integer.parseInt(args[1])>0) {
    							      int myxp = ((Player) sender).getTotalExperience();
    							 
    							      ((Player) sender).setTotalExperience(0);
    							      ((Player) sender).setLevel(0);
    							      ((Player) sender).setExp(0);
    							      ((Player) sender).giveExp(myxp - Integer.parseInt(args[1]));
    							      sender.sendMessage(ChatColor.GRAY+"You now have "+ChatColor.RED+(myxp-Integer.parseInt(args[1]))+ChatColor.GRAY+" XP");
    							      player.sendMessage(ChatColor.GRAY+"You were sent "+ChatColor.GREEN+(Integer.parseInt(args[1]))+ChatColor.GRAY+" XP by "+ChatColor.BLUE+sender.getName());
    								
    								
    							player.giveExp(Integer.parseInt(args[1]));
    							}
    							else {
    								sender.sendMessage(""+Integer.parseInt(args[1]));
    								sender.sendMessage(ChatColor.GRAY+"No matter how stupid you are, "+ChatColor.RED+args[1]+ChatColor.GRAY+" will not be greater than 0.");
    							}
//    	        					  player.setExp(player.getTotalExperience()-Integer.parseInt(cost`));
          				  }
    						else {
    							sender.sendMessage(ChatColor.GRAY+"You only have "+ChatColor.RED+((Player) sender).getTotalExperience()+ChatColor.GRAY+" exp.");
    						}
          				  }
          				  catch (Exception e) {
          					sender.sendMessage(ChatColor.GRAY+"Unknown amount: "+ChatColor.RED + args[1]+ChatColor.GRAY+".");
          				  }
    					}
    					else {
    						sender.sendMessage(ChatColor.RED+"Cannot trade between worlds.");
    					}
    					
    				}
    			}
    			else {
    				sender.sendMessage(ChatColor.RED+"It takes one with great stupidity to try to pay oneself.");
    			}
    			//Check if player exists
    			//Check if they are in the same map
    			//Check you have enough XP
    			//Transfer XP
    		}
    		}
    		else {
    			sender.sendMessage(ChatColor.GRAY+"Missing requirements: "+ChatColor.RED+"signranks.xpp");
    		}
    	}
    	else if ((cmd.getName().equalsIgnoreCase("signranks"))||(cmd.getName().equalsIgnoreCase("sr"))) {
    	if (args.length > 0) {
		if ((args[0].equalsIgnoreCase("reload"))){
	    	timer.cancel();
	    	timer.purge();
			this.reloadConfig();
			this.saveDefaultConfig();
	    	if (getConfig().getString("economy.salary.enabled").equalsIgnoreCase("true")) {
	    		if (getConfig().getInt("economy.salary.interval") > 0) {
	    			timer.schedule (mytask, 0l, 1000*(getConfig().getInt("economy.salary.interval")));
	    		}
	    	}
			if (sender instanceof Player) {
				if (((Player) sender).hasPermission("signranks.reload")) {
					sender.sendMessage(ChatColor.GRAY + "Successfully reloaded" + ChatColor.RED + " SignRanksPlus+"+ChatColor.WHITE + ".");
				}
				else {
					sender.sendMessage(ChatColor.RED + "Sorry, you do not have permission to perform this action.");
				}
			}
			else {
				System.out.println("Successfully reloaded SignRanksPlus+");
			}
		}
		else if ((args[0].equalsIgnoreCase("pay"))) {
			if (sender.hasPermission("signranks.pay")==true) {

				double paymoney;
				Set<String> groups = getConfig().getConfigurationSection("economy.salary.groups").getKeys(false);
		    	for(Player player:getServer().getOnlinePlayers()){
		    		if (getConfig().getInt("economy.salary.notify-level")>1) {
		    			String message = getConfig().getString("economy.salary.message");
		    			if (message != "") {
		    				player.sendMessage(message);
		    			}
		    		}
					int payexp = 0;
					String amount = "";
					String amount2 = "";
					String amount3 = "";
					int paylvl = 0;
		    		paymoney = 0;
		    		for(String group : groups) {
		    			boolean has = false;
		    			if (getConfig().getString("economy.salary.check-subgroups").equalsIgnoreCase("true")) {
		  	          	  String[] mygroups = perms.getPlayerGroups(player);
			        	  if (Arrays.asList(mygroups).contains(group)) {
			        		  has = true;
			        	  }
			        	  else {
			        	  }
		    			}
		    			else if (perms.getPrimaryGroup(player)==group) {
		    				has = true;
		    			}

		    			if (has) {
		    				if (true)
		    				{
		    					String cost = getConfig().getString(("economy.salary.groups."+group+".exp.base"));
		    				  	  if (cost.contains(" exp")) {
		    				  		  cost = cost.substring(0,cost.length() - 4);
		    				  		  payexp += Integer.parseInt(cost);
		    				  	  }
		    				  	  else if (cost.contains(" lvl")) {
		    				  		  cost = cost.substring(0,cost.length() - 4);
		    				  		  paylvl += Integer.parseInt(cost);
		    				  	  }
		    					cost = getConfig().getString(("economy.salary.groups."+group+".exp.bonus"));
		    				  	  if (cost.contains(" exp")) {
		    				  		  cost = cost.substring(0,cost.length() - 4);
		    				  		  payexp += Integer.parseInt(cost)*(getServer().getOnlinePlayers().length-1);
		    				  	  }
		    				  	  else if (cost.contains(" lvl")) {
		    				  		  cost = cost.substring(0,cost.length() - 4);
		    				  		  paylvl += Integer.parseInt(cost)*(getServer().getOnlinePlayers().length-1);
		    				  	  }
		    					payexp += (getConfig().getInt(("economy.salary.groups."+group+".exp.percentage"))*(econ.getBalance(player.getName())))/100; 
		    					
		    					
		    				}
		    				if (true)
		    				{
		    					paymoney += getConfig().getInt(("economy.salary.groups."+group+".money.base"));
		    					paymoney += getConfig().getInt(("economy.salary.groups."+group+".money.bonus"))*(getServer().getOnlinePlayers().length-1);
		    					paymoney += (getConfig().getInt(("economy.salary.groups."+group+".money.percentage"))*(econ.getBalance(player.getName())))/100;
		    				}
		    			if ((paymoney + payexp + paylvl!=0)&&(getConfig().getInt("economy.salary.notify-level")>1)) {
		    				player.sendMessage("    "+ChatColor.GRAY+getConfig().getString("economy.symbol")+paymoney+ChatColor.WHITE+", "+ChatColor.GRAY+payexp+" exp"+ChatColor.WHITE+", "+ChatColor.GRAY+paylvl+" lvl"+ChatColor.WHITE+" from group "+ChatColor.BLUE+group);
		    			}

		    			}
		    			else {
		    			}

		    			
		    			
		    		}
			    	// pay salary
					if (paymoney > 0) {
						amount = ChatColor.GRAY+"You earned: "+ChatColor.GREEN+getConfig().getString("economy.symbol")+paymoney;
						econ.depositPlayer(player.getName(), paymoney);
					}
					else if (paymoney < 0) {
						econ.withdrawPlayer(player.getName(), -paymoney);
						amount = ChatColor.GRAY+"You paid: "+ChatColor.RED+getConfig().getString("economy.symbol")+paymoney;
					}
					if (payexp > 0) {
						amount2 = ChatColor.GRAY+", gained: "+ChatColor.GREEN+payexp+" exp";
					      int myxp = player.getTotalExperience();
					      player.setTotalExperience(0);
					      player.setLevel(0);
					      player.setExp(0);
					      player.giveExp(myxp + payexp);  
					}
					else if (payexp < 0) {
						amount2 = ChatColor.GRAY+", lost: "+ChatColor.RED+payexp+" exp";
					      int myxp = player.getTotalExperience();
					      player.setTotalExperience(0);
					      player.setLevel(0);
					      player.setExp(0);
					      player.giveExp(myxp + payexp);  
					}
					if (paylvl > 0) {
						amount3 = ChatColor.GRAY+" and gained: "+ChatColor.GREEN+paylvl+" level/s";
					    player.giveExpLevels(paylvl); 
					}
					else if (paylvl < 0) {
						amount3 = ChatColor.GRAY+" and lost: "+ChatColor.RED+paylvl+" level/s";
						player.giveExpLevels(paylvl); 
					}
					if ((amount!="")||(amount2!="")||(amount3!="")) {
	    				if (getConfig().getInt("economy.salary.notify-level")>0) {
	    	    			player.sendMessage(amount+amount2+amount3);
	    	    		}
					}
					else {
						player.sendMessage(ChatColor.GRAY+"You were paid "+ChatColor.RED+"nothing"+ChatColor.GRAY+".");
					}
		    }
			}
			else {
				sender.sendMessage(ChatColor.RED+"You do not have permission to perform this action.");
			}
		}
    	}
		}
		return false;
    }
    
}