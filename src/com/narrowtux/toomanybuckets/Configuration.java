package com.narrowtux.toomanybuckets;

import java.io.File;

import com.narrowtux.Utils.FlatFileReader;

public class Configuration {
	private boolean setCustomNames = false;
	FlatFileReader reader ;
	
	public boolean isSetCustomNames() {
		return setCustomNames;
	}

	public Configuration(){
		reader = new FlatFileReader(new File(TMBMain.getInstance().getDataFolder(), "toomanyitems.cfg"), false);
		load();
		reader.write();
	}

	private void load() {
		setCustomNames = reader.getBoolean("setcustomnames", false);
	}
}
