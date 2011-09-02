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

package com.narrowtux.toomanybuckets.listeners;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.event.screen.TextFieldChangeEvent;
import org.getspout.spoutapi.gui.Screen;

import com.narrowtux.toomanybuckets.TMB;
import com.narrowtux.toomanybuckets.gui.TMBScreen;

public class TMBScreenListener extends ScreenListener {
	private TMB plugin;

	public TMBScreenListener(TMB tmbMain) {
		plugin = tmbMain;
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if(!event.getButton().isVisible())
			return;
		Screen screen = event.getScreen();
		if(screen instanceof TMBScreen){
			TMBScreen tmi = ((TMBScreen)screen);
			tmi.handleClick(event.getButton());
		}
	}

	@Override
	public void onTextFieldChange(TextFieldChangeEvent event) {
		Screen screen = event.getScreen();
		if(screen instanceof TMBScreen){
			((TMBScreen)screen).handleTextFieldChange(event.getTextField(), event.getNewText());
		}
	}

	@Override
	public void onScreenClose(ScreenCloseEvent event) {
		Screen screen = event.getScreen();
		if(screen instanceof TMBScreen){
			TMBScreen tmi = ((TMBScreen)screen);
			tmi.handleClose();
		}
	}
}
