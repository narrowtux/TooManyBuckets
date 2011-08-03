package com.narrowtux.toomanybuckets.listeners;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.event.screen.TextFieldChangeEvent;
import org.getspout.spoutapi.gui.Screen;

import com.narrowtux.toomanybuckets.TMBMain;
import com.narrowtux.toomanybuckets.gui.TMBMainScreen;

public class TMBScreenListener extends ScreenListener {

	private TMBMain plugin;
	
	public TMBScreenListener(TMBMain tmbMain) {
		plugin = tmbMain;
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if(!event.getButton().isVisible())
			return;
		Screen screen = event.getScreen();
		if(screen instanceof TMBMainScreen){
			TMBMainScreen tmi = ((TMBMainScreen)screen);
			tmi.handleClick(event.getButton());
		}
	}

	@Override
	public void onTextFieldChange(TextFieldChangeEvent event) {
		Screen screen = event.getScreen();
		if(screen instanceof TMBMainScreen){
			((TMBMainScreen)screen).handleTextFieldChange(event.getTextField(), event.getNewText());
		}
	}

	@Override
	public void onScreenClose(ScreenCloseEvent event) {
	}

}
