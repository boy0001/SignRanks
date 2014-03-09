package com.empcraft;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
public class SignRanks extends JavaPlugin implements Listener {
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;
    public static int counter0 = 0;
    public static int counter = 0;
    public static int counter2 = 0;
    public static Map<String, Object> globals = new HashMap<String, Object>();
    public static Map<String, Object> protocol = new HashMap<String, Object>();
    public static Map<String, Integer> kills = new HashMap<String, Integer>();
    public Map<String, Object> changers = new HashMap<String, Object>();
	public static List<Location> list = new ArrayList();
	public static List<String> players = new ArrayList();
	public boolean isenabled = false;
	public int recursion = 0;
	public ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");
	SignRanks plugin;
		
    
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			String playername = event.getPlayer().getName();
			File yamlFile = new File(getDataFolder()+File.separator+"expdata.yml");
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
			if (yaml.contains(playername)) {
				ExperienceManager expMan = new ExperienceManager(event.getPlayer());
				expMan.changeExp(yaml.getInt(playername));
				msg(event.getPlayer(),"§1"+this.getConfig().getString("signs.types.shop.text")+"&7: "+getmsg("PAY3")+" &a"+yaml.getInt(playername) +" exp&7.");
				yaml.set(playername, null);
				yaml.save(yamlFile);
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String javascript(String line) {
        try {
        	Object toreturn =  engine.eval(line);
        	try {
        		Double num = (Double) toreturn;
        		if (Math.ceil(num) == Math.floor(num)) {
        			line = Long.toString(Math.round(num));
        		}
        		else {
        			throw new Exception();
        		}
        	}
        	catch (Exception d) {
        	try {
        		Long num = (Long) toreturn;
        		line = Long.toString(num);
        	}
        	catch (Exception f) {
            	try {
            		Integer num = (Integer) toreturn;
            		line = Integer.toString(num);
            	}
            	catch (Exception g) {
                	try {
                		Float num = (Float) toreturn;
                		line = Float.toString(num);
                	}
                	catch (Exception h) {
                    	try {
                    		line = "" + toreturn;
                    	}
                    	catch (Exception i) {
                    	}
                	}
            	}
        	}
        	}
			} catch (Exception e) { }
	        return line;
		}
	
		
	
		public void isadd(Player player, Location loc) {
			if (list.contains(loc)==false) {
			players.add(player.getName());
			list.add(loc);
			}
		}
		public String getConfig(String node) {
			return getConfig().getString(node);
		}
    	@EventHandler
	    public void onPlayerkill(PlayerDeathEvent event){
	    	Player killer = event.getEntity().getKiller();
	    	Player killed = event.getEntity();
	    	 if((killer instanceof Player)){
	 	    	try {
		    		kills.put(killer.getName(),kills.get(killer.getName())+1);
		    	}
	 	    	catch (Exception e) {
	 	    		kills.put(killer.getName(),1);
	 	    	}
	    	 }
	    	 kills.put(killed.getName(),0);

	    }
    	public Location getSelectedBlock(Player p) {
    		   return p.getTargetBlock(null, 200).getLocation();
    		}
    	public String getmsg(String key) {
    		File yamlFile = new File(getDataFolder(), getConfig().getString("language").toLowerCase()+".yml"); 
    		YamlConfiguration.loadConfiguration(yamlFile);
    		try {
    			return colorise(YamlConfiguration.loadConfiguration(yamlFile).getString(key));
    		}
    		catch (Exception e){
    			return "";
    		}
    	}
    	
    	public boolean inventoryspace(Player player,ItemStack item) {
    		for (ItemStack i : player.getInventory().getContents()) {
    			if (i == null) {
    				return true;
    			}
    			else if (i.getType().equals(Material.AIR)) {
    				return true;
    			}
    			else if (i.isSimilar(item)) {
    				if (i.getEnchantments().size()==0)
    				{
    					if (i.hasItemMeta()==false) {
		    				if (i.getAmount()+item.getAmount()<=i.getMaxStackSize()) {
		    					return true;
		    				}
    					}
    				}
    			}
    		}
    		
    		
    		return false;
    	}
    	
    	public int countitem(Player player, ItemStack item) {
    		int count = 0;
    		for (ItemStack i : player.getInventory().getContents()) {
    			if (i!=null) {
    				if (i.getType().equals(Material.AIR)==false) {
		    			if (i.getDurability()==item.getDurability()) {
		    				if (i.getType().equals(item.getType())) {
		    					if (i.getEnchantments().size()==0) {
		    						if (i.hasItemMeta()==false) {
		    							count+=i.getAmount();
		    						}
		    					}
		    				}
	    				}
    				}
    			}
    		}
    		return count;
    	}
    	
        public String matchgroup(String group) {
    		String[] groups = (perms.getGroups());
    		for (String current:groups) {
    			if (group.equalsIgnoreCase(current)) {
    				return current;
    			}
    		}
    		return "";
        }
        public String expandperm(String perm) {
        	Set<String> shorten = getConfig().getConfigurationSection("shortened-perms").getKeys(false);
        	for (String myshort:shorten) {
        		String[] nodes = perm.split("\\.");
        		if (myshort.equals(perm)) {
        			perm = getConfig().getString("shortened-perms."+myshort);
        		}
        		else {
	        		for(int i = 0; i < nodes.length-1; i++) {
	        			if (nodes[i].equals(myshort)) {
	        				nodes[i] = getConfig().getString("shortened-perms."+myshort);
	        			}
	        		}
	        		perm = StringUtils.join(nodes,".");
        		}
        	}
        	return perm;
        }
        public boolean checkperm(Player player,String perm) {
        	boolean hasperm = false;
        	perm = expandperm(perm);
        	String[] nodes = perm.split("\\.");
        	String n2 = "";
        	if (player==null) {
        		return true;
        	}
        	else if (player.hasPermission(perm)) {
        		hasperm = true;
        	}
        	else if (player.isOp()==true) {
        		hasperm = true;
        	}
        	else {
        		for(int i = 0; i < nodes.length-1; i++) {
        			n2+=nodes[i]+".";
                	if (player.hasPermission(n2+"*")) {
                		hasperm = true;
                	}
        		}
        	}
    		return hasperm;
        }
    public String timetosec(String input) {
    	try {
    	input = input.toLowerCase().trim();
    	int toadd = 0;
    	if (input.contains("w")) {
    		toadd = 604800*Integer.parseInt(input.split("w")[0].trim());
    	}
    	else if ((input.contains("d"))&&(input.contains("sec")==false)) {
    		toadd = 86400*Integer.parseInt(input.split("d")[0].trim());
    	}
    	else if (input.contains("h")) {
    		toadd = 3600*Integer.parseInt(input.split("h")[0].trim());
    	}
    	else if (input.contains("m")) {
    		toadd = 60*Integer.parseInt(input.split("m")[0].trim());
    	}
    	else if (input.contains("s")) {
    		toadd = Integer.parseInt(input.split("s")[0].trim());
    	}
    	
    	String time = Long.toString((System.currentTimeMillis()/1000)+toadd);
    	if (toadd!=0) {
		return time;
    	}
    	else {
    		return null;
    	}
    	}
    	catch (Exception e) {
    		return null;
    	}
    }
    public String execute(String line, Player user, Player sender, Boolean elevation) {
    	recursion++;
    	try {
    	final Map<String, Object> locals = new HashMap<String, Object>();
    	locals.put("{var}", StringUtils.join(locals.keySet(),",").replace("{","").replace("}", ""));
    	String[] mycmds = line.split(";");
		boolean hasperm = true;
		int depth = 0;
		int last = 0;
		int i2 = 0;
		String myvar = ",null";
		for(int i = 0; i < mycmds.length; i++) {
			if (i>=i2) {
			String mycommand = evaluate(mycmds[i],user,sender,elevation);
            for (final Entry<String, Object> node : locals.entrySet()) {
              	 if (mycommand.contains(node.getKey())) {
              		 mycommand = mycommand.replace(node.getKey(), (CharSequence) node.getValue());
              	 }
              }
            
			if ((mycommand.equals("")||mycommand.equals("null"))==false) {
			String[] cmdargs = mycommand.split(" ");
			
            if (cmdargs[0].trim().equalsIgnoreCase("for")) {
            	if (hasperm) {
    				int mylength = 0;
    				int mode = 0;
            		String mytest = "";
            		int depth2 = 1;
            		int j = 0;
            		for(j = i+1; j < mycmds.length; j++) {
            			if (mycmds[j].split(" ")[0].trim().equals("for")) {
            				depth2+=1;
            			}
            			else if (mycmds[j].split(" ")[0].trim().equals("endloop")) {
            				depth2-=1;
            			}
            			if (depth2>0) {
            				mytest+=mycmds[j]+";";
            			}
            			else {
            			}
            			if ((depth2 == 0)||(j==mycmds.length-1)) {
            				if (cmdargs[1].contains(":")) {
            					try {
            						mylength = Integer.parseInt(cmdargs[1].split(":")[1].trim());
            					}
            					catch (Exception e) {
            						mylength = cmdargs[1].split(":")[1].split(",").length;
            						mode = 1;
            					}
            				}
            				else {
            					try {

            					mylength = Integer.parseInt(cmdargs[1].trim());
            					}
            					catch (Exception e) {
            						mylength = 0;
            					}
            				}
            				if (mode == 1) {
            					myvar = "{"+cmdargs[1].split(":")[0]+"},"+globals.get("{"+cmdargs[1].split(":")[0]+"}");
            				}
            				if (mylength>1024) {
            					mylength = 1024;
            				}
            				break;
	            			}
	            			}
            				for(int k = 0; k < mylength; k++) {
            					if (mode == 1) {
            						globals.put("{"+cmdargs[1].split(":")[0]+"}", cmdargs[1].split(":")[1].split(",")[k]);
            					}
            					if (recursion<1024) {
            						execute(mytest,user,sender,elevation);
            					}
            				}
            				if (mode == 1) {
            					if (myvar.split(",")[1].equals("null")) {
            						globals.remove("{"+cmdargs[1].split(":")[0]+"}");
            					}
            					else {
            						globals.put("{"+cmdargs[1].split(":")[0]+"}", myvar.split(",")[1]);
            					}
            				}
            				i2=j+1;
            	}
            }
            else if (cmdargs[0].equalsIgnoreCase("setuser")) {
            	Player lastuser = user;
            	try {
            		if (cmdargs[1].equals("null")) {
            			user = null;
            		}
            		else {
	            		user = Bukkit.getPlayer(cmdargs[1]);
	            		if (user==null) {
	            			user = lastuser;
	            		}
            		}
            	}
            	catch (Exception e5) {
            	}
            }
            else if (cmdargs[0].equalsIgnoreCase("if")) {
          	  if (hasperm&&(depth==last)) {
          		  last++;
          		recursion = 0;
					hasperm = testif(mycommand);
          	  }
          	  else {
          	  }
          	  depth++;
            }
              else if (cmdargs[0].equalsIgnoreCase("else")) {
            	  if (last==depth) {
            	  if (hasperm) {
            		  hasperm = false;
            	  }
            	  else {
            		  hasperm = true;
            	  }
            	  if (user != null) {
            	  }
            	  }
              }
              else if (cmdargs[0].equalsIgnoreCase("endif")) {
            	  if (depth >0) {
            		  if (last==depth) {
            			  last-=1;
            		  }
            		  depth-=1;
            		  if (last==depth) {
            			  hasperm = true;
            			  if (user != null) {
            		  }
            		  }
            	  }
              }
              else if (cmdargs[0].equalsIgnoreCase("gvar")) {
            	  if (cmdargs.length>1) {
            	  if (cmdargs.length>2) {
            	  try {
            	  globals.put("{"+evaluate(cmdargs[1],user,sender,elevation)+"}", evaluate(StringUtils.join(Arrays.copyOfRange(cmdargs, 2, cmdargs.length)," "),user,sender,elevation));
            	  if (user != null) {
            	  }
            	  }
            	  catch (Exception e) {
            		  if (user != null) {
            	  }
            	  }
            	  }
            	  else {
            		  try {
            		  globals.remove("{"+cmdargs[1]+"}");
            		  if (user != null) {
            		  }
            		  }
            		  catch (Exception e2) {
            			  if (user != null) {
            		  }
            		  }
            	  }
              }
              }
              else if (cmdargs[0].equalsIgnoreCase("var")) {
            	  if (cmdargs.length>1) {
            	  if (cmdargs.length>2) {
            	  try {
            		  
            	  locals.put("{"+evaluate(cmdargs[1],user,sender,elevation)+"}", evaluate(StringUtils.join(Arrays.copyOfRange(cmdargs, 2, cmdargs.length)," "),user,sender,elevation));
            	  if (user != null) {
            	  }
            	  }
            	  catch (Exception e) {
            		  if (user != null) {
            	  }
            	  }
              }
            	  else {
            		  try {
            		  locals.remove("{"+cmdargs[1]+"}");
            		  if (user != null) {
            		  }
            		  }
            		  catch (Exception e2) {
            			  if (user != null) {
            		  }
            		  }
            	  }
            	  }
              }
              else if (hasperm) {
                  for (final Entry<String, Object> node : locals.entrySet()) {
                    	 if (mycommand.contains(node.getKey())) {
                    		 mycommand = mycommand.replace(node.getKey(), (CharSequence) node.getValue());
                    	 }
                    }
            	  mycommand = mycommand.trim();
			if (mycommand.charAt(0)=='\\') {
				mycommand = mycommand.substring(1,mycommand.length());
				if (user != null) {
				user.chat(mycommand);
				}
				else {
					getServer().dispatchCommand(getServer().getConsoleSender(), "say "+mycommand);
				}
			}
			else if (user != null) {
			 if (cmdargs[0].equalsIgnoreCase("do")){
				mycommand = mycommand.substring(3,mycommand.length());
			if (user.isOp()) {
				Bukkit.dispatchCommand(user, mycommand);
			}
			else {
        	  try
        	  {
        		  if (elevation) {
        			  user.setOp(true);
        		  }
        	      Bukkit.dispatchCommand(user, mycommand);
        	  }
        	  catch(Exception e)
        	  {
        	      e.printStackTrace();
        	  }
        	  finally
        	  {
        		  user.setOp(false); 
        	  }
        	  
			}
			
		}
			 else if (cmdargs[0].equalsIgnoreCase("return")){
				 return mycommand.substring(7,mycommand.length());
			 }
			else {
				msg(user,colorise(evaluate(mycommand, user,sender,elevation)));
			}
              }
			else {
				if (cmdargs[0].equalsIgnoreCase("do")){
					mycommand = mycommand.substring(3,mycommand.length());
					getServer().dispatchCommand(getServer().getConsoleSender(), mycommand);
				}
				else {
					System.out.println(evaluate(mycommand, user,sender,elevation));
				}
			}
			
		}
			
		}
    	}
    	}
    }
        catch (Exception e2) {
        	if (user!=null) {
        	msg(user,colorise(getmsg("ERROR")+getmsg("ERROR1"))+e2);
        	
        	}
        	else {
        		System.out.println(colorise(getmsg("ERROR"))+e2);
        	}
        }
    	return "null";
    }
    public String fphs(String line, Player user, Player sender, Boolean elevation) {
    	String[] mysplit = line.substring(1,line.length()-1).split(":");
    	if (mysplit.length==2) {
    		if ((Bukkit.getPlayer(mysplit[1])!=null)) {
        		user = Bukkit.getPlayer(mysplit[1]);
        		line = "{"+mysplit[0]+"}";
        	}
    	}
    	if (line.contains("{setgroup:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)&&(user!=null)) {
    			perms.playerAddGroup(user, mysplit[1]);
    			if (perms.getPrimaryGroup(user).equals(mysplit[1])==false) {
        			perms.playerRemoveGroup(user, perms.getPrimaryGroup(user));
        			perms.playerRemoveGroup(user, mysplit[1]);
        			perms.playerAddGroup(user, mysplit[1]);
    			}
    		}
    		else if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				perms.playerAddGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    				if (perms.getPrimaryGroup(Bukkit.getPlayer(mysplit[1])).equals(mysplit[2])==false) {
            			perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), perms.getPrimaryGroup(user));
            			perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
            			perms.playerAddGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    				}
    			}
    			
    		}
    		else if (mysplit.length == 4){
    			try {
    				perms.playerAddGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    				if (perms.getPrimaryGroup(Bukkit.getWorld(mysplit[3]),mysplit[1]).equals(mysplit[2])==false) {
        				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1],perms.getPrimaryGroup(Bukkit.getWorld(mysplit[3]),mysplit[1]));
        				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1],mysplit[2]);
        				perms.playerAddGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    				}
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{delsub:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)&&(user!=null)) {
    			perms.playerRemoveGroup(user, mysplit[1]);
    		}
    		else if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), mysplit[1]);
    			}
    		}
    		else if (mysplit.length == 4){
    			try {
    				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{delperm:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)&&(sender!=null)) {
    			perms.playerRemove(user, mysplit[1]);
    		}
    		else if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				perms.playerRemove(Bukkit.getPlayer(mysplit[1]), mysplit[1]);
    			}
    		}
    		else if (mysplit.length == 4){
    			try {
    				perms.playerRemove(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{prefix:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				return chat.getPlayerPrefix(Bukkit.getPlayer(mysplit[1]));
    			}
    			else {
    				chat.setPlayerPrefix(user, mysplit[1]);
    			}
    		}
    		if ((mysplit.length >= 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				chat.setPlayerPrefix(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{suffix:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				return chat.getPlayerSuffix(Bukkit.getPlayer(mysplit[1]));
    			}
    			else {
    				chat.setPlayerSuffix(user, mysplit[1]);
    			}
    		}
    		if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				chat.setPlayerSuffix(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    			}
    		}
    		else if (mysplit.length == 4){
    			try {
    				chat.setPlayerSuffix(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{rand:")) {
    		Random random = new Random();
    		return (""+random.nextInt(Integer.parseInt(mysplit[1])));
    	}
    	else if (line.contains("{range:")) {
    		String mylist = "";
    		int start = 0;
    		int stop = 0;
    		if (mysplit.length==2) {
    			stop = Integer.parseInt(mysplit[1]);
    		}
    		else if (mysplit.length==3) {
    			start = Integer.parseInt(mysplit[1]);
    			stop = Integer.parseInt(mysplit[2]);
    		}
    		if (stop-start<512) {
    		for(int i = start; i <= stop; i++) {
    			mylist+=i+",";
    		}
    		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.contains("{matchplayer:")) {
    		List<Player> matches = getServer().matchPlayer(mysplit[1]);
    		String mymatches = "";
    		if (matches.isEmpty()==false) {
    			for (Player match:matches) {
    				mymatches+=match.getName()+",";
    			}
    			return mymatches.substring(0,mymatches.length()-1);
    		}
    		else {
    			return "null";
    		}
    	}
    	else if (line.contains("{matchgroup:")) {
    		return matchgroup(mysplit[1]);
    	}
    	else if (line.contains("{index:")) {
    		return mysplit[1].split(",")[Integer.parseInt(mysplit[2])];
    	}
    	else if (line.contains("{setindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		
    		int myindex = Integer.parseInt(mysplit[2]);
    		for(int i = 0; i < mylist.length; i++) {
    			if (i==myindex) {
    				newlist+=mysplit[3]+",";
    			}
    			else {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{delindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		int myindex = Integer.parseInt(mysplit[2]);
    		for(int i = 0; i < mylist.length; i++) {
    			if (i==myindex) {
    				
    			}
    			else {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{sublist:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		int i1 = Integer.parseInt(mysplit[2]);
    		int i2 = Integer.parseInt(mysplit[3]);
    		for(int i = 0; i < mylist.length; i++) {
    			if ((i>=i1)&&(i<=i2)) {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{getindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				newlist+=i+",";
    			}
    		}
    		if (newlist.equals("")) {
    			return "null";
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{listhas:")) {
    		String[] mylist = mysplit[1].split(",");
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				return "true";
    			}
    		}
    		return "false";
    	}
    	else if (line.contains("{contains:")) {
    		if (mysplit[1].contains(mysplit[2])) {
    			return "true";
    		}
    		return "false";
    	}
    	else if (line.contains("{substring:")) {
    		return mysplit[1].substring(Integer.parseInt(mysplit[2]), Integer.parseInt(mysplit[3]));
    	}
    	else if (line.contains("{length:")) {
    		if (mysplit[1].contains(",")) {
    			return ""+mysplit[1].split(",").length;
    		}
    		else {
    			return ""+mysplit[1].length();
    		}
    	}
    	else if (line.contains("{split:")) {
    		return mysplit[1].replace(mysplit[2],",");
    	}
    	else if (line.contains("{hasperm:")) {
    		if (user==null) {
    			return "true";
    		}
    		else if (mysplit.length==3) {
    			return ""+perms.playerHas(user.getWorld(),mysplit[1], mysplit[2]);
    		}
    		else if (checkperm(user,mysplit[1])) {
    			return "true";
    		}
    		return "false";
    	}
    	else if (line.contains("{randchoice:")) {
    		String[] mylist = mysplit[1].split(",");
    		Random random = new Random();
    		return mylist[random.nextInt(mylist.length-1)];
    	}
    	else if (line.contains("{worldtype:")) {
    		Location loc = getloc(mysplit[1], user);
    		return ""+loc.getWorld().getWorldType().getName();
    	}
    	else if (line.contains("{listreplace:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				newlist+=mysplit[3]+",";
    			}
    		}
    		if (newlist.equals("")) {
    			return "null";
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{worldticks}")) {
    		return Long.toString(user.getWorld().getTime());
    	}
    	else if (line.contains("{worldticks:")) {
    		Location loc = getloc(mysplit[1], user);
    		return Long.toString(loc.getWorld().getTime());
    	}
    	else if (line.contains("{time}")) {
    		Double time = user.getWorld().getTime() / 1000.0;
    		if (time>24) { time-=24; }
    		String hr = ""+time.intValue() + 6;
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		if (hr.length()==1) {
    			hr = "0"+hr;
    		}
    		return ""+hr+":"+min;
    	}
    	else if (line.contains("{time:")) {
    		Location loc = getloc(mysplit[1], user);
    		Double time = loc.getWorld().getTime() / 1000.0;
    		if (time>24) { time-=24; }
    		String hr = ""+time.intValue() + 6;
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		if (hr.length()==1) {
    			hr = "0"+hr;
    		}
    		return ""+hr+":"+min;
    	}
    	else if (line.contains("{time12}")) {
    		String ampm = " AM";
    		Double time = user.getWorld().getTime() / 1000.0;
    		if (time>24) { time-=24; }
    		if (time+6>12) {
    			time-=12;
    			ampm = " PM";
    		}
    		String hr = ""+time.intValue() + 6;
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		return ""+hr+":"+min+ampm;
    	}
    	else if (line.contains("{time12:")) {
    		String ampm = " AM";
    		Location loc = getloc(mysplit[1], user);
    		Double time = loc.getWorld().getTime() / 1000.0;
    		if (time>24) { time-=24; }
    		if (time+6>12) {
    			ampm = " PM";
    			time-=12;
    		}
    		String hr = ""+time.intValue() + 6;
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		return ""+hr+":"+min+ampm;
    	}
    	else if (line.contains("{replace:")) {
    		return mysplit[1].replace(mysplit[2], mysplit[3]);
    	}
    	else if (line.contains("{config:")) {
    		return getConfig().getString(mysplit[1]);
    	}
    	else if (line.contains("{structures:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().canGenerateStructures()+"";
    	}
    	else if (line.contains("{structures}")) {
    		return ""+user.getWorld().canGenerateStructures();
    	}
    	else if (line.contains("{autosave}")) {
    		return ""+user.getWorld().isAutoSave();
    	}
    	else if (line.contains("{autosave:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().isAutoSave()+"";
    	}
    	else if (line.contains("{animals:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getAllowAnimals()+"";
    	}
    	else if (line.contains("{animals}")) {
    		return ""+user.getWorld().getAllowAnimals();
    	}
    	else if (line.contains("{monsters:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getAllowMonsters()+"";
    	}
    	else if (line.contains("{monsters}")) {
    		return ""+user.getWorld().getAllowMonsters();
    	}
    	else if (line.contains("{online:")) {
    		Location loc = getloc(mysplit[1], user);
    		return ""+loc.getWorld().getPlayers();
    	}
    	else if (line.contains("{colors}")) {
    		return "&1,&2,&3,&4,&5,&6,&7,&8,&9,&0,&a,&b,&c,&d,&e,&f,&r,&l,&m,&n,&o,&k";
    	}
    	else if (line.contains("{difficulty:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getDifficulty().toString();
    	}
    	else if (line.contains("{difficulty}")) {
    		return ""+user.getWorld().getDifficulty().name();
    	}
    	else if (line.contains("{weatherduration}")) {
    		return ""+user.getWorld().getWeatherDuration();
    	}
    	else if (line.contains("{weatherduration:")) {
    		Location loc = getloc(mysplit[1], user);
    		return ""+loc.getWorld().getWeatherDuration();
    	}
    	else if (line.contains("{environment:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getEnvironment().toString();
    	}
    	else if (line.contains("{environment}")) {
    		return ""+user.getWorld().getEnvironment().name();
    	}
    	else if (line.contains("{player}")) {
    		if (user==null) {
    			return "CONSOLE";
    		}
    		else {
    			return user.getName();
    		}
    	}
    	else if (line.contains("{gvar}")) {
    		return StringUtils.join(globals.keySet(),",").replace("{","").replace("}", "");
    	}
    	else if (line.contains("{sender}")) {
    		if (sender==null) {
    			return "CONSOLE";
    		}
    		else {
    			return sender.getName();
    		}
    	}
    	else if (line.contains("{elevated}")) {
    		return ""+elevation;
    	}
    	else if (line.contains("{gamerules:")) {
    		Location loc = getloc(mysplit[1], user);
    		return StringUtils.join(loc.getWorld().getGameRules(),",");
    	}
    	else if (line.contains("{gamerules}")) {
    		return StringUtils.join(user.getWorld().getGameRules(),",");
    	}
    	else if (line.contains("{seed:")) {
    		Location loc = getloc(mysplit[1], user);
    		return ""+loc.getWorld().getSeed();
    	}
    	else if (line.contains("{seed}")) {
    		return ""+user.getWorld().getSeed();
    	}
    	else if (line.contains("{spawn:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getName()+","+loc.getWorld().getSpawnLocation().getX()+","+loc.getWorld().getSpawnLocation().getY()+","+loc.getWorld().getSpawnLocation().getZ();
    	}
    	else if (line.contains("{difficulty}")) {
    		return ""+user.getWorld().getSpawnLocation();
    	}
    	else if (line.contains("{count:")) {
    		if (mysplit[1].contains(",")) {
    			int count = 0;
    			String[] mylist = mysplit[1].split(",");
    			for (String mynum:mylist) {
    				if (mynum.equals(mysplit[2])) {
    					count+=1;
    				}
    			}
    			return ""+count;
    		}
    		else {
    			return ""+StringUtils.countMatches(mysplit[1],mysplit[2]);
    		}
    	}
    	else if (line.equals("{epoch}")) {
    		return Long.toString(System.currentTimeMillis()/1000);
    	}
    	else if (line.contains("{js:")) {
    		return javascript(line.substring(4,line.length()-1));
    	}
    	else if (line.contains("{javascript:")) {
    		return javascript(line.substring(4,line.length()-1));
    	}
    	else if (line.equals("{epochmilli}")) {
    		return Long.toString(System.currentTimeMillis());
    	}
    	else if (line.equals("{epochnano}")) {
    		return Long.toString(System.nanoTime());
    	}
    	else if (line.equals("{online}")) {
    		String online = "";
      		for (Player qwert:Bukkit.getServer().getOnlinePlayers()) {
      			online+=qwert.getName()+",";
      		}
    		return online.substring(0,online.length()-1);
    	}
    	else if (line.equals("{motd}")) {
    		return ""+Bukkit.getMotd();
    	}
    	else if (line.equals("{banlist}")) {
    		String mylist = "";
      		for (OfflinePlayer clist:Bukkit.getBannedPlayers()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.equals("{playerlist}")) {
    			List<String> names = new ArrayList<String>();
                File playersFolder = new File("world" + File.separator + "players");
                String[] dat = playersFolder.list(new FilenameFilter() {
                    public boolean accept(File f, String s) {
                        return s.endsWith(".dat");
                    }
                });
                for (String current : dat) {
                    names.add(current.replaceAll(".dat$", ""));
                }
                return StringUtils.join(names,",");
    	}
    	else if (line.equals("{baniplist}")) {
    		String mylist = "";
      		for (String clist:Bukkit.getIPBans()) {
      			mylist+=clist+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.equals("{worlds}")) {
    		String mylist = "";
      		for (World clist:getServer().getWorlds()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.equals("{slots}")) {
    		return ""+Bukkit.getMaxPlayers();
    	}
    	else if (line.equals("{port}")) {
    		return ""+Bukkit.getPort();
    	}
    	else if (line.equals("{version}")) {
    		return Bukkit.getVersion().split(" ")[0];
    	}
    	else if (line.equals("{allowflight}")) {
    		return ""+Bukkit.getAllowFlight();
    	}
    	else if (line.equals("{viewdistance}")) {
    		return ""+Bukkit.getViewDistance();
    	}
    	else if (line.equals("{defaultgamemode}")) {
    		return ""+Bukkit.getDefaultGameMode();
    	}
    	else if (line.equals("{operators}")) {
    		String mylist = "";
      		for (OfflinePlayer clist:Bukkit.getOperators()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.equals("{whitelist}")) {
    		Set<OfflinePlayer> mylist = Bukkit.getWhitelistedPlayers();
    		String mystr = "";
    		Iterator<OfflinePlayer> it = mylist.iterator();
    		for (int i=0;i<mylist.size();i++) {
    			if (i==0) {
    				mystr+=it.next().getName();
    			}
    			else {
    				mystr+=","+it.next().getName();
    			}
    		}
    		return mystr;
    	}
    	else if (line.equals("{plugins}")) {
    		Plugin[] myplugins = getServer().getPluginManager().getPlugins();
    		String mystr = "";
    		for (int i=0;i<myplugins.length;i++) {
    			if (i==0) {
    				mystr+=myplugins[i].getName();
    			}
    			else {
    				mystr+=","+myplugins[i].getName();
    			}
    		}
    		return mystr;
    	}
		else if (line.contains("{exhaustion:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getExhaustion();
		}
		else if (line.contains("{firstjoin:")) {
			return Long.toString(Bukkit.getOfflinePlayer(mysplit[1]).getFirstPlayed()/1000);		
		}
		else if (line.contains("{lastplayed:")) {
			return Long.toString(Bukkit.getOfflinePlayer(mysplit[1]).getLastPlayed()/1000);		
		}
		else if (line.contains("{hunger:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getFoodLevel();
		}
		else if (line.contains("{air:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getRemainingAir();
		}
		else if (line.contains("{bed:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getBedSpawnLocation().getX()+","+offlineplayer.getBedSpawnLocation().getY()+","+offlineplayer.getBedSpawnLocation().getZ();
		}
		else if (line.contains("{exp:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getTotalExperience();
		}
		else if (line.contains("{lvl:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			ExperienceManager expMan = new ExperienceManager(user);
			return ""+expMan.getLevelForExp((int) Math.floor(offlineplayer.getTotalExperience()));
		}
		else if (line.contains("{money:")) {
			return ""+econ.getBalance(mysplit[1]);
		}
		else if (line.contains("{prefix:")) {
			String myworld = "world";
			if (user!=null) {
				myworld = user.getWorld().getName();
			}
			return ""+chat.getPlayerPrefix(myworld, mysplit[1]);
		}
		else if (line.contains("{suffix:")) {
			String myworld = "world";
			if (user!=null) {
				myworld = user.getWorld().getName();
			}
			return ""+chat.getPlayerSuffix(myworld, mysplit[1]);
		}
		else if (line.contains("{group:")) {
			String myworld = "world";
			if (user!=null) {
				myworld = user.getWorld().getName();
			}
			return ""+chat.getPrimaryGroup(myworld, mysplit[1]);
		}
		else if (line.contains("{operator:")) {
			return ""+Bukkit.getOfflinePlayer(mysplit[1]).isOp();
		}
		else if (line.contains("{itemid:")) {
			//TODO item
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getItemInHand();
		}
		else if (line.contains("{itemamount:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getInventory().getItemInHand().getAmount();
		}
		else if (line.contains("{itemname:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getInventory().getItemInHand().getType().toString();
		}
		else if (line.contains("{durability:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getInventory().getItemInHand().getDurability();
		}
		else if (line.contains("{gamemode}")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			if(offlineplayer.getGameMode() == GameMode.CREATIVE){
	        	return "CREATIVE";
	        }
	        else if(offlineplayer.getGameMode() == GameMode.SURVIVAL){
	        	return "SURVIVAL";
	        }
	        else {
	        	return "ADVENTURE";
	        }
		}
		else if (line.contains("{direction:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
        	String tempstr = "null";
            int degrees = (Math.round(offlineplayer.getLocation().getYaw()) + 270) % 360;
            if (degrees <= 22)  {tempstr="WEST";}
            else if (degrees <= 67) {tempstr="NORTHWEST";}
            else if (degrees <= 112) {tempstr="NORTH";}
            else if (degrees <= 157) {tempstr="NORTHEAST";}
            else if (degrees <= 202) {tempstr="EAST";}
            else if (degrees <= 247) {tempstr="SOUTHEAST";}
            else if (degrees <= 292) {tempstr="SOUTH";}
            else if (degrees <= 337) {tempstr="SOUTHWEST";}
            else if (degrees <= 359) {tempstr="WEST";}
            return tempstr;
		}
		else if (line.contains("{health:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return String.valueOf(offlineplayer.getHealthInt());
		}
		else if (line.contains("{biome:")) {
			Location loc = getloc(mysplit[1], user);
			return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()).toString();
		}
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
		else if (line.equals("{storm:")) {
			Location loc = getloc(mysplit[1], user);
			if (loc.getWorld().hasStorm()) {
				return "true";
			}
			return "false";
		}
		else if (line.equals("{thunder:")) {
			Location loc = getloc(mysplit[1], user);
			if (loc.getWorld().isThundering()) {
				return "true";
			}
			return "false";
		}
		else if (line.contains("{x:")) {
			return String.valueOf(Math.round(getloc(mysplit[1], user).getX()));
		}
		else if (line.contains("{y:")) {
			return String.valueOf(Math.round(getloc(mysplit[1], user).getY()));
		}
		else if (line.contains("{z:")) {
			return String.valueOf(Math.round(getloc(mysplit[1], user).getZ()));
		}
    	//TODO
    	else if (user != null) {
    		if (line.contains(":")) {
    			line = line.split(":")[0]+"}";
    		}
    		if (line.equals("{player}")) {
    			return ""+user.getName();
    		}
    		else if (line.equals("{sneaking}")) {
    			return ""+user.isSneaking();
    		}
    		if (line.equals("{itempickup}")) {
	          return ""+user.getCanPickupItems();
	        }
    		else if (line.equals("{flying}")) {
    			return ""+user.getAllowFlight();
    		}
    		else if (line.equals("{blocking}")) {
    			return ""+user.isBlocking();
    		}
    		else if (line.equals("{exhaustion}")) {
    			return ""+user.getExhaustion();
    		}
    		else if (line.equals("{firstjoin}")) {
    			return ""+Long.toString(user.getFirstPlayed()/1000);
    		}
    		else if (line.equals("{hunger}")) {
    			return ""+user.getFoodLevel();
    		}
    		else if (line.equals("{grounded}")) {
    			return ""+user.isOnGround();
    		}
    		else if (line.equals("{passenger}")) {
    			if (user.getVehicle()==null) {
    				return "false";
    			}
    			return ""+user.getVehicle().toString();
    		}
    		else if (line.equals("{maxhealth}")) {
    			return ""+user.getMaxHealth();
    		}
    		else if (line.equals("{maxair}")) {
    			return ""+user.getMaximumAir();
    		}
    		else if (line.equals("{air}")) {
    			return ""+(user.getRemainingAir()/20);
    		}
    		else if (line.equals("{age}")) {
    			return ""+(user.getTicksLived()/20);
    		}
    		else if (line.equals("{bed}")) {
    			return ""+user.getBedSpawnLocation().getX()+","+user.getBedSpawnLocation().getY()+","+user.getBedSpawnLocation().getZ();
    		}
    		else if (line.equals("{compass}")) {
    			return ""+user.getCompassTarget().getX()+","+user.getCompassTarget().getY()+","+user.getCompassTarget().getZ();
    		}
    		else if (line.equals("{storm}")) {
    			if (user.getWorld().hasStorm()) {
    				return "true";
    			}
    			return "false";
    		}
    		else if (line.equals("{thunder}")) {
    			if (user.getWorld().isThundering()) {
    				return "true";
    			}
    			return "false";
    		}

    		else if (line.equals("{dead}")) {
    			return ""+user.isDead();
    		}
    		else if (line.equals("{sleeping}")) {
    			return ""+user.isSleeping();
    		}
    		else if (line.equals("{whitelisted}")) {
    			return ""+user.isWhitelisted();
    		}
    		else if (line.equals("{world}")) {
    			return user.getWorld().getName();
    		}
        	else if (line.contains("{world:")) {
        		return Bukkit.getWorld(mysplit[1]).getName();
        	}
        	else if (line.equals("{x}")) {
    			return String.valueOf(Math.round(user.getLocation().getX()));
    		}
    		else if (line.equals("{y}")) {
    			return String.valueOf(Math.round(user.getLocation().getY()));
    		}
    		else if (line.equals("{z}")) {
    			return String.valueOf(Math.round(user.getLocation().getZ()));
    		}

    		else if (line.equals("{lvl}")) {
    			ExperienceManager expMan = new ExperienceManager(user);
    			return ""+expMan.getLevelForExp(expMan.getCurrentExp());
    		}
    		else if (line.equals("{exp}")) {
    			ExperienceManager expMan = new ExperienceManager(user);
    			return ""+expMan.getCurrentExp();
    		}
    		else if (line.equals("{money}")) {
    			return ""+econ.getBalance(user.getName());
    		}
    		else if (line.equals("{prefix}")) {
    			return ""+chat.getPlayerPrefix(user);
    		}
    		else if (line.equals("{suffix}")) {
    			return ""+chat.getPlayerSuffix(user);
    		}
    		else if (line.equals("{group}")) {
    			return ""+perms.getPrimaryGroup(user);
    		}
    		else if (line.equals("{operator}")) {
    			if (user==null) {
    				return "true";
    			}
    			else {
    				return ""+user.isOp();
    			}
    		}
    		else if (line.equals("{worldtype}")) {
    			return ""+user.getWorld().getWorldType();
    		}
    		else if (line.equals("{itemid}")) {
    			return String.valueOf(user.getInventory().getItemInHand().getTypeId());
    		}
    		else if (line.equals("{itemamount}")) {
    			return String.valueOf(user.getInventory().getItemInHand().getAmount());
    		}
    		else if (line.equals("{itemname}")) {
    			return String.valueOf(user.getInventory().getItemInHand().getType());
    		}
    		else if (line.equals("{durability}")) {
    			return String.valueOf(user.getInventory().getItemInHand().getDurability());
    		}
    		else if (line.equals("{ip}")) {
    			return user.getAddress().getAddress().toString().split("/")[(user.getAddress().toString().split("/").length)-1].split(":")[0];
    		}
    		else if (line.equals("{display}")) {
    			return ""+user.getDisplayName();
    		}
    		else if (line.equals("{gamemode}")) {
    			if(user.getGameMode() == GameMode.CREATIVE){
    	        	return "CREATIVE";
    	        }
    	        else if(user.getGameMode() == GameMode.SURVIVAL){
    	        	return "SURVIVAL";
    	        }
    	        else {
    	        	return "ADVENTURE";
    	        }
    		}
    		else if (line.equals("{direction}")) {
    	        	String tempstr = "null";
    	            int degrees = (Math.round(user.getLocation().getYaw()) + 270) % 360;
    	            if (degrees <= 22)  {tempstr="WEST";}
    	            else if (degrees <= 67) {tempstr="NORTHWEST";}
    	            else if (degrees <= 112) {tempstr="NORTH";}
    	            else if (degrees <= 157) {tempstr="NORTHEAST";}
    	            else if (degrees <= 202) {tempstr="EAST";}
    	            else if (degrees <= 247) {tempstr="SOUTHEAST";}
    	            else if (degrees <= 292) {tempstr="SOUTH";}
    	            else if (degrees <= 337) {tempstr="SOUTHWEST";}
    	            else if (degrees <= 359) {tempstr="WEST";}
    	            return tempstr;
    		}
    		else if (line.equals("{biome}")) {
    			return user.getWorld().getBiome(user.getLocation().getBlockX(), user.getLocation().getBlockZ()).toString();
    		}
    		else if (line.equals("{health}")) {
    			return String.valueOf(user.getHealth());
    		}
    	}
    	
    	for (Entry<String, Object> node : globals.entrySet()) {
    		if (line.equals(node.getKey())) {
    			return ""+node.getValue();
    		}
        }
    	Set<String> custom = null;
    	FileConfiguration myconfig = getConfig();
		custom = myconfig.getConfigurationSection("signs.placeholders").getKeys(false);
    	if (custom.size()>0) {
    		for (String mycustom:custom) {
    			
    			if (line.contains("{"+mycustom+":")||line.equals("{"+mycustom+"}")) {
	    			List<String> current = myconfig.getStringList("scripting.placeholders."+mycustom);
	    			String mycommands = StringUtils.join(current,";");
	    			for(int i = 0; i < mysplit.length; i++) {
	    				mycommands.replace("{arg"+i+"}", mysplit[i]);
	    			}
	    			try {
	    				String result = execute(mycommands,user,sender,elevation);
	    				if (result.substring(0,3).equals("if ")) {
	    					return ""+testif(result);
	    				}
	    			return result;
	    			}
	    			catch (Exception e) {
//	    				System.out.println("F "+e);
	    			}
    			}
    		}
    	}
    	
    	return "null";
    	
    }
    public String evaluate(String line, Player user, Player sender, Boolean elevation) {
        String[] args = line.split(" "); 
        for(int i = 0; i < args.length; i++) {
        	if (line.contains("{arg"+(i+1)+"}")){
        		line.replace("{arg"+(i+1)+"}", args[i]);
        	}
        }

        
      	 int last = 0;
      	 boolean isnew = true;
      	 int q = 0;
       	while (StringUtils.countMatches(line, "{")==StringUtils.countMatches(line, "}")) {
       		q++;
       		if ((q>1000)||(StringUtils.countMatches(line, "{")==0)) {
       			break;
       		}
       	for(int i = 0; i < line.length(); i++) {
       		
       		String current = ""+line.charAt(i);
       		if (current.equals("{")) {
       			isnew = true;
       			last = i;
       		}
       		else if (current.equals("}")) {
       			if (isnew) {
       				String toreplace = line.substring(last,i+1);
       				line.substring(1,line.length()-1).split(":");
       				boolean replaced = false;
       				if (replaced==false) {
       					try {
       						line = line.replace(toreplace, fphs(toreplace,user,sender,elevation));
       					}
       					catch (Exception e) {
       						line = line.replace(toreplace, "null");
       					}
       				}
       				
           			break;
       			}
       			isnew = false;
       		}
       		
       	}
       	}	
       	if (line.contains(",")==false)
       	{
       		if(line.matches(".*\\d.*")){
       			boolean num = false;
       			if (line.contains("+")) {
       				num = true;
       			}
       			else if (line.contains("-")) {
       				num = true;
       			}
       			else if (line.contains("*")) {
       				num = true;
       			}
       			else if (line.contains("/")) {
       				num = true;
       			}
       			else if (line.contains("%")) {
       				num = true;
       			}
       			else if (line.contains("=")) {
       				num = true;
       			}
       			else if (line.contains(">")) {
       				num = true;
       			}
       			else if (line.contains("<")) {
       				num = true;
       			}
       			else if (line.contains("|")) {
       				num = true;
       			}
       			else if (line.contains("&")) {
       				num = true;
       			}
       			if (num) {
       				line = javascript(line);
       			}
       		}
       	}
        if (line.equals("null")) {
        	return "";
        }
    	return line;
    }
    private String getDirection(Player player) {
        int degrees = (Math.round(player.getLocation().getYaw()) + 270) % 360;
        if (degrees <= 22) return getmsg("DIRECTION7");
        if (degrees <= 67) return getmsg("DIRECTION8");
        if (degrees <= 112) return getmsg("DIRECTION1");
        if (degrees <= 157) return getmsg("DIRECTION2");
        if (degrees <= 202) return getmsg("DIRECTION3");
        if (degrees <= 247) return getmsg("DIRECTION4");
        if (degrees <= 292) return getmsg("DIRECTION5");
        if (degrees <= 337) return getmsg("DIRECTION6");
        if (degrees <= 359) return getmsg("DIRECTION7");
        return "null";
    }

    public String colorise(String mystring) {
    	String[] codes = {"&1","&2","&3","&4","&5","&6","&7","&8","&9","&0","&a","&b","&c","&d","&e","&f","&r","&l","&m","&n","&o","&k"};
    	for (String code:codes) {
    		mystring = mystring.replace(code, "§"+code.charAt(1));
    	}
    	return mystring;
    }
    public Location getloc(String string,Player user) {
		if (string.contains(",")==false) {
			Player player = Bukkit.getPlayer(string);
			if (player!=null) {
				return player.getLocation();
			}
			else {
				ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(string);
				if (offlineplayer.exists()) {
					return offlineplayer.getLocation();
				}
				else {
					World world = Bukkit.getWorld(string);
					if (world!=null) {
						return world.getSpawnLocation();
					}
				}
			}
			
		}
		else {
			String[] mysplit = string.split(",");
			World world = Bukkit.getWorld(mysplit[0]);
			if (world!=null) {
				double x;double y;double z;
				if (mysplit.length==4) {
					try { x = Double.parseDouble(mysplit[1]);} catch (Exception e) {x=world.getSpawnLocation().getX();}
					try { y = Double.parseDouble(mysplit[2]);} catch (Exception e) {y=world.getSpawnLocation().getY();}
					try { z = Double.parseDouble(mysplit[3]);} catch (Exception e) {z=world.getSpawnLocation().getZ();}
					return new Location(world, x, y, z);
				}
			}
			else {
				return null;
			}
		}
		return null;
	}
    public boolean testif(String mystring) {
    	String[] args;
    	if (mystring.substring(0, 2).equalsIgnoreCase("if")) {
    		mystring = mystring.substring(3,mystring.length());
    	}
    	int splittype = 0;
    	mystring=mystring.trim();
    	if (mystring.contains("!=") == true) {
    		splittype = 6;
    		args = mystring.split("!=");
    	}
    	else if (mystring.contains(">=") == true) {
    		splittype = 4;
    		args = mystring.split(">=");
    	}
    	else if (mystring.contains("<=") == true) {
    		splittype = 5;
    		args = mystring.split("<=");
    	}
    	else if (mystring.contains("=~") == true) {
    		splittype = 7;
    		args = mystring.split("=~");
    	}
    	else if (mystring.contains("=") == true) {
    		splittype = 1;
    		args = mystring.split("=");
    	}
    	else if (mystring.contains(">") == true) {
    		splittype = 2;
    		args = mystring.split(">");
    	}
    	else if (mystring.contains("<") == true) {
    		splittype = 3;
    		args = mystring.split("<");
    	}
    	else if (mystring.contains("!") == true) {
    		splittype = 6;
    		args = mystring.split("!");
    	}
    	else {
    		args = "true false".split(" ");
    		splittype = 1;
    	}
    	boolean toreturn = false;
    	String left = args[0].trim();
    	String right = args[1].trim();
    	try {
    		boolean failed = false;
			int result1 = 0;
			int result2 = 1;
    		try {
    		result1 = (int) Double.parseDouble("" + engine.eval(left));
    		result2 = (int) Double.parseDouble("" + engine.eval(right));
    		}
    		catch (Exception e) {
    			failed = true;
    		}
    		if (failed == false) {
    		if (splittype == 1) { if (result1==result2) { toreturn = true; } }
    		else if (splittype == 2) { if (result1>result2) { toreturn = true; } }
    		else if (splittype == 3) { if (result1<result2) { toreturn = true; } }
    		else if (splittype == 4) { if (result1>=result2) { toreturn = true; } }
    		else if (splittype == 5) { if (result1<=result2) { toreturn = true; } }
    		else if (splittype == 6) { if (result1!=result2) { toreturn = true; } }
    		}
		} catch (Exception e) {
			
		}
    	if (toreturn == false) {
    	try {
    		boolean failed = false;
			String result1 = "true";
			String result2 = "false";
    		try {
    		result1 = left.trim();
    		result2 = right.trim();
    		}
    		catch (Exception e3) {
    			failed = true;
    		}
    		if (failed == false) {
    		if (splittype == 1) { if (result1.equals(result2)) { toreturn = true; } }
    		else if (splittype == 2) { if (result1.length()>result2.length()) { toreturn = true; } }
    		else if (splittype == 3) { if (result1.length()<result2.length()) { toreturn = true; } }
    		else if (splittype == 4) { if (result1.length()>=result2.length()) { toreturn = true; } }
    		else if (splittype == 5) { if (result1.length()<=result2.length()) { toreturn = true; } }
    		else if (splittype == 6) { if (result1.equals(result2)==false) { toreturn = true; } }
    		else if (splittype == 7) { if (result1.equalsIgnoreCase(result2)) { toreturn = true; } }
    		}
		} catch (Exception e1) {
		}
    	}   	
    	if (mystring.equalsIgnoreCase("false")) {
    		toreturn = false;
    	}
    	else if (mystring.equalsIgnoreCase("true")) {
    		toreturn = true;
    	}
    	
    	return toreturn;
    }
    
    public String pay() {
    	if (getServer().getOnlinePlayers().length==0) {
			return "&cNo online players&7.";
		}
    	String toreturn = colorise("&7"+getmsg("PAY1"))+"\n";

    	double paymoney;
		Set<String> groups = getConfig().getConfigurationSection("economy.salary.groups").getKeys(false);
		
		
		Set<String> bank2 = getConfig().getConfigurationSection("economy.salary.bank").getKeys(false);
		Map<String, Integer> banks2 = new HashMap<String, Integer>();
		for (String current:bank2) {
			banks2.put(current, getConfig().getInt("economy.salary.bank."+current+".money")/getServer().getOnlinePlayers().length);
		}
		
    	for(Player player:getServer().getOnlinePlayers()){
    		toreturn += ChatColor.GRAY+"    - "+getmsg("PAY2")+" "+ChatColor.RED+player.getName()+ChatColor.GRAY+".\n";
    		if (getConfig().getInt("economy.salary.notify-level")>1) {
    			String message = getConfig().getString("economy.salary.message");
    			if (message != "") {
    				msg(player,colorise(message));
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
    				msg(player,"    "+ChatColor.GRAY+getConfig().getString("economy.symbol")+paymoney+ChatColor.WHITE+", "+ChatColor.GRAY+payexp+" exp"+ChatColor.WHITE+", "+ChatColor.GRAY+paylvl+" lvl"+ChatColor.WHITE+" from group "+ChatColor.BLUE+group);
    			}
    			}
    			else {
    			}
    			try {
    				String mybank = getConfig().getString("economy.salary.groups."+group+".bank.withdraw");
    				if (mybank.equals("__global__")==false) {
	    				paymoney += banks2.get(mybank);
	    				getConfig().set("economy.salary.bank."+mybank+".money", getConfig().getInt("economy.salary.bank."+mybank+".money")-banks2.get(mybank));
    				}
    			}
    			catch (Exception e) {
    				msg(player,"Error "+e);
    			}
    			if (paymoney<0) {
    				try {
    				Set<String> deposit = getConfig().getConfigurationSection("economy.salary.groups."+group+".bank.deposit").getKeys(false);
    				msg(player,deposit+"{");
    				if (deposit.size()<0) {
    					for (String bank:deposit) {
    						String percentage = getConfig().getString("economy.salary.groups."+group+".bank.deposit."+bank);
    						percentage = percentage.substring(0,percentage.length()-1).trim();
    						getConfig().set("economy.bank."+bank, getConfig().getInt("economy.bank."+bank)-(((paymoney)*(Integer.parseInt(percentage)))/100));
    					}
    				}
    				else {
    					getConfig().set("economy.bank.__global__",getConfig().getInt("economy.bank.__global__")+paymoney);
    				}
    				}
    				catch (Exception e) {
    					
    				}
    			}
    			else if (paymoney > 0) {
    				// DO NOTHING
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
				  ExperienceManager expMan = new ExperienceManager(player);
				  expMan.changeExp(payexp);
			}
			else if (payexp < 0) {
				amount2 = ChatColor.GRAY+", lost: "+ChatColor.RED+payexp+" exp";
				  ExperienceManager expMan = new ExperienceManager(player);
				  expMan.changeExp(payexp); 
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
	    			msg(player,amount+amount2+amount3);
	    		}
			}
			else {
				msg(player,ChatColor.GRAY+getmsg("PAY3")+" "+ChatColor.RED+getmsg("PAY4")+ChatColor.GRAY+".");
			}
    }
    	toreturn += ChatColor.GREEN+getmsg("DONE");
    	return toreturn;
    }
    
    
    
    
    Timer timer = new Timer ();
    
	TimerTask mytask = new TimerTask () {
		@Override
	    public void run () {
			if (counter0<1000) {
				counter0++;
			}
			else {
				if (list.size()>1000) {
					list = list.subList(list.size()-960,list.size());
					players = players.subList(players.size()-960,players.size());
				}
//				System.out.println("FDA "+list.size());
			counter0=0;
			counter++;
			counter2++;
			if (counter >= getConfig().getInt("economy.salary.interval")) {
	    	if (getConfig().getString("economy.salary.enabled").equalsIgnoreCase("true")) {
	    		if (getConfig().getInt("economy.salary.interval") > 0) {
	    			counter = 0;
	    			pay();	    			
	    		}
	    	}
		}
			if (counter2 > 1200) {
				counter2 = 0;
				System.out.println("[SignRanksPlus] Saving variables...");
				getConfig().getConfigurationSection("scripting").set("variables", null);
		        for (final Entry<String, Object> node : globals.entrySet()) {
		        	getConfig().options().copyDefaults(true);
		        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
		        	
		        	saveConfig();
		        }
		        System.out.println("DONE!");
			}
			else if (counter2%4==0) {
				
				
				try {
				// load scheduled tasks
				Set<String> tasks = getConfig().getConfigurationSection("scripting.tasks").getKeys(false);
				for(String current : tasks) {
					Long time = (System.currentTimeMillis()/1000);
					Long task = Long.parseLong(current);
					if (time>task) {
						List<String> toexecute = getConfig().getStringList("scripting.tasks."+current);
						recursion = 0;
						execute(StringUtils.join(toexecute,";"),null,null,true);
						getConfig().getConfigurationSection("scripting.tasks").set(current, null);
						saveConfig();
					}
					
				}
				}
				catch (Exception e) {
				}
			}
		}
		}
		
	};
	

	
	
    @Override
    public void onDisable() {
    	
    	try {
        	timer.cancel();
        	timer.purge();
    	}
    	catch (IllegalStateException e) {
    		
    	}
    	catch (Throwable e) {
    		
    	}
    	getConfig().getConfigurationSection("signs.types").set("custom", null);
    	getConfig().getConfigurationSection("scripting").set("custom-placeholders", null);
    	this.saveConfig();
		System.out.println("[SignRanksPlus] "+getmsg("SAVE1"));
        for (final Entry<String, Object> node : globals.entrySet()) {
        	getConfig().options().copyDefaults(true);
        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
        	
        	saveConfig();
        }
        System.out.println(getmsg("DONE"));
    }
    
    
    
	@Override
    public void onEnable(){
		plugin = this;
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // LANG START
        File f0 = new File(getDataFolder() + File.separator+"expdata.yml");
        if(f0.exists()!=true) {  saveResource("expdata.yml", false); }
        File f3 = new File(getDataFolder() + File.separator+"english.yml");
        if(f3.exists()!=true) {  saveResource("english.yml", false); }
        File f4 = new File(getDataFolder() + File.separator+"french.yml");
        if(f4.exists()!=true) {  saveResource("french.yml", false); }
        File f5 = new File(getDataFolder() + File.separator+"signs"+File.separator+"cmd.yml");
        if(f5.exists()!=true) {  saveResource("signs"+File.separator+"cmd.yml", false); }
        File f6 = new File(getDataFolder() + File.separator+"signs"+File.separator+"cmdop.yml");
        if(f6.exists()!=true) {  saveResource("signs"+File.separator+"cmdop.yml", false); }
        File f7 = new File(getDataFolder() + File.separator+"signs"+File.separator+"cmdcon.yml");
        if(f7.exists()!=true) {  saveResource("signs"+File.separator+"cmdcon.yml", false); }
        File f8 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"example.yml");
        if(f8.exists()!=true) {  saveResource("scripts"+File.separator+"example.yml", false); }
        
        saveResource("idlist.yml", true);
        //TODO update checking.
        
    	/* - TODO setuser setelevation setworld (DEFAULT WORLD = USER)
	       - TODO proper functional place-holders
		user.getWorld().getDifficulty();
		user.getWorld().playSound(arg0, arg1, arg2, arg3);
		user.getWorld().playEffect(arg0, arg1, arg2, arg3);
		user.getWorld().canGenerateStructures()
		user.getWorld().isGameRule(arg0);
		user.getWorld().isAutoSave()
		user.getWorld().isChunkInUse(arg0, arg1);
		user.getWorld().isChunkLoaded(arg0);
		user.getWorld().getAllowAnimals();
		user.getWorld().getAllowMonsters();
		user.getWorld().getAmbientSpawnLimit();
		user.getWorld().getAnimalSpawnLimit();
		user.getWorld().getTemperature(arg0, arg1);
		user.getWorld().getWeatherDuration();
		user.getWorld().getBlockAt(arg0);
		user.getWorld().getBlockTypeIdAt(arg0);
		user.getWorld().getEnvironment();
		user.getWorld().getGameRules();
		user.getWorld().getGameRuleValue(arg0);
		user.getWorld().getGenerator();
		user.getWorld().getHighestBlockAt(arg0);
		user.getWorld().getHumidity(arg0, arg1)
		user.getWorld().getMaxHeight();
		user.getWorld().getMonsterSpawnLimit();
		user.getWorld().getTime();
		user.getWorld().getWaterAnimalSpawnLimit();
		user.getWorld().getThunderDuration();
		user.getWorld().getSeed();
		user.getWorld().getSpawnLocation();
		user.getWorld().getPVP();
		user.getWorld().getPlayers(); 
		
		*/
        
        File f1 = new File(getDataFolder() + File.separator + "scripts");
        File[] mysigns = f1.listFiles();
        for (int i = 0; i < mysigns.length; i++) {
        	if (mysigns[i].isFile()) {
        		if (mysigns[i].getName().contains(".yml")) {
	        		FileConfiguration current = YamlConfiguration.loadConfiguration(mysigns[i]);
	        		Set<String> values = current.getConfigurationSection("").getKeys(false);
					for(String myval:values) {
	        			getConfig().set("scripting.placeholders."+mysigns[i].getName().substring(0,mysigns[i].getName().length()-4), current.get(myval));
	        		}
        		}
        	}
        }
        
        
        
        File f2 = new File(getDataFolder() + File.separator + "signs");
        File[] myscripts = f2.listFiles();
        for (int i = 0; i < myscripts.length; i++) {
        	if (myscripts[i].isFile()) {
        		if (myscripts[i].getName().contains(".yml")) {
	        		FileConfiguration current = YamlConfiguration.loadConfiguration(myscripts[i]);
	        		Set<String> values = current.getConfigurationSection("").getKeys(false);
					for(String myval:values) {
	        			getConfig().set("signs.types.custom."+myscripts[i].getName().replace(".","")+"."+myval, current.get(myval));
	        		}
        		}
        	}
        }        
        getConfig().options().copyDefaults(true);
        
        
        final Map<String, Object> options = new HashMap<String, Object>();
        getConfig().set("version", "0.6.1");
        options.put("signs.protect",true);
        options.put("language","english");
        
        options.put("individual-signs.auto-update",true);
        
        options.put("signs.types.promote.enabled",true);
        options.put("signs.types.promote.text","[Promote]");
        
        options.put("signs.types.perm.enabled",true);
        options.put("signs.types.perm.text","[Perm]");
        
        options.put("signs.types.inherit.enabled",true);
        options.put("signs.types.inherit.text","[Inherit]");
        options.put("signs.types.inherit.refund",false);
        
        options.put("signs.types.prefix.enabled",true);
        options.put("signs.types.prefix.refund",false);
        options.put("signs.types.prefix.text","[Prefix]");
        
        options.put("signs.types.extend.enabled",true);
        options.put("signs.types.extend.location","SIDE");
        options.put("signs.types.extend.text","[Require]");
        
        options.put("shortened-perms.ess","essentials");
        options.put("shortened-perms.sr","signranks");
        
        options.put("signs.types.suffix.enabled",true);
        options.put("signs.types.suffix.refund",false);
        options.put("signs.types.suffix.text","[Suffix]");
        
        options.put("signs.types.xpbank.enabled",true);
        options.put("signs.types.xpbank.text","[xpBank]");
        options.put("signs.types.xpbank.cost","5 lvl");
        options.put("signs.types.xpbank.storage.Default",5000);
        options.put("signs.types.xpbank.storage.Builder",10000);
        
        options.put("signs.types.shop.enabled",true);
        options.put("signs.types.shop.text","[Shop]");
        options.put("signs.types.shop.storage",2304);
       
        
        options.put("economy.symbol","$");
        options.put("economy.salary.enabled",false);
        options.put("economy.salary.bank.__global__.money",0);
        options.put("economy.salary.bank.__global__.experience",0);
        options.put("economy.salary.message","&9[&3SignRanksPlus+&9]&f Pay Day example message.");
        options.put("economy.salary.notify-level",2);
        options.put("economy.salary.check-subgroups",true);
        options.put("economy.salary.interval",1200);
        
        options.put("economy.salary.groups.Default.bank.deposit.__global__","100%");
        options.put("economy.salary.groups.Default.bank.withdraw","__global__");
        options.put("economy.salary.groups.Default.exp.base","0 exp");
        options.put("economy.salary.groups.Default.exp.bonus","0 exp");
        options.put("economy.salary.groups.Default.exp.percentage",0);
        options.put("economy.salary.groups.Default.money.base","0 exp");
        options.put("economy.salary.groups.Default.money.bonus","0 exp");
        options.put("economy.salary.groups.Default.money.percentage",0);
        options.put("cross_map_trade",false);
        options.put("show-sender",false);
        options.put("scripting.save-variables",false);
        options.put("scripting.replace-chat",false);
        options.put("scripting.show-sender",false);
        options.put("scripting.debug-level",0);
        
        
        for (final Entry<String, Object> node : options.entrySet()) {
        	 if (!getConfig().contains(node.getKey())) {
        		 getConfig().set(node.getKey(), node.getValue());
        	 }
        }
        try {
        	Set<String> vars = getConfig().getConfigurationSection("scripting.variables").getKeys(false);
        	for(String current : vars) {
        		
    			globals.put("{"+current+"}", this.getConfig().getString("scripting.variables."+current));
    		}
        }
        catch (Exception e) {
        	
        }
    	 
    	saveConfig();
        setupPermissions();
        setupChat();
    	this.saveDefaultConfig();
    	getServer().getPluginManager().registerEvents(this, this);
    	timer.schedule (mytask,0l, 1);
    	
	}
	private boolean setupChat() {
	        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
	        chat = rsp.getProvider();
	        return chat != null;
	    }
	 @EventHandler
	 public void onPlayerChat(PlayerChatEvent event){
	       if(event.isCancelled()) {
	     	return;
	     }
	        else {
       	if (this.getConfig().getString("scripting.replace-chat").trim().equalsIgnoreCase("true")) {
       		recursion = 0;
       		String msg = evaluate(event.getMessage(),event.getPlayer(),event.getPlayer(),false);
       		if (msg.equals(event.getMessage())==false) {
       			if (msg.length()>200) {
       				msg = msg.substring(0,200);
       			}
       			event.getPlayer().chat(colorise(msg));
       			event.setCancelled(true);
       		}
       		
       		
       	}
	       else if (this.getConfig().getString("scripting.replace-chat").trim().equalsIgnoreCase("false")) {
	    	   
	       }
	       else {
	    	   boolean cancel = true;
	    	   recursion = 0;
	    	   String msg = evaluate(this.getConfig().getString("scripting.replace-chat")+""+event.getMessage(),event.getPlayer(),event.getPlayer(),false);
	       		msg(null,msg);
	       		for(Player user:getServer().getOnlinePlayers()){
	       			msg = evaluate(this.getConfig().getString("scripting.replace-chat").replace("{line}",event.getMessage()),user,event.getPlayer(),false);
	       			if (msg.length()>150) {
	       				msg = msg.substring(0,150);
	       			}
	       			if (msg.equals(event.getMessage())==false) {
	       			msg(user,msg);
	       			}
	       			else {
	       				cancel = false;
	       			}
	       		}
       			event.setCancelled(cancel);
	       }
       }
	 }
	 
	 
	 public Object[] LevensteinDistance(String string) {
		 	Object[] toreturn = new Object[2];
		 	try {
		 		toreturn[0] = new ItemStack(Integer.parseInt(string));
		 		toreturn[1] = Material.getMaterial(Integer.parseInt(string)).toString();
	    		return  toreturn;
	    	}
	    	catch (Exception e) {
	    		
	    	}
	    	string = StringUtils.replace(string.toString(), " ", "_").toUpperCase();
	    	Material[] materials = Material.values();
			int smallest = -1;
			String materialname = null;
	    	ItemStack lastmaterial = null;
			File yamlFile = new File(getDataFolder()+File.separator+"idlist.yml");
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
			Set<String> ids = yaml.getConfigurationSection("item-ids").getKeys(false);
			try {
				toreturn[1] = ""+yaml.getString("item-ids."+string.replace(":","-"));
				toreturn[0] = new ItemStack(Integer.parseInt(string.split(":")[0]),1, Short.parseShort(string.split(":")[1]));
				return toreturn;
			}
			catch (Exception e) {
				if (string.contains(":")) {
					try {
						toreturn[1] = Integer.parseInt(string.split(":")[0])+":"+Integer.parseInt(string.split(":")[1]);
						toreturn[0] = new ItemStack(Integer.parseInt(string.split(":")[0]),1, Short.parseShort(string.split(":")[1]));
						return toreturn;
					}
					catch (Exception e2) {
						
					}
				}
			}
			for (String current:ids) {
				String itemname = yaml.getString("item-ids."+current);
				if (smallest == -1) {
					lastmaterial = new ItemStack(Material.AIR);
					smallest = 100;
				}
				else {
					int distance = StringUtils.getLevenshteinDistance(string.toUpperCase(), itemname.toUpperCase());
					if (itemname.contains(string)) {
						distance = StringUtils.getLevenshteinDistance(string.toUpperCase(), itemname.toUpperCase())-4;
						if (distance<smallest) {
							if (current.contains("-")) {
								lastmaterial = new ItemStack(Integer.parseInt(current.split("-")[0]),1, Short.parseShort(current.split("-")[1]));
							}
							else {
								lastmaterial = new ItemStack(Integer.parseInt(current),1, Short.parseShort("0"));
							}
							smallest = distance;
							materialname=itemname;
						}
					}
					else {
						if (distance<smallest) {
							if (current.contains("-")) {
								lastmaterial = new ItemStack(Integer.parseInt(current.split("-")[0]),1, Short.parseShort(current.split("-")[1]));
							}
							else {
								lastmaterial = new ItemStack(Integer.parseInt(current),1, Short.parseShort("0"));
							}
							materialname=itemname;
							smallest = distance;
						}
					}
				}
			}
	    	for (Material mymaterial:materials) {
	    		String current = mymaterial.toString();
	    		if (smallest == -1) {
	    			lastmaterial = new ItemStack(mymaterial);
	    			materialname=mymaterial.toString();
	    			smallest = 100;
	    		}
	    		else {
	    			int distance;
	    			if (current.contains(string)) {
	    				distance = StringUtils.getLevenshteinDistance(string.toUpperCase(), current)-4;
	    				if (distance==-1) {
	    					distance = 0;
	    				}
	    			}
	    			else {
	    				distance = StringUtils.getLevenshteinDistance(string.toUpperCase(), current)+Math.abs(string.length()-current.length());
	    			}
	    			if (distance<smallest) {
	    				materialname=mymaterial.toString();
	    				smallest = distance;
	    				lastmaterial = new ItemStack(mymaterial);
	    			}
	    		}
	    	}
	    	toreturn[0] = lastmaterial;
	    	toreturn[1] = materialname;
	    	return toreturn;
	    }
	 
	 @EventHandler
	 public void blockSignBreak(BlockBreakEvent event) {
		 if (this.getConfig().getString("signs.protect").equalsIgnoreCase("true")) {
		 Player player = event.getPlayer();
		 if (checkperm(player,"signranks.destroy.*")==false) {
	        if(event.getBlock().getState() instanceof Sign) {
	            Sign sign = (Sign) event.getBlock().getState();  
	            if (((this.getConfig().getString("signs.types.perm.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.perm.text"))))||((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.extend.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.extend.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.suffix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.suffix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text")))) || ((this.getConfig().getString("signs.types.shop.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text"))))) {              
	            	msg(player,ChatColor.LIGHT_PURPLE+"You broke a sign :O");
	                if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))&&(checkperm(player,"signranks.destroy.promote")==false)) {
		            	event.setCancelled(true);
		                msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.destroy.promote");
	                }
	                else if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))&&(checkperm(player,"signranks.destroy.prefix")==false)) {
		            	event.setCancelled(true);
		                msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.destroy.prefix");
	                }
	                else if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.perm.text")))&&(checkperm(player,"signranks.destroy.perm")==false)) {
		            	event.setCancelled(true);
		                msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.destroy.perm");
	                }
	                else if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.suffix.text")))&&(checkperm(player,"signranks.destroy.suffix")==false)) {
		            	event.setCancelled(true);
		                msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.destroy.suffix");
	                }
	                else if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))&&(checkperm(player,"signranks.destroy.inherit")==false)) {
		            	event.setCancelled(true);
		                msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.destroy.inherit");
	                }
	                else if (((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text")))&&(checkperm(player,"signranks.destroy.xpbank")||(player.getName().toLowerCase().contains(sign.getLine(3).toLowerCase()))))) {
	                	if (player.getName().toLowerCase().contains(sign.getLine(3).toLowerCase())) {
		            		ExperienceManager expMan = new ExperienceManager(player);
		            		expMan.changeExp(Integer.parseInt(sign.getLine(1)));
		            		String msg = "";
		            		if (sign.getLine(1)!="0") {
		            			msg = ChatColor.GRAY+": "+ChatColor.GREEN+"+"+sign.getLine(1)+" exp";
		            		}
		            		sign.setLine(1,"0");
		            		sign.update(true);
		            		msg(player,ChatColor.GRAY+getmsg("DESTROY1")+" " +getmsg("DESTROY3")+msg+ChatColor.GRAY+".");
		            	}
		            	else if (checkperm(player,"signranks.destroy.xpbank")) {
		            		if (sign.getLine(1)!="0") {
		            			msg(player,ChatColor.BLUE+sign.getLine(3)+ChatColor.GRAY+" "+getmsg("DESTROY2")+" "+ChatColor.RED+sign.getLine(1)+" exp"+ChatColor.GRAY+".");
		            		}
		            		else {
		            			msg(player,ChatColor.GRAY+getmsg("DESTROY1") +ChatColor.RED+ getmsg("DESTROY4") + ChatColor.GRAY+ getmsg("DESTROY5"));
		            		}
		            		
		            	}
		            	else {
	            		  		msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.destroy.xpbank");
		            		  	event.setCancelled(true);
		            	}        	
	                }
	                else if (((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text")))&&(checkperm(player,"signranks.destroy.shop")||(player.getName().toLowerCase().contains(sign.getLine(3).toLowerCase()))))) {
	                	String items = sign.getLine(0).replace(" - §1"+this.getConfig().getString("signs.types.shop.text"), "");
	                	if (player.getName().toLowerCase().contains(sign.getLine(3).toLowerCase())) {
		            		String msg = "";
		            		if (items!="0") {
		            			try {
		            			msg = ChatColor.GRAY+": "+ChatColor.GREEN+"+"+items+" "+sign.getLine(1);
		            			int num = Integer.parseInt(items);
		            			Object[] iteminfo = LevensteinDistance(sign.getLine(1));
		            			ItemStack itemstack = new ItemStack(((ItemStack) iteminfo[0]).getType(), num,((ItemStack) iteminfo[0]).getDurability());
		            			player.getWorld().dropItemNaturally(sign.getLocation(), itemstack);
		            			}
		            			catch (Exception e) {
		            				msg(player,"&7Error Code: "+e.getMessage()+"\n&4An error occured when trying to return your items&7 - please contact one of the staff.");
		            				event.setCancelled(true);
		            			}
		            		}
		            		sign.setLine(1,"0");
		            		sign.update(true);
		            		msg(player,ChatColor.GRAY+getmsg("DESTROY1")+msg+ChatColor.GRAY+".");
		            	}
		            	else if (checkperm(player,"signranks.destroy.shop")) {
		            		if (sign.getLine(1)!="0") {
		            			msg(player,ChatColor.BLUE+sign.getLine(3)+ChatColor.GRAY+" "+getmsg("DESTROY2")+" "+ChatColor.RED+items+" "+sign.getLine(1)+ChatColor.GRAY+".");
		            		}
		            		else {
		            			msg(player,ChatColor.GRAY+getmsg("DESTROY1") +ChatColor.RED+ getmsg("DESTROY4") + ChatColor.GRAY+ getmsg("DESTROY5"));
		            		}
		            		
		            	}
		            	else {
            		  		msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.destroy.shop");
	            		  	event.setCancelled(true);
		            	}        	
	                }
	                else {
	                	event.setCancelled(true);
	                }
	            }
	        }
	        else {
	        if(event.getBlock().getRelative(BlockFace.NORTH, 1).getTypeId() == 68) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.NORTH, 1).getState();
	            if (((this.getConfig().getString("signs.types.perm.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.perm.text"))))||((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.extend.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.extend.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.suffix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.suffix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text")))) || ((this.getConfig().getString("signs.types.shop.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text"))))) {
	            	event.setCancelled(true);
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.EAST, 1).getTypeId() == 68) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.EAST, 1).getState();
	            if (((this.getConfig().getString("signs.types.perm.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.perm.text"))))||((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.extend.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.extend.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.suffix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.suffix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text")))) || ((this.getConfig().getString("signs.types.shop.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text"))))) {
	            	event.setCancelled(true);
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.SOUTH, 1).getTypeId() == 68) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.SOUTH, 1).getState();
	            if (((this.getConfig().getString("signs.types.perm.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.perm.text"))))||((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.extend.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.extend.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.suffix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.suffix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text")))) || ((this.getConfig().getString("signs.types.shop.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text"))))) {
	            	event.setCancelled(true);
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.WEST, 1).getTypeId() == 68) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.WEST, 1).getState();
	            if (((this.getConfig().getString("signs.types.perm.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.perm.text"))))||((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.extend.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.extend.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.suffix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.suffix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text")))) || ((this.getConfig().getString("signs.types.shop.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text"))))) {
	            	event.setCancelled(true);
	            }
	        }
	        if(event.getBlock().getRelative(BlockFace.UP, 1).getTypeId() == 63) {
	            Sign sign = (Sign) event.getBlock().getRelative(BlockFace.UP, 1).getState();
	            if (((this.getConfig().getString("signs.types.perm.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.perm.text"))))||((this.getConfig().getString("signs.types.promote.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.promote.text")))) || ((this.getConfig().getString("signs.types.extend.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.extend.text")))) || ((this.getConfig().getString("signs.types.prefix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.prefix.text")))) || ((this.getConfig().getString("signs.types.suffix.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.suffix.text")))) || ((this.getConfig().getString("signs.types.inherit.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.inherit.text")))) || ((this.getConfig().getString("signs.types.xpbank.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.xpbank.text")))) || ((this.getConfig().getString("signs.types.shop.enabled").equalsIgnoreCase("true"))&&(sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text"))))) {
	            	event.setCancelled(true);
	            }
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
		String type2 = "";
		boolean hasperm = false;
		boolean error = false;
		if (((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.promote.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.promote.text"))))&&(this.getConfig().getBoolean("signs.types.promote.enabled"))) {
			type2 = this.getConfig().getString("signs.types.promote.text");
			if (checkperm(player,"signranks.create.promote")) {
	        	  if (Arrays.asList(perms.getGroups()).contains(matchgroup(line2))) {
	        		  line2 = matchgroup(line2);
	        		  event.setLine(1, line2);
	        		  sign.update(true);
	        		  hasperm = true;
	        	  }
	        	  else {
	        		  type2 = "";
	        		  msg(player,ChatColor.GRAY+getmsg("ERROR2")+ChatColor.RED+line2+ChatColor.WHITE+"\nGroups: "+Arrays.asList(perms.getGroups()));
	        	  }
			}
		}
		else if (((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.inherit.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.inherit.text"))))&&(this.getConfig().getBoolean("signs.types.inherit.enabled"))) {
			type2 = this.getConfig().getString("signs.types.inherit.text");
			if (checkperm(player,"signranks.create.inherit")) {
	        	  if (Arrays.asList(perms.getGroups()).contains(matchgroup(line2))) {
	        		  line2 = matchgroup(line2);
	        		  event.setLine(1, line2);
	        		  sign.update(true);
	        		  hasperm = true;
	        	  }
	        	  else {
	        		  type2 = "";
	        		  msg(player,ChatColor.GRAY+getmsg("ERROR2")+ChatColor.RED+line2+ChatColor.WHITE+"\nGroups: "+Arrays.asList(perms.getGroups()));
	        	  }
			}
		}
		else if (((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.perm.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.perm.text"))))&&(this.getConfig().getBoolean("signs.types.perm.enabled"))) {
			type2 = this.getConfig().getString("signs.types.perm.text");
			if (checkperm(player,"signranks.create.perm")) {
    		  hasperm = true;
			}
		}
		else if (((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.prefix.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.prefix.text"))))&&(this.getConfig().getBoolean("signs.types.prefix.enabled"))) {
			type2 = this.getConfig().getString("signs.types.prefix.text");
			if (checkperm(player,"signranks.create.prefix")) {
				hasperm = true;
			}
		}
		else if (((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.suffix.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.suffix.text"))))&&(this.getConfig().getBoolean("signs.types.suffix.enabled"))) {
			type2 = this.getConfig().getString("signs.types.suffix.text");
			if (checkperm(player,"signranks.create.suffix")) {
				hasperm = true;
			}
		}
		else if (((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.extend.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.extend.text"))))&&(this.getConfig().getBoolean("signs.types.extend.enabled"))) {
			hasperm = false;
			type2 = this.getConfig().getString("signs.types.extend.text");
			Sign mysign = null;
			try {
      			if((block.getRelative(BlockFace.UP, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("below"))) {
    				mysign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
    			}
    			if((block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("above"))) {
    				mysign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();
    			}
    			if((block.getRelative(BlockFace.SOUTH, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
    				mysign = (Sign) block.getRelative(BlockFace.SOUTH, 1).getState();
    			}
    			if((block.getRelative(BlockFace.WEST, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
    				mysign = (Sign) block.getRelative(BlockFace.WEST, 1).getState();
    			}
			if (mysign != null) {
				if (mysign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.promote.text"))) {
					if (checkperm(player,"signranks.create.promote")) { hasperm = true; }
				}
				else if (mysign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.prefix.text"))) {
					if (checkperm(player,"signranks.create.prefix")) { hasperm = true; }
				}
				else if (mysign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.perm.text"))) {
					if (checkperm(player,"signranks.create.perm")) { hasperm = true; }
				}
				else if (mysign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.suffix.text"))) {
					if (checkperm(player,"signranks.create.suffix")) { hasperm = true; }
				}
				else if (mysign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.inherit.text"))) {
					if (checkperm(player,"signranks.create.inherit")) { hasperm = true; }
				}
				else {
					hasperm = false;
					error = true;
					msg(player,colorise("&7"+getmsg("ERROR3")+" &4"+getConfig().getString("signs.types.extend.location")+"&7."));
				}
				if (checkperm(player,"signranks.create.extend")) {
					
				}
				else {
					hasperm = false;
				}
				if ((hasperm)&&(type2!="")) {
					event.setLine(0, "§1"+getConfig().getString("signs.types.extend.text"));
					if (!matchgroup(event.getLine(1)).equals("")) {
						event.setLine(1,matchgroup(event.getLine(1)));
					}
					if (!matchgroup(event.getLine(2)).equals("")) {
						event.setLine(2,matchgroup(event.getLine(2)));
					}
					if (!matchgroup(event.getLine(3)).equals("")) {
						event.setLine(3,matchgroup(event.getLine(3)));
					}
				}
				else {
					if (error) {
						// nothing yet :(
					}
					else {
					msg(player,ChatColor.GRAY+getmsg("ERROR4")+" "+ChatColor.RED+mysign.getLine(0)+ChatColor.GRAY+" "+getmsg("CREATE2")+"."); }
				}
				
			}
			else {
				hasperm = false;
				msg(player,colorise("&7"+getmsg("ERROR3")+" &4"+getConfig().getString("signs.types.extend.location")+"&7."));
			}
			}
			catch (Exception e) {
				hasperm = false;
				msg(player,colorise("&7"+getmsg("ERROR3")+" &4"+getConfig().getString("signs.types.extend.location")+"&7."));
			}
			if (error||(hasperm == false)||(type2.equals(""))) {
				event.setLine(0, "§4"+getConfig().getString("signs.types.extend.text"));
				}
		}
		else if (((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.xpbank.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.xpbank.text"))))&&(this.getConfig().getBoolean("signs.types.xpbank.enabled"))) {
			type2 = this.getConfig().getString("signs.types.xpbank.text");
			if (checkperm(player,"signranks.create.xpbank")) {
				ExperienceManager expMan = new ExperienceManager(player);
				String cost = getConfig().getString("signs.types.xpbank.cost");
			  	  if (cost.contains(" lvl")) {
			  		  cost = cost.substring(0,cost.length() - 4);
					  if (expMan.getLevelForExp(expMan.getCurrentExp()) >= Integer.parseInt(cost)) {
						  player.giveExpLevels(-Integer.parseInt(cost));
						  hasperm = true;
					  }
					  else {
						  error = true;
					  }
			  		  
			  	  }
			  	  else if (cost.contains(" exp")) {
			  		  cost = cost.substring(0,cost.length() - 4);
					  if (expMan.getCurrentExp() >= Integer.parseInt(cost)) {
						  expMan.changeExp(-Integer.parseInt(cost));
						  hasperm = true;
					  }
					  else {
						  error = true;
					  }
			  	  }
			  	  else if (cost.contains(this.getConfig().getString("economy.symbol"))) { 
			  		  cost = cost.substring(1,cost.length());
					  EconomyResponse r = econ.withdrawPlayer(player.getName(), Integer.parseInt(cost));
					  if(r.transactionSuccess()) {
						  hasperm = true;
						  
					  }
					  else {
						  error = true;
					  }
			  	  }
			  	 if ((hasperm)&&(cost!="0")) {
			  		msg(player,ChatColor.GRAY+getmsg("PAY5")+" "+ ChatColor.RED + getConfig().getString("signs.types.xpbank.cost") +ChatColor.GRAY + " for making an xpBank" );
			  	 }
			}
		}
		else if (((line1.contains("§1"+this.getConfig().getString("signs.types.shop.text"))) || (line1.equalsIgnoreCase(this.getConfig().getString("signs.types.shop.text"))))&&(this.getConfig().getBoolean("signs.types.shop.enabled"))) {
			type2 = this.getConfig().getString("signs.types.shop.text");
			if (checkperm(player,"signranks.create.shop")) {
				Object[] materialinfo = LevensteinDistance(event.getLine(1));
				if (event.getLine(1).trim().equals("")) {
					msg(player,"&7"+getmsg("ERROR14")+":&c 2&7.");
					error = true;
				}
				else if (((ItemStack) materialinfo[0]).getType().equals(Material.AIR)) {
					msg(player,"&7"+getmsg("ERROR14")+":&c 2&7.");
					error = true;
				}
				else {
					try {
						int cost;
						if (event.getLine(2).contains(" exp")) {
							cost = Integer.parseInt(event.getLine(2).substring(0,event.getLine(2).length() - 4));
						}
						else if (event.getLine(2).contains(" lvl")) {
							cost = Integer.parseInt(event.getLine(2).substring(0,event.getLine(2).length() - 4));
						}
						else {
							cost = Integer.parseInt(event.getLine(2).substring(1));
						}
						if (cost < 0) {
							error = true;
							msg(player,"&7"+getmsg("ERROR7")+": &c"+sign.getLine(2)+"&7.");
						}
						msg(player,"Worked");
						event.setLine(0, "0 - §1" + type2);
						msg(player,"1");
						msg(player,"1"+materialinfo[1]);
						msg(player,"1.1");
						if (((String) materialinfo[1]).length()>16) {
							materialinfo[1] = ((ItemStack) materialinfo[0]).getTypeId()+":"+((ItemStack) materialinfo[0]).getDurability();
						}
						msg(player,"2");
						event.setLine(1, (String) materialinfo[1]);
						msg(player,"3");
						event.setLine(3, player.getName());
						msg(player,"4");
					}
					catch (Exception e) {
						error = true;
						msg(player,"&7"+getmsg("ERROR14")+":&c 3&7.");
					}
				}
		  		
			}
			else {
				hasperm = false;
				error = true;
			}
		}
		
		
		
		
		
		if ((hasperm)&&(type2==this.getConfig().getString("signs.types.xpbank.text"))) {
			event.setLine(0, "§1" + type2);
			event.setLine(1, "0");
			event.setLine(2, "exp");
			event.setLine(3, player.getName());
		}
		else if ((hasperm)&&(type2==this.getConfig().getString("signs.types.extend.text"))) {
			msg(player,ChatColor.GRAY+getmsg("CREATE1")+" "+ChatColor.GREEN+type2+ChatColor.GRAY+" "+getmsg("CREATE2")+".");
			event.setLine(0, "§1" + type2);
		}
		else if ((type2==this.getConfig().getString("signs.types.shop.text"))) {
			if (error) {
				event.setLine(0, "§4" + type2);
				msg(player,"&c"+getmsg("ERROR34")+"&7.");
			}
			else {
				msg(player,ChatColor.GRAY+getmsg("CREATE1")+" "+ChatColor.GREEN+type2+ChatColor.GRAY+" "+getmsg("CREATE2")+".");
			}
		}
		else if ((hasperm)&&(type2!="")&&(error==false)) {
			try {
			String cost = line3;
			try {
				Integer.parseInt(line3);
				cost = this.getConfig().getString("economy.symbol")+line3;
				line3 = cost;
				event.setLine(2,cost);
			}
			catch (Exception e) {
				
			}
		  	  if (cost.contains(" exp")) {
		  		  cost = cost.substring(0,cost.length() - 4);
		  	  }
		  	  else if (cost.contains(" lvl")) {
		  		  cost = cost.substring(0,cost.length() - 4);
		  	  }
		  	  else if (cost.contains(this.getConfig().getString("economy.symbol"))) { 
		  		  cost = cost.substring(1,cost.length());
		  	  }
		  	  else {
		  		  msg(player,ChatColor.GRAY+getmsg("ERROR5")+": "+ChatColor.RED+"`"+line3+"'"+ChatColor.GRAY+". Use:"+ChatColor.RED+" ["+this.getConfig().getString("economy.symbol")+", exp, lvl]");
		  		  hasperm = false;
		  	  }
		  	  if (Integer.parseInt(cost) < 0) {
		  		  msg(player,ChatColor.RED+getmsg("ERROR6")+ChatColor.GRAY +getmsg("ERROR7")+": "+ChatColor.RED+line3);
		  	  }
		  	  try {
		  		Integer.parseInt(line4);
		  	  }
		  	  catch (Exception e) {
	        	  if (Arrays.asList(perms.getGroups()).contains(line4)) {
	        		  
	        	  }
	        	  else if (Arrays.asList(perms.getGroups()).contains(matchgroup(line4))) {
					  event.setLine(3, matchgroup(line4));
	        	  }
	        	  else {
	        		  boolean worked = false;
	        		  try {
	        			  if (timetosec(line4)!=null) {
	        				  worked = true;
	        			  }
	        		  }
	        		  catch (Exception f) {
	        			  
	        		  }
	        		  if (worked == false) {
	        			  if (event.getLine(3).contains(" exp")) {
	        			  }
	        			  else if (event.getLine(3).contains(" lvl")) {
	        			  }
	        			  else if (event.getLine(3).contains(getConfig().getString("economy.symbol"))) {
	        			  }
	        			  else {
	        				  event.setLine(3, "-1");
	        			  }
	        		  }
	        	  }
		  	  }
		  	  if (hasperm) {
			  	  msg(player,ChatColor.GRAY+getmsg("CREATE1")+" "+ChatColor.GREEN+type2+ChatColor.GRAY+" "+getmsg("CREATE2")+".");
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
			if (error) {
				msg(player,ChatColor.GRAY+getmsg("ERROR8")+" "+ChatColor.RED+getConfig().getString("signs.types.xpbank.cost")+ChatColor.GRAY+" "+getmsg("ERROR9")+".");
			}
			else {
				if (type2.equals(this.getConfig().getString("signs.types.extend.text"))) {
					
				}
				else {
					if (type2.equals(this.getConfig().getString("signs.types.xpbank.text"))) {
						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.create.xpbank");
					}
					else if (type2.equals(this.getConfig().getString("signs.types.promote.text"))) {
						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.create.promote");
					}
					else if (type2.equals(this.getConfig().getString("signs.types.inherit.text"))) {
						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.create.inherit");
					}
					else if (type2.equals(this.getConfig().getString("signs.types.perm.text"))) {
						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.create.perm");
					}
					else if (type2.equals(this.getConfig().getString("signs.types.prefix.text"))) {
						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.create.prefix");
					}
					else if (type2.equals(this.getConfig().getString("signs.types.suffix.text"))) {
						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.create.suffix");
					}
					else if (type2.equals(this.getConfig().getString("signs.types.shop.text"))) {
						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.create.shop");
					}
					else {
						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"No permission");
					}
					
				}
			}

			event.setLine(0, "§4" + type2);
		}
		else {
			
			Set<String> signs = getConfig().getConfigurationSection("signs.types.custom").getKeys(false);
    		for(String current : signs) {
    			if ((line1.equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.custom."+current+".text")))||(line1.equalsIgnoreCase(this.getConfig().getString("signs.types.custom."+current+".text")))) {
    				if (getConfig().getBoolean("signs.types.custom."+current+".enabled")) {
    					if ((checkperm(player,"signranks.create."+current))||(checkperm(player,"signranks.create.*"))) {
    						event.setLine(0,"§1"+getConfig().getString("signs.types.custom."+current+".text"));
    						msg(player,ChatColor.GRAY+"Created a new "+ChatColor.GREEN+getConfig().getString("signs.types.custom."+current+".text")+ChatColor.GRAY+" sign");
    					}
    					else {
    						event.setLine(0,"§4"+getConfig().getString("signs.types.custom."+current+".text"));
    						msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.create."+current+ChatColor.GRAY+".");	
    					}
    				}
    			}
    		}
    			
			
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
	  		int costxp = 0;
	  		int costlvl = 0;
	  		int costbal = 0;
	  		int uses = -1;
	  		int useline = -1;
	  		boolean hasperm = true;
	  		boolean error = false;
	  		String mytime = null;
	  		String reason = "";
          if (type2=="right") {
          if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.promote.text")))&&(this.getConfig().getBoolean("signs.types.promote.enabled"))) {
        	  if (checkperm(player,"signranks.use.promote")) {
        		Sign mysign = null;
	          	  String group = sign.getLine(1);
	          	  String[] mygroups = perms.getPlayerGroups(player);
	          	  if (Arrays.asList(mygroups).contains(group)) {
	          		  reason+="&7"+getmsg("ERROR10")+"&c "+sign.getLine(1)+"&7.\n";
	          		  hasperm = false;
	          	  }
          	  	ExperienceManager expMan = new ExperienceManager(player);
        		if (hasperm) {
      			if((block.getRelative(BlockFace.UP, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("above"))) {
    				mysign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
    			}
    			if((block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("below"))) {
    				mysign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();
    			}
    			if((block.getRelative(BlockFace.NORTH, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
    				mysign = (Sign) block.getRelative(BlockFace.NORTH, 1).getState();
    			}
    			if((block.getRelative(BlockFace.EAST, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
    				mysign = (Sign) block.getRelative(BlockFace.EAST, 1).getState();
    			}
    			List<String> elines;
    			if (mysign!=null) {
    			if (mysign.getLine(0).equals(colorise("&1")+getConfig().getString("signs.types.extend.text"))==false) {
    				mysign = null;
    			}
    			}
        		if (mysign!=null) {
        			elines = Arrays.asList(sign.getLine(2),sign.getLine(3),mysign.getLine(1),mysign.getLine(2),mysign.getLine(3));
        		}
        		else {
        			elines = Arrays.asList(sign.getLine(2),sign.getLine(3));
        		}
        		for(int i = 0; i < elines.size(); i++) {
        			String myline = elines.get(i);
        			try {
        			if (Arrays.asList(perms.getGroups()).contains(myline)) {
        				if (Arrays.asList(mygroups).contains(myline)) { 
        				}
        				else {
        					hasperm = false;
        					reason += "&7"+getmsg("ERROR11")+"&c "+myline+"&7.\n";
        				}
        			}
        			else if (myline.contains(" exp")) {
        				costxp = Integer.parseInt(myline.substring(0,myline.length() - 4));
        				if (expMan.getCurrentExp() >= costxp) {
        					
        				}
        				else {
        					hasperm = false;
        					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
        				}
        			}
	       			else if (myline.contains(" lvl")) {
	       				costlvl = Integer.parseInt(myline.substring(0,myline.length() - 4));
	       				if (expMan.getLevelForExp(expMan.getCurrentExp()) >= costlvl) {
	       					
	       				}
	       				else {
	       					hasperm = false;
	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
	       				}
        			}
	       			else if (myline.contains(getConfig().getString("economy.symbol"))) {
	       				costbal = Integer.parseInt(myline.substring(1,myline.length()));
	       				if (econ.getBalance(player.getName()) >= costbal) {
	       					
	       				}
	       				else {
	       					hasperm = false;
	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
	       				}
	    			}
	       			else {
	       				mytime = timetosec(myline);
	       				if (mytime!=null) {

	       				}
	       				else {
	       					try {
	       						uses = Integer.parseInt(myline);
	       						if (uses>0) {
	       							useline = i;
	       						}
	       						if (uses==0) {
	       							hasperm = false;
	       							reason += "&7"+getmsg("ERROR12")+" &c0&7 "+getmsg("ERROR13")+"\n";
	       						}
	       					}
	       					catch (Exception e) {
	       						if (myline.equals("")) {
	       							
	       						}
	       						else {
	       							String result = evaluate(myline,player,player,false);
	       							if (result.equals("true")) {
	       							
	       							}
	       							else {
			       						hasperm = false;
			       						error = true;
			       						if ((result.equalsIgnoreCase("null")==false)&&(result.equalsIgnoreCase("")==false)) {
			       							reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(result)+"&c'&7.\n";
			       						}
			       						else {
			       							reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+"&c'&7.\n";
			       						}
	       							}
	       						}
	       						// custom conditions
	       					}
	       				}
	       			}
        			}
        			catch (Exception e2) {
   						hasperm = false;
   						error = true;
   						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline+2)+"'&7.\n";
        			}
        		}
        	  }
        		if ((costxp>0)&&(costlvl>0)) {
						hasperm = false;
						error = true;
						reason += "&7"+getmsg("ERROR15")+" &cexp &7& &clvl&7.\n";
        		}
        		if ((hasperm==false)||error) {
        			msg(player,colorise(reason.trim()));
        		}
        		else {
        			if (mytime!=null) {
        				if (!getConfig().contains("scripting.tasks."+mytime)) {
    						  List<String> tasks = Arrays.asList("{setgroup:"+player.getName()+":"+perms.getPrimaryGroup(player.getWorld(),player.getName())+":"+player.getWorld().getName()+"}");
    						  getConfig().set("scripting.tasks."+mytime, tasks);
    					  }
    					  else {
    						  List<String> tasks = this.getConfig().getStringList("scripting.tasks."+mytime);
    						  tasks.add("{setgroup:"+player.getName()+":"+perms.getPrimaryGroup(player.getWorld(),player.getName())+":"+player.getWorld().getName()+"}");
    						  getConfig().set("scripting.tasks."+mytime, tasks);
    					  }
    					  saveConfig();
        			}
        			expMan.changeExp(-costxp);
        			player.giveExpLevels(-costlvl);
        			econ.withdrawPlayer(player.getName(),costbal);
        			if (useline!=-1) {
        				if (useline <2) {
      					  sign.setLine(useline+2, "" + (uses-1));
      					  sign.update(true);
        				}
        				else {
      					  mysign.setLine(useline-1, "" + (uses-1));
      					  mysign.update(true);
        				}
        			}
        			String primary = perms.getPrimaryGroup(player.getWorld(),player.getName());
        			perms.playerAddGroup(player.getWorld(),player.getName(),sign.getLine(1));
        			perms.playerRemoveGroup(player.getWorld(),player.getName(),primary);
        			perms.playerRemoveGroup(player, primary);
      				if (perms.getPrimaryGroup(player.getWorld(),player.getName()).equals(sign.getLine(1))==false||perms.playerInGroup(player, primary)) {
      					perms.playerRemoveGroup(player.getWorld(),player.getName(),perms.getPrimaryGroup(player.getWorld(),player.getName()));
        				perms.playerRemoveGroup(player.getWorld(),player.getName(),sign.getLine(1));
        				perms.playerAddGroup(player.getWorld(),player.getName(),sign.getLine(1));
      				}
          			String strcost = "";
          			if (costxp!=0) { strcost+=costxp+" exp "; }
          			if (costlvl!=0) { strcost+=costlvl+" lvl "; }
          			if (costbal!=0) { strcost+="$"+costbal+" "; }
          			msg(player,ChatColor.GRAY+getmsg("SUCCESS1")+" "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+strcost+ChatColor.GRAY+getmsg("SUCCESS2")+"!");
        		}
        	  }
        	  else {
        		  msg(player,ChatColor.GRAY+getmsg("REQ1")+": "+ ChatColor.RED+"signranks.use.promote");
        	  }
          }
          else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.inherit.text")))&&(this.getConfig().getBoolean("signs.types.inherit.enabled"))) {
        	  if (checkperm(player,"signranks.use.inherit")) {
        		  
        		  Sign mysign = null;
	          	  String group = sign.getLine(1);
	          	  String[] mygroups = perms.getPlayerGroups(player);
	          	  if (Arrays.asList(mygroups).contains(group)) {
	          		  reason+="&7"+getmsg("ERROR10")+"&c "+sign.getLine(1)+"&7.\n";
	          		  hasperm = false;
	          	  }
          	  	ExperienceManager expMan = new ExperienceManager(player);
        		if (hasperm) {
      			if((block.getRelative(BlockFace.UP, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("above"))) {
    				mysign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
    			}
    			if((block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("below"))) {
    				mysign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();
    			}
    			if((block.getRelative(BlockFace.NORTH, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
    				mysign = (Sign) block.getRelative(BlockFace.NORTH, 1).getState();
    			}
    			if((block.getRelative(BlockFace.EAST, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
    				mysign = (Sign) block.getRelative(BlockFace.EAST, 1).getState();
    			}
    			List<String> elines;
    			if (mysign!=null) {
    			if (mysign.getLine(0).equals(colorise("&1")+getConfig().getString("signs.types.extend.text"))==false) {
    				mysign = null;
    			}
    			}
        		if (mysign!=null) {
        			elines = Arrays.asList(sign.getLine(2),sign.getLine(3),mysign.getLine(1),mysign.getLine(2),mysign.getLine(3));
        		}
        		else {
        			elines = Arrays.asList(sign.getLine(2),sign.getLine(3));
        		}
        		for(int i = 0; i < elines.size(); i++) {
        			String myline = elines.get(i);
        			try {
        			if (Arrays.asList(perms.getGroups()).contains(myline)) {
        				if (Arrays.asList(mygroups).contains(myline)) {
        				}
        				else {
        					hasperm = false;
        					reason += "&7"+getmsg("ERROR11")+" &c"+myline+"&7.\n";
        				}
        			}
        			else if (myline.contains(" exp")) {
        				costxp = Integer.parseInt(myline.substring(0,myline.length() - 4));
        				if (expMan.getCurrentExp() >= costxp) {
        					
        				}
        				else {
        					hasperm = false;
        					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
        				}
        			}
	       			else if (myline.contains(" lvl")) {
	       				costlvl = Integer.parseInt(myline.substring(0,myline.length() - 4));
	       				if (expMan.getLevelForExp(expMan.getCurrentExp()) >= costlvl) {
	       					
	       				}
	       				else {
	       					hasperm = false;
	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
	       				}
        			}
	       			else if (myline.contains(getConfig().getString("economy.symbol"))) {
	       				costbal = Integer.parseInt(myline.substring(1,myline.length()));
	       				if (econ.getBalance(player.getName()) >= costbal) {
	       					
	       				}
	       				else {
	       					hasperm = false;
	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
	       				}
	    			}
	       			else {
	       				mytime = timetosec(myline);
	       				if (mytime!=null) {

	       				}
	       				else {
	       					try {
	       						uses = Integer.parseInt(myline);
	       						if (uses>0) {
	       							useline = i;
	       						}
	       						if (uses==0) {
	       							hasperm = false;
	       							reason += "&7"+getmsg("ERROR12")+" &c0&7 "+getmsg("ERROR13")+"\n";
	       						}
	       					}
	       					catch (Exception e) {
	       						if (myline.equals("")) {
	       							
	       						}
	       						else {
	       							if ((StringUtils.countMatches(myline, "{")==1)&&(StringUtils.countMatches(myline, "}")==1)) {
	       								if (evaluate(myline,player,player,false).equals("true")) {
	       									
	       								}
	       								else {
				       						hasperm = false;
				       						error = true;
				       						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+" != true'&7.\n";
	       								}
	       							}
	       							else {
			       						hasperm = false;
			       						error = true;
			       						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+"'&7.\n";
	       							}
	       						}
	       						// custom conditions
	       					}
	       				}
	       			}
        			}
        			catch (Exception e2) {
   						hasperm = false;
   						error = true;
   						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline+2)+"'&7.\n";
        			}
        		}
        	  }
        		if ((costxp>0)&&(costlvl>0)) {
						hasperm = false;
						error = true;
						reason += "&7"+getmsg("ERROR15")+" &cexp &7& &clvl&7.\n";
        		}
        		if ((hasperm==false)||error) {
        			msg(player,colorise(reason.trim()));
        		}
        		else {
       				if (mytime!=null) {
       					if (!getConfig().contains("scripting.tasks."+mytime)) {
  						  List<String> tasks = Arrays.asList("{delsub:"+player.getName()+":"+sign.getLine(1)+":"+player.getWorld().getName()+"}");
  						  getConfig().set("scripting.tasks."+mytime, tasks);
  					  }
  					  else {
  						  List<String> tasks = this.getConfig().getStringList("scripting.tasks."+mytime);
  						  tasks.add("{delsub:"+player.getName()+":"+sign.getLine(1)+":"+player.getWorld().getName()+"}");
  						  getConfig().set("scripting.tasks."+mytime, tasks);
  					  }
  					  saveConfig();
       				}
        			expMan.changeExp(-costxp);
        			player.giveExpLevels(-costlvl);
        			econ.withdrawPlayer(player.getName(),costbal);
        			if (useline!=-1) {
        				if (useline <2) {
      					  sign.setLine(useline+2, "" + (uses-1));
      					  sign.update(true);
        				}
        				else {
      					  mysign.setLine(useline-1, "" + (uses-1));
      					  mysign.update(true);
        				}
        			}
          			String strcost = "";
          			if (costxp!=0) { strcost+=costxp+" exp "; }
          			if (costlvl!=0) { strcost+=costlvl+" lvl "; }
          			if (costbal!=0) { strcost+="$"+costbal+" "; }
      				  msg(player,ChatColor.GRAY+getmsg("SUCCESS1")+" "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+strcost+ChatColor.GRAY+getmsg("SUCCESS2")+"!");
        			if (Bukkit.getPluginManager().isPluginEnabled("GroupManager")) {
  					  //manuaddsub <user> <group>
  					  getServer().dispatchCommand(getServer().getConsoleSender(), "manuaddsub "+ player.getName() +" "+sign.getLine(1));
  				  }
  				  else if (Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
  					  //pex user <user> group add <group>
  					  getServer().dispatchCommand(getServer().getConsoleSender(), "pex user "+ player.getName() +" group add "+sign.getLine(1));
  				  }
  				  else if (Bukkit.getPluginManager().isPluginEnabled("bPermissions")) {
  					  ///exec u:codename_B a:setgroup v:admin
  					  getServer().dispatchCommand(getServer().getConsoleSender(), "exec u:"+ player.getName() +" a:addgroup v:"+sign.getLine(1));
  				  }
  				  else if (Bukkit.getPluginManager().isPluginEnabled("PermissionsBukkit")) {
  					  getServer().dispatchCommand(getServer().getConsoleSender(), "permissions "+ player.getName() +" addgroup "+sign.getLine(1));
  				  }
  				  else if (Bukkit.getPluginManager().isPluginEnabled("DroxPerms")) {
  					  getServer().dispatchCommand(getServer().getConsoleSender(), "changeplayer addsub "+ player.getName() +" "+sign.getLine(1));
  				  }
  				  else if (Bukkit.getPluginManager().isPluginEnabled("zPermissions")) {
  					  ///permissions player <player> addgroup <group>
  					  getServer().dispatchCommand(getServer().getConsoleSender(), "permissions player "+ player.getName() +" addgroup "+sign.getLine(1));
  				  }
  				  else {
  					  msg(player,ChatColor.RED+getmsg("ERROR16")+"...");
  					  perms.playerAddGroup(player, sign.getLine(1));
  					  msg(player,ChatColor.RED+getmsg("ERROR17")+" (PEX, GroupManager, bPermissions, PermissionsBukkit, DroxPerms, zPermissions).");
  				  }
        		}  
          }
    	  else {
    		  msg(player,ChatColor.GRAY+getmsg("REQ1")+": "+ ChatColor.RED+"signranks.use.inherit");
    	  }
          }
          
          
          
          
          else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.perm.text")))&&(this.getConfig().getBoolean("signs.types.perm.enabled"))) {
        	  if (checkperm(player,"signranks.use.perm")) {
        		  
        		  Sign mysign = null;
	          	  String group = sign.getLine(1);
	          	  String[] mygroups = perms.getPlayerGroups(player);
	          	  if (checkperm(player,group)) {
	          		  reason+="&7"+getmsg("ERROR10")+"&c "+expandperm(sign.getLine(1))+"&7.\n";
	          		  hasperm = false;
	          	  }
          	  	ExperienceManager expMan = new ExperienceManager(player);
        		if (hasperm) {
      			if((block.getRelative(BlockFace.UP, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("above"))) {
    				mysign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
    			}
    			if((block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("below"))) {
    				mysign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();
    			}
    			if((block.getRelative(BlockFace.NORTH, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
    				mysign = (Sign) block.getRelative(BlockFace.NORTH, 1).getState();
    			}
    			if((block.getRelative(BlockFace.EAST, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
    				mysign = (Sign) block.getRelative(BlockFace.EAST, 1).getState();
    			}
    			List<String> elines;
    			if (mysign!=null) {
    			if (mysign.getLine(0).equals(colorise("&1")+getConfig().getString("signs.types.extend.text"))==false) {
    				mysign = null;
    			}
    			}
        		if (mysign!=null) {
        			elines = Arrays.asList(sign.getLine(2),sign.getLine(3),mysign.getLine(1),mysign.getLine(2),mysign.getLine(3));
        		}
        		else {
        			elines = Arrays.asList(sign.getLine(2),sign.getLine(3));
        		}
        		for(int i = 0; i < elines.size(); i++) {
        			String myline = elines.get(i);
        			try {
        			if (Arrays.asList(perms.getGroups()).contains(myline)) {
        				if (Arrays.asList(mygroups).contains(myline)) {
        				}
        				else {
        					hasperm = false;
        					reason += "&7"+getmsg("ERROR11")+" &c"+myline+"&7.\n";
        				}
        			}
        			else if (myline.contains(" exp")) {
        				costxp = Integer.parseInt(myline.substring(0,myline.length() - 4));
        				if (expMan.getCurrentExp() >= costxp) {
        					
        				}
        				else {
        					hasperm = false;
        					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
        				}
        			}
	       			else if (myline.contains(" lvl")) {
	       				costlvl = Integer.parseInt(myline.substring(0,myline.length() - 4));
	       				if (expMan.getLevelForExp(expMan.getCurrentExp()) >= costlvl) {
	       					
	       				}
	       				else {
	       					hasperm = false;
	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
	       				}
        			}
	       			else if (myline.contains(getConfig().getString("economy.symbol"))) {
	       				costbal = Integer.parseInt(myline.substring(1,myline.length()));
	       				if (econ.getBalance(player.getName()) >= costbal) {
	       					
	       				}
	       				else {
	       					hasperm = false;
	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
	       				}
	    			}
	       			else {
	       				mytime = timetosec(myline);
	       				if (mytime!=null) {

	       				}
	       				else {
	       					try {
	       						uses = Integer.parseInt(myline);
	       						if (uses>0) {
	       							useline = i;
	       						}
	       						if (uses==0) {
	       							hasperm = false;
	       							reason += "&7"+getmsg("ERROR12")+" &c0&7 "+getmsg("ERROR13")+"\n";
	       						}
	       					}
	       					catch (Exception e) {
	       						if (myline.equals("")) {
	       							
	       						}
	       						else {
	       							if ((StringUtils.countMatches(myline, "{")==1)&&(StringUtils.countMatches(myline, "}")==1)) {
	       								if (evaluate(myline,player,player,false).equals("true")) {
	       									
	       								}
	       								else {
				       						hasperm = false;
				       						error = true;
				       						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+" != true'&7.\n";
	       								}
	       							}
	       							else {
			       						hasperm = false;
			       						error = true;
			       						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+"'&7.\n";
	       							}
	       						}
	       						// custom conditions
	       					}
	       				}
	       			}
        			}
        			catch (Exception e2) {
   						hasperm = false;
   						error = true;
   						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline+2)+"'&7.\n";
        			}
        		}
        	  }
        		if ((costxp>0)&&(costlvl>0)) {
						hasperm = false;
						error = true;
						reason += "&7"+getmsg("ERROR15")+" &cexp &7& &clvl&7.\n";
        		}
        		if ((hasperm==false)||error) {
        			msg(player,colorise(reason.trim()));
        		}
        		else {
       				if (mytime!=null) {
       					if (!getConfig().contains("scripting.tasks."+mytime)) {
  						  List<String> tasks = Arrays.asList("{delperm:"+player.getName()+":"+expandperm(sign.getLine(1))+":"+player.getWorld().getName()+"}");
  						  getConfig().set("scripting.tasks."+mytime, tasks);
  						  msg(player,"{delperm:"+player.getName()+":"+expandperm(sign.getLine(1))+":"+player.getWorld().getName()+"}");
       					}
  					  else {
  						  msg(player,"{delperm:"+player.getName()+":"+expandperm(sign.getLine(1))+":"+player.getWorld().getName()+"}");
  						  List<String> tasks = this.getConfig().getStringList("scripting.tasks."+mytime);
  						  tasks.add("{delperm:"+player.getName()+":"+expandperm(sign.getLine(1))+":"+player.getWorld().getName()+"}");
  						  getConfig().set("scripting.tasks."+mytime, tasks);
  					  }
  					  saveConfig();
       				}
        			expMan.changeExp(-costxp);
        			player.giveExpLevels(-costlvl);
        			econ.withdrawPlayer(player.getName(),costbal);
        			if (useline!=-1) {
        				if (useline <2) {
      					  sign.setLine(useline+2, "" + (uses-1));
      					  sign.update(true);
        				}
        				else {
      					  mysign.setLine(useline-1, "" + (uses-1));
      					  mysign.update(true);
        				}
        			}
          			String strcost = "";
          			if (costxp!=0) { strcost+=costxp+" exp "; }
          			if (costlvl!=0) { strcost+=costlvl+" lvl "; }
          			if (costbal!=0) { strcost+="$"+costbal+" "; }
      				  msg(player,ChatColor.GRAY+getmsg("SUCCESS1")+" "+ChatColor.GREEN+expandperm(sign.getLine(1))+ChatColor.GRAY+" for "+ChatColor.GREEN+strcost+ChatColor.GRAY+getmsg("SUCCESS2")+"!");
      				  	String perm = expandperm(sign.getLine(1));
	      	        	perms.playerAdd(player, perm);
        		} 
          }
    	  else {
    		  msg(player,ChatColor.GRAY+getmsg("REQ1")+": "+ ChatColor.RED+"signranks.use.inherit");
    	  }
          }
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.prefix.text")))&&(this.getConfig().getBoolean("signs.types.prefix.enabled"))) {
        	  if (checkperm(player,"signranks.use.prefix")) {
        		  

          		Sign mysign = null;
  	          	  sign.getLine(1);
  	          	  String[] mygroups = perms.getPlayerGroups(player);
  	          	if (chat.getPlayerPrefix(player) == sign.getLine(1)) {
  	          		  reason+="&7"+getmsg("ERROR10")+"&c "+sign.getLine(1)+"&7.\n";
  	          		  hasperm = false;
  	          	  }
            	  	ExperienceManager expMan = new ExperienceManager(player);
          		if (hasperm) {
        			if((block.getRelative(BlockFace.UP, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("above"))) {
      				mysign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
      			}
      			if((block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("below"))) {
      				mysign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();
      			}
      			if((block.getRelative(BlockFace.NORTH, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
      				mysign = (Sign) block.getRelative(BlockFace.NORTH, 1).getState();
      			}
      			if((block.getRelative(BlockFace.EAST, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
      				mysign = (Sign) block.getRelative(BlockFace.EAST, 1).getState();
      			}
      			List<String> elines;
    			if (mysign!=null) {
    			if (mysign.getLine(0).equals(colorise("&1")+getConfig().getString("signs.types.extend.text"))==false) {
    				mysign = null;
    			}
    			}
          		if (mysign!=null) {
          			elines = Arrays.asList(sign.getLine(2),sign.getLine(3),mysign.getLine(1),mysign.getLine(2),mysign.getLine(3));
          		}
          		else {
          			elines = Arrays.asList(sign.getLine(2),sign.getLine(3));
          		}
          		for(int i = 0; i < elines.size(); i++) {
          			String myline = elines.get(i);
          			try {
          			if (Arrays.asList(perms.getGroups()).contains(myline)) {
          				if (Arrays.asList(mygroups).contains(myline)) {
          				}
          				else {
          					hasperm = false;
          					reason += "&7"+getmsg("ERROR11")+" &c"+myline+"&7.\n";
          				}
          			}
          			else if (myline.contains(" exp")) {
          				costxp = Integer.parseInt(myline.substring(0,myline.length() - 4));
          				if (expMan.getCurrentExp() >= costxp) {
          					
          				}
          				else {
          					hasperm = false;
          					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
          				}
          			}
  	       			else if (myline.contains(" lvl")) {
  	       				costlvl = Integer.parseInt(myline.substring(0,myline.length() - 4));
  	       				if (expMan.getLevelForExp(expMan.getCurrentExp()) >= costlvl) {
  	       					
  	       				}
  	       				else {
  	       					hasperm = false;
  	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
  	       				}
          			}
  	       			else if (myline.contains(getConfig().getString("economy.symbol"))) {
  	       				costbal = Integer.parseInt(myline.substring(1,myline.length()));
  	       				if (econ.getBalance(player.getName()) >= costbal) {
  	       					
  	       				}
  	       				else {
  	       					hasperm = false;
  	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
  	       				}
  	    			}
  	       			else {
	       				mytime = timetosec(myline);
	       				if (mytime!=null) {

	       				}
  	       				else {
  	       					try {
  	       						uses = Integer.parseInt(myline);
  	       						if (uses>0) {
  	       							useline = i;
  	       						}
  	       						if (uses==0) {
  	       							hasperm = false;
  	       							reason += "&7"+getmsg("ERROR12")+" &c0&7 "+getmsg("ERROR13")+"\n";
  	       						}
  	       					}
  	       					catch (Exception e) {
	       						if (myline.equals("")) {
	       							
	       						}
  	       						else {
	       							if ((StringUtils.countMatches(myline, "{")==1)&&(StringUtils.countMatches(myline, "}")==1)) {
	       								if (evaluate(myline,player,player,false).equals("true")) {
	       									
	       								}
	       								else {
				       						hasperm = false;
				       						error = true;
				       						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+" != true'&7.\n";
	       								}
	       							}
	       							else {
			       						hasperm = false;
			       						error = true;
			       						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+"'&7.\n";
	       							}
	       						}
  	       						// custom conditions
  	       					}
  	       				}
  	       			}
          			}
          			catch (Exception e2) {
     						hasperm = false;
     						error = true;
     						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline+2)+"'&7.\n";
          			}
          		}
          	  }
        		if ((costxp>0)&&(costlvl>0)) {
						hasperm = false;
						error = true;
						reason += "&7"+getmsg("ERROR15")+" &cexp &7& &clvl&7.\n";
        		}
          		if ((hasperm==false)||error) {
          			msg(player,colorise(reason.trim()));
          		}
          		else {
	       				if (mytime!=null) {
  	       					if (!getConfig().contains("scripting.tasks."+mytime)) {
	  						  List<String> tasks = Arrays.asList("{prefix:"+player.getName()+":"+chat.getPlayerPrefix(player.getWorld(),player.getName())+":"+player.getWorld().getName()+"}");
	  						  getConfig().set("scripting.tasks."+mytime, tasks);
	  					  }
	  					  else {
	  						  List<String> tasks = this.getConfig().getStringList("scripting.tasks."+mytime);
	  						  tasks.add("{prefix:"+player.getName()+":"+chat.getPlayerPrefix(player.getWorld(),player.getName())+":"+player.getWorld().getName()+"}");
	  						  getConfig().set("scripting.tasks."+mytime, tasks);
	  					  }
        					  saveConfig();
  	       				}
          			expMan.changeExp(-costxp);
          			player.giveExpLevels(-costlvl);
          			econ.withdrawPlayer(player.getName(),costbal);
          			if (useline!=-1) {
          				if (useline <2) {
        					  sign.setLine(useline+2, "" + (uses-1));
        					  sign.update(true);
          				}
          				else {
      					  mysign.setLine(useline-1, "" + (uses-1));
      					  mysign.update(true);
          				}
          			}
          			chat.setPlayerPrefix(player, sign.getLine(1));
          			String strcost = "";
          			if (costxp!=0) { strcost+=costxp+" exp "; }
          			if (costlvl!=0) { strcost+=costlvl+" lvl "; }
          			if (costbal!=0) { strcost+="$"+costbal+" "; }
      				  msg(player,ChatColor.GRAY+getmsg("SUCCESS1")+" "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+strcost+ChatColor.GRAY+getmsg("SUCCESS2")+"!");
          		}
          }
        	  else {
        		  msg(player,ChatColor.GRAY+getmsg("REQ1")+": "+ ChatColor.RED+"signranks.use.prefix");
        	  }
          }
          
          
          
          
          else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.suffix.text")))&&(this.getConfig().getBoolean("signs.types.suffix.enabled"))) {
        	  if (checkperm(player,"signranks.use.suffix")) {
        		  
        		  

          		Sign mysign = null;
  	          	  sign.getLine(1);
  	          	  String[] mygroups = perms.getPlayerGroups(player);
  	          	if (chat.getPlayerSuffix(player) == sign.getLine(1)) {
  	          		  reason+="&7"+getmsg("ERROR10")+"&c "+sign.getLine(1)+"&7.\n";
  	          		  hasperm = false;
  	          	  }
            	  	ExperienceManager expMan = new ExperienceManager(player);
          		if (hasperm) {
        			if((block.getRelative(BlockFace.UP, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("above"))) {
      				mysign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
      			}
      			if((block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("below"))) {
      				mysign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();
      			}
      			if((block.getRelative(BlockFace.NORTH, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
      				mysign = (Sign) block.getRelative(BlockFace.NORTH, 1).getState();
      			}
      			if((block.getRelative(BlockFace.EAST, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
      				mysign = (Sign) block.getRelative(BlockFace.EAST, 1).getState();
      			}
      			List<String> elines;
    			if (mysign!=null) {
    			if (mysign.getLine(0).equals(colorise("&1")+getConfig().getString("signs.types.extend.text"))==false) {
    				mysign = null;
    			}
    			}
          		if (mysign!=null) {
          			elines = Arrays.asList(sign.getLine(2),sign.getLine(3),mysign.getLine(1),mysign.getLine(2),mysign.getLine(3));
          		}
          		else {
          			elines = Arrays.asList(sign.getLine(2),sign.getLine(3));
          		}
          		for(int i = 0; i < elines.size(); i++) {
          			String myline = elines.get(i);
          			try {
          			if (Arrays.asList(perms.getGroups()).contains(myline)) {
          				if (Arrays.asList(mygroups).contains(myline)) {
          				}
          				else {
          					hasperm = false;
          					reason += "&7"+getmsg("ERROR11")+" &c"+myline+"&7.\n";
          				}
          			}
          			else if (myline.contains(" exp")) {
          				costxp = Integer.parseInt(myline.substring(0,myline.length() - 4));
          				if (expMan.getCurrentExp() >= costxp) {
          					
          				}
          				else {
          					hasperm = false;
          					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
          				}
          			}
  	       			else if (myline.contains(" lvl")) {
  	       				costlvl = Integer.parseInt(myline.substring(0,myline.length() - 4));
  	       				if (expMan.getLevelForExp(expMan.getCurrentExp()) >= costlvl) {
  	       					
  	       				}
  	       				else {
  	       					hasperm = false;
  	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
  	       				}
          			}
  	       			else if (myline.contains(getConfig().getString("economy.symbol"))) {
  	       				costbal = Integer.parseInt(myline.substring(1,myline.length()));
  	       				if (econ.getBalance(player.getName()) >= costbal) {
  	       					
  	       				}
  	       				else {
  	       					hasperm = false;
  	       					reason += "&7"+getmsg("ERROR8")+"&c "+myline+"&7.\n";
  	       				}
  	    			}
  	       			else {
	       				mytime = timetosec(myline);
	       				if (mytime!=null) {

	       				}
  	       				else {
  	       					try {
  	       						uses = Integer.parseInt(myline);
  	       						if (uses>0) {
  	       							useline = i;
  	       						}
  	       						if (uses==0) {
  	       							hasperm = false;
  	       							reason += "&7"+getmsg("ERROR12")+" &c0&7 "+getmsg("ERROR13")+"\n";
  	       						}
  	       					}
  	       					catch (Exception e) {
	       						if (myline.equals("")) {
	       							
	       						}
  	       						else {
	       							if ((StringUtils.countMatches(myline, "{")==1)&&(StringUtils.countMatches(myline, "}")==1)) {
	       								if (evaluate(myline,player,player,false).equals("true")) {
	       									
	       								}
	       								else {
				       						hasperm = false;
				       						error = true;
				       						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+" != true'&7.\n";
	       								}
	       							}
	       							else {
			       						hasperm = false;
			       						error = true;
			       						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline)+"'&7.\n";
	       							}
	       						}
  	       						// custom conditions
  	       					}
  	       				}
  	       			}
          			}
          			catch (Exception e2) {
     						hasperm = false;
     						error = true;
     						reason += "&7"+getmsg("ERROR14")+" "+i+":&c `"+(myline+2)+"'&7.\n";
          			}
          		}
          	  }
        		if ((costxp>0)&&(costlvl>0)) {
						hasperm = false;
						error = true;
						reason += "&7"+getmsg("ERROR15")+" &cexp &7& &clvl&7.\n";
        		}
          		if ((hasperm==false)||error) {
          			msg(player,colorise(reason.trim()));
          		}
          		else {
	       				if (mytime!=null) {
  	       					if (!getConfig().contains("scripting.tasks."+mytime)) {
    						  List<String> tasks = Arrays.asList("{suffix:"+player.getName()+":"+chat.getPlayerSuffix(player.getWorld(),player.getName())+":"+player.getWorld().getName()+"}");
    						  getConfig().set("scripting.tasks."+mytime, tasks);
    					  }
    					  else {
    						  List<String> tasks = this.getConfig().getStringList("scripting.tasks."+mytime);
    						  tasks.add("{suffix:"+player.getName()+":"+chat.getPlayerSuffix(player.getWorld(),player.getName())+":"+player.getWorld().getName()+"}");
    						  getConfig().set("scripting.tasks."+mytime, tasks);
    					  }
        					  saveConfig();
  	       				}
          			expMan.changeExp(-costxp);
          			player.giveExpLevels(-costlvl);
          			econ.withdrawPlayer(player.getName(),costbal);
          			if (useline!=-1) {
          				if (useline <2) {
        					  sign.setLine(useline+2, "" + (uses-1));
        					  sign.update(true);
          				}
          				else {
      					  mysign.setLine(useline-1, "" + (uses-1));
      					  mysign.update(true);
          				}
          			}
          			chat.setPlayerSuffix(player, sign.getLine(1));
          			String strcost = "";
          			if (costxp!=0) { strcost+=costxp+" exp "; }
          			if (costlvl!=0) { strcost+=costlvl+" lvl "; }
          			if (costbal!=0) { strcost+="$"+costbal+" "; }
      				  msg(player,ChatColor.GRAY+getmsg("SUCCESS1")+" "+ChatColor.GREEN+sign.getLine(1)+ChatColor.GRAY+" for "+ChatColor.GREEN+strcost+ChatColor.GRAY+getmsg("SUCCESS2")+"!");
          		}
          }
        	  else {
        		  msg(player,ChatColor.GRAY+getmsg("REQ1")+": "+ ChatColor.RED+"signranks.use.suffix");
        	  }
          }
          else if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text")))&&(this.getConfig().getBoolean("signs.types.shop.enabled"))) {
        	  //TODO right click shop
        	  //TODO use shop
        	  int items = Integer.parseInt(sign.getLine(0).replace(" - §1"+this.getConfig().getString("signs.types.shop.text"),""));
        	  if (player.getName().contains(sign.getLine(3))) {
        		  int num;
        		  if (items<=0) {
        			  msg(player,"&c"+getmsg("ERROR18")+"&7.");
        			  return;
        		  }
        		  else if (player.isSneaking()) {
        			  num = Math.min(64,items);
        		  }
        		  else {
        			  num = 1;
        		  }
        		  
        		  Object[] iteminfo = LevensteinDistance(sign.getLine(1));
        		  ItemStack itemstack = new ItemStack(((ItemStack) iteminfo[0]).getType(), num,((ItemStack) iteminfo[0]).getDurability());
        		  if (inventoryspace(player, itemstack)!=true) {
        			  msg(player,"&c"+getmsg("ERROR35")+"&7.");
					  return;
				  }
        		  player.getInventory().addItem(itemstack);
        		  player.updateInventory();
        		  sign.setLine(0, (items-num)+" - §1" + this.getConfig().getString("signs.types.shop.text"));
        		  sign.update();
        	  }
        	  else {
        		  if (items<=0) {
        			  msg(player,"&c"+getmsg("ERROR36")+"&7.");
        			  return;
        		  }
        		  else {
        			  ExperienceManager expMan = new ExperienceManager(player);
        			  int cost = -1;
        			  int costtype = 0;
        			  if (sign.getLine(2).contains(" exp")) {
        				  costtype = 1;
    			  		  cost = Integer.parseInt(sign.getLine(2).substring(0,sign.getLine(2).length() - 4));
    			  	  }
    			  	  else if (sign.getLine(2).contains(getConfig().getString("economy.symbol"))) {
    			  		  costtype = 2;
    			  		  cost = Integer.parseInt(sign.getLine(2).substring(1,sign.getLine(2).length()));
    			  	  }
    			  	  else if (sign.getLine(2).contains(" lvl")) {
        				  costtype = 1;
        				  int costint = Integer.parseInt(sign.getLine(2).substring(0,sign.getLine(2).length() - 4));
        				  if (player.isSneaking()) {
    						  costint *= Math.min(64,items);
    					  }
        				  
        				  if (costint > expMan.getLevelForExp(expMan.getCurrentExp())) {
        					  msg(player,"&7"+getmsg("ERROR8")+": &c"+costint+" lvl&7.");
        					  return;
        				  }
        				  else {
        					  cost = expMan.getXpForLevel(expMan.getLevelForExp(expMan.getCurrentExp()))-expMan.getXpForLevel(expMan.getLevelForExp(expMan.getCurrentExp())-costint);
        					  if (player.isSneaking()) {
        						  cost /= Math.min(64,items);
        					  }
        				  }
        			  }
    			  	  else {
    			  		msg(player,"&7"+getmsg("ERROR14")+":&c 3&7.");
    			  		  return;
    			  	  }
        			  if (player.isSneaking()) {
        				  boolean canbuy = false;
        				  cost*=Math.min(64,items);
        				  Object[] iteminfo = LevensteinDistance(sign.getLine(1));
    					  ItemStack itemstack = new ItemStack(((ItemStack) iteminfo[0]).getType(), Math.min(64,items),((ItemStack) iteminfo[0]).getDurability());
    					  if (inventoryspace(player, itemstack)!=true) {
    						  msg(player,"&c"+getmsg("ERROR35")+"&7.");
    						  return;
    					  }
    					  if (costtype==2) {
        					  EconomyResponse r = econ.withdrawPlayer(player.getName(), cost);
        					  if (r.transactionSuccess()) {
        						  econ.depositPlayer(sign.getLine(3), cost);
        						  canbuy = true;
        						  try {
        							  msg(Bukkit.getPlayer(sign.getLine(3)),"&1"+getConfig().getString("signs.types.shop.text")+" &a+"+getConfig().getString("economy.symbol")+cost+"&7 - &c"+player.getName()+"&7.");
        						  }
        						  catch (Exception e) {
        							  
        						  }
        					  }
        					  else {
        						  msg(player,"&7"+getmsg("ERROR8")+": &c$"+cost+" &7.");
        					  }
        				  }
        				  else {
        					  if (expMan.getCurrentExp()>=cost) {
        						  expMan.changeExp(-cost);
        						  canbuy = true;
           						  if (Bukkit.getPlayer(sign.getLine(3))!=null) {
        							  ExperienceManager expMan2 = new ExperienceManager(Bukkit.getPlayer(sign.getLine(3)));
        							  expMan2.changeExp(cost);
        							  try {
            							  msg(Bukkit.getPlayer(sign.getLine(3)),"&1"+getConfig().getString("signs.types.shop.text")+" &a+"+cost+" exp&7 - &c"+player.getName()+"&7.");
            						  }
            						  catch (Exception e) {
            							  
            						  }
        						  }
        						  else {
        							  String playername = sign.getLine(3);
        							  File yamlFile = new File(getDataFolder()+File.separator+"expdata.yml");
        							  YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
        							  if (yaml.contains(playername)) {
        								  yaml.set(playername, yaml.getInt(playername)+cost);
        							  }
        							  else {
        								  yaml.set(playername,cost);
        							  }
        							  try {
										yaml.save(yamlFile);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        						  //TODO give XP to offline player
        						  }
        					  }
        					  else {
        						  msg(player,"&7"+getmsg("ERROR8")+": &c"+cost+" exp&7.");
        					  }
        				  }
        				  if (canbuy) {
        						  player.getInventory().addItem(itemstack);
        						  player.updateInventory();
        						  msg(player,"&7"+getmsg("SUCCESS1")+" &a"+sign.getLine(1)+"&7 "+getmsg("SUCCESS2")+": &c-"+sign.getLine(2)+" EA&7.");
        						  sign.setLine(0, (items-Math.min(64,items))+" - §1" + this.getConfig().getString("signs.types.shop.text"));
        						  sign.update();
        				  }
        			  }
        			  else {
        				  boolean canbuy = false;
        				  Object[] iteminfo = LevensteinDistance(sign.getLine(1));
    					  ItemStack itemstack = new ItemStack(((ItemStack) iteminfo[0]).getType(), 1,((ItemStack) iteminfo[0]).getDurability());
    					  if (inventoryspace(player, itemstack)!=true) {
    						  msg(player,"&c"+getmsg("ERROR35")+"&7.");
    						  return;
    					  }
    					  if (costtype==2) {
        					  EconomyResponse r = econ.withdrawPlayer(player.getName(), cost);
        					  if (r.transactionSuccess()) {
        						  canbuy = true;
        						  if (r.transactionSuccess()) {
            						  econ.depositPlayer(sign.getLine(3), cost);
            						  canbuy = true;
            						  try {
            							  msg(Bukkit.getPlayer(sign.getLine(3)),"&1"+getConfig().getString("signs.types.shop.text")+" &a+"+getConfig().getString("economy.symbol")+cost+"&7 - &c"+player.getName()+"&7.");
            						  }
            						  catch (Exception e) {
            							  
            						  }
            					  }
            					  else {
            						  msg(player,"&7"+getmsg("ERROR8")+": &c$"+cost+" &7.");
            					  }
        					  }
        					  else {
        						  msg(player,"&7"+getmsg("ERROR8")+": &c$"+cost+" &7.");
        					  }
        				  }
        				  else {
        					  if (expMan.getCurrentExp()>=cost) {
        						  expMan.changeExp(-cost);
        						  canbuy = true;
           						  if (Bukkit.getPlayer(sign.getLine(3))!=null) {
        							  ExperienceManager expMan2 = new ExperienceManager(Bukkit.getPlayer(sign.getLine(3)));
        							  expMan2.changeExp(cost);
        							  try {
            							  msg(Bukkit.getPlayer(sign.getLine(3)),"&1"+getConfig().getString("signs.types.shop.text")+" &a+"+cost+" exp&7 - &c"+player.getName()+"&7.");
            						  }
            						  catch (Exception e) {
            							  
            						  }
        						  }
        						  else {
        							  String playername = sign.getLine(3);
        							  File yamlFile = new File(getDataFolder()+File.separator+"expdata.yml");
        							  YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
        							  if (yaml.contains(playername)) {
        								  yaml.set(playername, yaml.getInt(playername)+cost);
        							  }
        							  else {
        								  yaml.set(playername,cost);
        							  }
        							  try {
										yaml.save(yamlFile);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        						  //TODO give XP to offline player
        						  }
        					  }
        					  else {
        						  msg(player,"&7"+getmsg("ERROR8")+": &c"+cost+" exp&7.");
        					  }
        				  }
        				  if (canbuy) {
        						  player.getInventory().addItem(itemstack);
        						  sign.setLine(0, (items-1)+" - §1" + this.getConfig().getString("signs.types.shop.text"));
        						  sign.update();
        						  player.updateInventory();
        						  msg(player,"&7"+getmsg("SUCCESS1")+" &a"+sign.getLine(1)+"&7 "+getmsg("SUCCESS2")+": &c-"+sign.getLine(2)+" EA&7.");
        				  }
        			  }
        			  // buy it
        		  }
        	  }
          }
          
          
          
          
          
          else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.xpbank.text")))&&(this.getConfig().getBoolean("signs.types.xpbank.enabled"))) {
        	  if ((player.getName().toLowerCase().contains(sign.getLine(3).toLowerCase()))||(checkperm(player,"signranks.use.xpbank")==true)) {
        		  ExperienceManager expMan = new ExperienceManager(player);
        	  int amount = 0;
        	  if (player.isSneaking()) {
  			  	amount = Integer.parseInt(sign.getLine(1));  
        	  }
        	  else {
        		  // set amount to gain 1 level
        		  amount = expMan.getXpForLevel(expMan.getLevelForExp(expMan.getCurrentExp())+1)-expMan.getCurrentExp();
        	  }
			  int amount2 = Integer.parseInt(sign.getLine(1));
			  if (amount > amount2)  {
				  amount = amount2;
			  }
    		  if (amount != 0) {
			      expMan.changeExp(amount);
				  sign.setLine(1, "" + (Integer.parseInt(sign.getLine(1))-amount));
				  sign.update(true);
    		  }
    		  else {
    			  msg(player,ChatColor.RED+getmsg("ERROR18")+".");
    		  }
        	  
        	  }
        	  else {
        		  if ((checkperm(player,"signranks.use.xpbank"))) {
        			  msg(player,ChatColor.RED+this.getConfig().getString("signs.types.xpbank.text")+ChatColor.GRAY+" "+getmsg("INFO1")+" "+ChatColor.RED+sign.getLine(3));
        		  }
        		  else {
        			  msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.use.xpbank");  
        		  }
        		  
        	  
        		  }
          }
          else {           
        	  Set<String> signs = getConfig().getConfigurationSection("signs.types.custom").getKeys(false);
        		for(String current : signs) {
        			if (sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.custom."+current+".text"))) {
	        			if (getConfig().getBoolean("signs.types.custom."+current+".enabled")) {
	        				if (checkperm(player,"signranks.use."+current)) {
	        					List<String> toexecute = this.getConfig().getStringList("signs.types.custom."+current+".commands");
	        					String mycommand="";
	        					for (String m:toexecute) {
	        						mycommand+=m+";";
	        					}
	        							recursion = 0;
	        			              final Map<String, Object> placeholders = new HashMap<String, Object>();
	        			              placeholders.put("{player}", player.getName());
	        			              placeholders.put("{line1}", sign.getLine(0));
	        			              placeholders.put("{line2}", sign.getLine(1));
	        			              placeholders.put("{line3}", sign.getLine(2));
	        			              placeholders.put("{line4}", sign.getLine(3));
	        			              placeholders.put("{lines}", sign.getLine(0)+sign.getLine(1)+sign.getLine(2)+sign.getLine(3));
	        			              placeholders.put("{arg1}", sign.getLine(1));
	        			              placeholders.put("{arg2}", sign.getLine(2));
	        			              placeholders.put("{arg3}", sign.getLine(3));
	        			              for (final Entry<String, Object> node : placeholders.entrySet()) {
		        			             	 if (mycommand.contains(node.getKey())) {
		        			             		 mycommand = mycommand.replace(node.getKey(), (CharSequence) node.getValue());
		        			             	 }
		        			             }
		        						if ((getConfig().getString("signs.types.custom."+current+".elevation").equalsIgnoreCase("operator"))||(getConfig().getString("signs.types.custom."+current+".elevation").equalsIgnoreCase("op"))) {

			        						if (player.isOp()) {
			        							execute(mycommand,player,player,true);
			        						}
			        						else {
			        			        	  try
			        			        	  {
			        			        	      player.setOp(true);
			        			        	      execute(mycommand,player,player,true);
			        			        	  }
			        			        	  catch(Exception e)
			        			        	  {
			        			        	      e.printStackTrace();
			        			        	  }
			        			        	  finally
			        			        	  {
			        			        	      player.setOp(false); 
			        			        	  }
			        			        	  
			        						}
			        						
			        					}
			        						else if (getConfig().getString("signs.types.custom."+current+".elevation").equalsIgnoreCase("console")){
			        							execute(mycommand,null,null,true);
			        						}
			        						else {
			        							execute(mycommand,player,player,false);
			        						}

	        				}
        			}
        			}
        		}
        	  
          }
        }
      else {
    	  if ((sign.getLine(0).contains("§1"+this.getConfig().getString("signs.types.shop.text")))&&(this.getConfig().getBoolean("signs.types.shop.enabled"))) {
    		  //TODO left click shop
    		  //CHECK IF OWNER
    		  //CHECK IF CROUCHING
    		  if (player.getName().contains(sign.getLine(3))) {
    			  
    			  
    			  
    			  
    			  int items = Integer.parseInt(sign.getLine(0).replace(" - §1"+this.getConfig().getString("signs.types.shop.text"),""));
    			  Object[] iteminfo = LevensteinDistance(sign.getLine(1));
    			  int maxstorage = getConfig().getInt("signs.types.shop.storage");
    			  if (items>=maxstorage) {
    				  msg(player,"&c"+getmsg("ERROR8")+"&7.");
    				  return;
    			  }
    			  else if (player.isSneaking()) {
    				  ItemStack itemstack = new ItemStack(((ItemStack) iteminfo[0]).getType(), 1,((ItemStack) iteminfo[0]).getDurability());
    				  int mymin = countitem(player, itemstack);
    				  if (mymin>0) {
    					  player.getInventory().removeItem(new ItemStack(((ItemStack) iteminfo[0]).getType(), Math.min(mymin, Math.min(64, maxstorage-items)),((ItemStack) iteminfo[0]).getDurability()));
    					  player.updateInventory();
    					  sign.setLine(0, (items+Math.min(mymin, Math.min(64, maxstorage-items)))+" - §1" + this.getConfig().getString("signs.types.shop.text"));
    					  sign.update();
    				  }
    				  else {
    					  msg(player,"&7"+getmsg("ERROR8")+":&c "+sign.getLine(1)+"&7.");
    				  }
    			  }
    			  else {
    				  ItemStack itemstack = new ItemStack(((ItemStack) iteminfo[0]).getType(), 1,((ItemStack) iteminfo[0]).getDurability());
    				  if (player.getInventory().containsAtLeast(itemstack, 1)) {
    					  player.getInventory().removeItem(new ItemStack(((ItemStack) iteminfo[0]).getType(), 1,((ItemStack) iteminfo[0]).getDurability()));
    					  player.updateInventory();
    					  sign.setLine(0, (items+1)+" - §1" + this.getConfig().getString("signs.types.shop.text"));
    					  sign.update();
    				  }
    				  else {
    					  msg(player,"&d"+getmsg("GREETING"));
    				  }
    			  }
    			  
    			  
    			  
    			  
    		  }
    		  else {
    			  msg(player,"&d"+getmsg("GREETING"));
    		  }
    	  }
    	  else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.promote.text")))&&(this.getConfig().getBoolean("signs.types.promote.enabled"))) {
    		  msg(player,ChatColor.LIGHT_PURPLE+getmsg("GREETING"));
    	  }
    	  else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.inherit.text")))&&(this.getConfig().getBoolean("signs.types.inherit.enabled"))) {
    		  msg(player,ChatColor.LIGHT_PURPLE+getmsg("GREETING"));
    	  }
    	  else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.perm.text")))&&(this.getConfig().getBoolean("signs.types.perm.enabled"))) {
    		  msg(player,ChatColor.LIGHT_PURPLE+getmsg("GREETING"));
    	  }
    	  else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.prefix.text")))&&(this.getConfig().getBoolean("signs.types.prefix.enabled"))) {
			  if (chat.getPlayerPrefix(player) == sign.getLine(1)) {
				  if (this.getConfig().getString("signs.types.prefix.refund").equalsIgnoreCase("false")) {
					  msg(player,ChatColor.RED+getmsg("ERROR19")+" prefixes.");
				  }
				  else {
				  if (player.isSneaking()) {
					  Sign mysign = null;
	  	          	  sign.getLine(1);
	  	          	  perms.getPlayerGroups(player);

		            	  	ExperienceManager expMan = new ExperienceManager(player);
		          		if (hasperm) {
		        			if((block.getRelative(BlockFace.UP, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("above"))) {
		      				mysign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
		      			}
		      			if((block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("below"))) {
		      				mysign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();
		      			}
		      			if((block.getRelative(BlockFace.NORTH, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
		      				mysign = (Sign) block.getRelative(BlockFace.NORTH, 1).getState();
		      			}
		      			if((block.getRelative(BlockFace.EAST, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
		      				mysign = (Sign) block.getRelative(BlockFace.EAST, 1).getState();
		      			}
		      			List<String> elines;
		    			if (mysign!=null) {
		        			if (mysign.getLine(0).equals(colorise("&1")+getConfig().getString("signs.types.extend.text"))==false) {
		        				mysign = null;
		        			}
		        			}
		          		if (mysign!=null) {
		          			elines = Arrays.asList(sign.getLine(2),sign.getLine(3),mysign.getLine(1),mysign.getLine(2),mysign.getLine(3));
		          		}
		          		else {
		          			elines = Arrays.asList(sign.getLine(2),sign.getLine(3));
		          		}
		          		boolean canreturn = false;
		          		for(int i = 0; i < elines.size(); i++) {
		          			String myline = elines.get(i);
		          			if (myline.contains(" exp")) {
		          				costxp = Integer.parseInt(myline.substring(0,myline.length() - 4));
		          			}
		          			else if (myline.contains(" lvl")) {
		          				costlvl = Integer.parseInt(myline.substring(0,myline.length() - 4));
		          			}
		          			else if (myline.contains(getConfig().getString("economy.symbol"))) {
		  	       				costbal = Integer.parseInt(myline.substring(1,myline.length()));
		          			}
		          			else {
		          				if (myline!=null) {
		          					try {
		          						uses = Integer.parseInt(myline);
		          						useline = i;
		          						canreturn = true;
		          					}
		          					catch (Exception e) {
		          						
		          					}
		          				}
		          			}
		          		}
		          			if (useline!=-1) {
		          				if (useline <2) {
		        					  sign.setLine(useline+2, "" + (uses+1));
		        					  sign.update(true);
		          				}
		          				else {
		          					  mysign.setLine(useline-1, "" + (uses+1));
		            					  mysign.update(true);
		          				}
		          				chat.setPlayerPrefix(player, chat.getGroupPrefix(player.getWorld().getName(),perms.getPrimaryGroup(player)));
		          				if (this.getConfig().getString("signs.types.prefix.refund").equalsIgnoreCase("true")) {
			              			String strcost = "";
			              			if (costxp!=0) { strcost+=costxp+" exp "; }
			              			if (costlvl!=0) { strcost+=costlvl+" lvl "; }
			              			if (costbal!=0) { strcost+="$"+costbal+" "; }
			          				msg(player,colorise("&7"+getmsg("SUCCESS3")+" prefix: &a+"+strcost+" &7."));
		          					expMan.changeExp(costxp);
		          					player.giveExpLevels(-costlvl);
		          					econ.depositPlayer(player.getName(), costbal);
		          				}
		          				else {
		          					msg(player,colorise("&7"+getmsg("SUCCESS3")+" &aprefix&7."));
		          				}
		          			}
		          		if (canreturn == false) {
		          			msg(player,colorise("&c"+getmsg("ERROR20")+"."));
		          		}
		          		}
		          		else {
		          			msg(player,colorise("&c"+getmsg("ERROR21")+"."));
		          		}
	    		  }
	    		  else {
	    			  msg(player,ChatColor.GRAY+getmsg("ERROR22"));
	    		  }
				  }
    		  }
    		  else {
    			  msg(player,colorise("&c"+getmsg("ERROR21")+"."));
    		  }  
    	  }
    	  else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.suffix.text")))&&(this.getConfig().getBoolean("signs.types.suffix.enabled"))) {
    		  if (chat.getPlayerSuffix(player) == sign.getLine(1)) {
				  if (this.getConfig().getString("signs.types.suffix.refund").equalsIgnoreCase("false")) {
					  msg(player,ChatColor.RED+getmsg("ERROR19")+" suffixes.");
				  }
				  else {
				  if (player.isSneaking()) {
					  Sign mysign = null;
	  	          	  sign.getLine(1);
	  	          	  perms.getPlayerGroups(player);

		            	  	ExperienceManager expMan = new ExperienceManager(player);
		          		if (hasperm) {
		        			if((block.getRelative(BlockFace.UP, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("above"))) {
		      				mysign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
		      			}
		      			if((block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("below"))) {
		      				mysign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();
		      			}
		      			if((block.getRelative(BlockFace.NORTH, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
		      				mysign = (Sign) block.getRelative(BlockFace.NORTH, 1).getState();
		      			}
		      			if((block.getRelative(BlockFace.EAST, 1).getTypeId() == 68)&&(getConfig().getString("signs.types.extend.location").equalsIgnoreCase("side"))) {
		      				mysign = (Sign) block.getRelative(BlockFace.EAST, 1).getState();
		      			}
		      			List<String> elines;
		    			if (mysign!=null) {
		        			if (mysign.getLine(0).equals(colorise("&1")+getConfig().getString("signs.types.extend.text"))==false) {
		        				mysign = null;
		        			}
		        			}
		          		if (mysign!=null) {
		          			elines = Arrays.asList(sign.getLine(2),sign.getLine(3),mysign.getLine(1),mysign.getLine(2),mysign.getLine(3));
		          		}
		          		else {
		          			elines = Arrays.asList(sign.getLine(2),sign.getLine(3));
		          		}
		          		boolean canreturn = false;
		          		for(int i = 0; i < elines.size(); i++) {
		          			String myline = elines.get(i);
		          			if (myline.contains(" exp")) {
		          				costxp = Integer.parseInt(myline.substring(0,myline.length() - 4));
		          			} 
		          			else if (myline.contains(" lvl")) {
		          				costlvl = Integer.parseInt(myline.substring(0,myline.length() - 4));
		          			}
		          			else if (myline.contains(getConfig().getString("economy.symbol"))) {
		  	       				costbal = Integer.parseInt(myline.substring(1,myline.length()));
		          			}
		          			else {
		          				if (myline!=null) {
		          					try {
		          						uses = Integer.parseInt(myline);
		          						useline = i;		          						
		          						canreturn = true;
		          					}
		          					catch (Exception e) {
		          						
		          					}
		          				}
		          			}
		          		}
		          			if (useline!=-1) {
		          				if (useline <2) {
		        					  sign.setLine(useline+2, "" + (uses+1));
		        					  sign.update(true);
		          				}
		          				else {
		          					  mysign.setLine(useline-1, "" + (uses+1));
		            					  mysign.update(true);
		          				}
		          				chat.setPlayerSuffix(player, chat.getGroupSuffix(player.getWorld().getName(),perms.getPrimaryGroup(player)));
		          				if (this.getConfig().getString("signs.types.suffix.refund").equalsIgnoreCase("true")) {
			              			String strcost = "";
			              			if (costxp!=0) { strcost+=costxp+" exp "; }
			              			if (costlvl!=0) { strcost+=costlvl+" lvl "; }
			              			if (costbal!=0) { strcost+="$"+costbal+" "; }
			              			msg(player,colorise("&7"+getmsg("SUCCESS3")+" suffix: &a+"+strcost+" &7."));
		          					expMan.changeExp(costxp);
		          					for(int i = 1; i < costlvl; i++) {
		        						expMan.changeExp(expMan.getXpNeededToLevelUp(expMan.getLevelForExp(expMan.getCurrentExp())));
		        					}
		          					econ.depositPlayer(player.getName(), costbal);
		          				}
		          				else {
		          					msg(player,colorise("&7"+getmsg("SUCCESS3")+" &asuffix&7."));
		          				}
		          			}
		          		if (canreturn == false) {
		          			msg(player,colorise("&c"+getmsg("ERROR20")+"."));
		          		}
		          		}
		          		else {
		          			msg(player,colorise("&c"+getmsg("ERROR21")+"."));
		          		}
	    		  }
	    		  else {
	    			  msg(player,ChatColor.GRAY+getmsg("ERROR22"));
	    		  }
				  }
    		  }
    		  else {
    			  msg(player,colorise("&c"+getmsg("ERROR21")+"."));
    		  }  
    	  }
    	  
    	  else if ((sign.getLine(0).equalsIgnoreCase("§1"+this.getConfig().getString("signs.types.xpbank.text")))&&(this.getConfig().getBoolean("signs.types.xpbank.enabled"))) {
    		  if ((player.getName().toLowerCase().contains(sign.getLine(3).toLowerCase()))&&(checkperm(player,"signranks.use.xpbank"))) {
    			  ExperienceManager expMan = new ExperienceManager(player);
    		  int amount = 0;
    		  int mylevel = expMan.getLevelForExp(expMan.getCurrentExp());
    		  if (player.isSneaking()) {
    			  
    			  	amount = expMan.getCurrentExp();  
    		  }
    		  else {
    			  if ((mylevel==0)) {
    			  }
    			  else if (player.getExp() != 0){
            		  amount = expMan.getCurrentExp()-expMan.getXpForLevel(expMan.getLevelForExp(expMan.getCurrentExp()));
            		  if (amount == 0) {
        				  amount = expMan.getCurrentExp()-expMan.getXpForLevel(expMan.getLevelForExp(expMan.getCurrentExp())-1);
            		  }
    			  }
    			  else {
    				  amount = expMan.getCurrentExp()-expMan.getXpForLevel(expMan.getLevelForExp(expMan.getCurrentExp())-1);
    			  }
    		  }
			  int amount2 = Integer.parseInt(sign.getLine(1));
			  int storage;
			  try {
				  storage = Integer.parseInt(getConfig().getString("signs.types.xpbank.storage."+perms.getPrimaryGroup(player)));
			  }
			  catch (Exception e) {
				  storage = Integer.parseInt(getConfig().getString("signs.types.xpbank.storage.Default"));
			  }
			  if (storage < (amount2 + amount))  {
				  amount = storage-amount2;
				  msg(player,"changing");
			  }
    		  if (amount != 0) {
			      expMan.changeExp(-amount);
				  sign.setLine(1, "" + (Integer.parseInt(sign.getLine(1))+amount));
				  sign.update(true);
    		  }
    		  else {
    			  msg(player,ChatColor.RED+getmsg("ERROR23")+".");
    		  }
    		  
    		
    	  }
        	  else {
        		  if ((checkperm(player,"signranks.use.xpbank"))) {
        			  msg(player,ChatColor.RED+this.getConfig().getString("signs.types.xpbank.text")+ChatColor.GRAY+" "+getmsg("INFO1")+" "+ChatColor.RED+sign.getLine(3));
        		  }
        		  else {
        			  msg(player,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.use.xpbank");    
        		  }
        		  
        	  
        		  }
      }
    	  else {
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
    	Player user = null;
		try { user = (Player) sender; }catch (Exception e) {}
    	String line = "";
    	for (String i:args) {
    		line+=i+" ";
    	}
    	if(cmd.getName().equalsIgnoreCase("xpp")){
    		if (checkperm(user,"signranks.xpp")) {
    		if (args.length != 2){
    			msg(user,ChatColor.GRAY+getmsg("ERROR24")+".");
    		}
    		else {
    			if (sender.getName().equalsIgnoreCase(args[0])!= true) {
    				List<Player> matches = getServer().matchPlayer(args[0]);
    				if (matches.isEmpty()) {
    					msg(user,ChatColor.GRAY+getmsg("ERROR25")+": "+ChatColor.RED+args[0]);
    				}
    				else if (matches.size() > 1) {
    					msg(user,ChatColor.GRAY+getmsg("ERROR26")+": "+ChatColor.RED + matches);
    				}
    				else {
    					Player player = matches.get(0);
    					if ((player.getWorld() == (user).getWorld()) || (this.getConfig().getString("cross_map_trade").equalsIgnoreCase("true"))) {
          				  try {
    						if ((user).getTotalExperience() >= Integer.parseInt(args[1])) {
    							if (Integer.parseInt(args[1])>0) {
    							      int myxp = (user).getTotalExperience();
    							 
    							      (user).setTotalExperience(0);
    							      (user).setLevel(0);
    							      (user).setExp(0);
    							      (user).giveExp(myxp - Integer.parseInt(args[1]));
    							      msg(user,ChatColor.GRAY+getmsg("SUCCESS4")+" "+ChatColor.RED+(myxp-Integer.parseInt(args[1]))+ChatColor.GRAY+" XP");
    							      msg(player,ChatColor.GRAY+getmsg("SUCCESS5")+" "+ChatColor.GREEN+(Integer.parseInt(args[1]))+ChatColor.GRAY+" XP - "+ChatColor.BLUE+sender.getName());
    								
    								
    							player.giveExp(Integer.parseInt(args[1]));
    							}
    							else {
    								msg(user,""+Integer.parseInt(args[1]));
    								msg(user,ChatColor.GRAY+getmsg("ERROR28")+": "+ChatColor.RED+args[1]+ChatColor.GRAY+".");
    							}
          				  }
    						else {
    							msg(user,getmsg("ERROR8")+" "+ChatColor.RED+Integer.parseInt(args[1])+" exp"+ChatColor.GRAY+".");
    						}
          				  }
          				  catch (Exception e) {
          					msg(user,ChatColor.GRAY+getmsg("ERROR5")+": "+ChatColor.RED + args[1]+ChatColor.GRAY+".");
          				  }
    					}
    					else {
    						msg(user,ChatColor.RED+getmsg("ERROR29")+".");
    					}
    					
    				}
    			}
    			else {
    				msg(user,ChatColor.RED+getmsg("ERROR30")+".");
    			}
    			//Check if player exists
    			//Check if they are in the same map
    			//Check you have enough XP
    			//Transfer XP
    		}
    		}
    		else {
    			msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.xpp");
    		}
    	}
    	else if ((cmd.getName().equalsIgnoreCase("signranks"))||(cmd.getName().equalsIgnoreCase("sr"))) {
    	if (args.length > 0) {
    		if ((args[0].equalsIgnoreCase("reload"))){
    			
    			//TODO RELOAD SCRIPTS FOLDER AND SIGNS FOLDER
    			
    			boolean hasperm = false;
    			if (sender instanceof Player==false) {
    				hasperm = true;
    			}
    			else if (checkperm(user,"signranks.reload")) {
    				hasperm = true;
    			}
    			
    			if (hasperm) { 
    		    	getConfig().getConfigurationSection("signs.types").set("custom", null);
    		    	getConfig().getConfigurationSection("scripting").set("custom-placeholders", null);
    		        File f1 = new File(getDataFolder() + File.separator + "scripts");
    		        File[] mysigns = f1.listFiles();
    		        for (int i = 0; i < mysigns.length; i++) {
    		        	if (mysigns[i].isFile()) {
    		        		if (mysigns[i].getName().contains(".yml")) {
    			        		FileConfiguration current = YamlConfiguration.loadConfiguration(mysigns[i]);
    			        		Set<String> values = current.getConfigurationSection("").getKeys(false);
    							for(String myval:values) {
    			        			getConfig().set("scripting.placeholders."+mysigns[i].getName().substring(0,mysigns[i].getName().length()-4), current.get(myval));
    			        		}
    		        		}
    		        	}
    		        }
    		        
    		        
    		        
    		        File f2 = new File(getDataFolder() + File.separator + "signs");
    		        File[] myscripts = f2.listFiles();
    		        for (int i = 0; i < myscripts.length; i++) {
    		        	if (myscripts[i].isFile()) {
    		        		if (myscripts[i].getName().contains(".yml")) {
    			        		FileConfiguration current = YamlConfiguration.loadConfiguration(myscripts[i]);
    			        		Set<String> values = current.getConfigurationSection("").getKeys(false);
    							for(String myval:values) {
    			        			getConfig().set("signs.types.custom."+myscripts[i].getName().replace(".","")+"."+myval, current.get(myval));
    			        		}
    		        		}
    		        	}
    		        }
    			try {
	    			Set<String> vars = getConfig().getConfigurationSection("scripting.variables").getKeys(false);
	    			for(String current : vars) {
	    				globals.put("{"+current+"}", this.getConfig().getString("scripting.variables."+current));
	    			}
    			}
    			catch (Exception e) {
    				
    			}
    			counter = 0;
    			this.reloadConfig();
    			this.saveDefaultConfig();
				msg(user,ChatColor.LIGHT_PURPLE+getmsg("SUCCESS6")+".");
    			}
    			else {
    				msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.reload"+ChatColor.GRAY+".");
    			}
    		}
    		else if ((args[0].equalsIgnoreCase("save"))){
    			boolean hasperm = false;
    			if (checkperm(user,"signranks.save")) {
    				hasperm = true;
    			}
    			if (hasperm) {
    				getConfig().getConfigurationSection("scripting").set("variables", null);
    				counter2 = 0;
    				System.out.println("[SignRanksPlus] Saving variables...");
    		        for (final Entry<String, Object> node : globals.entrySet()) {
    		        	getConfig().options().copyDefaults(true);
    		        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
    		        	
    		        	this.saveConfig();
    		        	this.reloadConfig();
    		        }
    		        System.out.println("DONE!");
    			this.saveConfig();
    			this.reloadConfig();
				msg(user,ChatColor.LIGHT_PURPLE+getmsg("DONE"));
    			}
    			else {
    				msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.save"+ChatColor.GRAY+".");
    			}
    		}
    		else if ((args[0].equalsIgnoreCase("help"))){
    			msg(user,"/sr <save|reload|pay|help>");
    		}
		else if ((args[0].equalsIgnoreCase("pay"))) {
			if (checkperm(user,"signranks.pay")==true) {
				msg(user,pay());
			}
			else {
				msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.save"+ChatColor.GRAY+".");
			}
		}
    	}
    	else {
    		// signranks help
    		msg(user,"/sr <save|reload|pay|help>");
    	}
		}
    	else if (cmd.getName().equalsIgnoreCase("all")) {
    		if (checkperm(user,"signranks.all")) {
    			if (args.length > 0) {
    				if (user==null) {
    					execute(line,null,null,true);
    				}
    				for(Player user2:getServer().getOnlinePlayers()){
    					recursion = 0;
    					if (checkperm(user,"signranks.all.elevate")) {
    						execute(line,user2,user,true);
    					}
    					else {
    						execute(line,user2,user,false);
    					}
    	            if (getConfig().getInt("scripting.debug-level") > 2) {
    	            	if (user2.getName()!=sender.getName()) {
    	            		msg(user,ChatColor.RED+getmsg("SUCCESS7")+" "+user2.getName()+": "+ChatColor.WHITE+colorise(evaluate(line, user2,user,false)));
    	            	}
    	            }
					}
//    				}
    			
    			}
    			else {
    				msg(user,ChatColor.RED+"/all <text>");
    			}
    		}
    		else {
    			msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.all"+ChatColor.GRAY+".");
    		}
    	}
    	else if ((cmd.getName().equalsIgnoreCase("se"))||(cmd.getName().equalsIgnoreCase("signedit"))||(cmd.getName().equalsIgnoreCase("sedit"))) {
    		if (checkperm(user,"signranks.signedit")) {
		    		try {
		    			Block block = (getSelectedBlock(user).getBlock());
		    			Sign sign = (Sign) block.getState();
		    			try {
		    				boolean hasperm = checkperm(user,"signranks.signedit.override");
		    				if (hasperm == false) {
		    					BlockBreakEvent mybreak = new BlockBreakEvent(block, user);
		    					Bukkit.getServer().getPluginManager().callEvent(mybreak);
		    					if (mybreak.isCancelled()) {
		    						hasperm = false;
		    					}
		    					else {
		    						hasperm = true;
		    					}
		    					BlockPlaceEvent place = new BlockPlaceEvent(block, block.getState(), block, null, user, true);
		    					Bukkit.getServer().getPluginManager().callEvent(place);
		    					if (place.isCancelled()) {
		    						hasperm = false;
		    					}
		    				}
		    				else {
		    				}
		    				if (hasperm) {
			    				line = "";
			    				for(int i = 1; i < args.length; i++) {
			    					line+=args[i]+" ";
			    				}
				    			sign.setLine(Integer.parseInt(args[0])-1, colorise(line).trim());
				    			sign.update(true);
		    				}
		    				else {
		    					msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.signedit.override"+ChatColor.GRAY+".");
		    				}
		    			}
		    			catch (Exception e) {
		    				msg(user,ChatColor.GRAY+getmsg("ERROR33"));
		    			}
		    		}
		    		catch (Exception e) {
		    			msg(user,ChatColor.RED+getmsg("ERROR32")+ChatColor.GRAY+".");
		    		}
		    		}
    		else {
    			msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.signedit"+ChatColor.GRAY+".");
    		}
    	}
//    	Sign sign = (Sign) (loc.getBlock().getState());
    	else if (cmd.getName().equalsIgnoreCase("if")) {
    		recursion = 0;
            line = evaluate(line,user,user,false);
            msg(user,"if "+line+" -> "+testif(line));
    		return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("schedule")) {
    		boolean hasperm = false;
    		if (user instanceof Player) {
    			if (checkperm(user,"signranks.schedule")) {
    				hasperm = true;
    				msg(user,colorise("&7"+getmsg("SUCCESS8")+": &a"+line+"&7."));
    			}
    		}
    		else {
    			System.out.println(getmsg("SUCCESS8")+": "+line);
    			hasperm = true;
    		}
    		if (hasperm) {
				  String mytime = timetosec(args[0]);
				  if (mytime!=null) {
	    				line = "";
	    				for(int i = 1; i < args.length; i++) {
	    					line+=args[i]+" ";
	    				}
					  if (!getConfig().contains("scripting.tasks."+mytime)) {
							  List<String> tasks = Arrays.asList(line);
							  getConfig().set("scripting.tasks."+mytime, tasks);
					  }
					  else {
						  List<String> tasks = this.getConfig().getStringList("scripting.tasks."+mytime);
						  tasks.add(line);
						  getConfig().set("scripting.tasks."+mytime, tasks);
					  }
					  saveConfig();
				  }
    		}
    		else {
    			msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.schedule"+ChatColor.GRAY+".");
    		}
    		return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("endif")) {
    		return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("else")) {
    		return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("eval")) {
    		recursion = 0;
    		if (getConfig().getInt("scripting.debug-level") > 0) {
    			msg(user,"eval "+line+" -> "+evaluate(line,user,user,false));
    		}
    		return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("execute")) {
    		recursion = 0;
    		boolean hasperm = false;
    		if (checkperm(user,"signranks.execute")) {
    			hasperm = true;
    		}
    		if (user instanceof Player==false) {
    			execute(line,null,null,true);
    		}
    		else {
    			if (hasperm) {
    				execute(line,user,user,true);
    			}
    			else {
    				msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.execute"+ChatColor.GRAY+".");
    			}
    		}
    		return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("gvar")) {
    		recursion = 0;
    			if (args.length>0) {
    				if (checkperm(user,"signranks.variable")) {
    			 if (args.length>1) {
	            	  try {
	            	  globals.put("{"+evaluate(args[0],user,user,false)+"}", evaluate(StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," "),user,user,false));
	            	  
	              		if (getConfig().getInt("scripting.debug-level") > 1) {
	              			msg(user,"global var "+args[0]+" -> "+args[1]);
	              			if (getConfig().getInt("scripting.debug-level") > 2) { msg(user,""+globals); }
	            		}
	            	  }
	            	  catch (Exception e) {
		              		if (getConfig().getInt("scripting.debug-level") > 0) {
		              			msg(user,line+" -> invalid syntax");
		            		}
	            	  }
	            	  }
	            	  else {
	            		  try {
	            		  globals.remove("{"+args[0]+"}");
	            		  if (getConfig().getInt("scripting.debug-level") > 1) {
	            			  msg(user,"removed global "+args[0]);
		            		}
	            		  }
	            		  catch (Exception e2) {
		              		if (getConfig().getInt("scripting.debug-level") > 0) {
		              			msg(user,"failed to remove global "+args[0]);
		            		}
	            		  }
	            	  }
    				}
    				else {
    					
    				}
    		}
    			else {
    				if (getConfig().getInt("scripting.debug-level") > 0) { msg(user,"globals: "+globals); }
    			}
    		return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("player")) {
    		if (checkperm(user,"signranks.player")) {
    			recursion = 0;
    			if (args.length > 1) {
    				line = "";
    				for(int i = 1; i < args.length; i++) {
    					line+=args[i]+" ";
    				}
    				if ((Bukkit.getPlayer(args[0])!=null||args[0].equalsIgnoreCase("console")||args[0].equals("null"))) {
    					Player user2 = Bukkit.getPlayer(args[0]);
    		            line = "";
    		            for(int i = 1; i < args.length; i++) {
    		            	line+=args[i]+" ";
    		            }
    		            if (checkperm(user,"signranks.player.elevate")) {
    		            	execute(line,user2,user,true);
    		            }
    		            else {
    		            	execute(line,user2,user,false);
    		            }
    					
    				}
    				else {
    					msg(user,ChatColor.GRAY+getmsg("ERROR25")+": "+ChatColor.RED+args[0]+ChatColor.GRAY+".");
    				}
    			}
    			else {
    				msg(user,ChatColor.RED+getmsg("ERROR31"));
    			}
    		}
    		else {
    			msg(user,ChatColor.GRAY+getmsg("REQ1")+" "+ChatColor.RED+"signranks.player"+ChatColor.GRAY+".");
    		}
    	}
		return true;
    }
    public void msg(Player player,String mystring) {
    	if (player==null) {
    		getServer().getConsoleSender().sendMessage(colorise(mystring));
    	}
    	else if (player instanceof Player==false) {
    		getServer().getConsoleSender().sendMessage(colorise(mystring));
    	}
    	else {
    		player.sendMessage(colorise(mystring));
    	}

    }
    
}