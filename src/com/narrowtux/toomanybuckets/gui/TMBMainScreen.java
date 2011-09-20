package com.narrowtux.toomanybuckets.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.narrowtux.Assistant.GenericWindow;
import com.narrowtux.Main.NarrowtuxLib;
import com.narrowtux.toomanybuckets.ItemInfo;
import com.narrowtux.toomanybuckets.TMBMain;
import com.nijikokun.register.payment.Method.MethodAccount;

public class TMBMainScreen extends GenericWindow {
	private SpoutPlayer player;
	private TextField search;
	private List<ItemInfo> visibleItems = new ArrayList<ItemInfo>();
	private Button clearInventoryButton, clearSearchButton, closeWindowButton;
	private Map<GridLocation, ItemButton> buttons = new HashMap<GridLocation, ItemButton>();
	
	public class GridLocation{
		int x, y;
		public GridLocation(int x, int y){
			this.x = x;
			this.y = y;
		}
		@Override
		public int hashCode() {
			return x+y*10000;
		}
	}
	
	public TMBMainScreen(SpoutPlayer player){
		super("Too Many Buckets", player);
		this.player = player;
		visibleItems = TMBMain.getInstance().getDefaultView();
		initScreen();
	}
	
	private void initScreen() {
		TMBMain plugin = TMBMain.getInstance();
		search = new GenericTextField();
		search.setX(getMarginLeft()).setY(getMarginTop()+25).setHeight(20).setWidth(185);
		attachWidget(plugin, search);
		clearInventoryButton = new GenericButton(ChatColor.RED+"Clear Inventory");
		clearInventoryButton.setX(getMarginLeft()+195).setY(search.getY()).setHeight(20).setWidth(185);
		attachWidget(plugin, clearInventoryButton);
		clearSearchButton = new GenericButton("X");
		clearSearchButton.setWidth(20).setHeight(20).setX(getMarginLeft()+185-20).setY(search.getY());
		clearSearchButton.setPriority(RenderPriority.Low);
		clearSearchButton.setTooltip("Clear search");
		closeWindowButton = new GenericButton(ChatColor.RED+"X");
		closeWindowButton.setTooltip("Close Window");
		closeWindowButton.setWidth(20).setHeight(20).setX(getMarginRight()-26).setY(getMarginTop()-2);
		closeWindowButton.setPriority(RenderPriority.Low);
		attachWidget(plugin, closeWindowButton);
		search.setTooltip("Search for an item");
		attachWidget(plugin, clearSearchButton);
		for(int y = 0;y<6;y++){
			for(int x = 0;x<19;x++){
				GridLocation loc = new GridLocation(x, y);
				ItemButton current = new ItemButton(null, this);
				current.setX(x*20+getMarginLeft());
				current.setY(y*20+getMarginTop()+50);
				current.attachToScreen();
				buttons.put(loc, current);
			}
		}
		refreshView();
	}

	public void open(){
		player.getMainScreen().attachPopupScreen(this);
		setDirty(true);
		for(Widget widget:getAttachedWidgets()){
			widget.setVisible(true);
			widget.setDirty(true);
		}
		refreshView();
	}
	
	public void hide(){
		close();
	}
	
	public void handleTextFieldChange(TextField field, String newValue){
		if(field.equals(search)){
			doSearch(newValue);
		}
	}

	public void doSearch(String query) {
		List<ItemInfo> result = TMBMain.getSearchResult(query);
		visibleItems = result;
		refreshView();
	}

	public void handleClick(Button button) {
		if(button.equals(clearInventoryButton)){
			player.getInventory().clear();
			return;
		}
		if(button.equals(clearSearchButton)){
			search.setText("");
			search.setDirty(true);
			doSearch("");
		}
		if(button.equals(closeWindowButton)){
			close();
		}
		ItemButton ibtn = ItemButton.getByButton(button);
		if(ibtn!=null)
		{
			ItemInfo info = ibtn.getType();
			ItemStack stack = info.stack.clone();
			if(info.price>0){
				MethodAccount account = NarrowtuxLib.getMethod().getAccount(player.getName());
				if(account.hasEnough(info.price)){
					account.subtract(info.price);
					player.sendNotification(ChatColor.YELLOW.toString()+info.stack.getAmount()+" "+info.name, 
							"Bought for "+NarrowtuxLib.getMethod().format(info.price), 
							info.stack.getType(), 
							info.stack.getDurability(), 5000);
				} else {
					player.sendNotification(ChatColor.RED+"Not enough money for", 
							info.name, 
							info.stack.getType(), 
							info.stack.getDurability(), 5000);
					return;
				}
			} else {
				player.sendNotification(ChatColor.YELLOW.toString()+info.stack.getAmount()+" "+info.name, 
						"Added to your inventory", 
						info.stack.getType(), 
						info.stack.getDurability(), 5000);
			}
			player.getInventory().addItem(stack);
		}
	}
	
	public void refreshView(){
		int i = 0;
		for(ItemButton button:buttons.values()){
			ItemInfo info = getInfo(i);
			if(info==null)
			{
				button.setVisible(false);
			} else {
				button.setVisible(true);
				button.setType(info);
			}
			i++;
		}
	}
	
	public ItemInfo getInfo(int i){
		if(visibleItems.size()>i){
			return visibleItems.get(i);
		} else {
			return null;
		}
	}

	public void handleClose() {
		
	}
}
