package com.narrowtux.toomanybuckets.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.ItemWidget;
import org.getspout.spoutapi.gui.Screen;

public class ItemButton {
	private ItemStack type;
	private Button btn;
	private Screen screen;
	private ItemWidget itemWidget;
	private static Map<Button, ItemButton> instances = new HashMap<Button, ItemButton>();
	@SuppressWarnings("unused")
	private int x, y;
	private boolean visible;
	private boolean clickedThisTick = false;
	public ItemButton(ItemStack type, Screen screen)
	{
		if(type == null)
		{
			type = new ItemStack(0);
		}
		this.type = type;
		this.screen = screen;
		initWidgets();
	}
	
	private void initWidgets() {
		btn = new GenericButton();
		btn.setHeight(20).setWidth(20);
		itemWidget = new GenericItemWidget(type);
		if(type.getType().isBlock()){
			itemWidget.setWidth(8).setHeight(8).setDepth(8);
		} else {
			itemWidget.setWidth(1).setHeight(1).setDepth(1);
		}
		instances.put(btn, this);
	}
	
	public void attachToScreen(){
		screen.attachWidget(btn).attachWidget(itemWidget);
		setVisible(true);
	}

	public void setX(int x){
		this.x = x;
		btn.setX(x);
		itemWidget.setX(x+2);
		dirtyWidgets();
	}
	
	public void setY(int y){
		this.y = y;
		btn.setY(y);
		itemWidget.setY(y+2);
		dirtyWidgets();
	}

	public void setVisible(boolean b) {
		btn.setVisible(b);
		btn.setEnabled(b);
		itemWidget.setVisible(b);
		dirtyWidgets();
		visible = b;
	}
	
	public void dirtyWidgets(){
		btn.setDirty(true);
		itemWidget.setDirty(true);
	}

	public void remove() {
		screen.removeWidget(itemWidget);
		screen.removeWidget(btn);
	}
	
	
	public ItemStack getType(){
		return type;
	}
	
	public Button getButton(){
		return btn;
	}
	
	public Screen getScreen(){
		return screen;
	}
	
	public void setType(ItemStack stack){
		this.type = stack;
		itemWidget.setTypeId(stack.getTypeId()).setData(stack.getDurability());
		itemWidget.setDirty(true);
	}
	
	public static ItemButton getByButton(Button btn)
	{
		return instances.get(btn);
	}

	public boolean isVisible() {
		return visible;
	}
	
	public boolean isClickedThisTick(){
		return false;/*
		boolean tmp = clickedThisTick;
		clickedThisTick = true;
		return tmp;*/ //disable fix temporally
	}
	
	public static void onTick(){
		for(ItemButton button:instances.values()){
			button.clickedThisTick = false;
		}
	}
}
