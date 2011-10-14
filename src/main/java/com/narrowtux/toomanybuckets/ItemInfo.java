package com.narrowtux.toomanybuckets;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.narrowtux.narrowtuxlib.NarrowtuxLib;

public class ItemInfo {
	public ItemStack stack;
	public String name = null;
	public boolean inDefaultView = false;
	public double price = 0;
	
	public String getTooltip(){
		String data = " ("+stack.getTypeId();
		if(stack.getDurability()!=0)
		{
			data+= ":"+stack.getDurability();
		}
		data+=")";
		String tooltip = ChatColor.GREEN+name;
		if(price > 0){
			tooltip += " "+ChatColor.GOLD+NarrowtuxLib.getMethod().format(price);
		}
		if(TMB.getInstance().getConfig().isShowItemId()){
			tooltip+=ChatColor.WHITE + data;
		}
		return tooltip;
	}
}
