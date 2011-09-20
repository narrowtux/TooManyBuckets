/*
 * Copyright (C) 2011 Moritz Schmale <narrow.m@gmail.com>
 *
 * TooManyBuckets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.narrowtux.toomanybuckets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.narrowtux.narrowtuxlib.utils.FileUtils;
import com.narrowtux.toomanybuckets.gui.TMBMainScreen;
import com.narrowtux.toomanybuckets.listeners.CommandListener;
import com.narrowtux.toomanybuckets.listeners.TMBPlayerListener;
import com.narrowtux.toomanybuckets.listeners.TMBScreenListener;

public class TMB extends JavaPlugin{
	private Logger log;
	private CommandListener cmdListener = new CommandListener(this);
	private TMBScreenListener screenListener = new TMBScreenListener(this);
	private TMBPlayerListener playerListener = new TMBPlayerListener();
	private Map<String, TMBMainScreen> screens = new HashMap<String, TMBMainScreen>();
	private static TMB instance = null;
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
		checkForLibs();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.CUSTOM_EVENT, screenListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_KICK, playerListener, Priority.Normal, this);
		sendDescription("enabled");
		createDataFolder();
		config = new Configuration();
		load();
	}

	private void checkForLibs() {
		PluginManager pm = getServer().getPluginManager();
		if(pm.getPlugin("NarrowtuxLib")==null){
			try{
				File toPut = new File("plugins/NarrowtuxLib.jar");
				download(getServer().getLogger(), new URL("http://tetragaming.com/narrowtux/plugins/NarrowtuxLib.jar"), toPut);
				pm.loadPlugin(toPut);
				pm.enablePlugin(pm.getPlugin("NarrowtuxLib"));
			} catch (Exception exception){
				log.severe("[Showcase] could not load NarrowtuxLib, try again or install it manually.");
				pm.disablePlugin(this);
			}
		}
	}

	public static void download(Logger log, URL url, File file) throws IOException {
	    if (!file.getParentFile().exists())
	        file.getParentFile().mkdir();
	    if (file.exists())
	        file.delete();
	    file.createNewFile();
	    final int size = url.openConnection().getContentLength();
	    log.info("Downloading " + file.getName() + " (" + size / 1024 + "kb) ...");
	    final InputStream in = url.openStream();
	    final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
	    final byte[] buffer = new byte[1024];
	    int len, downloaded = 0, msgs = 0;
	    final long start = System.currentTimeMillis();
	    while ((len = in.read(buffer)) >= 0) {
	        out.write(buffer, 0, len);
	        downloaded += len;
	        if ((int)((System.currentTimeMillis() - start) / 500) > msgs) {
	            log.info((int)((double)downloaded / (double)size * 100d) + "%");
	            msgs++;
	        }
	    }
	    in.close();
	    out.close();
	    log.info("Download finished");
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
			if(!infoFile.exists()){
				try {
					FileUtils.copyFromJarToDisk("items.yml", this, getFile());
				} catch (IOException e) {
				}
			}
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
						if(itemData.containsKey("price")){
							info.price = (Double)itemData.get("price");
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
				doLog("You need the items.yml file to use TooManyBuckets. Generating a (very) default one.");
				HashMap<String, Object> itemData = new HashMap<String, Object>();
				for(Material m:Material.values()){
					HashMap<String, Object> item = new HashMap<String, Object>();
					item.put("type", m.getId());
					item.put("data", 0);
					item.put("amount", 64);
					item.put("indefaultview", false);
					String name = SpoutManager.getItemManager().getItemName(m, (short) 0);
					if(name==null)
						name = m.toString();
					itemData.put(name, item);
				}
				FileWriter writer;
				try {
					writer = new FileWriter(infoFile);
					yaml.dump(itemData, writer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}

	public void doLog(String string) {
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
			if(!screens.containsKey(player.getName())){
				screens.put(player.getName(), new TMBMainScreen(player));
			}
			screen = screens.get(player.getName());
			screen.open();
		} else {
			player.sendMessage("You may not use TooManyBuckets");
		}
	}

	public static List<ItemInfo> getSearchResult(String query){
		if(query.trim().equals("")){
			return getInstance().defaultView;
		}
		int id = -1;
		try{
			id = Integer.valueOf(query);
		} catch(Exception e){
			id = -1;
		}
		query = query.toLowerCase();
		List<ItemInfo> result = new ArrayList<ItemInfo>();
		for(ItemInfo mat :getInstance().infos){
			String words[] = mat.name.toLowerCase().split(" ");
			boolean matches = false;
			
			if(id == mat.stack.getTypeId()){
				matches = true;
			}
			for(String word:words){
				if(word.startsWith(query)){
					matches = true;
				}
			}
			if(mat.name.toLowerCase().startsWith(query)){
				matches = true;
			}
			if(matches){
				result.add(mat);
			}
		}
		return result;
	}

	public void removeScreen(TMBMainScreen screen)
	{
		if(screen != null){
			screens.remove(screen.getPlayer().getName());
		}
	}

	public static TMB getInstance(){
		return instance;
	}

	public List<ItemInfo> getDefaultView(){
		return Collections.unmodifiableList(defaultView);
	}

	public Configuration getConfig() {
		return config;
	}

	public void removeScreen(Player player) {
		removeScreen(screens.get(player.getName()));
	}
}
