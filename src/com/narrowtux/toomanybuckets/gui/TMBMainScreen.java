package com.narrowtux.toomanybuckets.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.narrowtux.Assistant.GenericWindow;
import com.narrowtux.toomanybuckets.TMBMain;

public class TMBMainScreen extends GenericWindow {
	private SpoutPlayer player;
	private TextField search;
	private List<ItemStack> visibleItems = new ArrayList<ItemStack>();
	private Button clearInventoryButton, clearSearchButton;
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
		initScreen();
	}
	
	private void initScreen() {
		search = new GenericTextField();
		search.setX(getMarginLeft()).setY(getMarginTop()+25).setHeight(20).setWidth(185);
		attachWidget(search);
		clearInventoryButton = new GenericButton(ChatColor.RED+"Clear Inventory");
		clearInventoryButton.setX(getMarginLeft()+195).setY(search.getY()).setHeight(20).setWidth(185);
		attachWidget(clearInventoryButton);
		clearSearchButton = new GenericButton("X");
		clearSearchButton.setWidth(20).setHeight(20).setX(getMarginLeft()+185-20).setY(search.getY());
		attachWidget(clearSearchButton);
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
		if(query.equals("")){
			visibleItems.clear();
		} else {
			List<ItemStack> result = TMBMain.getSearchResult(query);
			visibleItems = result;
		}
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
		ItemButton ibtn = ItemButton.getByButton(button);
		if(ibtn!=null&&!ibtn.isClickedThisTick())
		{
			ItemStack stack = ibtn.getType().clone();
			stack.setAmount(64); //TODO: Gain correct stack size values!
			player.getInventory().addItem(stack);
		}
	}
	
	public void refreshView(){
		int i = 0;
		for(ItemButton button:buttons.values()){
			ItemStack stack = getStack(i);
			if(stack==null)
			{
				button.setVisible(false);
			} else {
				button.setVisible(true);
				button.setType(stack);
			}
			i++;
		}
	}
	
	public ItemStack getStack(int i){
		if(visibleItems.size()>i){
			return visibleItems.get(i);
		} else {
			return null;
		}
	}
}
