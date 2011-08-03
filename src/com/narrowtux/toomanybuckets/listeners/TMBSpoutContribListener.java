package com.narrowtux.toomanybuckets.listeners;

import org.getspout.spoutapi.event.spout.ServerTickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;

import com.narrowtux.toomanybuckets.gui.ItemButton;

public class TMBSpoutContribListener extends SpoutListener {

	@Override
	public void onServerTick(ServerTickEvent event) {
		try{
			ItemButton.onTick();
		} catch(LinkageError e){}
	}

}
