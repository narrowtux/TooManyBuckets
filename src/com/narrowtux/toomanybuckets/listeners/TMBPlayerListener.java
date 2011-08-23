package com.narrowtux.toomanybuckets.listeners;

import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.narrowtux.toomanybuckets.TMBMain;

public class TMBPlayerListener extends PlayerListener {

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		TMBMain.getInstance().removeScreen(event.getPlayer());
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		TMBMain.getInstance().removeScreen(event.getPlayer());
	}

}
