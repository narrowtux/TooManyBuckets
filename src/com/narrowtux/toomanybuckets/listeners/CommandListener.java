package com.narrowtux.toomanybuckets.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.narrowtux.toomanybuckets.TMBMain;

public class CommandListener {
	private TMBMain plugin;
	
	public CommandListener(TMBMain tmbMain) {
		plugin = tmbMain;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]){
		if(!(sender instanceof SpoutPlayer)){
			sender.sendMessage("You need to be player!");
			return true;
		}
		SpoutPlayer player = (SpoutPlayer)sender;
		if(player.getVersion()<18){
			player.sendMessage("You need to have the latest Spoutcraft v0.1 at least!");
			return true;
		}
		if(cmd.getName().equals("toomanybuckets")){
			player.closeActiveWindow();
			plugin.openOverlay(player);
			return true;
		}
		return false;
	}
}
