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

package com.narrowtux.toomanybuckets.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.ItemWidget;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.inventory.ItemManager;

import com.narrowtux.toomanybuckets.ItemInfo;
import com.narrowtux.toomanybuckets.TMB;

public class ItemButton {
	private ItemInfo type;
	private Button btn;
	private Screen screen;
	private ItemWidget itemWidget;
	private static Map<Button, ItemButton> instances = new HashMap<Button, ItemButton>();
	@SuppressWarnings("unused")
	private int x, y;
	private boolean visible;
	public ItemButton(ItemInfo type, Screen screen)
	{
		if(type == null)
		{
			type = new ItemInfo();
			type.stack = new ItemStack(0);
		}
		this.type = type;
		this.screen = screen;
		initWidgets();
	}

	private void initWidgets() {
		btn = new GenericButton();
		btn.setHeight(20).setWidth(20);
		btn.setPriority(RenderPriority.High);
		itemWidget = new GenericItemWidget(type.stack);
		itemWidget.setPriority(RenderPriority.Normal);
		if(type.stack.getType().isBlock()){
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


	public ItemInfo getType(){
		return type;
	}

	public Button getButton(){
		return btn;
	}

	public Screen getScreen(){
		return screen;
	}

	public void setType(ItemInfo info){
		this.type = info;
		itemWidget.setTypeId(info.stack.getTypeId()).setData(info.stack.getDurability());
		if(info!=null){
			String data = " ("+info.stack.getTypeId();
			if(info.stack.getDurability()!=0)
			{
				data+= ":"+info.stack.getDurability();
			}
			data+=")";
			String tooltip = info.name;
			if(TMB.getInstance().getConfig().isShowItemId()){
				tooltip+= data;
			}
			btn.setTooltip(tooltip);
		}
		itemWidget.setDirty(true);
	}

	public static ItemButton getByButton(Button btn)
	{
		return instances.get(btn);
	}

	public boolean isVisible() {
		return visible;
	}
}
