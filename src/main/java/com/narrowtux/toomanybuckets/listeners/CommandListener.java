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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
//import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.narrowtux.toomanybuckets.TMB;

public class CommandListener {
    private TMB plugin;

    public CommandListener(TMB tmbMain) {
        plugin = tmbMain;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String args[]) {
        if (sender instanceof SpoutPlayer) {
            SpoutPlayer player = (SpoutPlayer) sender;
            if (cmd.getName().equals("toomanybuckets") && args.length == 0) {
                plugin.openOverlay(player);
                return true;
            } else {
                if (args.length == 1 && args[0].equalsIgnoreCase("rml")) {
                    player.sendMessage(ChatColor.RED
                            + "rml can only be issued from the console!");
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if (args.length != 1 || !args[0].equalsIgnoreCase("rml")) {
                sender.sendMessage("You need to be player to invoke the GUI interface!");
                return true;
            } else {
                plugin.load(true);
                return true;
            }
        }
    }
}
