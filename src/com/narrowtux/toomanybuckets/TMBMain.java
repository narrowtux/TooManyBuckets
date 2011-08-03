package com.narrowtux.toomanybuckets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.narrowtux.toomanybuckets.gui.TMBMainScreen;
import com.narrowtux.toomanybuckets.listeners.CommandListener;
import com.narrowtux.toomanybuckets.listeners.TMBSpoutContribListener;
import com.narrowtux.toomanybuckets.listeners.TMBScreenListener;

public class TMBMain extends JavaPlugin{

	private Logger log;
	private CommandListener cmdListener = new CommandListener(this);
	private TMBScreenListener screenListener = new TMBScreenListener(this);
	private TMBSpoutContribListener spoutListener = new TMBSpoutContribListener();
	private Map<SpoutPlayer, TMBMainScreen> screens = new HashMap<SpoutPlayer, TMBMainScreen>();

	
	@Override
	public void onDisable() {
		sendDescription("disabled");
	}

	@Override
	public void onEnable() {
		log = Logger.getLogger("Minecraft");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.CUSTOM_EVENT, screenListener, Priority.Normal, this);
		pm.registerEvent(Type.CUSTOM_EVENT, spoutListener, Priority.Normal, this);
		sendDescription("enabled");
	}
	
	public void sendDescription(String startup){
		PluginDescriptionFile pdf = getDescription();
		String authors = "";
		for(String name: pdf.getAuthors()){
			if(authors.length()>0){
				authors+=", ";
			}
			authors+=name;
		}
		log.log(Level.INFO, "["+pdf.getName()+"] v"+pdf.getVersion()+" by ["+authors+"] "+startup+".");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]){
		return cmdListener.onCommand(sender, cmd, label, args);
	}
	
	public void openOverlay(SpoutPlayer player){
		TMBMainScreen screen = null;
		if(!screens.containsKey(player)){
			screens.put(player, new TMBMainScreen(player));
		}
		screen = screens.get(player);
		screen.open();
	}
	
	public static List<ItemStack> getSearchResult(String query){
		List<ItemStack> result = new ArrayList<ItemStack>();
		for(Material mat:Material.values()){
			if(mat.toString().contains(query.toUpperCase())){
				result.add(new ItemStack(mat));
			}
		}
		return result;
	}
}
