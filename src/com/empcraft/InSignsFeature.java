package com.empcraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.blablubbabc.insigns.Changer;
import de.blablubbabc.insigns.InSigns;
import de.blablubbabc.insigns.SignSendEvent;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;


public class InSignsFeature  implements Listener {	
	Plugin insignsPlugin;
	SignRanks plugin;
		
	@EventHandler
	public void SignSendEvent(SignSendEvent event) {
		Player player = event.getPlayer();
		Location loc = event.getLocation();
		String l1 = event.getLine(0);
		String l2 = event.getLine(1);
		String l3 = event.getLine(2);
		String l4 = event.getLine(3);
		boolean modified = false;
		if (l1.equals("")==false) {
			String result = plugin.evaluate(l1,player,player, false);
			if (result.equals(l1)==false) {
				event.setLine(0,plugin.colorise(result));
				modified = true;
			}
		}
		if (l2.equals("")==false) {
			String result = plugin.evaluate(l2,player,player, false);
			if (result.equals(l2)==false) {
				event.setLine(1,plugin.colorise(result));
				modified = true;
			}
		}
		if (l3.equals("")==false) {
			String result = plugin.evaluate(l3,player,player, false);
			if (result.equals(l3)==false) {
				event.setLine(2,plugin.colorise(result));
				modified = true;
			}
		}
		if (l4.equals("")==false) {
			String result = plugin.evaluate(l4,player,player, false);
			if (result.equals(l4)==false) {
				event.setLine(3,plugin.colorise(result));
				modified = true;
			}
		}
		if (modified==true) {
			plugin.isadd(player, loc);
		}
	}
	
	public void sendSignChange(Player player, Sign sign) {
		InSigns insigns = (InSigns) insignsPlugin;
		insigns.sendSignChange(player, sign);
	}
	
    public InSignsFeature(Plugin p2,SignRanks p3) {
    	insignsPlugin = p2;
    	plugin = p3;
    	
    }
}