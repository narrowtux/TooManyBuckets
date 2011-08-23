package com.narrowtux.toomanybuckets;

import java.io.File;

import com.narrowtux.Utils.FlatFileReader;

public class Configuration {
	private boolean setCustomNames = false;
	private boolean showItemId = false;
	FlatFileReader reader ;

	public Configuration(){
		reader = new FlatFileReader(new File(TMBMain.getInstance().getDataFolder(), "toomanyitems.cfg"), false);
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
