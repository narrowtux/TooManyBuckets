package com.narrowtux.toomanybuckets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.yaml.snakeyaml.Yaml;

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
	private static TMBMain instance = null;
	private List<ItemInfo> infos = new ArrayList<ItemInfo>();
	private List<ItemInfo> defaultView = new ArrayList<ItemInfo>();
	private Configuration config;
	
	@Override
	public void onDisable() {
		sendDescription("disabled");
	}

	@Override
	public void onEnable() {
		instance = this;
		log = Logger.getLogger("Minecraft");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.CUSTOM_EVENT, screenListener, Priority.Normal, this);
		pm.registerEvent(Type.CUSTOM_EVENT, spoutListener, Priority.Normal, this);
		sendDescription("enabled");
		createDataFolder();
		config = new Configuration();
		load();
	}
	
	private void createDataFolder() {
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}
	}

	@SuppressWarnings("unchecked")
	private void load() {
			Yaml yaml = new Yaml();
			File infoFile = new File(getDataFolder(), "items.yml");
			if(infoFile.exists()){
				try {
					FileReader reader = new FileReader(infoFile);
					HashMap<String, Object> data = (HashMap<String, Object>) yaml.load(reader);
					for(String name:data.keySet()){
						HashMap<String, Object> itemData = (HashMap<String, Object>) data.get(name);
						ItemInfo info = new ItemInfo();
						info.name = name;
						ItemStack stack = new ItemStack(1);
						try{
							stack.setTypeId((Integer) itemData.get("type"));
						} catch (ClassCastException e){
							String t = (String)itemData.get("type");
							Material type = Material.getMaterial(t.toUpperCase());
							if(type!=null)
							{
								stack.setType(type);
							} else {
								doLog("'"+t+"' was not found as an bukkit type name. Use the type-id or verify the name.");
								continue;
							}
						}
						stack.setAmount((Integer) itemData.get("amount"));
						if(itemData.containsKey("data")){
							stack.setDurability((short)(int)(Integer) itemData.get("data"));
						}
						info.stack = stack;
						if(itemData.containsKey("indefaultview"))
							info.inDefaultView = (Boolean) itemData.get("indefaultview");
						if(info.inDefaultView){
							defaultView.add(info);
						}
						if(config.isSetCustomNames()){
							SpoutManager.getItemManager().setItemName(stack.getType(), stack.getDurability(), name);
						}
						infos.add(info);
					}
					reader.close();
				} catch (FileNotFoundException e) {} 
				catch (IOException e) {}
			} else {
				doLog("You need the items.yml file to use TooManyBuckets.");
			}
	}

	private void doLog(String string) {
		log.log(Level.INFO,"[TooManyBuckets] "+string);
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
		if(player.hasPermission("toomanybuckets.use")){
			TMBMainScreen screen = null;
			if(!screens.containsKey(player)){
				screens.put(player, new TMBMainScreen(player));
			}
			screen = screens.get(player);
			screen.open();
		} else {
			player.sendMessage("You may not use TooManyBuckets");
		}
	}
	
	public static List<ItemInfo> getSearchResult(String query){
		if(query.trim().equals("")){
			return getInstance().defaultView;
		}
		query = query.toLowerCase();
		List<ItemInfo> result = new ArrayList<ItemInfo>();
		for(ItemInfo mat :getInstance().infos){
			if(mat.name.toLowerCase().contains(query)){
				result.add(mat);
			}
		}
		return result;
	}
	
	public void removeScreen(TMBMainScreen screen)
	{
		screens.remove(screen);
	}
	
	public static TMBMain getInstance(){
		return instance;
	}
	
	public List<ItemInfo> getDefaultView(){
		return Collections.unmodifiableList(defaultView);
	}
}
