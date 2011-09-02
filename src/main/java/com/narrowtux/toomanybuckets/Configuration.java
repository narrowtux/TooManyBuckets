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

import java.io.File;

import com.narrowtux.narrowtuxlib.utils.FlatFileReader;

public class Configuration {
	private boolean setCustomNames = false;
	private boolean showItemId = false;
	FlatFileReader reader ;

	public Configuration(){
		reader = new FlatFileReader(new File(TMB.getInstance().getDataFolder(), "toomanyitems.cfg"), false);
		load();
		reader.write();
	}

	private void load() {
		setCustomNames = reader.getBoolean("setcustomnames", false);
		showItemId = reader.getBoolean("showitemid", false);
	}

	public boolean isShowItemId() {
		return showItemId;
	}

	public boolean isSetCustomNames() {
		return setCustomNames;
	}
}
